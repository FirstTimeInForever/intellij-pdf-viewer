package com.firsttimeinforever.intellij.pdf.viewer.ui.editor

import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.jcef.JCEFHtmlPanel
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFileEvent
import com.intellij.openapi.vfs.VirtualFileListener
import javax.swing.JComponent


class PdfEditorJcefPanelController: PdfEditorPanelController(), Disposable {
    private val browserPanel = JCEFHtmlPanel("about:blank")
    private val logger = logger<PdfEditorJcefPanelController>()
    private lateinit var virtualFile: VirtualFile

    init {
        Disposer.register(this, browserPanel)
    }

    override fun getComponent(): JComponent {
        return browserPanel.component
    }

    private fun addUpdateHandler() {
        val fileSystem = LocalFileSystem.getInstance()
        fileSystem.addRootToWatch(virtualFile.path, false)
        fileSystem.addVirtualFileListener(object: VirtualFileListener {
            override fun contentsChanged(event: VirtualFileEvent) {
                logger.debug("Got some events batch")
                if (event.file != virtualFile) {
                    logger.debug("Seems like target file (${virtualFile.path}) is not changed")
                    return
                }
                logger.debug("Target file (${virtualFile.path}) changed. Reloading page!")
                val targetUrl = StaticServer.getInstance()?.getFilePreviewUrl(virtualFile.path)
                browserPanel.loadURL(targetUrl!!.toExternalForm())
            }
        })
    }

    override fun openDocument(targetFile: VirtualFile) {
        virtualFile = targetFile
        addUpdateHandler()
        reloadDocument()
    }

    override fun reloadDocument() {
        val targetUrl = StaticServer.getInstance()?.getFilePreviewUrl(virtualFile.path)
        logger.debug("Tryign to load url: ${targetUrl!!.toExternalForm()}")
        browserPanel.loadURL(targetUrl.toExternalForm())
    }

    override fun dispose() {
    }
}
