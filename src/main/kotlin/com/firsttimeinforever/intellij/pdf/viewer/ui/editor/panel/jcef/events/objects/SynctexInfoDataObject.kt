package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.objects

import com.firsttimeinforever.intellij.pdf.viewer.util.parseSynctexEditOutput
import com.firsttimeinforever.intellij.pdf.viewer.util.runCommand
import com.intellij.codeInsight.daemon.impl.EditorTracker
import com.intellij.diff.actions.impl.OpenInEditorAction
import com.intellij.execution.services.OpenInNewTabAction
import com.intellij.ide.actions.OpenInRightSplitAction
import com.intellij.ide.util.EditSourceUtil
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ApplicationNamesInfo
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.fileEditor.impl.OpenEditorInOppositeTabGroupAction
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import kotlinx.serialization.Serializable
import org.jetbrains.annotations.NotNull
import java.io.File

@Serializable
data class SynctexInfoDataObject(
    val page: Int,
    val x: Int,
    val y: Int
) {

    /**
     * Use SyncTeX to open the corresponding tex file at page [page] and the line corresponding to [x].
     *
     * @param pdfFile The virtual file of the pdf file to be opened.
     * @param project The project to which both the tex and the pdf file belong.
     * @param requestFocus True iff the tex file should request focus after opening.
     */
    fun syncEditor(pdfFile: VirtualFile, project: Project, requestFocus: Boolean = true) {
        val pdfDir = File(pdfFile.parent.path)

        val command = arrayOf(
            "synctex", "edit", "-o", "$page:$x:$y:${pdfFile.nameWithoutExtension}.tex",
        )
        val synctexOutput = runCommand(*command, directory = pdfDir) ?: return
        val (texPath, line) = parseSynctexEditOutput(synctexOutput) ?: return
        val texFile = LocalFileSystem.getInstance().findFileByPath(texPath) ?: return
        val fileEditorManager = FileEditorManager.getInstance(project)
        val openFileDescriptor = OpenFileDescriptor(project, texFile, line - 1, 0)

        runInEdt {
            // If the file is already open, navigate to that file and move to the correct line.
            if (fileEditorManager.isFileOpen(texFile)) {
                val editor = fileEditorManager.getSelectedEditor(texFile) as TextEditor
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
        println("$texPath: $line")
    }
}