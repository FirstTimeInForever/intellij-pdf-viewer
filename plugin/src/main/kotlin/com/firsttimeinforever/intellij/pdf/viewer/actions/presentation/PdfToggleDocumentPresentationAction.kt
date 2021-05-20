package com.firsttimeinforever.intellij.pdf.viewer.actions.presentation

import com.firsttimeinforever.intellij.pdf.viewer.PdfViewerActionsBundle
import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfDumbAwareAction
import com.firsttimeinforever.intellij.pdf.viewer.actions.ViewModeAwareness
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent

class PdfToggleDocumentPresentationAction: PdfDumbAwareAction(ViewModeAwareness.BOTH) {
  override fun actionPerformed(event: AnActionEvent) {
    val controller = findController(event) ?: return
    controller.presentationController.togglePresentationMode()
  }

  override fun update(event: AnActionEvent) {
    super.update(event)
    with(event.presentation) {
      when (findController(event)?.presentationController?.isPresentationModeActive) {
        true -> {
          text = PdfViewerActionsBundle.message("pdf.viewer.ToggleDocumentPresentationAction.text.toggled")
          description = PdfViewerActionsBundle.message("pdf.viewer.ToggleDocumentPresentationAction.description.toggled")
          icon = AllIcons.General.CollapseComponentHover
        }
        else -> {
          text = PdfViewerActionsBundle.message("pdf.viewer.ToggleDocumentPresentationAction.text")
          description = PdfViewerActionsBundle.message("pdf.viewer.ToggleDocumentPresentationAction.description")
          icon = AllIcons.General.ExpandComponentHover
        }
      }
    }
  }
}
