package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.DocumentInfoDataObject
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.SwingConstants

class DocumentInfoPanel(documentInfo: DocumentInfoDataObject) : JPanel() {
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        add(InfoEntryPanel("File Name", documentInfo.fileName))
        add(InfoEntryPanel("File Size", documentInfo.fileSize))
        add(JSeparator(SwingConstants.HORIZONTAL))
        add(InfoEntryPanel("Title", documentInfo.title))
        add(InfoEntryPanel("Subject", documentInfo.subject))
        add(InfoEntryPanel("Author", documentInfo.author))
        add(InfoEntryPanel("Creator", documentInfo.creator))
        add(InfoEntryPanel("Creation Date", documentInfo.creationDate))
        add(InfoEntryPanel("Modification Date", documentInfo.modificationDate))
        add(InfoEntryPanel("Producer", documentInfo.producer))
        add(InfoEntryPanel("Version", documentInfo.version))
        add(JSeparator(SwingConstants.HORIZONTAL))
        add(InfoEntryPanel("Page Size", documentInfo.pageSize))
        add(InfoEntryPanel("Linearized", documentInfo.linearized))
    }
}
