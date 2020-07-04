package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.DocumentPageState
import com.intellij.openapi.Disposable
import com.intellij.openapi.vfs.VirtualFile
import java.awt.BorderLayout
import javax.swing.JPanel

abstract class PdfFileEditorPanel(
    val virtualFile: VirtualFile
): JPanel(), Disposable {
    init {
        layout = BorderLayout()
    }

    open fun reloadDocument() = Unit
    open fun increaseScale() = Unit
    open fun decreaseScale() = Unit
    open fun nextPage() = Unit
    open fun previousPage() = Unit
    open fun findNext() = Unit
    open fun findPrevious() = Unit

    open var currentPageNumber: Int = 0
    open val pagesCount: Int = 0

    protected val pageStateChangeListeners =
        mutableListOf<(DocumentPageState) -> Unit>()

    fun addPageChangeListener(listener: (DocumentPageState) -> Unit) {
        pageStateChangeListeners.add(listener)
    }

    fun removePageChangeListener(listener: (DocumentPageState) -> Unit): Boolean {
        return pageStateChangeListeners.remove(listener)
    }

    fun pageStateChanged() {
        pageStateChangeListeners.forEach {
            it(DocumentPageState(currentPageNumber, pagesCount))
        }
    }
}
