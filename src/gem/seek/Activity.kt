package gem.seek

import gem.seek.util.ACTIONBAR_SIZE
import javafx.beans.property.SimpleObjectProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage
import java.io.IOException
import java.net.URL
import java.util.*

/**
 * <p>An Activity object encapsulates a single or group of task into one. An Activity can start or stop another Activity.
 * Each Activity object has a state which can be utilized by executing logic depending on the state the Activity is in.
 * For example an Activity is on CREATE state. Logic for creating objects, fields etc. can be done in the onCreate()
 * method. All clean up logic like closing the database can be executed inside the onStop() method, and so on.

 * <p>The goal of encapsulating these tasks into an Activity object is to make the organization of code logic easier. We
 * can only worry of what logic to execute for each Activity state without thinking when they will be called, since
 * these problems are being taken care of by the application.

 * <p>For more of these information, see [SeekApplication].

 * Created by Gem Seeker on 4/19/2017.
 */
abstract class Activity : StatefulActivity, Context, Initializable {

    /** Default action bar of the Activity */
    private val actionBar = HBox()

    /** Default action bar home button */
    private val homeButton = Button("Home")

    /** The Stage activityWindow of this Activity, if [isWindowed] is `true`. */
    private val activityWindow = Stage()

    /** Default Activity window width */
    private var windowWidth = 400.0

    /** Default Activity window height */
    private var windowHeight = 500.0

    /** The FXMLLoader object used to load the layout view of this Activity */
    private var fxmlLoader: FXMLLoader? = null

    /** The Node layout view of this Activity */
    private var contentView: Node? = null

    /** Observable state property of this Activity */
    val stateProperty = SimpleObjectProperty(State.NOT_DEFINED)

    /** Default FragmentManager of this Activity */
    val defaultFragmentManager: FragmentManager?
        get() {
            if (applicationContext != null) {
                return applicationContext!!.getFragmentManager(this)
            }
            return null
        }

    /** The main root component of the Activity */
    val root = VBox()

    var onHomePressedAction: EventHandler<ActionEvent>? = null

    var showHomeEnabled = false
        set(value) {
            actionBar.isVisible = value
        }

    var displayHomeAsUp = false
        set(homeAsUp) {
            if (homeAsUp) {
                homeButton.text = "Back"
            }
        }

    var isWindowed = false

    /** The name of this Activity. If name is not set, the return value will be the class name of this Activity. */
    var name: String = ""
        get() = if (field.isEmpty()) javaClass.simpleName else field

    /** The parent Activity of this Activity */
    var parentActivity: Activity? = null

    /** The ApplicationContext of this Activity */
    var applicationContext: ApplicationContext? = null
        set(value) {
            field = value
            if (value is SeekApplication) {
                // inherit styles
                activityWindow.scene.stylesheets.add(value.stylesheet)
            }
        }

    /**
     * Initialize defaults
     */
    init {
        root.styleClass.add("activity_root")

        homeButton.apply {
            styleClass.add("activity_home_button")
            onAction = EventHandler {
                if (displayHomeAsUp || isWindowed) {
                    onBackPressed()
                } else {
                    onHomePressedAction?.handle(it)
                }
            }
        }

        actionBar.apply {
            styleClass.add("activity_action_bar")
            padding = Insets(8.0)
            spacing = 8.0
            alignment = Pos.CENTER_LEFT
            minHeight = ACTIONBAR_SIZE
            maxHeight = ACTIONBAR_SIZE
            isVisible = false
            children.add(homeButton)
        }

        root.children.add(actionBar)

        // init Activity window
        activityWindow.initModality(Modality.APPLICATION_MODAL) // blocks event
    }

    /**
     * Sets the layout view of this Activity.
     */
    fun setContentView(loader: FXMLLoader?) {
        if (loader == null) {
            throw IllegalArgumentException("FXMLLoader should not be NULL")
        }
        if (loader.getRoot<Any>() != null) {
            loader.setRoot(null)
        }
        loader.setController(this) // overwrite controller
        try {
            loader.load<Any>()
            contentView = loader.getRoot<Node>()
            VBox.setVgrow(contentView, Priority.ALWAYS)
            root.children.add(contentView)

            // set Activity window scene
            activityWindow.scene = Scene(root, windowWidth, windowHeight)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        fxmlLoader = loader
    }

    // <editor-fold desc="Stateful Overridden Methods" defaultstate="collapsed">
    /**
     * Called upon the creation of the Activity.
     */
    override fun onCreate() {
        stateProperty.set(State.CREATED)
    }

    /**
     * Called right after onCreate() or when it is requested to be visible to the user again after onStop().
     */
    override fun onStart() {
        stateProperty.set(State.STARTED)
    }

    /**
     * Called when another Activity is requested to be visible to the user.
     */
    override fun onPause() {
        stateProperty.set(State.PAUSED)
    }

    /**
     * Called after this Activity is paused.
     */
    override fun onResume() {
        stateProperty.set(State.RESUMED)
    }

    /**
     * Called when Activity is requested to stop.
     */
    override fun onStop() {
        stateProperty.set(State.STOPPED)
    }

    /**
     * Called when Activity is forced to be destroyed or when the application is about to close/terminate.
     */
    override fun onDestroy() {
        stateProperty.set(State.DESTROYED)
    }
    // </editor-fold>

    // <editor-fold desc="Context Overridden Methods" defaultstate="collapsed">
    /**
     * @see Context
     */
    override fun startActivity(activity: Class<out Activity>) {
        applicationContext?.startActivity(activity)
    }

    /**
     * @see Context
     */
    override fun startActivity(activity: Activity) {
        applicationContext?.startActivity(activity)
    }

    override fun startActivityFromParent(activity: Class<out Activity>, parentActivity: Activity) {
        applicationContext?.startActivityFromParent(activity, parentActivity)
    }

    override fun startActivityFromParent(activity: Activity, parentActivity: Activity) {
        applicationContext?.startActivityFromParent(activity, parentActivity)
    }
    // </editor-fold>

    /**
     * Activity is also an {@see Initializable} subclass. Override this to fully utilize JavaFX initialize feature.
     */
    override fun initialize(location: URL?, resources: ResourceBundle?) {
        // Override to initialize FXML resources
    }

    /**
     * Execute action when invoked or when Back button is pressed.
     */
    override fun onBackPressed() {
        parentActivity?. let {
            if (isWindowed) {
                if (activityWindow.isShowing) activityWindow.close()
            }
            startActivity(it)
        }
    }

    /**
     * Called by the SeekApplication to start. [onResume] is invoked after this method call.
     */
    fun showActivityWindow() {
        if (activityWindow.scene === null) throw NullPointerException()
        if (isWindowed) {
            // using show() instead of showAndWait() since I don't want this Activity to block any activities of the
            // SeekApplication object... I'll let the SeekApplication to take care of everything...
            activityWindow.show()
        }
    }
}
