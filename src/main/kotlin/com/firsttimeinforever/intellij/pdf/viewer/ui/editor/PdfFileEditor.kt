package com.firsttimeinforever.intellij.pdf.viewer.ui.editor

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.PdfEditorPanelProvider
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.PdfFileEditorPanel
import com.intellij.diff.util.FileEditorBase
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.fileEditor.FileEditorStateLevel
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.JComponent

class PdfFileEditor(private val project: Project, private val virtualFile: VirtualFile) : FileEditorBase(), DumbAware {
    val viewPanel: PdfFileEditorPanel<*> = PdfEditorPanelProvider.createPanel(project, virtualFile)

    init {
        Disposer.register(this, viewPanel)
        viewPanel.addPageChangeListener {
            notifyPageChanged(it)
        }
        notifyPageChanged(pageState)
    }

    private fun notifyPageChanged(pageState: DocumentPageState) {
        project.messageBus.syncPublisher(DocumentPageStateListener.DOCUMENT_PAGE_STATE).run {
            pageStateChanged(pageState)
        }
    }

    val pageState
        get() = DocumentPageState(viewPanel.currentPageNumber, viewPanel.properties.pagesCount)

    override fun getName(): String = NAME

    override fun setState(state: FileEditorState) {
        if (state !is PdfFileEditorState) {
            return
        }
        viewPanel.currentPageNumber = state.page
    }

    override fun getState(level: FileEditorStateLevel): FileEditorState {
        return PdfFileEditorState(viewPanel.currentPageNumber)
    }

    override fun getComponent(): JComponent = viewPanel

    override fun getPreferredFocusedComponent(): JComponent = viewPanel

    override fun getFile() = virtualFile

    companion object {
        private const val NAME = "Pdf Viewer File Editor"
    }
}
