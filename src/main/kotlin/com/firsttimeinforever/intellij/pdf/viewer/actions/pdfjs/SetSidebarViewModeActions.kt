package com.firsttimeinforever.intellij.pdf.viewer.actions.pdfjs

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.SidebarViewMode
import com.intellij.openapi.actionSystem.AnActionEvent

abstract class SetSidebarViewModeActions(
    private val targetViewMode: SidebarViewMode
): PdfEditorPdfjsToggleActionAdapter(
    isDisabledInPresentationMode = true,
    isDisabledInIdePresentationMode = false
) {
    override fun isSelected(event: AnActionEvent): Boolean {
        val panel = getPanel(event) ?: return false
        return panel.sidebarViewState.mode == targetViewMode
    }

    override fun setSelected(event: AnActionEvent, state: Boolean) {
        val panel = getPanel(event) ?: return
        panel.setSidebarViewMode(targetViewMode)
    }

    override fun update(event: AnActionEvent) {
        super.update(event)
        val panel = getPanel(event) ?: return
        event.presentation.isEnabledAndVisible = panel.sidebarAvailableViewModes.isViewModeAvailable(targetViewMode)
    }
}

class SetSidebarThumbnailsViewModeAction:
    SetSidebarViewModeActions(SidebarViewMode.THUMBNAILS)

class SetSidebarBookmarksViewModeAction:
    SetSidebarViewModeActions(SidebarViewMode.BOOKMARKS)

class SetSidebarAttachmentsViewModeAction:
    SetSidebarViewModeActions(SidebarViewMode.ATTACHMENTS)
