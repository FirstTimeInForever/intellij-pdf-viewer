package com.firsttimeinforever.intellij.pdf.viewer.ui.editor

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.view.PdfJcefPreviewController
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

object PdfEditorUtils {
  fun findPdfEditor(project: Project, file: VirtualFile): PdfFileEditor? {
    val editorManager = FileEditorManagerEx.getInstanceEx(project)
    val selectedEditor = editorManager.getSelectedEditor(file) as? PdfFileEditor
    if (selectedEditor != null) {
      return selectedEditor
    }
    return editorManager.getEditors(file).find { it is PdfFileEditor } as? PdfFileEditor
  }

  fun findPdfViewController(project: Project, file: VirtualFile): PdfJcefPreviewController? {
    return findPdfEditor(project, file)?.viewComponent?.controller
  }

  fun findPdfViewController(editor: FileEditor): PdfJcefPreviewController? {
    return (editor as? PdfFileEditor)?.viewComponent?.controller
  }
}
