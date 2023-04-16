package com.firsttimeinforever.intellij.pdf.viewer.actions.search

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfDumbAwareAction
import com.firsttimeinforever.intellij.pdf.viewer.model.SearchDirection
import com.intellij.openapi.actionSystem.AnActionEvent

sealed class PdfSearchAction(private val direction: SearchDirection) : PdfDumbAwareAction() {
  override fun actionPerformed(event: AnActionEvent) {
    val editor = findEditorInView(event) ?: return
    val controller = findController(event) ?: return
    val searchQuery = editor.viewComponent.searchPanel.searchQuery
    controller.find(searchQuery, direction)
  }

  override fun update(event: AnActionEvent) {
    super.update(event)
    val searchPanel = findEditorInView(event)?.viewComponent?.searchPanel
    event.presentation.isEnabled = searchPanel?.searchText?.isNotEmpty() == true && searchPanel.isVisible
  }

  class Forward : PdfSearchAction(SearchDirection.FORWARD)

  class Backward : PdfSearchAction(SearchDirection.BACKWARD)
}
