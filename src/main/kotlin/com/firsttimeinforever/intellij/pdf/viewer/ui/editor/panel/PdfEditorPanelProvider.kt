package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.PdfFileEditorJcefPanel
import com.intellij.openapi.util.registry.Registry
import com.intellij.ui.jcef.JBCefApp


object PdfEditorPanelProvider {
    private fun hasJcef(): Boolean {
        try {
            if (Class.forName("com.intellij.ui.jcef.JBCefApp", false, javaClass.classLoader) != null) {
                return JBCefApp.isSupported() && Registry.get("ide.browser.jcef.enabled").asBoolean()
            }
        }
        catch (exception: ClassNotFoundException) {}
        return false
    }

    fun createPanel(): PdfFileEditorPanel {
        if (!hasJcef()) {
            return PdfFileEditorStubPanel()
        }
        return PdfFileEditorJcefPanel()
    }
}
