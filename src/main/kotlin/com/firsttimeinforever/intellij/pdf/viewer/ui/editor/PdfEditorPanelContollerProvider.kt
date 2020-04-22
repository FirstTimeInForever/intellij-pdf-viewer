package com.firsttimeinforever.intellij.pdf.viewer.ui.editor

import com.intellij.openapi.util.registry.Registry


class PdfEditorPanelContollerProvider private constructor() {
    companion object {
        val INSTANCE = PdfEditorPanelContollerProvider()
    }

    private fun hasJcef(): Boolean {
        try {
            if (Class.forName("org.cef.browser.CefBrowser", true, javaClass.classLoader) != null) {
                return Registry.get("ide.browser.jcef.enabled").asBoolean()
            }
        }
        catch (exception: ClassNotFoundException) {}
        return false
    }

    fun createController(): PdfEditorPanelController {
        if (!hasJcef()) {
            return PdfEditorPanelStubController()
        }
        return PdfEditorJcefPanelController()
    }
}
