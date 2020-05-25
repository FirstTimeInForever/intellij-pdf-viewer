package com.firsttimeinforever.intellij.pdf.viewer.actions

import com.intellij.openapi.actionSystem.AnActionEvent

class ToggleSidebarAction: PdfEditorPdfjsAction() {
    override fun actionPerformed(event: AnActionEvent) {
        getPanel(event)?.toggleSidebar()
    }
}
