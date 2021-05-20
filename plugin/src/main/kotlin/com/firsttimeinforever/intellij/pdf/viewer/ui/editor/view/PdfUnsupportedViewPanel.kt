package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.view

import com.firsttimeinforever.intellij.pdf.viewer.utility.PdfResourceLoader
import com.intellij.openapi.project.DumbAware
import com.intellij.ui.components.JBScrollPane
import java.awt.Desktop
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.JTextPane
import javax.swing.event.HyperlinkEvent

class PdfUnsupportedViewPanel : JPanel(), DumbAware {
  private val textPane = JTextPane()
  private val scrollPane = JBScrollPane(textPane)

  init {
    with(textPane) {
      isEditable = false
      contentType = "text/html"
      text = content
      addHyperlinkListener {
        if (it.eventType == HyperlinkEvent.EventType.ACTIVATED) {
          Desktop.getDesktop().browse(it.url.toURI())
        }
      }
      border = BorderFactory.createEmptyBorder(20, 20, 20, 20)
    }
    add(scrollPane)
  }

  companion object {
    private val content by lazy {
      PdfResourceLoader.loadString<PdfUnsupportedViewPanel>("stubPanelContent.html")
    }
  }
}
