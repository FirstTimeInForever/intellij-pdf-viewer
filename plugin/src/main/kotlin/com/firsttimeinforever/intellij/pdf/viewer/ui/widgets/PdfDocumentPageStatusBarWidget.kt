package com.firsttimeinforever.intellij.pdf.viewer.ui.widgets

import com.firsttimeinforever.intellij.pdf.viewer.PdfViewerBundle
import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.ViewStateChangeReason
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditor
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.view.PdfViewStateChangedListener
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.util.Consumer
import java.awt.Component
import java.awt.event.MouseEvent

internal class PdfDocumentPageStatusBarWidget(val project: Project): StatusBarWidget {
    private lateinit var actualStatusBar: StatusBar
    private val busConnection = project.messageBus.connect(this)
    private var pagesCount: Int = 0
    private var currentPageNumber: Int = 0

    private val fileEditorListener = object: FileEditorManagerListener {
        override fun selectionChanged(event: FileEditorManagerEvent) {
            event.newEditor.takeIf { it is PdfFileEditor }?.let {
                updateValues(it as PdfFileEditor)
                refresh()
            }
        }
    }

    private val viewStateListener = PdfViewStateChangedListener { controller, state, reason ->
        if (reason in viewStateChangeReasons) {
            currentPageNumber = state.page
            pagesCount = controller.viewProperties.pagesCount
            refresh()
        }
    }

    private fun updateValues(editor: PdfFileEditor) {
        editor.viewComponent.controller?.let {
            currentPageNumber = it.viewState.page
            pagesCount = it.viewProperties.pagesCount
        }
    }

    override fun ID(): String = ID

    private fun refresh() {
        actualStatusBar.updateWidget(ID)
    }

    override fun install(statusBar: StatusBar) {
        logger.debug("Installing widget")
        actualStatusBar = statusBar
        val editor = FileEditorManager.getInstance(project).selectedEditor
        if (editor is PdfFileEditor) {
            updateValues(editor)
        }
        refresh()
        busConnection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, fileEditorListener)
        busConnection.subscribe(PdfViewStateChangedListener.TOPIC, viewStateListener)
    }

    override fun getPresentation(): StatusBarWidget.WidgetPresentation {
        return Presentation()
    }

    override fun dispose() = Unit

    private inner class Presentation: StatusBarWidget.TextPresentation {
        override fun getTooltipText(): String {
            return PdfViewerBundle.message("pdf.viewer.widgets.document.page.statusbar.widget.tooltip")
        }

        override fun getClickConsumer(): Consumer<MouseEvent>? = null

        override fun getAlignment(): Float = Component.CENTER_ALIGNMENT

        override fun getText(): String {
           return "$currentPageNumber/$pagesCount"
        }
    }

    companion object {
        private val logger = logger<PdfDocumentPageStatusBarWidget>()

        private val viewStateChangeReasons = listOf(
            ViewStateChangeReason.INITIAL,
            ViewStateChangeReason.PAGE_NUMBER,
            ViewStateChangeReason.UNSPECIFIED
        )

        const val ID = "PdfViewer.DocumentPageStatusBarWidget"
    }
}
