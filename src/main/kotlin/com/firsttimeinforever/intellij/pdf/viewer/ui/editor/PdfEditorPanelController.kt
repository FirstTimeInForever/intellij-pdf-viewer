package com.firsttimeinforever.intellij.pdf.viewer.ui.editor

import com.intellij.openapi.Disposable
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.JComponent
import javax.swing.JPanel

abstract class PdfEditorPanelController: Disposable {
    abstract fun getComponent(): JComponent

    abstract fun openDocument(file: VirtualFile)

    abstract fun reloadDocument()
}
