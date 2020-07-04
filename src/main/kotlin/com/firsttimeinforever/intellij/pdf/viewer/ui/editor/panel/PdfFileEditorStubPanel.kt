package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBScrollPane
import java.awt.Desktop
import javax.swing.BorderFactory
import javax.swing.JTextPane
import javax.swing.event.HyperlinkEvent

class PdfFileEditorStubPanel(virtualFile: VirtualFile): PdfFileEditorPanel(virtualFile) {
    private val textPane = JTextPane()
    private val scrollPane = JBScrollPane(textPane)

    init {
        textPane.isEditable = false
        textPane.contentType = "text/html"
        textPane.text = """
            <html>
            <h2>JCEF is not available!</h2>
            <p>
                Seems like your current runtime does not provide JCEF classes.<br>
                If you are sure that it does - please ensure <strong>'ide.browser.jcef.enabled'</strong> registry property is set to true.
            </p>
            <br><br>
            <h3>Why am I seeing this?</h3>
            <p>
                This plugin uses new IntelliJ platform implementation of web browser called JCEF (Java Chromium Embedded Framework). This feature is still in development, so there might be some bugs.
            </p>
            <br>
            <h3>Please ensure you are:</h3>
            <ul>
                <li>Running IDE with JetBrains Runtime</li>
                <li>You are using default JBR bundled with IDE</li>
                <li>JCEF is enabled (check ide.browser.jcef.enabled registry flag - it should be enabled)</li>
            </ul>
            </html>
        """.trimIndent()
        textPane.addHyperlinkListener {
            if (it.eventType == HyperlinkEvent.EventType.ACTIVATED) {
                Desktop.getDesktop().browse(it.url.toURI())
            }
        }
        textPane.border = BorderFactory.createEmptyBorder(20, 20, 20, 20)
        scrollPane.setViewportView(textPane)
        add(scrollPane)
    }

    override fun dispose() = Unit
}
