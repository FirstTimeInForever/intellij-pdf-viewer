package com.firsttimeinforever.intellij.pdf.viewer.utility

import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

internal abstract class DocumentListenerAdapter : DocumentListener {
  override fun insertUpdate(event: DocumentEvent) = Unit
  override fun removeUpdate(event: DocumentEvent) = Unit
  override fun changedUpdate(event: DocumentEvent) = Unit
}
