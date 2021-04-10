package com.firsttimeinforever.intellij.pdf.viewer.ui.editor

import com.firsttimeinforever.intellij.pdf.viewer.lang.PdfFileType
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class PdfFileEditorProvider : FileEditorProvider, DumbAware {
  override fun getEditorTypeId() = "PDF"

  override fun accept(project: Project, file: VirtualFile): Boolean {
    return file.fileType == PdfFileType
  }

  override fun createEditor(project: Project, file: VirtualFile) = PdfFileEditor(project, file)

  override fun getPolicy() = FileEditorPolicy.HIDE_DEFAULT_EDITOR
}
