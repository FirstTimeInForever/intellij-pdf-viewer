package com.firsttimeinforever.intellij.pdf.viewer.actions.old.pdfjs

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfToggleAction
import com.intellij.openapi.actionSystem.AnActionEvent

abstract class PdfToggleActionAdapter(
    isDisabledInIdePresentationMode: Boolean = true,
    isDisabledInPresentationMode: Boolean = false
): PdfToggleAction() {
    override val base: PdfPdfjsAction = object: PdfPdfjsAction(
        isDisabledInIdePresentationMode,
        isDisabledInPresentationMode
    ) {
        override fun actionPerformed(event: AnActionEvent) {
            throw IllegalAccessException("This method should not be called")
        }
    }

    val disabledInPresentationMode
        get() = base.disabledInPresentationMode
}
