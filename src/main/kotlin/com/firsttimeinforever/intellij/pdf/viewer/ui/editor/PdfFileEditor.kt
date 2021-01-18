package com.firsttimeinforever.intellij.pdf.viewer.ui.editor

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.PdfEditorPanelProvider
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.PdfFileEditorPanel
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorLocation
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.fileEditor.FileEditorStateLevel
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import java.beans.PropertyChangeListener
import javax.swing.JComponent

class PdfFileEditor(private val project: Project, virtualFile: VirtualFile) : FileEditor {
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

    override fun isModified(): Boolean = false

    override fun addPropertyChangeListener(listener: PropertyChangeListener) = Unit

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

    override fun <T : Any?> getUserData(key: Key<T>): T? = null

    override fun <T : Any?> putUserData(key: Key<T>, value: T?) = Unit

    override fun getCurrentLocation(): FileEditorLocation? = null

    override fun isValid(): Boolean = true

    override fun removePropertyChangeListener(listener: PropertyChangeListener) = Unit

    override fun dispose() = Unit

    companion object {
        private const val NAME = "Pdf Viewer File Editor"
    }
}
