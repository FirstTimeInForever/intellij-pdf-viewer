package com.firsttimeinforever.intellij.pdf.viewer.actions.view

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfDumbAwareAction
import com.intellij.openapi.actionSystem.AnActionEvent

class PdfIncreaseScaleAction : PdfDumbAwareAction() {
  override fun actionPerformed(event: AnActionEvent) {
    findController(event)?.steppedChangeScale(increase = true)
  }
}
