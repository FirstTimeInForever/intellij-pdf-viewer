package com.firsttimeinforever.intellij.pdf.viewer.ui.editor

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorLocation
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.util.Key
import java.beans.PropertyChangeListener
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefLoadHandler
import org.cef.handler.CefLoadHandlerAdapter
import org.cef.network.CefRequest
import javax.swing.JComponent

class PdfFileEditor(private val virtualFile: VirtualFile): FileEditor {
    companion object {
        private const val NAME = "Pdf Viewer File Editor"
    }

    private val viewPanel: JCEFPanel? = JCEFPanelProvider.get()

    init {
        if (viewPanel == null) {
            throw RuntimeException("viewPannel was null")
        }
        with(viewPanel) {
            loadURL(StaticServer.getInstance()?.getFileUrl("/index.html").toString())
            jbCefClient.addLoadHandler(object: CefLoadHandlerAdapter() {
                override fun onLoadEnd(browser: CefBrowser?, frame: CefFrame?, httpStatusCode: Int) {
                    println("Page loaded")
                    val targetFileUrl = StaticServer.getInstance()?.getFileUrl("/get-file/${virtualFile.path}")
                    cefBrowser.executeJavaScript(wrapPdfLoadCall(targetFileUrl.toString()), null, 0)
                }
            }, cefBrowser)
        }
    }

    private fun wrapPdfLoadCall(content: String): String {
        return "loadPdfDocument(\"$content\")";
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
