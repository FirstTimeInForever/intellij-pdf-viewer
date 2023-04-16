package com.firsttimeinforever.intellij.pdf.viewer.actions.view

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfAction
import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfToggleAction
import com.firsttimeinforever.intellij.pdf.viewer.model.SidebarViewMode
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.view.PdfJcefPreviewController
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware

sealed class PdfSetSidebarViewModeAction(private val targetViewMode: SidebarViewMode) : PdfToggleAction(), DumbAware {
  override fun isSelected(event: AnActionEvent): Boolean {
    val viewController = PdfAction.findController(event) ?: return false
    return viewController.viewState.sidebarViewMode == targetViewMode
  }

  override fun setSelected(event: AnActionEvent, state: Boolean) {
    val viewController = PdfAction.findController(event) ?: return
    viewController.setSidebarViewMode(targetViewMode)
  }

  override fun update(event: AnActionEvent) {
    super.update(event)
    event.presentation.isVisible = PdfAction.hasEditorInView(event)
    event.presentation.isEnabled = canBeEnabled(PdfAction.findController(event))
  }

  private fun canBeEnabled(controller: PdfJcefPreviewController?): Boolean {
    return controller != null && targetViewMode in controller.viewProperties.availableSidebarViewModes
  }

  class Hide : PdfSetSidebarViewModeAction(SidebarViewMode.NONE) {
    override fun update(event: AnActionEvent) {
      super.update(event)
      event.presentation.isEnabled = true
    }
  }

  class Thumbnails : PdfSetSidebarViewModeAction(SidebarViewMode.THUMBNAILS)

  class Outline : PdfSetSidebarViewModeAction(SidebarViewMode.OUTLINE)

  class Attachments : PdfSetSidebarViewModeAction(SidebarViewMode.ATTACHMENTS)
}
