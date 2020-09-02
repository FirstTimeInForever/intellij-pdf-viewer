package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.objects

import com.firsttimeinforever.intellij.pdf.viewer.PdfViewerBundle
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.InfoEntryPanel
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.SwingConstants

class DocumentInfoPanel(documentInfo: DocumentInfoDataObject) : JPanel() {
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        with (documentInfo) {
            add(InfoEntryPanel(PdfViewerBundle.message("pdf.viewer.document.info.filename"), fileName))
            add(InfoEntryPanel(PdfViewerBundle.message("pdf.viewer.document.info.file.size"), fileSize))
            add(JSeparator(SwingConstants.HORIZONTAL))
            add(InfoEntryPanel(PdfViewerBundle.message("pdf.viewer.document.info.title"), title))
            add(InfoEntryPanel(PdfViewerBundle.message("pdf.viewer.document.info.subject"), subject))
            add(InfoEntryPanel(PdfViewerBundle.message("pdf.viewer.document.info.author"), author))
            add(InfoEntryPanel(PdfViewerBundle.message("pdf.viewer.document.info.creator"), creator))
            add(InfoEntryPanel(PdfViewerBundle.message("pdf.viewer.document.info.creation.date"), creationDate))
            add(
                InfoEntryPanel(
                    PdfViewerBundle.message("pdf.viewer.document.info.modification.date"),
                    modificationDate
                )
            )
            add(InfoEntryPanel(PdfViewerBundle.message("pdf.viewer.document.info.producer"), producer))
            add(InfoEntryPanel(PdfViewerBundle.message("pdf.viewer.document.info.version"), version))
            add(JSeparator(SwingConstants.HORIZONTAL))
            add(InfoEntryPanel(PdfViewerBundle.message("pdf.viewer.document.info.page.size"), pageSize))
            add(InfoEntryPanel(PdfViewerBundle.message("pdf.viewer.document.info.linearized"), linearized))
        }
    }
}
