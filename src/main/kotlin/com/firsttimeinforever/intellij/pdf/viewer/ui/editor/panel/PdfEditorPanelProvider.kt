package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.PdfFileEditorJcefPanel
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.jcef.JBCefApp

object PdfEditorPanelProvider {
    private val logger = logger<PdfEditorPanelProvider>()

    fun createPanel(virtualFile: VirtualFile): PdfFileEditorPanel {
        return if (!JBCefApp.isSupported()) {
            logger.warn("JCEF is not supported in running IDE")
            PdfFileEditorStubPanel(virtualFile)
        }
        else {
            PdfFileEditorJcefPanel(virtualFile)
        }
    }
}
