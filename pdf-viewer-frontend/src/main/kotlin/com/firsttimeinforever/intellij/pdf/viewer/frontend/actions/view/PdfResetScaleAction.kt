package com.firsttimeinforever.intellij.pdf.viewer.frontend.actions.view

import com.firsttimeinforever.intellij.pdf.viewer.frontend.actions.PdfDumbAwareAction
import com.firsttimeinforever.intellij.pdf.viewer.frontend.actions.ViewModeAwareness
import com.intellij.openapi.actionSystem.AnActionEvent

class PdfResetScaleAction : PdfDumbAwareAction(ViewModeAwareness.BOTH) {
  override fun actionPerformed(event: AnActionEvent) {
    findController(event)?.setZoomMode("auto")
  }
}
