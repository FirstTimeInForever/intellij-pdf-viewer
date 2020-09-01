package com.firsttimeinforever.intellij.pdf.viewer.actions.pdfjs

import com.firsttimeinforever.intellij.pdf.viewer.PDFViewerBundle
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent

class ToggleDocumentPresentationModeAction: PdfEditorPdfjsAction(
    disabledInIdePresentationMode = false
) {
    override fun actionPerformed(event: AnActionEvent) {
        val panel = getPanel(event)?: return
        panel.presentationModeController.togglePresentationMode()
    }

    override fun update(event: AnActionEvent) {
        super.update(event)
        val panel = getPanel(event)?: return
        if (panel.presentationModeController.isPresentationModeActive()) {
            event.presentation.text = ACTIVE_TEXT
            event.presentation.description = ACTIVE_DESCRIPTION
            event.presentation.icon = AllIcons.General.CollapseComponentHover
        }
        else {
            event.presentation.text = NORMAL_TEXT
            event.presentation.description = NORMAL_DESCRIPTION
            event.presentation.icon = AllIcons.General.ExpandComponentHover
        }
    }

    private companion object {
        val NORMAL_TEXT = PDFViewerBundle.message("pdf.viewer.actions.pdfjs.enterpdfpresentationmode")
        val NORMAL_DESCRIPTION = PDFViewerBundle.message("pdf.viewer.actions.pdfjs.entersdocumentpresentationmode")
        val ACTIVE_TEXT = PDFViewerBundle.message("pdf.viewer.actions.pdfjs.exitpdfpresentationmode")
        val ACTIVE_DESCRIPTION = PDFViewerBundle.message("pdf.viewer.actions.pdfjs.exitsdocumentpresentationmode")
    }
}
