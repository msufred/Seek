package gem.seek.controls

import gem.seek.ApplicationContext
import gem.seek.SeekApplication
import gem.seek.util.TOAST_ANIM_DURATION
import gem.seek.util.TOAST_SIZE
import javafx.animation.Interpolator
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.property.SimpleDoubleProperty
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.util.Duration
import java.util.*

/**
 * Toast is sort of a popup UI used to display short notifications in the application. But instead of it popping up in
 * the application's window, it slides up from the bottom of the application's window.
 *
 * <p>Toast can be set as auto closeable. This allows the Toast message to close immediately after the given duration.
 * [Toast.DURATION_SHORT] or [Toast.DURATION_LONG] can be used to set Toast auto close duration.
 *
 * <p>If another Toast is set to show, previous Toast will be removed immediately allowing the next Toast message to
 * be displayed.
 *
 * <p>Toast can't be instantiated directly (i.e., `val toast = Toast()`). Use the static function [make] to create
 * toast instance.
 *
 * Created by Gem Seeker on 4/21/2017.
 */
class Toast private constructor(
        private val applicationContext: ApplicationContext?,
        private val message: String,
        private val autoClose: Boolean,
        private val duration: Double) {

    companion object {
        const val DURATION_SHORT = 1000.0   // 1 second duration
        const val DURATION_LONG = 3000.0    // 3 seconds duration

        var instance: Toast? = null

        /**
         * Static function used to create Toast instance.
         */
        fun make(applicationContext: ApplicationContext?, message: String, autoClose: Boolean, duration: Double): Toast {
            if (instance == null) {
                instance = Toast(applicationContext, message, autoClose, duration)
            }
            return instance!!
        }
    }

    private var contentView = HBox()
    private val closeButton = Button("x")
    private val messageLabel = Label()
    // todo warning image

    private val rootWidthProperty = SimpleDoubleProperty()
    private val rootHeightProperty = SimpleDoubleProperty()
    private var isAnimatingShow = false
    private var isShown = false
    private var closeTask: AutoCloseTask? = null

    init {
        closeButton.apply {
            // todo add close image
            styleClass.add("toast_close_button")
            maxWidth = 16.0
            maxHeight = 16.0
            onAction = EventHandler {
                if (!autoClose) close()
            }
        }

        messageLabel.apply {
            styleClass.add("toast_message")
            text = message
            isWrapText = true
            AnchorPane.setLeftAnchor(this, 0.0)
            AnchorPane.setRightAnchor(this, 0.0)
            AnchorPane.setTopAnchor(this, 0.0)
            AnchorPane.setBottomAnchor(this, 0.0)
            prefHeight = rootHeightProperty.get()
            prefWidth = rootWidthProperty.value - closeButton.width.minus(16)
        }

        val messagePane = AnchorPane()
        messagePane.apply {
            HBox.setHgrow(this, Priority.ALWAYS)
            children.add(messageLabel)
        }

        contentView.apply {
            styleClass.add("toast_root")
            minHeight = TOAST_SIZE
            maxHeight = TOAST_SIZE
            padding = Insets(8.0)
            spacing = 8.0
            children.add(messagePane)
            if (!autoClose) children.add(closeButton)
        }

        rootWidthProperty.bind(contentView.widthProperty())
        rootHeightProperty.bind(contentView.heightProperty())
    }

    fun show() {
        if (applicationContext is SeekApplication) {
            // CHECKPOINTS
            // check if current Toast in application is this Toast instance
            applicationContext.currentToast?.let {
                if (it === this) {
                    // If current Toast is this instance
                    // ignore if it is already shown, or if it is still animating
                    if (it.isShown || it.isAnimatingShow) {
                        return
                    }
                }

                // if current Toast is not this Toast instance, remove current toast
                applicationContext.container.children.remove(it.contentView)
            }

            // at this point, it is assumed that the current toast is either removed or null
            // reposition Toast content view and set as current, add it to the application
            contentView.translateY = TOAST_SIZE
            applicationContext.currentToast = this
            applicationContext.container.children.add(contentView)

            // start show animation
            isAnimatingShow = true
            val anim = Timeline(KeyFrame(
                    Duration(TOAST_ANIM_DURATION),
                    EventHandler {
                        // if set as auto closeable, close Toast after the given duration
                        if (autoClose) {
                            // create Timer, set as daemon, this will terminate task immediately after it is done
                            val timer = Timer(true)

                            // create close task if null
                            if (closeTask == null) closeTask = AutoCloseTask(this)

                            // schedule task for a given duration
                            timer.schedule(closeTask, duration.toLong())
                        } else {
                            isShown = true
                            isAnimatingShow = false
                        }
                    },
                    KeyValue(contentView.translateYProperty(), 0, Interpolator.EASE_BOTH)))
            anim.play()
        }
    }

    private fun close() {
        if (!autoClose) {
            // if close() is called via event...
            isAnimatingShow = true
        }

        // start close animation
        val anim = Timeline(KeyFrame(
                Duration(400.0),
                EventHandler {
                    if (applicationContext is SeekApplication) {
                        applicationContext.container.children.remove(contentView)
                        isShown = false
                        isAnimatingShow = false

                        // if Toast autoClose is true, cancel/terminate close task
                        if (autoClose && closeTask != null) {
                            closeTask?.cancel()
                            closeTask = null
                        }
                    }
                },
                KeyValue(contentView.translateYProperty(), TOAST_SIZE, Interpolator.EASE_BOTH)))
        anim.play()
    }

    private class AutoCloseTask(val toast: Toast): TimerTask() {
        override fun run() {
            toast.close()
        }
    }
}