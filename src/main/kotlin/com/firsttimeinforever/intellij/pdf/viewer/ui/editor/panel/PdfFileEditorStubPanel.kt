package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBScrollPane
import java.awt.Desktop
import javax.swing.JTextPane
import javax.swing.event.HyperlinkEvent


class PdfFileEditorStubPanel: PdfFileEditorPanel() {
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
                This plugin uses new IntelliJ platform implementation of web browser called JCEF (Java Chromium Embedded Framework). This feature is still in development. There is high chance that you need to switch your JBR.
                <br><br>
                <a href='https://youtrack.jetbrains.com/issue/IDEA-231833#focus=streamItem-27-3993099.0-0'>Youtrack issue</a><br>
                <a href='https://www.jetbrains.com/help/idea/switching-boot-jdk.html'>How to switch IDE runtime</a>
            </p>
            </html>
        """.trimIndent()
        textPane.addHyperlinkListener {
            if (it.eventType == HyperlinkEvent.EventType.ACTIVATED) {
                Desktop.getDesktop().browse(it.url.toURI())
            }
        }
        scrollPane.setViewportView(textPane)
        add(scrollPane)
    }

    override fun openDocument(file: VirtualFile) = Unit
    override fun reloadDocument() = Unit
    override fun toggleSidebar()  = Unit
    override fun increaseScale() = Unit
    override fun decreaseScale() = Unit
    override fun findNext() = Unit
    override fun findPrevious() = Unit
    override fun printDocument() = Unit
    override fun nextPage() = Unit
    override fun previousPage() = Unit

    override fun getCurrentPageNumber(): Int = 0

    override fun setCurrentPageNumber(page: Int) = Unit

    override fun dispose() = Unit
}
