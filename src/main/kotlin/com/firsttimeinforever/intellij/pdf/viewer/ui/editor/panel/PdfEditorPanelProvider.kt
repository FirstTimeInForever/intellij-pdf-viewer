package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.PdfFileEditorJcefPanel
import com.intellij.ui.jcef.JBCefApp


object PdfEditorPanelProvider {
    fun createPanel(): PdfFileEditorPanel {
        if (!JBCefApp.isSupported()) {
            return PdfFileEditorStubPanel()
        }
        return PdfFileEditorJcefPanel()
    }
}
