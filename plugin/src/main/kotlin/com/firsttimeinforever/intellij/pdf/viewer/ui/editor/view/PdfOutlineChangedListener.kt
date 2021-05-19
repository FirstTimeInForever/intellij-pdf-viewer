package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.view

import com.firsttimeinforever.intellij.pdf.viewer.model.PdfOutlineNode
import com.intellij.util.messages.Topic

fun interface PdfOutlineChangedListener {
  fun outlineChanged(outline: PdfOutlineNode)

  companion object {
    val TOPIC = Topic.create("PdfOutlineChangedListener", PdfOutlineChangedListener::class.java)
  }
}
