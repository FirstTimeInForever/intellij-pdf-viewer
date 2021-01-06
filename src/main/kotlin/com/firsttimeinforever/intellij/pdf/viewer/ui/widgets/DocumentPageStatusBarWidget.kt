package com.firsttimeinforever.intellij.pdf.viewer.ui.widgets

import com.firsttimeinforever.intellij.pdf.viewer.PdfViewerBundle
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.DocumentPageState
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.DocumentPageStateListener
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditor
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

class DocumentPageStatusBarWidget(val project: Project): StatusBarWidget {
    private val logger = logger<DocumentPageStatusBarWidget>()
    private lateinit var actualStatusBar: StatusBar
    private var currentPageState = DocumentPageState(0, 0)
    private val messageBusConnection = project.messageBus.connect(this)

    private val fileEditorListener = object: FileEditorManagerListener {
        override fun selectionChanged(event: FileEditorManagerEvent) {
            when (val editor = event.newEditor ?: return) {
                is PdfFileEditor -> {
                    logger.debug("Selection changed. Updating current page state and calling update for widget.")
                    currentPageState = editor.pageState
                    refresh()
                }
            }
        }
    }

    private val pageStateChangeListenerer = object: DocumentPageStateListener {
        override fun pageStateChanged(pageState: DocumentPageState) {
            currentPageState = pageState
            refresh()
        }
    }

    override fun ID(): String = ID

    private fun refresh() {
        actualStatusBar.updateWidget(ID)
    }

    override fun install(statusBar: StatusBar) {
        logger.debug("Installing widget")
        actualStatusBar = statusBar
        FileEditorManager.getInstance(project)?.also { editorManager ->
            editorManager.selectedEditor?.also {
                if (it is PdfFileEditor) {
                    currentPageState = it.pageState
                }
            }
        }
        refresh()
        messageBusConnection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, fileEditorListener)
        messageBusConnection.subscribe(DocumentPageStateListener.DOCUMENT_PAGE_STATE, pageStateChangeListenerer)
    }

    override fun getPresentation(): StatusBarWidget.WidgetPresentation? {
        return Presentation()
    }

    override fun dispose() = Unit

    private inner class Presentation: StatusBarWidget.TextPresentation {
        override fun getTooltipText() = PdfViewerBundle.message("pdf.viewer.widgets.document.page.statusbar.widget.tooltip")

        override fun getClickConsumer(): Consumer<MouseEvent>? = null

        override fun getAlignment(): Float = Component.CENTER_ALIGNMENT

        override fun getText(): String {
            return with (currentPageState) {
                "$current/$pagesCount"
            }
        }
    }

    companion object {
        const val ID = "PdfViewer.DocumentPageStatusBarWidget"
    }
}
