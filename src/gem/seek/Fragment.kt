package gem.seek

import javafx.beans.property.SimpleObjectProperty
import javafx.fxml.FXMLLoader
import javafx.scene.Node

/**
 * A Fragment, like the Activity, has its own lifecycle. But unlike the Activity where lifecycle's scope depends on the
 * application, Fragment's lifecycle depends on the Activity where it resides on. It also has its own layout view. The
 * only thing to remember is that Fragment should not contain heavy tasks or operations. These kinds of tasks and
 * operations must be put in the Activity. Fragment can be used to display, or execute minimal tasks that could help
 * the Activity serve its purpose.
 *
 * Created by Gem Seeker on 7/18/2018.
 */
abstract class Fragment : Stateful, Context {

    val stateProperty = SimpleObjectProperty(State.NOT_DEFINED)
    var activity: Activity? = null
    var contentView: Node? = null
    var name: String? = null
        get() = if (field == null || field!!.isEmpty()) javaClass.simpleName else field

    fun setContentView(loader: FXMLLoader?) {
        if (loader == null) throw IllegalArgumentException("FXMLLoader must not be null!")
        loader.setController(this)
        contentView = loader.getRoot()
    }

    // <editor-fold desc="Stateful Overriden Methods" defaultstate="collapsed">
    override fun onCreate() {
        stateProperty.set(State.CREATED)
    }

    override fun onStart() {
        stateProperty.set(State.STARTED)
    }

    override fun onPause() {
        stateProperty.set(State.PAUSED)
    }

    override fun onResume() {
        stateProperty.set(State.RESUMED)
    }

    override fun onStop() {
        stateProperty.set(State.STOPPED)
    }

    override fun onDestroy() {
        stateProperty.set(State.DESTROYED)
    }
    //</editor-fold>

    //<editor-fold desc="Context Overridden Methods" defaultstate="collapsed">
    override fun startActivity(activity: Class<out Activity>) {
        this.activity?.let { it.startActivity(activity) }
    }

    override fun startActivity(activity: Activity) {
        this.activity?.let { it.startActivity(activity) }
    }
    //</editor-fold>
}