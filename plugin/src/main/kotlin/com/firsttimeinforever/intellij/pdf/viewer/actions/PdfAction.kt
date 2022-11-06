package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.firsttimeinforever.intellij.pdf.viewer.PdfViewerBundle
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditor
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.view.PdfJcefPreviewController
import com.intellij.ide.ui.UISettings
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.fileEditor.FileEditorManager

abstract class PdfAction(protected val viewModeAwareness: ViewModeAwareness = ViewModeAwareness.IDE) : AnAction() {
  override fun update(event: AnActionEvent) {
    val editor = findAnyEditor(event)
    with(event.presentation) {
      isVisible = editor != null
      isEnabled = findController(editor) != null
    }
    adjustPresentationVisibility(event)
  }

  protected fun adjustPresentationVisibility(event: AnActionEvent) {
    val controller = findController(event) ?: return
    val idePresentation = UISettings.getInstance().presentationMode
    val documentPresentation = controller.presentationController.isPresentationModeActive
    event.presentation.isEnabledAndVisible = when (viewModeAwareness) {
      ViewModeAwareness.NONE -> !idePresentation && !documentPresentation
      ViewModeAwareness.IDE -> !documentPresentation
      ViewModeAwareness.IDE_ONLY -> idePresentation && !documentPresentation
      ViewModeAwareness.DOCUMENT -> !idePresentation
      ViewModeAwareness.DOCUMENT_ONLY -> !idePresentation && documentPresentation
      ViewModeAwareness.BOTH -> true
      ViewModeAwareness.BOTH_ONLY -> idePresentation && documentPresentation
    }
  }

  companion object {
    fun findEditor(event: AnActionEvent): PdfFileEditor? {
      return event.getData(PlatformDataKeys.FILE_EDITOR) as? PdfFileEditor
    }

    fun findAnyEditor(event: AnActionEvent): PdfFileEditor? {
      val project = event.project ?: return null
      val editorManager = FileEditorManager.getInstance(project)
      return editorManager.allEditors.firstOrNull { it is PdfFileEditor } as? PdfFileEditor
    }

    fun hasOpenedEditor(event: AnActionEvent): Boolean {
      return findAnyEditor(event) != null
    }

    fun findController(event: AnActionEvent): PdfJcefPreviewController? {
      return findAnyEditor(event)?.viewComponent?.controller
    }

    fun findController(editor: PdfFileEditor?): PdfJcefPreviewController? {
      return editor?.viewComponent?.controller
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
