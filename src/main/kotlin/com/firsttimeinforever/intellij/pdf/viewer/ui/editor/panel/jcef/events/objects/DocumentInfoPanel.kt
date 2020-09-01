package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef

import com.firsttimeinforever.intellij.pdf.viewer.PDFViewerBundle
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.objects.DocumentInfoDataObject
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.SwingConstants

class DocumentInfoPanel(documentInfo: DocumentInfoDataObject) : JPanel() {
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        add(InfoEntryPanel(PDFViewerBundle.message("pdf.viewer.ui.editor.panel.jcef.documentpanel.filename"), documentInfo.fileName))
        add(InfoEntryPanel(PDFViewerBundle.message("pdf.viewer.ui.editor.panel.jcef.documentpanel.filesize"), documentInfo.fileSize))
        add(JSeparator(SwingConstants.HORIZONTAL))
        add(InfoEntryPanel(PDFViewerBundle.message("pdf.viewer.ui.editor.panel.jcef.documentpanel.title"), documentInfo.title))
        add(InfoEntryPanel(PDFViewerBundle.message("pdf.viewer.ui.editor.panel.jcef.documentpanel.subject"), documentInfo.subject))
        add(InfoEntryPanel(PDFViewerBundle.message("pdf.viewer.ui.editor.panel.jcef.documentpanel.author"), documentInfo.author))
        add(InfoEntryPanel(PDFViewerBundle.message("pdf.viewer.ui.editor.panel.jcef.documentpanel.creator"), documentInfo.creator))
        add(InfoEntryPanel(PDFViewerBundle.message("pdf.viewer.ui.editor.panel.jcef.documentpanel.creationdate"), documentInfo.creationDate))
        add(InfoEntryPanel(PDFViewerBundle.message("pdf.viewer.ui.editor.panel.jcef.documentpanel.modificationdate"), documentInfo.modificationDate))
        add(InfoEntryPanel(PDFViewerBundle.message("pdf.viewer.ui.editor.panel.jcef.documentpanel.producer"), documentInfo.producer))
        add(InfoEntryPanel(PDFViewerBundle.message("pdf.viewer.ui.editor.panel.jcef.documentpanel.version"), documentInfo.version))
        add(JSeparator(SwingConstants.HORIZONTAL))
        add(InfoEntryPanel(PDFViewerBundle.message("pdf.viewer.ui.editor.panel.jcef.documentpanel.pagesize"), documentInfo.pageSize))
        add(InfoEntryPanel(PDFViewerBundle.message("pdf.viewer.ui.editor.panel.jcef.documentpanel.linearized"), documentInfo.linearized))
    }
}
