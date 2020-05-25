package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditor
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.PdfFileEditorJcefPanel
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnActionEvent

abstract class PdfEditorPdfjsAction: PdfEditorAction() {
    override fun haveVisibleEditor(event: AnActionEvent): Boolean {
        return haveVisibleEditor(event) {
            it is PdfFileEditor && it.viewPanel is PdfFileEditorJcefPanel
        }
    }

    fun getPanel(event: AnActionEvent): PdfFileEditorJcefPanel? {
        val editor = getEditor(event)?: return null
        return when (editor.viewPanel) {
            is PdfFileEditorJcefPanel -> editor.viewPanel
            else -> {
                showUnsupportedActionNotification(event)
                null
            }
        }
    }

    companion object {
        fun showUnsupportedActionNotification(event: AnActionEvent) {
            Notifications.Bus.notify(
                Notification(
                    "IntelliJ PDF Viewer",
                    "Usupported action",
                    "${event.presentation.text} action is not supported for this type of view!",
                    NotificationType.ERROR
                )
            )
        }
    }
}
