package com.firsttimeinforever.intellij.pdf.viewer.ui.widgets

import com.firsttimeinforever.intellij.pdf.viewer.PdfViewerBundle
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

class DocumentPageStatusBarWidgetFactory: StatusBarWidgetFactory {
    override fun getId(): String = DocumentPageStatusBarWidget.ID

    override fun getDisplayName(): String = PdfViewerBundle.message("pdf.viewer.widgets.document.page.statusbar.widget.display.name")

    override fun disposeWidget(widget: StatusBarWidget) = Unit

    override fun createWidget(project: Project): StatusBarWidget =
        DocumentPageStatusBarWidget(project)

    override fun canBeEnabledOn(statusBar: StatusBar): Boolean {
        val project = statusBar.project ?: return false
        return FileEditorManager.getInstance(project).selectedEditor?.let {
            it is PdfFileEditor
        } ?: return false
    }

    override fun isAvailable(project: Project): Boolean {
        val editor = FileEditorManager.getInstance(project).selectedEditor ?: return false
        return editor is PdfFileEditor
    }

    override fun isConfigurable(): Boolean = false
}
