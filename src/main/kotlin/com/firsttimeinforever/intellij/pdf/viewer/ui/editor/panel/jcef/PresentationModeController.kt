package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.MessageEventReceiver
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.MessageEventSender
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.SubscribableEventType
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.TriggerableEventType
import java.awt.MouseInfo
import java.awt.Robot
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.FocusManager
import javax.swing.JComponent
import javax.swing.SwingUtilities

class PresentationModeController(
    private val panel: PdfFileEditorJcefPanel,
    private val browserComponent: JComponent,
    private val eventReceiver: MessageEventReceiver,
    private val eventSender: MessageEventSender
) {
    private val enterListeners = mutableListOf<PresentationModeListener>()
    private val exitListeners = mutableListOf<PresentationModeListener>()

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
        with (eventReceiver) {
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

    private fun invokeListeners(listeners: MutableList<PresentationModeListener>) {
        val shouldBeRemoved = mutableListOf<PresentationModeListener>()
        for (listener in listeners) {
            if (listener.presentationModeChanged(this)) {
                shouldBeRemoved.add(listener)
            }
        }
        listeners.removeAll(shouldBeRemoved)
    }

    fun addEnterListener(listener: PresentationModeListener) = enterListeners.add(listener)

    fun addExitListener(listener: PresentationModeListener) = exitListeners.add(listener)

    private fun clickInBrowserWindow() {
        val originalPosition = MouseInfo.getPointerInfo().location
        val originalFocusOwner = FocusManager.getCurrentManager().focusOwner
        val location = browserComponent.locationOnScreen
        val xcenter = browserComponent.width / 2
        val ycenter = browserComponent.height / 2
        with(Robot()) {
            mouseMove(location.x + xcenter, location.y + ycenter)
            mousePress(InputEvent.BUTTON1_DOWN_MASK)
            mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
            mouseMove(originalPosition.x, originalPosition.y)
        }
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
