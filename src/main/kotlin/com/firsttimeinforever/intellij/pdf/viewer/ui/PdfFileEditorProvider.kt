package com.firsttimeinforever.intellij.pdf.viewer.ui

import com.intellij.ide.scratch.ScratchUtil
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.WeighedFileEditorProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class PdfFileEditorProvider: WeighedFileEditorProvider() {
    companion object {
        private const val EDITOR_TYPE_ID = "pdf-viewer-preview-editor"
    }

    override fun getEditorTypeId(): String {
        return EDITOR_TYPE_ID
    }

    override fun accept(project: Project, file: VirtualFile): Boolean {
        println(file.fileType)
        return ScratchUtil.isScratch(file)
    }

    override fun createEditor(project: Project, file: VirtualFile): FileEditor {
        return PdfFileEditor()
    }

    override fun getPolicy(): FileEditorPolicy {
        return FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR
    }
}
