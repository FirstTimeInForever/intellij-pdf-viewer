package com.firsttimeinforever.intellij.pdf.viewer.ui.editor

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.PdfEditorPanelProvider
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.PdfFileEditorPanel
import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.*
import java.beans.PropertyChangeListener
import javax.swing.JComponent

class PdfFileEditor(virtualFile: VirtualFile): FileEditor {
    companion object {
        private const val NAME = "Pdf Viewer File Editor"
    }

    val viewPanel: PdfFileEditorPanel = PdfEditorPanelProvider.createPanel()

    init {
        Disposer.register(this, viewPanel)
        viewPanel.openDocument(virtualFile)
    }

    fun reloadDocument() = viewPanel.reloadDocument()
    fun increaseScale() = viewPanel.increaseScale()
    fun decreaseScale() = viewPanel.decreaseScale()
    fun nextPage() = viewPanel.nextPage()
    fun previousPage() = viewPanel.previousPage()
    fun findNext() = viewPanel.findNext()
    fun findPrevious() = viewPanel.findPrevious()

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

    override fun getPreferredFocusedComponent(): JComponent? = viewPanel

    override fun <T : Any?> getUserData(key: Key<T>): T? = null

    override fun <T : Any?> putUserData(key: Key<T>, value: T?) = Unit

    override fun getCurrentLocation(): FileEditorLocation? = null

    override fun isValid(): Boolean = true

    override fun removePropertyChangeListener(listener: PropertyChangeListener) = Unit

    override fun dispose() = Unit
}
