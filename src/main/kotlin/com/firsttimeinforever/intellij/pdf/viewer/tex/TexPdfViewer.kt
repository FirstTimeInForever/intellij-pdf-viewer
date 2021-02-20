package com.firsttimeinforever.intellij.pdf.viewer.tex

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditor
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.PdfFileEditorJcefPanel
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.TriggerableEventType
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.objects.SynctexFowardDataObject
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.objects.SynctexInverseDataObject
import com.firsttimeinforever.intellij.pdf.viewer.util.runCommand
import com.intellij.ide.actions.OpenInRightSplitAction
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import nl.hannahsten.texifyidea.run.pdfviewer.ExternalPdfViewer
import java.io.File

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
            val texFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(sourceFilePath) ?: return
            val pdfEditor = OpenFileDescriptor(project, file)
            val fileEditorManager = FileEditorManager.getInstance(project)

            val jcefPanel = if (fileEditorManager.isFileOpen(file)) {
                val editor = fileEditorManager.getSelectedEditor(file)
                pdfEditor.navigate(true)
                (editor as PdfFileEditor).viewPanel as PdfFileEditorJcefPanel
            }
            else {
                val editorWindow = OpenInRightSplitAction.openInRightSplit(project, file, pdfEditor)
                (editorWindow?.selectedEditor?.selectedWithProvider?.fileEditor as PdfFileEditor).viewPanel as PdfFileEditorJcefPanel
            }

            val command = arrayOf("synctex", "view", "-i", "$line:0:${texFile.name}", "-o", file.name)
            val synctexOutput = runCommand(*command, directory = File(file.parent.path)) ?: return
            val values: Map<String, Int> = NUMBER_REGEX.findAll(synctexOutput)
                .associate { it.groups["id"]?.value to it.groups["value"]?.value?.toInt() }
                .filter { it.key != null && it.value != null } as Map<String, Int>

            jcefPanel.eventSender.triggerWith(
                        TriggerableEventType.FORWARD_SEARCH, SynctexFowardDataObject(
                            values.getOrDefault("Page", 1),
                            values.getOrDefault("h", 0),
                            values.getOrDefault("v", 0),
                            values.getOrDefault("W", 0),
                            values.getOrDefault("H", 0)
                        )
                    )
        }
    }

    override fun toString(): String {
        return displayName
    }

    companion object {
        val NUMBER_REGEX = "(?<id>\\w+):(?<value>\\d+)".toRegex()
    }
}