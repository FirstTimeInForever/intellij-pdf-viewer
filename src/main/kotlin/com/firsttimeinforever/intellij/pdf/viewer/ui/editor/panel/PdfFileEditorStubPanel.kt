package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.ResourceLoader
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBScrollPane
import java.awt.Desktop
import java.nio.charset.Charset
import java.nio.file.Paths
import javax.swing.BorderFactory
import javax.swing.JTextPane
import javax.swing.event.HyperlinkEvent

class PdfFileEditorStubPanel(virtualFile: VirtualFile): PdfFileEditorPanel<Any>(virtualFile) {
    private val textPane = JTextPane()
    private val scrollPane = JBScrollPane(textPane)

    init {
        textPane.isEditable = false
        textPane.contentType = "text/html"
        textPane.text = content
        textPane.addHyperlinkListener {
            if (it.eventType == HyperlinkEvent.EventType.ACTIVATED) {
                Desktop.getDesktop().browse(it.url.toURI())
            }
        }
        textPane.border = BorderFactory.createEmptyBorder(20, 20, 20, 20)
        scrollPane.setViewportView(textPane)
        add(scrollPane)
    }

    companion object {
        private val content = ResourceLoader.load(
            Paths.get("messages", "stubPanelContent.html").toFile()
        ).toString(Charset.defaultCharset())
    }
}
