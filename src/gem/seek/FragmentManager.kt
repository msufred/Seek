package gem.seek

import gem.seek.logging.Logger
import gem.seek.util.*
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Parent
import javafx.scene.layout.StackPane
import java.util.*

/**
 * <p>FragmentManager objects manages Fragments and its lifecycle. FragmentManager has also its own lifecycle by which can
 * only be managed by the SeekApplication object. FragmentManager's lifecycle is alive as long as the application is
 * running.
 *
 * @see Fragment
 * @see SeekApplication
 *
 * Created by Gem Seeker on 7/18/2018.
 */
abstract class FragmentManager(private val activity: Activity): Stateful {

    private val debugName = javaClass.simpleName
    private val logger: Logger = Logger(LOG_TO_FILE)
    private val fragmentsMap = HashMap<String, Fragment>()
    private var mCurrentFragment: Fragment? = null

    val stateProperty = SimpleObjectProperty<State>(State.NOT_DEFINED)

    init {
        this.onCreate() // calls onCreate on initialization
    }

    /**
     * Replaces current Fragment (if there's any). The Parent layout is the container of the Fragment to
     * be replaced.
     */
    fun replaceFragment(layout: Parent?, fragment: Fragment?) {
        if (layout == null) throw IllegalArgumentException("Fragment layout parent must not be null!")
        if (fragment == null) throw IllegalArgumentException("Fragment must not be null!")

        // pause current fragment (if not null)
        mCurrentFragment?.let {
            if (DEBUG) logger.log(debugName, "Pausing current Fragment [${it.name}")
            it.onPause()
        }

        // replace layout view
        if (layout is StackPane) {
            layout.children.apply {
                clear()
                add(fragment.contentView)
            }
        }

        var state = fragment.stateProperty.get()

        // if fragment was just created, set parent activity and call onStart()
        if (state == State.CREATED){
            if (DEBUG) logger.log(debugName, "Sets Fragment's Activity and calling onStart() of Fragment [${fragment.name}]")
            fragment.activity = activity
            fragment.onStart()
        }

        // if fragment was stopped, call onStart()
        else if (state == State.STOPPED) {
            if (DEBUG) logger.log(debugName, "Calling onStart() of Fragment [${fragment.name}]")
            fragment.onStart()
        }

        // call onResume()
        if (DEBUG) logger.log(debugName, "Calling onResume() of Fragment [${fragment.name}]")
        fragment.onResume()

        // if fragment do not exists in the fragments map, put fragment in fragments map
        if (fragmentsMap.containsKey(fragment.javaClass.name)) fragmentsMap.put(fragment.javaClass.name, fragment)

        // set fragment as current
        mCurrentFragment = fragment
    }

    // <editor-fold desc="Stateful Implemented Methods" defaultstate="collapsed">

    override fun onCreate() {
        stateProperty.set(State.CREATED)
    }

    override fun onStart() {
        mCurrentFragment?.onStart()
        stateProperty.set(State.STARTED)
    }

    override fun onResume() {
        mCurrentFragment?.onResume()
        stateProperty.set(State.RESUMED)
    }

    override fun onPause() {
        mCurrentFragment?.onPause()
        stateProperty.set(State.PAUSED)
    }

    override fun onStop() {
        // onStop will stop all fragments
        fragmentsMap.forEach { _, fragment ->
            if (fragment.stateProperty.get() != State.STOPPED
                    || fragment.stateProperty.get() != State.DESTROYED) {
                if (DEBUG) logger.log(debugName, "Stopping Fragment [${fragment.name}]")
                fragment.onStop()
            }
        }
        stateProperty.set(State.STOPPED)
    }

    override fun onDestroy() {
        fragmentsMap.forEach { _, fragment ->
            if (fragment.stateProperty.get() != State.DESTROYED) {
                if (DEBUG) logger.log(debugName, "Destroying Fragment [${fragment.name}]")
                fragment.onDestroy()
            }
        }
        stateProperty.set(State.DESTROYED)
    }

    //</editor-fold>
}