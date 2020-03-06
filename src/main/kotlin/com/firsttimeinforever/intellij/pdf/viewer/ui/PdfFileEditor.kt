package com.firsttimeinforever.intellij.pdf.viewer.ui

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorLocation
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.util.Key
import java.beans.PropertyChangeListener
import com.intellij.openapi.diagnostic.Logger
import javax.swing.JComponent

class PdfFileEditor: FileEditor {
    companion object {
        private val NAME = "Pdf Viewer File Editor"
        private val logger = Logger.getInstance(this::class.java)
    }

    private val viewPanel = PdfViewPanel()

    override fun isModified(): Boolean {
        return false
    }

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {
        logger.debug("Call to addPropertyChangeListener")
    }

    override fun getName(): String {
        return NAME
    }

    override fun setState(state: FileEditorState) {
//        TODO("Not yet implemented")
    }

    override fun getComponent(): JComponent {
        return viewPanel.component
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return viewPanel.component
    }

    override fun <T : Any?> getUserData(key: Key<T>): T? {
//        TODO("Not yet implemented")
        return null
    }

    override fun <T : Any?> putUserData(key: Key<T>, value: T?) {
//        TODO("Not yet implemented")
    }

    override fun getCurrentLocation(): FileEditorLocation? {
        return null
    }

    override fun isValid(): Boolean {
        return true
    }

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {
//        TODO("Not yet implemented")
        logger.debug("Call to removePropertyChangeListener")
    }

    override fun dispose() {
//        if (viewPanel == null) {
//            return
//        }
//        Disposer.dispose(viewPanel)
    }
}
