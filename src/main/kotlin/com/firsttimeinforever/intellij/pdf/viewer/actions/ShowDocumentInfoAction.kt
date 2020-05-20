package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditor
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.PdfFileEditorJcefPanel
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager

class ShowDocumentInfoAction: AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val manager = FileEditorManager.getInstance(event.project?: return)
        manager?.selectedEditor?.let { editor ->
            if (editor !is PdfFileEditor) {
                return
            }
            when (editor.viewPanel) {
                is PdfFileEditorJcefPanel -> editor.viewPanel.getDocumentInfo()
                else ->
                    Notifications.Bus.notify(
                        Notification(
                            "IntelliJ PDF Viewer",
                            "Usupported action",
                            "Document info action is not supported for this type of view",
                            NotificationType.ERROR
                        )
                    )
            }
        }
    }
}
