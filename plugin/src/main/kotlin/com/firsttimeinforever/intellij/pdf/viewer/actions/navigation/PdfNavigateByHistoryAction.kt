package com.firsttimeinforever.intellij.pdf.viewer.actions.navigation

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfDumbAwareAction
import com.firsttimeinforever.intellij.pdf.viewer.model.HistoryNavigationDirection
import com.intellij.openapi.actionSystem.AnActionEvent

sealed class PdfNavigateByHistoryAction(private val direction: HistoryNavigationDirection): PdfDumbAwareAction() {
  override fun actionPerformed(event: AnActionEvent) {
    findController(event)?.navigateHistory(direction)
  }

  class Back: PdfNavigateByHistoryAction(HistoryNavigationDirection.BACK)

  class Forward: PdfNavigateByHistoryAction(HistoryNavigationDirection.FORWARD)
}
