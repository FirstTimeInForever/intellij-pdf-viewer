package com.firsttimeinforever.intellij.pdf.viewer.ui.editor

import com.firsttimeinforever.intellij.pdf.viewer.lang.PdfFileType
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.fileEditor.ex.FileEditorWithProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.rd.ide.model.FileEditorModel
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rdclient.fileEditors.FrontendFileEditorModelHandler

/**
 * Created by liudongmiao on 2024-09-30.
 */
class PdfFrontendFileEditorModelHandler : FrontendFileEditorModelHandler {
  override fun accept(project: Project, file: VirtualFile, model: FileEditorModel): Boolean {
    logger.debug("check accept, file: $file")
    return file.fileType == PdfFileType;
  }

  override fun createEditorWithProvider(project: Project, lifetime: Lifetime, file: VirtualFile, model: FileEditorModel): FileEditorWithProvider {
    val provider = PdfFileEditorProvider()
    return FileEditorWithProvider(provider.createEditor(project, file), provider)
  }

  companion object {
    private val logger = logger<PdfFrontendFileEditorModelHandler>()
  }
}
