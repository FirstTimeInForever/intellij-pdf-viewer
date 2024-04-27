package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.presentation

import com.firsttimeinforever.intellij.pdf.viewer.IdeMessages
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessagePipeSupport.send
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.view.PdfJcefPreviewController
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import java.awt.MouseInfo
import java.awt.Robot
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.FocusManager

class PdfPresentationController(val viewController: PdfJcefPreviewController) {
  private val escapeKeyListener = EscapeKeyListener()
  private val publisher = viewController.project.messageBus.syncPublisher(PdfPresentationModeListener.TOPIC)

  var isPresentationModeActive: Boolean = false
    private set

  fun enter() {
    require(!isPresentationModeActive)
    object: Task.Modal(viewController.project, "Entering Presentation Mode", false) {
      override fun run(indicator: ProgressIndicator) {
        clickInBrowserWindow()
        viewController.component.addKeyListener(escapeKeyListener)
        isPresentationModeActive = true
        publisher.presentationModeChanged(this@PdfPresentationController)
      }
    }.queue()
  }

  fun exit() {
    require(isPresentationModeActive)
    viewController.component.removeKeyListener(escapeKeyListener)
    viewController.pipe.send(IdeMessages.ExitPresentationMode())
    isPresentationModeActive = false
    publisher.presentationModeChanged(this)
  }

  fun togglePresentationMode() {
    when {
      isPresentationModeActive -> exit()
      else -> enter()
    }
  }

  private fun clickInBrowserWindow() {
    val originalPosition = MouseInfo.getPointerInfo().location
    val originalFocusOwner = FocusManager.getCurrentManager().focusOwner
    val browserComponent = viewController.browser.component
    // val location = browserComponent.locationOnScreen
    // val xcenter = browserComponent.width / 2
    val ycenter = browserComponent.height / 2
    viewController.browser.cefBrowser.setFocus(true)
    Robot().apply {
      mouseMove(originalPosition.x, originalPosition.y + ycenter)
      mousePress(InputEvent.BUTTON1_DOWN_MASK)
      mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
      mouseMove(originalPosition.x, originalPosition.y)
      keyPress(KeyEvent.VK_CONTROL)
      keyPress(KeyEvent.VK_ALT)
      keyPress(KeyEvent.VK_P)
      keyRelease(KeyEvent.VK_P)
      keyRelease(KeyEvent.VK_ALT)
      keyRelease(KeyEvent.VK_CONTROL)
    }
    originalFocusOwner?.requestFocus()
  }

  private inner class EscapeKeyListener: KeyListener {
    override fun keyPressed(event: KeyEvent) {
      if (event.keyCode == KeyEvent.VK_ESCAPE) {
        exit()
      }
    }

    override fun keyTyped(event: KeyEvent) = Unit

    override fun keyReleased(event: KeyEvent) = Unit
  }
}
