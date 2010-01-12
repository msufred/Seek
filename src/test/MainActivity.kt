package test

import gem.seek.Activity
import gem.seek.controls.Toast
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button

class MainActivity : Activity() {

    // declaring FXML components
    @FXML private lateinit var nextBtn: Button
    @FXML private lateinit var windowedActivityBtn: Button

    // set content view in onCreate()
    override fun onCreate() {
        super.onCreate() // <-- IMPORTANT!
        val loader = FXMLLoader(MainActivity::class.java.getResource("activity_main.fxml"))
        setContentView(loader)
        showHomeEnabled = true
        onHomePressedAction = EventHandler { println("Home is pressed!!!") }

        // start NextActivity on action
        nextBtn.onAction = EventHandler {
            startActivityFromParent(NextActivity::class.java, this)
            Toast.make(applicationContext, "This message is so long that the label will be forced" +
                    " to be wrapped in the Toast component. Wouldn't believe me? Try it!", false, Toast.DURATION_SHORT).show()
        }

        windowedActivityBtn.onAction = EventHandler {
            startActivityFromParent(WindowedActivity::class.java, this)
        }
    }
}