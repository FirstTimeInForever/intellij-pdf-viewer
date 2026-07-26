package com.firsttimeinforever.intellij.pdf.viewer.frontend.actions.view

import com.firsttimeinforever.intellij.pdf.viewer.frontend.actions.PdfToggleAction
import com.firsttimeinforever.intellij.pdf.viewer.frontend.actions.ViewModeAwareness
import com.firsttimeinforever.intellij.pdf.viewer.common.settings.PdfViewerSettings
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware

class PdfToggleInvertDocumentColorsAction : PdfToggleAction(ViewModeAwareness.BOTH), DumbAware {

  override fun isSelected(event: AnActionEvent) = PdfViewerSettings.instance.invertDocumentColors

  override fun setSelected(event: AnActionEvent, state: Boolean) {
    PdfViewerSettings.instance.invertDocumentColors = state
    PdfViewerSettings.instance.notifyListeners()
  }

  override fun update(event: AnActionEvent) {
    super.update(event)
    event.presentation.isEnabledAndVisible = !PdfViewerSettings.instance.invertColorsWithTheme
  }
}
