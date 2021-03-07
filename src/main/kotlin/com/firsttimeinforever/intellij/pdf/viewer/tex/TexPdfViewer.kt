package com.firsttimeinforever.intellij.pdf.viewer.tex

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditor
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.objects.SynctexCoordinateTransformation
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.objects.SynctexCoordinateTransformation.toPdf
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.objects.SynctexFowardDataObject
import com.firsttimeinforever.intellij.pdf.viewer.util.runCommand
import com.intellij.ide.actions.OpenInRightSplitAction
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import nl.hannahsten.texifyidea.run.pdfviewer.ExternalPdfViewer
import java.io.File

/**
 * PDF viewer for TeXiFy IDEA.
 */
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

            invokeLater {
                val jcefEditor = if (fileEditorManager.isFileOpen(file)) {
                    val editor = fileEditorManager.getSelectedEditor(file)
                    pdfEditor.navigate(true)
                    editor as PdfFileEditor
                } else {
                    val editorWindow = OpenInRightSplitAction.openInRightSplit(project, file, pdfEditor)
                    editorWindow?.selectedEditor?.selectedWithProvider?.fileEditor as PdfFileEditor
                }

                val command = arrayOf("synctex", "view", "-i", "$line:0:${texFile.name}", "-o", file.name)
                val synctexOutput = runCommand(*command, directory = File(file.parent.path)) ?: return@invokeLater
                val values: Map<String?, String?> = NUMBER_REGEX.findAll(synctexOutput)
                    .associate { it.groups["id"]?.value to it.groups["value"]?.value }
                    .filter { it.key != null && it.value != null }

                jcefEditor.viewPanel.setForwardSearchData(
                    SynctexFowardDataObject(
                        values["Page"]?.toInt() ?: 1,
                        toPdf(values["h"]?.toDouble() ?: 0.0),
                        toPdf(values["v"]?.toDouble() ?: 0.0),
                        toPdf(values["W"]?.toDouble() ?: 0.0),
                        toPdf(values["H"]?.toDouble() ?: 0.0),
                    )
                )
            }
        }
    }

    override fun toString(): String {
        return displayName
    }

    companion object {
        val NUMBER_REGEX = "(?<id>\\w+):(?<value>(\\d+)(.\\d+)?)".toRegex()
    }
}