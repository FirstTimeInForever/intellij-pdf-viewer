package com.firsttimeinforever.intellij.pdf.viewer.actions.old.pdfjs

import com.firsttimeinforever.intellij.pdf.viewer.PdfViewerBundle
import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfAction
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnActionEvent

abstract class PdfPdfjsAction(
  disabledInIdePresentationMode: Boolean = true,
  val disabledInPresentationMode: Boolean = false
) : PdfAction() {
//     override fun haveVisibleEditor(event: AnActionEvent): Boolean {
//         return false
// //        return haveVisibleEditor(event) {
// //            it is PdfFileEditor && it.viewPanel is PdfFileEditorJcefPanel
// //        }
//     }

//     fun getPanel(event: AnActionEvent): PdfFileEditorJcefPanel? {
//         val editor = findPdfFileEditor(event) ?: return null
//         return null
// //        return when (editor.viewPanel) {
// //            is PdfFileEditorJcefPanel -> editor.viewPanel
// //            else -> {
// //                showUnsupportedActionNotification(event)
// //                null
// //            }
// //        }
//     }

  companion object {
    fun showUnsupportedActionNotification(event: AnActionEvent) {
      Notifications.Bus.notify(
        Notification(
          PdfViewerBundle.message("pdf.viewer.notifications.group.id"),
          PdfViewerBundle.message("pdf.viewer.actions.pdfjs.notifications.usupported.action.title"),
          PdfViewerBundle.message(
            "pdf.viewer.actions.pdfjs.notifications.unsupported.action.content",
            event.presentation.text
          ),
          NotificationType.ERROR
        )
      )
    }
  }
}
