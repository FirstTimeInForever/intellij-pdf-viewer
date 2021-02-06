package com.firsttimeinforever.intellij.pdf.viewer.tex

import com.intellij.ide.actions.OpenInRightSplitAction
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import nl.hannahsten.texifyidea.run.pdfviewer.ExternalPdfViewer

class TexPdfViewer : ExternalPdfViewer {
    override val displayName: String = "Built-in PDF Viewer"

    override val name: String = displayName.toUpperCase().replace(" ", "-")

    override fun isAvailable(): Boolean = true

    override fun forwardSearch(
        pdfPath: String?,
        sourceFilePath: String,
        line: Int,
        project: Project,
        focusAllowed: Boolean
    ) {
        pdfPath ?: return
        val file = LocalFileSystem.getInstance().refreshAndFindFileByPath(pdfPath) ?: return
        val pdfEditor = OpenFileDescriptor(project, file)
        ApplicationManager.getApplication()
            .invokeLater { OpenInRightSplitAction.openInRightSplit(project, file, pdfEditor) }
    }

    override fun toString(): String {
        return displayName
    }
}