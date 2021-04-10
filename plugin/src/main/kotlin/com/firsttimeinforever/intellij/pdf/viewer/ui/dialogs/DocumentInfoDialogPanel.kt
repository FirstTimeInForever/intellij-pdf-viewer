package com.firsttimeinforever.intellij.pdf.viewer.ui.dialogs

import com.firsttimeinforever.intellij.pdf.viewer.PdfViewerBundle
import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.DocumentInfo
import java.awt.BorderLayout
import javax.swing.*
import javax.swing.border.EmptyBorder

internal class DocumentInfoDialogPanel(info: DocumentInfo) : JPanel() {
  init {
    layout = BoxLayout(this, BoxLayout.Y_AXIS)
    with(info) {
      add(EntryPanel(PdfViewerBundle.message("pdf.viewer.document.info.filename"), fileName))
      add(EntryPanel(PdfViewerBundle.message("pdf.viewer.document.info.file.size"), fileSize))
      add(JSeparator(SwingConstants.HORIZONTAL))
      add(EntryPanel(PdfViewerBundle.message("pdf.viewer.document.info.title"), title))
      add(EntryPanel(PdfViewerBundle.message("pdf.viewer.document.info.subject"), subject))
      add(EntryPanel(PdfViewerBundle.message("pdf.viewer.document.info.author"), author))
      add(EntryPanel(PdfViewerBundle.message("pdf.viewer.document.info.creator"), creator))
      add(EntryPanel(PdfViewerBundle.message("pdf.viewer.document.info.creation.date"), creationDate))
      add(
        EntryPanel(
          PdfViewerBundle.message("pdf.viewer.document.info.modification.date"),
          modificationDate
        )
      )
      add(EntryPanel(PdfViewerBundle.message("pdf.viewer.document.info.producer"), producer))
      add(EntryPanel(PdfViewerBundle.message("pdf.viewer.document.info.version"), version))
      add(JSeparator(SwingConstants.HORIZONTAL))
      add(EntryPanel(PdfViewerBundle.message("pdf.viewer.document.info.page.size"), pageSize))
      add(EntryPanel(PdfViewerBundle.message("pdf.viewer.document.info.linearized"), linearized))
    }
  }

  private class EntryPanel(label: String, value: String) : JPanel(BorderLayout()) {
    init {
      border = EmptyBorder(5, 10, 5, 10)
      add(JLabel("$label:  "), BorderLayout.WEST)
      add(
        when {
          value == "-" || value.isEmpty() -> JLabel("unspecified")
          else -> JLabel(value)
        }, BorderLayout.EAST
      )
    }
  }
}
