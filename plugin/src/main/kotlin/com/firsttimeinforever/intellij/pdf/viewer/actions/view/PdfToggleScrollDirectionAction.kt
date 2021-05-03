package com.firsttimeinforever.intellij.pdf.viewer.actions.view

import com.firsttimeinforever.intellij.pdf.viewer.PdfViewerBundle
import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfAction
import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.PageSpreadState
import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.ScrollDirection
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent

class PdfToggleScrollDirectionAction : PdfAction() {
  override fun actionPerformed(event: AnActionEvent) {
    val controller = findController(event) ?: return
    with(controller) {
      // This is needed to prevent weird behaviour
      setPageSpreadState(PageSpreadState.NONE)
      when (viewState.scrollDirection) {
        ScrollDirection.VERTICAL -> setScrollDirection(ScrollDirection.HORIZONTAL)
        ScrollDirection.HORIZONTAL -> setScrollDirection(ScrollDirection.VERTICAL)
      }
    }
  }

  override fun update(event: AnActionEvent) {
    super.update(event)
    val controller = findController(event) ?: return
    when (controller.viewState.scrollDirection) {
      ScrollDirection.VERTICAL -> {
        event.presentation.text = VERTICAL_TEXT
        event.presentation.description = VERTICAL_DESCRIPTION
        event.presentation.icon = AllIcons.Actions.SplitVertically
      }
      ScrollDirection.HORIZONTAL -> {
        event.presentation.text = HORIZONTAL_TEXT
        event.presentation.description = HORIZONTAL_DESCRIPTION
        event.presentation.icon = AllIcons.Actions.SplitHorizontally
      }
    }
  }

  private companion object {
    val VERTICAL_TEXT = PdfViewerBundle.message("pdf.viewer.actions.pdfjs.set.vertical.scrolling.name")
    val VERTICAL_DESCRIPTION = PdfViewerBundle.message("pdf.viewer.actions.pdfjs.set.vertical.scrolling.description")
    val HORIZONTAL_TEXT = PdfViewerBundle.message("pdf.viewer.actions.pdfjs.set.horizontal.scrolling.name")
    val HORIZONTAL_DESCRIPTION = PdfViewerBundle.message("pdf.viewer.actions.pdfjs.set.horizontal.scrolling.description")
  }
}
