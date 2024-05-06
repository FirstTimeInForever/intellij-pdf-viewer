package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup

class PdfSidebarViewModeActionGroup : DefaultActionGroup() {
  init {
    templatePresentation.isPopupGroup = true
  }

  override fun update(event: AnActionEvent) {
    event.presentation.isVisible = PdfAction.hasEditorInView(event)
    event.presentation.isEnabled = PdfAction.findController(event) != null
  }

  override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

}
