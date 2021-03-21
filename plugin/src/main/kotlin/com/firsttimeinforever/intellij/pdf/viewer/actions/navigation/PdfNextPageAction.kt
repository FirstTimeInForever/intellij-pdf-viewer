package com.firsttimeinforever.intellij.pdf.viewer.actions.navigation

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfAction
import com.intellij.openapi.actionSystem.AnActionEvent

class PdfNextPageAction: PdfAction(disableInIdePresentationMode = false) {
    override fun actionPerformed(event: AnActionEvent) {
//        findPdfFileEditor(event)?.viewPanel?.nextPage()
    }

//     override fun update(event: AnActionEvent) {
//         super.update(event)
//         // val editor = findPdfFileEditor(event) ?: return
// //        event.presentation.isEnabled = if (editor.viewPanel.properties.pagesCount != 0) {
// //            editor.viewPanel.currentPageNumber != editor.viewPanel.properties.pagesCount
// //        }
// //        else false
//     }
}
