package com.firsttimeinforever.intellij.pdf.viewer.actions.view

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfDumbAwareAction
import com.firsttimeinforever.intellij.pdf.viewer.actions.ViewModeAwareness
import com.intellij.openapi.actionSystem.AnActionEvent

class PdfResetScaleAction : PdfDumbAwareAction(ViewModeAwareness.BOTH) {
  override fun actionPerformed(event: AnActionEvent) {
    findController(event)?.setZoomMode("auto")
  }
}
