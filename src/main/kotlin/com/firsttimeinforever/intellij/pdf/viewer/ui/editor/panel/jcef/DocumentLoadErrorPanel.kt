package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef

import javax.swing.BorderFactory
import javax.swing.JTextPane

class DocumentLoadErrorPanel: JTextPane() {
    init {
        contentType = "text/html"
        text = "<html><strong>Could not load PDF document</strong></html>"
        border = BorderFactory.createEmptyBorder(20, 20, 20, 20)
    }
}
