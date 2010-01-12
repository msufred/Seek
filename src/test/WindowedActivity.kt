package test

import gem.seek.Activity
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button

class WindowedActivity : Activity() {

    @FXML private lateinit var backBtn: Button

    override fun onCreate() {
        super.onCreate()
        val loader = FXMLLoader(WindowedActivity::class.java.getResource("activity_windowed.fxml"))
        setContentView(loader)
        showHomeEnabled = true
        displayHomeAsUp = true
        isWindowed = true

        backBtn.onAction = EventHandler {
            onBackPressed()
        }
    }

}