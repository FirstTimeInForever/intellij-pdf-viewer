package com.firsttimeinforever.intellij.pdf.viewer.actions.view

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfAction
import com.firsttimeinforever.intellij.pdf.viewer.actions.old.pdfjs.PdfToggleActionAdapter
import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.PageSpreadState
import com.intellij.openapi.actionSystem.AnActionEvent

abstract class SetPageSpreadStateAction(
  private val targetState: PageSpreadState
) : PdfToggleActionAdapter(
  isDisabledInPresentationMode = true,
  isDisabledInIdePresentationMode = true
) {
  override fun isSelected(event: AnActionEvent): Boolean {
    val controller = PdfAction.findController(event) ?: return false
    return controller.viewState.pageSpreadState == targetState
  }

  override fun setSelected(event: AnActionEvent, state: Boolean) {
    val controller = PdfAction.findController(event) ?: return
    controller.setPageSpreadState(when {
      state -> targetState
      else -> PageSpreadState.NONE
    })
  }

  class Even : SetPageSpreadStateAction(PageSpreadState.EVEN)

  class None : SetPageSpreadStateAction(PageSpreadState.NONE)

  class Odd : SetPageSpreadStateAction(PageSpreadState.ODD)
}
