package com.firsttimeinforever.intellij.pdf.viewer.tex

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditor
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.PdfFileEditorJcefPanel
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.TriggerableEventType
import com.firsttimeinforever.intellij.pdf.viewer.util.isSynctexFileAvailable
import com.intellij.ide.actions.OpenInRightSplitAction
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorActivityManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import nl.hannahsten.texifyidea.run.pdfviewer.ExternalPdfViewer
import nl.hannahsten.texifyidea.util.currentTextEditor

class TexPdfViewer : ExternalPdfViewer {

    /**
     * Remember the last compiled/viewed pdf file so we can forward search to it later.
     */
    private var pdfFilePath: String? = null

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
        if (pdfPath != null) pdfFilePath = pdfPath
        if (pdfFilePath != null) {
            val file = LocalFileSystem.getInstance().refreshAndFindFileByPath(pdfFilePath!!) ?: return
            val pdfEditor = OpenFileDescriptor(project, file)
            ApplicationManager.getApplication()
                .invokeLater { OpenInRightSplitAction.openInRightSplit(project, file, pdfEditor) }
            (FileEditorManager.getInstance(project).allEditors.filterIsInstance<PdfFileEditor>().first().viewPanel as PdfFileEditorJcefPanel)
                .eventSender.triggerWith(TriggerableEventType.FORWARD_SEARCH, "")
        }
    }

    override fun toString(): String {
        return displayName
    }
}