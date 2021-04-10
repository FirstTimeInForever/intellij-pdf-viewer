package com.firsttimeinforever.intellij.pdf.viewer.actions.view

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfAction
import com.firsttimeinforever.intellij.pdf.viewer.actions.old.pdfjs.PdfToggleActionAdapter
import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.SidebarViewMode
import com.intellij.openapi.actionSystem.AnActionEvent

abstract class PdfSetSidebarViewModeActions(
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
    val viewController = PdfAction.findController(event) ?: return
    event.presentation.isEnabledAndVisible = true
    // targetViewMode in viewController.viewProperties.availableSidebarViewModes
  }
}

class PdfHideSidebarAction :
  PdfSetSidebarViewModeActions(SidebarViewMode.NONE)

class PdfSetSidebarThumbnailsViewModeAction :
  PdfSetSidebarViewModeActions(SidebarViewMode.THUMBNAILS)

class PdfPdfSetSidebarBookmarksViewModeAction :
  PdfSetSidebarViewModeActions(SidebarViewMode.BOOKMARKS)

class PdfSetSidebarAttachmentsViewModeAction :
  PdfSetSidebarViewModeActions(SidebarViewMode.ATTACHMENTS)
