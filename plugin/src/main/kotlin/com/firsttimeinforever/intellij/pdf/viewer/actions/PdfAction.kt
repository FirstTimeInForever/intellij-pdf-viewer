package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.firsttimeinforever.intellij.pdf.viewer.PdfViewerBundle
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditor
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.view.PdfJcefPreviewController
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager

abstract class PdfAction(val disableInIdePresentationMode: Boolean = true) : AnAction() {
  override fun update(event: AnActionEvent) {
    val controller = findController(event)
    event.presentation.isEnabledAndVisible = controller != null
    // if (UISettings.instance.presentationMode && disableInIdePresentationMode) {
    //     event.presentation.isEnabledAndVisible = false
    // }
  }

  companion object {
    fun findEditor(event: AnActionEvent): PdfFileEditor? {
      val project = event.project ?: return null
      val editor = FileEditorManager.getInstance(project).selectedEditor
      // val editor: Any = event.getData(CommonDataKeys.EDITOR) ?: return null
      // println(editor)
      return editor.takeIf { it is PdfFileEditor } as PdfFileEditor?
    }

    fun haveAnyVisibleEditors(event: AnActionEvent): Boolean {
      val project = event.project ?: return false
      return FileEditorManager.getInstance(project).selectedEditors.any { it is PdfFileEditor }
    }

    fun findController(event: AnActionEvent): PdfJcefPreviewController? {
      return findEditor(event)?.viewComponent?.controller
    }

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
