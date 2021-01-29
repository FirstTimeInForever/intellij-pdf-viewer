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
    fun syncEditor(pdfFile: VirtualFile, project: Project) {
        val editorPath = PathManager.getBinPath()
        val editorName = ApplicationNamesInfo.getInstance().scriptName
        val pdfDir = File(pdfFile.parent.path)

        val command = arrayOf(
            "synctex", "edit", "-o", "$page:$x:$y:${pdfFile.nameWithoutExtension}.tex",
        )
        val synctexOutput = runCommand(*command, directory = pdfDir) ?: return
        println(synctexOutput)
        val (texPath, line) = parseSynctexEditOutput(synctexOutput) ?: return
        val texFile = LocalFileSystem.getInstance().findFileByPath(texPath) ?: return
        val fileEditorManager = FileEditorManager.getInstance(project)
//        val editors = EditorTracker.getInstance(project).activeEditors
//        val editor = editors.firstOrNull { it.document.isWritable } ?: return // TODO do something if only pdf is open
//        FileEditorManager.getInstance(project).openFiles.firstOrNull { it.extension != "pdf" }
        val openFileDescriptor = OpenFileDescriptor(project, texFile, line - 1, 0)

        runInEdt {
            if (fileEditorManager.isFileOpen(texFile)) {
                val editor = fileEditorManager.getSelectedEditor(texFile) as TextEditor
                openFileDescriptor.navigateIn(editor.editor)
                openFileDescriptor.navigate(true)
            }

            else {
                val editor =
                    FileEditorManager.getInstance(project).allEditors.filterIsInstance<PsiAwareTextEditorImpl>()
                        .firstOrNull() as? TextEditor

                if (editor != null) {
                    openFileDescriptor.navigateIn(editor.editor)
//                    FileEditorManager.getInstance(project).openEditor(openFileDescriptor, true)
                } else {
                    val currentEditor =
                        FileEditorManager.getInstance(project).openFile(texFile, true).first() as TextEditor
                    currentEditor.editor.caretModel.primaryCaret.moveToLogicalPosition(LogicalPosition(line - 1, 0))
                }
//            EditSourceUtil.navigate(openFileDescriptor, true, false)

//            val editor = FileEditorManager.getInstance(project).openFile(texFile, true).firstOrNull() as TextEditor
//            editor.caretModel.primaryCaret.moveToLogicalPosition(LogicalPosition(line - 1, 0))
            }
        }
        println("$texPath: $line")
    }
}