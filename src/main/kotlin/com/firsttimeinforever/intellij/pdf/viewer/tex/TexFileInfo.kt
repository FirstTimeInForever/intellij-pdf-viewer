package com.firsttimeinforever.intellij.pdf.viewer.tex

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.objects.SynctexInfoDataObject
import com.firsttimeinforever.intellij.pdf.viewer.util.runCommand
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

class TexFileInfo(val file: VirtualFile, private val line: Int, private val column: Int) {

    /**
     * Use SyncTeX to open the corresponding tex [file] at [line] and [column].
     *
     * @param project The project to which both the tex and the pdf file belong.
     * @param requestFocus True iff the tex file should request focus after opening.
     */
    fun syncEditor(project: Project, requestFocus: Boolean = true) {
        val fileEditorManager = FileEditorManager.getInstance(project)
        val openFileDescriptor = OpenFileDescriptor(project, file, line - 1, column - 1)

        runInEdt {
            // If the file is already open, navigate to that file and move to the correct line.
            if (fileEditorManager.isFileOpen(file)) {
                val editor = fileEditorManager.getSelectedEditor(file) as TextEditor
                openFileDescriptor.navigateIn(editor.editor)
                openFileDescriptor.navigate(requestFocus)
            }
            // Otherwise check if there already is some other tex(t)file open and focus this editor before
            // opening the tex file. It is likely that the user has the pdf file open in a split pane, and we
            // don't want to open the tex file in the same pane as the pdf in that case.
            else {
                val editor = fileEditorManager.allEditors.filterIsInstance<PsiAwareTextEditorImpl>()
                    .firstOrNull() as? TextEditor

                if (editor != null) {
                    val currentEditorDescriptor = OpenFileDescriptor(project, editor.file!!)
                    currentEditorDescriptor.navigate(requestFocus)
                }
                fileEditorManager.openEditor(openFileDescriptor, requestFocus)
            }
        }
    }

    companion object {
        private val INPUT_REGEX = "Input:(?<file>[^\\n]+)".toRegex()
        private val LINE_REGEX = "Line:(?<line>\\d+)".toRegex()
        private val COLUMN_REGEX = "Column:(?<col>\\d+)".toRegex()

        /**
         *
         * @param pdfFile The virtual file of the pdf file to find the corresponding tex file of.
         * @param data [SynctexInfoDataObject] that contains information that has to be passed on to SyncTeX.
         */
        fun fromSynctexInfoData(pdfFile: VirtualFile, data: SynctexInfoDataObject): TexFileInfo? {
            val pdfDir = File(pdfFile.parent.path)

            val command = arrayOf(
                "synctex", "edit", "-o", "${data.page}:${data.x}:${data.y}:${pdfFile.name}",
            )
            val synctexOutput = runCommand(*command, directory = pdfDir) ?: return null

            val texPath = INPUT_REGEX.find(synctexOutput)?.groups?.get("file")?.value ?: return null
            val line = LINE_REGEX.find(synctexOutput)?.groups?.get("line")?.value?.toInt() ?: 1
            val column = COLUMN_REGEX.find(synctexOutput)?.groups?.get("col")?.value?.toInt() ?: 1

            val texFile = LocalFileSystem.getInstance().findFileByPath(texPath) ?: return null
            return TexFileInfo(texFile, line, column)
        }
    }
}