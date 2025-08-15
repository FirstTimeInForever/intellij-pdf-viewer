package com.firsttimeinforever.intellij.pdf.viewer.ui.widgets

import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.wm.impl.status.widget.StatusBarWidgetsManager

internal class PdfStatusBarProjectActivity : ProjectActivity {
  override suspend fun execute(project: Project) {
    logger.debug("Registering new FileEditorManagerListener for newly opened project")
    project.messageBus.connect().subscribe(
      FileEditorManagerListener.FILE_EDITOR_MANAGER,
      object : FileEditorManagerListener {
        override fun selectionChanged(event: FileEditorManagerEvent) {
          logger.debug("Selection changed")
          @Suppress("IncorrectServiceRetrieving") // Incorrect: PdfStatusBarProjectManagerListener is a project service
          project.service<StatusBarWidgetsManager>().run {
            logger.debug("Updating widget")
            updateWidget(PdfDocumentPageStatusBarWidgetFactory::class.java)
          }
        }
      }
    )
  }

  companion object {
    private val logger = logger<PdfStatusBarProjectActivity>()
  }
}
