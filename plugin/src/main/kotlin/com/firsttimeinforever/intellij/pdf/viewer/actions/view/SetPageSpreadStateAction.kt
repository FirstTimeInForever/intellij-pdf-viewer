package com.firsttimeinforever.intellij.pdf.viewer.actions.view

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfAction
import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfToggleAction
import com.firsttimeinforever.intellij.pdf.viewer.model.PageSpreadState
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware

abstract class SetPageSpreadStateAction(private val targetState: PageSpreadState) : PdfToggleAction(), DumbAware {
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
