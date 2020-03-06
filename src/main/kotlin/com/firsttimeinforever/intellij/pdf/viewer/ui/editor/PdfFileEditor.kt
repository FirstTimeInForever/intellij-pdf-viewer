package com.firsttimeinforever.intellij.pdf.viewer.ui.editor

import com.firsttimeinforever.intellij.pdf.viewer.ui.PdfViewPanel
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorLocation
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.util.Key
import java.beans.PropertyChangeListener
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Disposer
import javax.swing.JComponent

class PdfFileEditor: FileEditor {
    companion object {
        private val NAME = "Pdf Viewer File Editor"
        private val logger = Logger.getInstance(this::class.java)
    }

    private val viewPanel: PdfViewPanel? = PdfViewPanel()

    override fun isModified(): Boolean {
        return false
    }

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {}

    override fun getName(): String {
        return NAME
    }

    override fun setState(state: FileEditorState) {}

    override fun getComponent(): JComponent {
        return viewPanel!!.component
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        if (viewPanel == null) {
            return null
        }
        return viewPanel.component
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
        if (viewPanel == null) {
            return
        }
        Disposer.dispose(viewPanel)
    }
}
