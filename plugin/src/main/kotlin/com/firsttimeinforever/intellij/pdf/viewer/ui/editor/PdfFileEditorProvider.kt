package com.firsttimeinforever.intellij.pdf.viewer.ui.editor

import com.firsttimeinforever.intellij.pdf.viewer.lang.PdfFileType
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.fileEditor.AsyncFileEditorProvider
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class PdfFileEditorProvider : AsyncFileEditorProvider, DumbAware {
  override fun getEditorTypeId() = "PDF"

  override fun accept(project: Project, file: VirtualFile): Boolean {
    logger.debug("check accept, file: $file")
    return file.fileType == PdfFileType;
  }

  override fun createEditor(project: Project, file: VirtualFile): FileEditor {
    return createEditorAsync(project, file).build()
  }

  override fun getPolicy() = FileEditorPolicy.HIDE_DEFAULT_EDITOR

  override fun createEditorAsync(project: Project, file: VirtualFile): AsyncFileEditorProvider.Builder {
    return object: AsyncFileEditorProvider.Builder() {
      override fun build(): FileEditor = PdfFileEditor(project, file)
    }
  }

  companion object {
    private val logger = logger<PdfFileEditorProvider>()
  }
}
