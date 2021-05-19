package com.firsttimeinforever.intellij.pdf.viewer.actions.navigation

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfDumbAwareAction
import com.firsttimeinforever.intellij.pdf.viewer.actions.ViewModeAwareness
import com.firsttimeinforever.intellij.pdf.viewer.model.PageGotoDirection
import com.intellij.openapi.actionSystem.AnActionEvent

class PdfGotoNextPageAction : PdfDumbAwareAction(ViewModeAwareness.BOTH) {
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
