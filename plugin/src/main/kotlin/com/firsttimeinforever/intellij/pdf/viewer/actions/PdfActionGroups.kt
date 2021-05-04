package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup

class PdfActionGroup : DefaultActionGroup()

class PdfLeftToolbarActionGroup : DefaultActionGroup()

class PdfToolbarSearchActionGroup : DefaultActionGroup()

class PdfRightToolbarActionGroup : DefaultActionGroup()

class PdfSidebarViewModeActionGroup : DefaultActionGroup() {
  override fun isPopup(): Boolean = true

  override fun update(event: AnActionEvent) {
    event.presentation.isVisible = PdfAction.hasOpenedEditor(event)
    event.presentation.isEnabled = PdfAction.findController(event) != null
  }
}

class PdfPageSpreadActionGroup : DefaultActionGroup() {
  override fun isPopup(): Boolean = true

  override fun update(event: AnActionEvent) {
    event.presentation.isVisible = PdfAction.hasOpenedEditor(event)
    event.presentation.isEnabled = PdfAction.findController(event) != null
  }
}
