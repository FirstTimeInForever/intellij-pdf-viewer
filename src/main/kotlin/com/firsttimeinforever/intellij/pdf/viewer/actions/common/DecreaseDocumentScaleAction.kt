package com.firsttimeinforever.intellij.pdf.viewer.actions.common

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfEditorAction
import com.firsttimeinforever.intellij.pdf.viewer.actions.findPdfFileEditor
import com.intellij.openapi.actionSystem.AnActionEvent

class DecreaseDocumentScaleAction: PdfEditorAction(
    disableInIdePresentationMode = false
) {
    override fun actionPerformed(event: AnActionEvent) {
        findPdfFileEditor(event)?.viewPanel?.decreaseScale()
    }
}
