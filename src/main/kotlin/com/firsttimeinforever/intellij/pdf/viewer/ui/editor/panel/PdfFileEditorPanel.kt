package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel

import com.intellij.openapi.Disposable
import com.intellij.openapi.vfs.VirtualFile
import java.awt.BorderLayout
import javax.swing.JPanel

abstract class PdfFileEditorPanel: JPanel(), Disposable {
    init {
        layout = BorderLayout()
    }

    open fun openDocument(file: VirtualFile) = Unit

    open fun reloadDocument() = Unit
    open fun increaseScale() = Unit
    open fun decreaseScale() = Unit
    open fun nextPage() = Unit
    open fun previousPage() = Unit
    open fun findNext() = Unit
    open fun findPrevious() = Unit

    open var currentPageNumber: Int = 0
    open val pagesCount: Int = 0
}
