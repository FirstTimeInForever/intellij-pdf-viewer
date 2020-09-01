package com.firsttimeinforever.intellij.pdf.viewer.actions.pdfjs

import com.firsttimeinforever.intellij.pdf.viewer.PDFViewerBundle
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.PageSpreadState
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent

class ToggleScrollDirectionAction: PdfEditorPdfjsAction(
    disabledInPresentationMode = true
) {
    override fun update(event: AnActionEvent) {
        super.update(event)
        val panel = getPanel(event)?: return
        if (panel.isCurrentScrollDirectionHorizontal) {
            event.presentation.text = HORIZONTAL_TEXT
            event.presentation.description = HORIZONTAL_DESCRIPTION
            event.presentation.icon = AllIcons.Actions.SplitVertically
        }
        else {
            event.presentation.text = VERTICAL_TEXT
            event.presentation.description = VERTICAL_DESCRIPTION
            event.presentation.icon = AllIcons.Actions.SplitHorizontally
        }
    }

    override fun actionPerformed(event: AnActionEvent) {
        getPanel(event)?.run {
            // This is needed to prevent weird behaviour
            pageSpreadState = PageSpreadState.NONE
            toggleScrollDirection()
        }
    }

    private companion object {
        val VERTICAL_TEXT = PDFViewerBundle.message("pdf.viewer.actions.pdfjs.setverticalscrolling")
        val HORIZONTAL_TEXT = PDFViewerBundle.message("pdf.viewer.actions.pdfjs.sethorizontalscrolling")
        val VERTICAL_DESCRIPTION = PDFViewerBundle.message("pdf.viewer.actions.pdfjs.setsverticalscrollingfordocumentpages")
        val HORIZONTAL_DESCRIPTION = PDFViewerBundle.message("pdf.viewer.actions.pdfjs.setshorizontalscrollingfordocumentpages")
    }
}
