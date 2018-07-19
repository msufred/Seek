package test

import gem.seek.Activity
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button

class MainActivity : Activity() {

    // declaring FXML components
    @FXML private lateinit var nextBtn: Button

    // set content view in onCreate()
    override fun onCreate() {
        super.onCreate() // <-- IMPORTANT!
        val loader = FXMLLoader(MainActivity::class.java.getResource("activity_main.fxml"))
        setContentView(loader)

        // start NextActivity on action
        nextBtn.onAction = EventHandler {
            startActivity(NextActivity::class.java)
        }
    }
}