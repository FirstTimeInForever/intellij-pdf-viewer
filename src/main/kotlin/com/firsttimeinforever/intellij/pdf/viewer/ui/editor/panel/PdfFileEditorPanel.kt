package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.DocumentPageState
import com.intellij.openapi.Disposable
import com.intellij.openapi.vfs.VirtualFile
import java.awt.BorderLayout
import javax.swing.JPanel

abstract class PdfFileEditorPanel<PreviewState: Any>(val virtualFile: VirtualFile): JPanel(BorderLayout()), Disposable {
    open fun reloadDocument() = Unit

    open fun increaseScale() = setScale(currentScaleValue * SCALE_MULTIPLIER)

    open fun decreaseScale() = setScale(currentScaleValue / SCALE_MULTIPLIER)

    open fun setScale(value: Double) = Unit

    open fun nextPage() = Unit

    open fun previousPage() = Unit

    open fun findNext() = Unit

    open fun findPrevious() = Unit

    open var currentPageNumber: Int = 0
    open val currentScaleValue: Double = 1.0
    open val pagesCount: Int = 0

    open val previewState: PreviewState? = null

    open fun updatePreviewState(state: PreviewState) = Unit

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

    override fun dispose() = Unit

    companion object {
        const val SCALE_MULTIPLIER = 1.1
    }
}
