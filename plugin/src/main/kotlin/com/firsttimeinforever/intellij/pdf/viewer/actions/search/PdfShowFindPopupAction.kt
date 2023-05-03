package com.firsttimeinforever.intellij.pdf.viewer.actions.search

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfDumbAwareAction
import com.intellij.openapi.actionSystem.AnActionEvent

class PdfShowFindPopupAction: PdfDumbAwareAction() {
  override fun actionPerformed(event: AnActionEvent) {
    val searchPanel = findEditorInView(event)?.viewComponent?.searchPanel ?: return
    if (!searchPanel.isVisible) {
      searchPanel.setEnabledState(true)
    }
  }

  override fun update(event: AnActionEvent) {
    super.update(event)
    // TODO: Refactor
    val searchPanel = findEditorInView(event)?.viewComponent?.searchPanel ?: return
    event.presentation.isEnabled = !searchPanel.isVisible
  }
}
