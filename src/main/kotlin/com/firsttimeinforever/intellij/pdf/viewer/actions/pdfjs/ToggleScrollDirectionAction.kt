package com.firsttimeinforever.intellij.pdf.viewer.actions.pdfjs

import com.firsttimeinforever.intellij.pdf.viewer.PdfViewerBundle
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
        val VERTICAL_TEXT = PdfViewerBundle.message("pdf.viewer.actions.pdfjs.set.vertical.scrolling.name")
        val VERTICAL_DESCRIPTION = PdfViewerBundle.message("pdf.viewer.actions.pdfjs.set.vertical.scrolling.description")
        val HORIZONTAL_TEXT = PdfViewerBundle.message("pdf.viewer.actions.pdfjs.set.horizontal.scrolling.name")
        val HORIZONTAL_DESCRIPTION = PdfViewerBundle.message("pdf.viewer.actions.pdfjs.set.horizontal.scrolling.description")
    }
}
