package com.firsttimeinforever.intellij.pdf.viewer.actions.view

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfAction
import com.intellij.openapi.actionSystem.AnActionEvent

class PdfDecreaseScaleAction: PdfAction(disableInIdePresentationMode = false) {
    override fun actionPerformed(event: AnActionEvent) {
//        findPdfFileEditor(event)?.viewPanel?.decreaseScale()
    }
}
