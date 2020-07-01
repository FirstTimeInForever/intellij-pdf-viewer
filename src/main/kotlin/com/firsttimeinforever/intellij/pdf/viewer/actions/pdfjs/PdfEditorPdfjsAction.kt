package com.firsttimeinforever.intellij.pdf.viewer.actions.pdfjs

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfEditorAction
import com.firsttimeinforever.intellij.pdf.viewer.actions.findPdfFileEditor
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditor
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.PdfFileEditorJcefPanel
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnActionEvent

abstract class PdfEditorPdfjsAction(
    disabledInIdePresentationMode: Boolean = true,
    val disabledInPresentationMode: Boolean = false
): PdfEditorAction(disabledInIdePresentationMode) {
    override fun haveVisibleEditor(event: AnActionEvent): Boolean {
        return haveVisibleEditor(event) {
            it is PdfFileEditor && it.viewPanel is PdfFileEditorJcefPanel
        }
    }

    fun getPanel(event: AnActionEvent): PdfFileEditorJcefPanel? {
        return findPdfFileEditor(event)?.let { editor ->
            when (editor.viewPanel) {
                is PdfFileEditorJcefPanel -> editor.viewPanel
                else -> {
                    showUnsupportedActionNotification(event)
                    null
                }
            }
        }
    }

    override fun update(event: AnActionEvent) {
        super.update(event)
        val panel = getPanel(event)?: return
        event.presentation.isEnabled = !(panel.presentationModeController.isPresentationModeActive() && disabledInPresentationMode)
    }

    companion object {
        fun showUnsupportedActionNotification(event: AnActionEvent) {
            Notifications.Bus.notify(
                Notification(
                    "PDF Viewer",
                    "Usupported action",
                    "${event.presentation.text} action is not supported for this type of view!",
                    NotificationType.ERROR
                )
            )
        }
    }
}
