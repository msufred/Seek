package gem.seek.logging

import java.io.File
import java.io.FileNotFoundException
import java.io.PrintStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Default Logger of the application
 *
 * Created by Gem Seeker on 7/18/2018.
 */
class Logger(logToFile: Boolean = false) {

    init {
        if (logToFile) {
            setupLogFile()
        }
    }

    private fun setupLogFile() {
        val cal = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy_h-m-s")
        val filename = dateFormat.format(cal.time).toLowerCase() + ".txt"
        val file = File("log/$filename")
        if (!file.exists()) {
            var created = file.mkdirs();
            if (created) {
                try {
                    System.setOut(PrintStream(file))
                    System.setErr(PrintStream(file))
                } catch (ex: FileNotFoundException) {
                    println("Failed to create log file!")
                    ex.printStackTrace()
                }
            }
        }
    }

    fun log(debugName: String, message: String) {
        println("$debugName [VERBOSE]: $message")
    }

    fun logErr(debugName: String, message: String) {
        System.err?.println("$debugName [ERROR]: $message")
    }

    fun logErr(debugName: String, message: String, ex: Exception) {
        System.err?.println("$debugName [ERROR]: $message <${ex.message}>")
    }
}