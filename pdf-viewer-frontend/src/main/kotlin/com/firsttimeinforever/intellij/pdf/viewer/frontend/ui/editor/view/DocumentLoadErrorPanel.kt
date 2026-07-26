package com.firsttimeinforever.intellij.pdf.viewer.frontend.ui.editor.view

import com.firsttimeinforever.intellij.pdf.viewer.frontend.utility.PdfResourceLoader
import javax.swing.BorderFactory
import javax.swing.JTextPane

class DocumentLoadErrorPanel : JTextPane() {
  init {
    contentType = "text/html"
    text = content
    border = BorderFactory.createEmptyBorder(20, 20, 20, 20)
  }

  companion object {
    private val content = PdfResourceLoader.loadString<DocumentLoadErrorPanel>("documentLoadErrorPanelContent.html")
  }
}
