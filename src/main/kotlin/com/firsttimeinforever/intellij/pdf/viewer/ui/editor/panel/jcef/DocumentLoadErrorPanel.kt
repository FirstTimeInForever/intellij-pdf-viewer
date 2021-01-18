package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.ResourceLoader
import java.nio.charset.Charset
import java.nio.file.Paths
import javax.swing.BorderFactory
import javax.swing.JTextPane

class DocumentLoadErrorPanel: JTextPane() {
    init {
        contentType = "text/html"
        text = content
        border = BorderFactory.createEmptyBorder(20, 20, 20, 20)
    }

    companion object {
        private val content = ResourceLoader.load(
            Paths.get("messages", "documentLoadErrorPanelContent.html").toFile()
        ).toString(Charset.defaultCharset())
    }
}
