package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel

import com.intellij.openapi.Disposable
import com.intellij.openapi.vfs.VirtualFile
import java.awt.BorderLayout
import javax.swing.JPanel

abstract class PdfFileEditorPanel: JPanel(), Disposable {
    init {
        layout = BorderLayout()
    }

    abstract fun openDocument(file: VirtualFile)

    abstract fun reloadDocument()
    abstract fun toggleSidebar()
    abstract fun increaseScale()
    abstract fun decreaseScale()
    abstract fun printDocument()
    abstract fun nextPage()
    abstract fun previousPage()
    abstract fun findNext()
    abstract fun findPrevious()

    abstract fun getCurrentPageNumber(): Int
    abstract fun setCurrentPageNumber(page: Int)
}
