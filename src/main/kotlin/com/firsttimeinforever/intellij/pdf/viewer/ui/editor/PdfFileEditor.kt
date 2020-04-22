package com.firsttimeinforever.intellij.pdf.viewer.ui.editor

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorLocation
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.*
import java.beans.PropertyChangeListener
import javax.swing.JComponent


class PdfFileEditor(private val virtualFile: VirtualFile): FileEditor {
    companion object {
        private const val NAME = "Pdf Viewer File Editor"
    }

    private val viewPanelController: PdfEditorPanelController =
        PdfEditorPanelContollerProvider.INSTANCE.createController()

    init {
        Disposer.register(this, viewPanelController)
        viewPanelController.openDocument(virtualFile)
    }

    fun reloadDocument() {
        viewPanelController.reloadDocument()
    }

    override fun isModified(): Boolean {
        return false
    }

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {}

    override fun getName(): String {
        return NAME
    }

    override fun setState(state: FileEditorState) {}

    override fun getComponent(): JComponent {
        return viewPanelController.getComponent()
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return viewPanelController.getComponent()
    }

    override fun <T : Any?> getUserData(key: Key<T>): T? {
        return null
    }

    override fun <T : Any?> putUserData(key: Key<T>, value: T?) {}

    override fun getCurrentLocation(): FileEditorLocation? {
        return null
    }

    override fun isValid(): Boolean {
        return true
    }

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {}

    override fun dispose() {
    }
}
