package com.firsttimeinforever.intellij.pdf.viewer.actions.view

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfAction
import com.intellij.openapi.actionSystem.AnActionEvent

class PdfIncreaseScaleAction : PdfAction() {
  override fun actionPerformed(event: AnActionEvent) {
    findController(event)?.steppedChangeScale(increase = true)
  }
}
