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

    private val viewPanel: PdfFileEditorPanel =
        PdfEditorPanelProvider.INSTANCE.createPanel()

    init {
        Disposer.register(this, viewPanel)
        viewPanel.openDocument(virtualFile)
    }

    fun reloadDocument() {
        viewPanel.reloadDocument()
    }

    override fun isModified(): Boolean {
        return false
    }

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {}

    override fun getName(): String {
        return NAME
    }

    override fun setState(state: FileEditorState) {
        if (state !is PdfFileEditorState) {
            return
        }
        viewPanel.setCurrentPageNumber(state.page)
    }

    override fun getState(level: FileEditorStateLevel): FileEditorState {
        return PdfFileEditorState(viewPanel.getCurrentPageNumber())
    }

    override fun getComponent(): JComponent = viewPanel

    override fun getPreferredFocusedComponent(): JComponent? = viewPanel

    override fun <T : Any?> getUserData(key: Key<T>): T? = null

    override fun <T : Any?> putUserData(key: Key<T>, value: T?) {}

    override fun getCurrentLocation(): FileEditorLocation? = null

    override fun isValid(): Boolean = true

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {}

    override fun dispose() {
    }
}
