package com.firsttimeinforever.intellij.pdf.viewer.tex

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Experimental
object TexUtils {
  fun findTexifyEditor(fileEditor: FileEditor): TextEditor? {
    if (fileEditor !is TextEditor) {
      return null
    }
    return fileEditor.takeIf { fileEditor.file.extension in listOf("tex", "cls", "bib") }
  }

  fun findTexifyEditor(project: Project, fileEditor: FileEditor? = null): TextEditor? {
    if (fileEditor != null) {
      val editor = findTexifyEditor(fileEditor)
      if (editor != null) {
        return editor
      }
    }
    val editorManager = FileEditorManager.getInstance(project)
    return editorManager.selectedEditors.find { findTexifyEditor(it) != null } as? TextEditor
  }
}
