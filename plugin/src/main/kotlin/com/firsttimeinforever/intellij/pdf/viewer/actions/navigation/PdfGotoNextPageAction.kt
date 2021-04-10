package com.firsttimeinforever.intellij.pdf.viewer.actions.navigation

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfAction
import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.PageGotoDirection
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware

class PdfGotoNextPageAction : PdfAction(disableInIdePresentationMode = false), DumbAware {
  override fun actionPerformed(event: AnActionEvent) {
    findController(event)?.goToPage(PageGotoDirection.FORWARD)
  }

  override fun update(event: AnActionEvent) {
    val controller = findController(event)
    event.presentation.isEnabled = when {
      controller == null || controller.viewProperties.pagesCount == 0 -> false
      else -> controller.viewState.page < controller.viewProperties.pagesCount
    }
  }
}
