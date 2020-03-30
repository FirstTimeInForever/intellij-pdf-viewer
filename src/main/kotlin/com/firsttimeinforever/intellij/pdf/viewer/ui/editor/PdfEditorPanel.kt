package com.firsttimeinforever.intellij.pdf.viewer.ui.editor

import com.intellij.openapi.Disposable
import com.intellij.ui.jcef.JBCefBrowser
import javax.swing.JPanel


class PdfEditorPanel: JPanel(), Disposable {
    val browser = JBCefBrowser()

    init {
        add(browser.component)
    }

    override fun dispose() {
        browser.dispose()
    }
}
