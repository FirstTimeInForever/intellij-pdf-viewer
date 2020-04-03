package com.firsttimeinforever.intellij.pdf.viewer.ui.editor

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorLocation
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.jcef.JBCefBrowser
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefLoadHandlerAdapter
import java.beans.PropertyChangeListener
import javax.swing.JComponent


class PdfFileEditor(private val virtualFile: VirtualFile) : FileEditor {
    companion object {
        private const val NAME = "Pdf Viewer File Editor"
    }

    private val viewPanel: PdfEditorPanel = PdfEditorPanel()

    init {
        Disposer.register(this, viewPanel)
        openFile()
    }

    private fun openFile() {
        val fileUrl = StaticServer.getInstance()?.getFileUrl("/viewer.html").toString()
        addLoadHandler(viewPanel.browser)
        viewPanel.browser.loadURL(fileUrl)
    }

    private fun addLoadHandler(browser: JBCefBrowser) {
        browser.jbCefClient.addLoadHandler(object: CefLoadHandlerAdapter() {
            override fun onLoadEnd(browser: CefBrowser?, frame: CefFrame?, httpStatusCode: Int) {
                val targetFileUrl = StaticServer.getInstance()?.getFileUrl("/get-file/${virtualFile.path}")
                closeSidebar(browser!!)
                browser.executeJavaScript(wrapPdfLoadCall(targetFileUrl.toString()), null, 0)
            }
        }, browser.cefBrowser)
    }

    private fun wrapPdfLoadCall(content: String): String {
        return "PDFViewerApplication.open(\"$content\")"
    }

    private fun closeSidebar(cefBrowser: CefBrowser) {
        val script = "document.addEventListener(\"pagesloaded\", () => {PDFViewerApplication.pdfSidebar.close()})"
        cefBrowser.executeJavaScript(script, null, 0)
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
        return viewPanel.browser.component
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return viewPanel.browser.component
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
