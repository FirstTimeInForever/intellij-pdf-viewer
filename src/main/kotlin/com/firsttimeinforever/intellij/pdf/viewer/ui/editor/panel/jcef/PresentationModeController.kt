package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.MessageEventReceiver
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.MessageEventSender
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.SubscribableEventType
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.TriggerableEventType
import com.intellij.ui.jcef.JCEFHtmlPanel
import java.awt.MouseInfo
import java.awt.Robot
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.FocusManager
import javax.swing.SwingUtilities

typealias PresentationModeListenerType = (PresentationModeController) -> Boolean

class PresentationModeController(
    private val panel: PdfFileEditorJcefPanel,
    private val browserPanel: JCEFHtmlPanel,
    private val eventReceiver: MessageEventReceiver,
    private val eventSender: MessageEventSender
) {
    private val enterListeners = mutableListOf<PresentationModeListenerType>()
    private val exitListeners = mutableListOf<PresentationModeListenerType>()

    private var presentationModeActive = false

    private val escapeKeyListener = object: KeyListener {
        override fun keyPressed(event: KeyEvent?) {
            when (event?.keyCode) {
                KeyEvent.VK_ESCAPE -> {
                    exit()
                }
            }
        }
        override fun keyTyped(event: KeyEvent?) = Unit
        override fun keyReleased(event: KeyEvent?) = Unit
    }

    init {
        eventReceiver.run {
            addHandler(SubscribableEventType.PRESENTATION_MODE_ENTER_READY) {
                SwingUtilities.invokeLater {
                    clickInBrowserWindow()
                }
            }
            addHandler(SubscribableEventType.PRESENTATION_MODE_ENTER) {
                presentationModeActive = true
                panel.addKeyListener(escapeKeyListener)
                invokeListeners(enterListeners)
            }
            addHandler(SubscribableEventType.PRESENTATION_MODE_EXIT) {
                presentationModeActive = false
                invokeListeners(exitListeners)
            }
        }
    }

    private fun invokeListeners(listeners: MutableList<PresentationModeListenerType>) {
        val shouldBeRemoved = mutableListOf<PresentationModeListenerType>()
        listeners.forEach {
            if (it(this)) {
                shouldBeRemoved.add(it)
            }
        }
        listeners.removeAll(shouldBeRemoved)
    }

    fun addEnterListener(listener: PresentationModeListenerType) = enterListeners.add(listener)

    fun addExitListener(listener: PresentationModeListenerType) = exitListeners.add(listener)

    private fun clickInBrowserWindow() {
        val originalPosition = MouseInfo.getPointerInfo().location
        val originalFocusOwner = FocusManager.getCurrentManager().focusOwner
        val robot = Robot()
        val location = browserPanel.component.locationOnScreen
        val xcenter = browserPanel.component.width / 2
        val ycenter = browserPanel.component.height / 2
        robot.mouseMove(location.x + xcenter, location.y + ycenter)
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseMove(originalPosition.x, originalPosition.y)
        originalFocusOwner?.requestFocus()
    }

    fun exit() {
        if (!presentationModeActive) {
            return
        }
        panel.removeKeyListener(escapeKeyListener)
        eventSender.trigger(TriggerableEventType.TOGGLE_PRESENTATION_MODE)
    }

    fun enter() {
        if (presentationModeActive) {
            return
        }
        eventSender.trigger(TriggerableEventType.TOGGLE_PRESENTATION_MODE)
    }

    fun togglePresentationMode() {
        if (presentationModeActive) {
            exit()
        }
        else {
            enter()
        }
    }

    fun isPresentationModeActive() = presentationModeActive
}
