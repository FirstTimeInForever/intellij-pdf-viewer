package com.firsttimeinforever.intellij.pdf.viewer.actions.view

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfAction
import com.firsttimeinforever.intellij.pdf.viewer.actions.old.pdfjs.PdfToggleActionAdapter
import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.SidebarViewMode
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.view.PdfJcefPreviewController
import com.intellij.openapi.actionSystem.AnActionEvent

abstract class PdfSetSidebarViewModeAction(
  private val targetViewMode: SidebarViewMode
) : PdfToggleActionAdapter(
  isDisabledInPresentationMode = true,
  isDisabledInIdePresentationMode = false
) {
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
    val controller = PdfAction.findController(event)
    event.presentation.isVisible = controller != null
    event.presentation.isEnabled = canBeEnabled(controller)
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
