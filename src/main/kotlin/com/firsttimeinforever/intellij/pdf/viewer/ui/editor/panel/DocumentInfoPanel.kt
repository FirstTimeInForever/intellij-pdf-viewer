package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel

import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.SwingConstants

class DocumentInfoPanel(documentInfo: DocumentInfoDataObject) : JPanel() {
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        add(InfoEntry("File Name", documentInfo.fileName))
        add(InfoEntry("File Size", documentInfo.fileSize))
        add(JSeparator(SwingConstants.HORIZONTAL))
        add(InfoEntry("Title", documentInfo.title))
        add(InfoEntry("Subject", documentInfo.subject))
        add(InfoEntry("Author", documentInfo.author))
        add(InfoEntry("Creator", documentInfo.creator))
        add(InfoEntry("Creation Date", documentInfo.creationDate))
        add(InfoEntry("Modification Date", documentInfo.modificationDate))
        add(InfoEntry("Producer", documentInfo.producer))
        add(InfoEntry("Version", documentInfo.version))
        add(JSeparator(SwingConstants.HORIZONTAL))
        add(InfoEntry("Page Size", documentInfo.pageSize))
        add(InfoEntry("Linearized", documentInfo.linearized))
    }
}
