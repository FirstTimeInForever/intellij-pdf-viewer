package com.firsttimeinforever.intellij.pdf.viewer.actions.debug

import com.intellij.openapi.actionSystem.AnActionEvent

internal class PdfOpenDevtoolsAction: PdfDebugAction() {
    override fun actionPerformed(event: AnActionEvent) {
        findController(event)?.browser?.openDevtools()
    }
}
