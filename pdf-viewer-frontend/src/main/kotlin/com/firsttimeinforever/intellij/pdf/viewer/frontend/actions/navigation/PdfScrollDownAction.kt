package com.firsttimeinforever.intellij.pdf.viewer.frontend.actions.navigation

import com.firsttimeinforever.intellij.pdf.viewer.frontend.actions.PdfDumbAwareAction
import com.firsttimeinforever.intellij.pdf.viewer.frontend.actions.ViewModeAwareness
import com.firsttimeinforever.intellij.pdf.viewer.common.settings.PdfViewerSettings
import com.intellij.openapi.actionSystem.AnActionEvent

class PdfScrollDownAction : PdfDumbAwareAction(ViewModeAwareness.BOTH) {
  override fun actionPerformed(event: AnActionEvent) {
    findController(event)?.scrollDown(PdfViewerSettings.instance.scrollPixelsPerStep)
  }

  override fun update(event: AnActionEvent) {
    val controller = findController(event)
    event.presentation.isEnabled = controller != null
  }
}
