package com.firsttimeinforever.intellij.pdf.viewer.actions.pdfjs

import com.intellij.openapi.actionSystem.AnActionEvent

internal class PdfEditorPdfjsActionBaseImpl(
    override val disabledInPresentationMode: Boolean = false,
    override val disableInIdePresentationMode: Boolean = true
): PdfEditorPdfjsAction() {
    override fun actionPerformed(event: AnActionEvent) = Unit
}
