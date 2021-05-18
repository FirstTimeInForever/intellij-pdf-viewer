package com.firsttimeinforever.intellij.pdf.viewer.tex

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import nl.hannahsten.texifyidea.file.BibtexFileType
import nl.hannahsten.texifyidea.file.ClassFileType
import nl.hannahsten.texifyidea.file.LatexFileType
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Experimental
object TexUtils {
  fun findTexifyEditor(fileEditor: FileEditor): TextEditor? {
    if (fileEditor !is TextEditor) {
      return null
    }
    val fileType = fileEditor.file?.fileType
    return fileEditor.takeIf { fileType is LatexFileType || fileType is ClassFileType || fileType is BibtexFileType }
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
