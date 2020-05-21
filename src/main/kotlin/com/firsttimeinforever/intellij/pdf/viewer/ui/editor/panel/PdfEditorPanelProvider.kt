package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel

import com.intellij.openapi.util.registry.Registry


object PdfEditorPanelProvider {
    private fun hasJcef(): Boolean {
        try {
            if (Class.forName("org.cef.browser.CefBrowser", true, javaClass.classLoader) != null) {
                return Registry.get("ide.browser.jcef.enabled").asBoolean()
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
