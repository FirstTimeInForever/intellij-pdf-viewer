package com.firsttimeinforever.intellij.pdf.viewer.actions.view

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfAction
import com.intellij.openapi.actionSystem.AnActionEvent

class PdfReloadViewAction: PdfAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val controller = findController(event)
        controller?.reload(tryToPreserveState = true)
    }

    override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = true
    }

//     override fun update(event: AnActionEvent) {
//         super.update(event)
//         // val editor = findPdfFileEditor(event) ?: return
// //        event.presentation.isEnabled = if (editor.viewPanel is PdfFileEditorJcefPanel) {
// //            !editor.viewPanel.presentationModeController.isPresentationModeActive()
// //        }
// //        else true
//     }
}
