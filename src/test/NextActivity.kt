package test

import gem.seek.Activity
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button


class NextActivity : Activity(){

    @FXML private lateinit var backBtn: Button

    override fun onCreate() {
        super.onCreate()
        val loader = FXMLLoader(NextActivity::class.java.getResource("activity_next.fxml"))
        setContentView(loader)
        showHomeEnabled = true
        displayHomeAsUp = true

        backBtn.setOnAction {
            startActivity(MainActivity::class.java)
        }
    }

}