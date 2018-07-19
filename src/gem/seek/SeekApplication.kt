package gem.seek

import gem.seek.logging.Logger
import gem.seek.util.*
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.stage.Stage

import java.util.HashMap

/**
 * Seek is a library based on Android's lifecycle. A single or a group of task is encapsulated in an Activity.
 * Each Activity has a lifecycle. Seek Activity lifecycle has seven (7) states: CREATED, STARTED, PAUSED, RESUMED,
 * STOPPED, DESTROYED and NOT_DEFINED. Activity's behavior depends on the logic defined on each lifecycle method. These
 * methods are the: onCreate(), onStart(), onResume(), onPause(), onStop() and onDestroy(). One of the important things
 * to remember in using Seek is that Activity objects MUST NOT be instantiated manually. Seek application will be
 * responsible for creating and launching Activity objects, thus the only thing to worry is how the Activity will behave
 * on each of its state.

 * onCreate() is called after Activity is created. This is the best place to instantiate fields, objects etc.

 * onStart() is called right after onCreate(). Things like opening a database should be placed in here.

 * onResume() is called right after onStart() or onPause(). Operations like querying database, executing tasks after
 * the Activity's view is visible should be placed in here. If it is called after the onPause(), task like resuming
 * streaming, downloading etc. can be placed in here.

 * onPause() is called when the Activity is moved to the background, or another Activity is requested to be displayed
 * to the user. Tasks such as closing database, pausing downloads or streams can be placed here.

 * onStop() is called when the application purposely stops the Activity or the application itself. Closing the database,
 * stopping downloads and stopping other working resources can be placed in here.

 * onDestroy() is called when the application is about the terminate either due to an error or the user purposely
 * terminating the application. Clean up tasks such as freeing resources can be placed in here.

 * ---------------------------------------------------------------------------------

 * The heart of this library is the SeekApplication class. The state of each Activity
 * is managed within the SeekApplication object. Using SeekApplication is fairly easy,
 * create SeekApplication object, then start any Activity object by calling
 * SeekApplication.startActivity() function.
 */
class SeekApplication : ApplicationContext() {

    private val debugName = "SeekApplication"

    private val minWidth = 600.0
    private val minHeight = 400.0
    private val defTitle = "Seek Application"

    private val logger: Logger
    private val activitiesMap = HashMap<String, Activity>()
    private val fragmentManagerMap = HashMap<String, FragmentManager>()
    private var window: Stage? = null
    private var root = StackPane()
    private var mCurrentActivity: Activity? = null

    init {
        // initialize root view and Logger instance
        root.alignment = Pos.CENTER
        root.styleClass.add("container")

        // create logger
        logger = Logger(LOG_TO_FILE)
    }

    fun initialize(stage: Stage, windowTitle: String = defTitle, maximize: Boolean = false) {
        initialize(stage, windowTitle, minWidth, minHeight,  maximize)
    }

    fun initialize(stage: Stage,                    // Stage window
                   windowTitle: String = defTitle, // title of the window
                   width: Double = minWidth,       // min width of the window
                   height: Double = minHeight,     // min height of the window
                   maximize: Boolean = false) {     // maximize window or not

        stage.title = windowTitle
        stage.scene = Scene(root, width, height)
        stage.isMaximized = maximize
        window = stage

        // stop and destroy all fragments and activities on close
        window?.let {
            it.setOnCloseRequest { evt ->
                if (DEBUG) logger.log(debugName, "Closing Window")

                // stop and destroy all FragmentManager
                for ((k, fragment) in fragmentManagerMap) {
                    val state = fragment.stateProperty.get()
                    if (state !== State.STOPPED) fragment.onStop()
                    if (state !== State.DESTROYED) fragment.onDestroy()
                }

                // stop and destroy all Activity
                for ((k, activity) in activitiesMap) {
                    val state = activity.stateProperty.get()
                    if (state !== State.STOPPED) activity.onStop()
                    if (state !== State.DESTROYED) activity.onDestroy()
                }
            }
        }
    }

    fun show() {
        if (window == null) {
            if (DEBUG) logger.logErr(debugName, "Stage window is null")
            throw RuntimeException("SeekApplication.initialize() wasn't called before SeekApplication.show()")
        }
        if (DEBUG) logger.log(debugName, "Showing application window")
        window?.show()
    }

    override fun startActivity(activity: Class<out Activity>) {
        try {
            val _activity: Activity?
            val name = activity.name
            if (activitiesMap.containsKey(name)) {
                // if Activity already exists in map
                _activity = activitiesMap[name]         // get Activity from map
            } else {
                _activity = activity.newInstance()      // create Activity instance
                _activity.onCreate()                    // call onCreate()
                activitiesMap.put(name, _activity)      // put Activity to map
            }
            _activity?.let { startActivity(it) }
        } catch (e: InstantiationException) {
            if (DEBUG) logger.logErr(debugName, "Error while instantiating Activity", e)
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            if (DEBUG) logger.logErr(debugName, "IllegalAccessException", e)
            e.printStackTrace()
        }

    }

    override fun startActivity(activity: Activity) {
        // pause current activity and it's fragment
        mCurrentActivity?.let {
            if (DEBUG) logger.log(debugName, "Pausing current Activity [${it.name}]")

            // pause fragments first
            val fm = it.defaultFragmentManager
            if (fm != null) {
                val fmState = fm.stateProperty.get()
                // pause only if resumed
                if (fmState == State.RESUMED) {
                    fm.onPause()
                }
            }

            // pause activity
            it.onPause()
        }

        val state = activity.stateProperty.get()

        // checking activity state, if state is NOT_DEFINED resume current activity
        if (state != State.NOT_DEFINED) {
            if (DEBUG) logger.log(debugName, "Starting Activity [${activity.name}]")

            // TODO: Change layout view.
            root.children.clear()
            root.children.add(activity.contentView)

            val fm = activity.defaultFragmentManager

            // State.CREATED
            if (state == State.CREATED) {
                if (activity.applicationContext == null) {
                    activity.applicationContext = this
                }
                activity.onStart()
                activity.onResume()
                activitiesMap.put(if (activity.name != null) activity.name!! else activity.javaClass.name, activity)

                // start at resume fragment manager
                if (fm != null) {
                    fm.onCreate()
                    fm.onStart()
                    fm.onResume()
                }
            } else if (state == State.PAUSED) {
                activity.onResume()
                fm?.onResume()
            } else if (state == State.STOPPED) {
                activity.onStart()
                activity.onResume()
                if (fm != null) {
                    fm.onStart()
                    fm.onResume()
                }
            }// State.STOPPED
            // State.PAUSED

            mCurrentActivity = activity
        } else {
            if (DEBUG) logger.log(debugName, "NOT_DEFINED Activity State found. Resuming current Activity")
            if (mCurrentActivity != null) {
                mCurrentActivity!!.onResume()
                val curFm = mCurrentActivity!!.defaultFragmentManager
                curFm?.onResume()
            }
        }
    }

    override fun getFragmentManager(activity: Activity): FragmentManager? {
        val fm: FragmentManager?
        val name = activity.name
        if (fragmentManagerMap.containsKey(name)) {
            fm = fragmentManagerMap.get(name)
        } else {
            fm = SimpleFragmentManager(activity)
            fragmentManagerMap.put(activity.name!!, fm)
        }
        return fm
    }
}
