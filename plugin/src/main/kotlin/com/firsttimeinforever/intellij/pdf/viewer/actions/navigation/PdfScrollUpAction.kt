package com.firsttimeinforever.intellij.pdf.viewer.actions.navigation

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfDumbAwareAction
import com.firsttimeinforever.intellij.pdf.viewer.actions.ViewModeAwareness
import com.firsttimeinforever.intellij.pdf.viewer.settings.PdfViewerSettings
import com.intellij.openapi.actionSystem.AnActionEvent

class PdfScrollUpAction : PdfDumbAwareAction(ViewModeAwareness.BOTH) {
  override fun actionPerformed(event: AnActionEvent) {
    findController(event)?.scrollUp(PdfViewerSettings.instance.scrollPixelsPerStep)
  }

  override fun update(event: AnActionEvent) {
    val controller = findController(event)
    event.presentation.isEnabled = controller != null
  }
}
