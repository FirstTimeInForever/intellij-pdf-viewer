package com.firsttimeinforever.intellij.pdf.viewer.ui.editor

import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.JComponent


class PdfEditorPanelStubController: PdfEditorPanelController() {
    private val viewPanel = PdfEditorStubPanel()

    override fun getComponent(): JComponent {
        return viewPanel
    }

    override fun openDocument(file: VirtualFile) {
    }

    override fun reloadDocument() {
    }

    override fun dispose() {
    }
}
