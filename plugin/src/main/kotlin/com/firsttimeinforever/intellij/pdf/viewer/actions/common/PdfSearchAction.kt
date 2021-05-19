package com.firsttimeinforever.intellij.pdf.viewer.actions.common

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfDumbAwareAction
import com.firsttimeinforever.intellij.pdf.viewer.actions.ViewModeAwareness
import com.firsttimeinforever.intellij.pdf.viewer.model.SearchDirection
import com.intellij.openapi.actionSystem.AnActionEvent

open class PdfSearchAction(private val direction: SearchDirection) : PdfDumbAwareAction(ViewModeAwareness.NONE) {
  override fun actionPerformed(event: AnActionEvent) {
    val editor = findEditor(event) ?: return
    val controller = findController(event) ?: return
    val searchText = editor.viewComponent.controlPanel.searchText
    controller.find(searchText, direction)
  }

  override fun update(event: AnActionEvent) {
    super.update(event)
    val searchText = findEditor(event)?.viewComponent?.controlPanel?.searchText
    event.presentation.isEnabled = searchText?.isNotEmpty() == true
  }

  class Forward : PdfSearchAction(SearchDirection.FORWARD)

  class Backward : PdfSearchAction(SearchDirection.BACKWARD)
}
