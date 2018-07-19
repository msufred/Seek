package test

import gem.seek.SeekApplication
import javafx.application.Application
import javafx.stage.Stage

class SeekTest : Application() {

    override fun start(primaryStage: Stage) {
        val seekApplication = SeekApplication()
        seekApplication.initialize(primaryStage, "Seek Test", 480.0, 720.0)
        seekApplication.startActivity(MainActivity::class.java)
        seekApplication.show()
    }

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            Application.launch(SeekTest::class.java, *args)
        }
    }
}