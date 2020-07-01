package com.firsttimeinforever.intellij.pdf.viewer.actions.pdfjs

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfEditorToggleActionAdapter
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.PdfFileEditorJcefPanel
import com.intellij.openapi.actionSystem.AnActionEvent

abstract class PdfEditorPdfjsToggleActionAdapter(
    isDisabledInIdePresentationMode: Boolean = true,
    isDisabledInPresentationMode: Boolean = false
): PdfEditorToggleActionAdapter() {
    override val base: PdfEditorPdfjsAction = object: PdfEditorPdfjsAction(
        isDisabledInIdePresentationMode,
        isDisabledInPresentationMode
    ) {
        override fun actionPerformed(event: AnActionEvent) = Unit
    }

    val disabledInPresentationMode
        get() = base.disabledInPresentationMode

    fun getPanel(event: AnActionEvent): PdfFileEditorJcefPanel? =
        base.getPanel(event)

    companion object {
        fun showUnsupportedActionNotification(event: AnActionEvent) =
            PdfEditorPdfjsAction.showUnsupportedActionNotification(event)
    }
}
