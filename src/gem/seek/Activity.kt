package gem.seek

import javafx.beans.property.SimpleObjectProperty
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Node
import java.io.IOException
import java.net.URL
import java.util.*

/**
 * An Activity object encapsulates a single or group of task into one. An Activity can start or stop another Activity.
 * Each Activity object has a state which can be utilized by executing logic depending on the state the Activity is in.
 * For example an Activity is on CREATE state. Logic for creating objects, fields etc. can be done in the onCreate()
 * method. All clean up logic like closing the database can be executed inside the onStop() method, and so on.

 * The goal of encapsulating these tasks into an Activity object is to make the organization of code logic easier. We
 * can only worry of what logic to execute for each Activity state without thinking when they will be called, since
 * these problems are being taken care of by the application.

 * For more of these information, see [SeekApplication].

 * Created by Gem Seeker on 4/19/2017.
 */
abstract class Activity : Stateful, Context, Initializable {

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

    /** The FXMLLoader object used to load the layout view of this Activity */
    var fxmlLoader: FXMLLoader? = null

    /** The Node layout view of this Activity */
    var contentView: Node? = null

    /** The name of this Activity. If name is not set, the return value will be the class name of this Activity. */
    var name: String? = null
        get() = if (field == null || field!!.isEmpty()) javaClass.simpleName else field

    /** The parent Activity of this Activity */
    var parentActivity: Activity? = null

    /** The ApplicationContext of this Activity */
    var applicationContext: ApplicationContext? = null

    //=================================================================================================================
    //                                          Methods (or Functions whatever)
    //=================================================================================================================

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
    // </editor-fold>

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        // Override to initialize FXML resources
    }
}
