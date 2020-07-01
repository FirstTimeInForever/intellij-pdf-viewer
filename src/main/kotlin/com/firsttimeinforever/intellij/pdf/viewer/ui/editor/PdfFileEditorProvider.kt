package com.firsttimeinforever.intellij.pdf.viewer.ui.editor

import com.firsttimeinforever.intellij.pdf.viewer.lang.PdfFileType
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.WeighedFileEditorProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class PdfFileEditorProvider: WeighedFileEditorProvider() {
    companion object {
        private const val EDITOR_TYPE_ID = "pdf-viewer-preview-editor"
    }

    override fun getEditorTypeId() = EDITOR_TYPE_ID

    override fun accept(project: Project, file: VirtualFile): Boolean {
        return file.fileType == PdfFileType.INSTANCE
    }

    override fun createEditor(project: Project, file: VirtualFile) = PdfFileEditor(project, file)

    override fun getPolicy() = FileEditorPolicy.HIDE_DEFAULT_EDITOR
}
