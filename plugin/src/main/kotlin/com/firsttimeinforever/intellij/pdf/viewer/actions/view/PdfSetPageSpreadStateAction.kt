package com.firsttimeinforever.intellij.pdf.viewer.actions.view

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfAction
import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfToggleAction
import com.firsttimeinforever.intellij.pdf.viewer.actions.ViewModeAwareness
import com.firsttimeinforever.intellij.pdf.viewer.model.PageSpreadState
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware

sealed class PdfSetPageSpreadStateAction(private val targetState: PageSpreadState) : PdfToggleAction(ViewModeAwareness.BOTH), DumbAware {
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

  class Even : PdfSetPageSpreadStateAction(PageSpreadState.EVEN)

  class None : PdfSetPageSpreadStateAction(PageSpreadState.NONE)

  class Odd : PdfSetPageSpreadStateAction(PageSpreadState.ODD)
}
