package com.firsttimeinforever.intellij.pdf.viewer.ui.widgets

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.wm.impl.status.widget.StatusBarWidgetsManager

internal class PdfStatusBarProjectManagerListener: ProjectManagerListener {
    override fun projectOpened(project: Project) {
        logger.debug("Registering new FileEditorManagerListener for newly opened project")
        project.messageBus.connect().subscribe(
            FileEditorManagerListener.FILE_EDITOR_MANAGER,
            object: FileEditorManagerListener {
                override fun selectionChanged(event: FileEditorManagerEvent) {
                    logger.debug("Selection changed")
                    val targetFactory = StatusBarWidgetsManager::class.java
                    project.getServiceIfCreated(targetFactory)?.run {
                        logger.debug("Updating widget")
                        updateWidget(PdfDocumentPageStatusBarWidgetFactory::class.java)
                    }
                }
            }
        )
    }

    companion object {
        private val logger = logger<PdfStatusBarProjectManagerListener>()
    }
}
