package com.firsttimeinforever.intellij.pdf.viewer.actions.view

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfToggleAction
import com.firsttimeinforever.intellij.pdf.viewer.actions.ViewModeAwareness
import com.firsttimeinforever.intellij.pdf.viewer.settings.PdfViewerSettings
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.IconLoader

class PdfToggleInvertDocumentColorsAction : PdfToggleAction(ViewModeAwareness.BOTH), DumbAware {

  override fun isSelected(event: AnActionEvent) = PdfViewerSettings.instance.invertDocumentColors

  override fun setSelected(event: AnActionEvent, state: Boolean) {
    PdfViewerSettings.instance.invertDocumentColors = state
    PdfViewerSettings.instance.notifyListeners()
  }

  override fun update(event: AnActionEvent) {
    event.presentation.apply {
      icon = if (PdfViewerSettings.instance.invertDocumentColors) {
        IconLoader.getIcon("expui/meetNewUi/lightTheme.svg", this::class.java.classLoader)
      } else {
        IconLoader.getIcon("expui/meetNewUi/darkTheme.svg", this::class.java.classLoader)
      }
      selectedIcon = icon
    }
    super.update(event)
  }
}
