package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.view

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.BoxLayout
import javax.swing.JPanel

class PdfEditorViewComponent(project: Project, virtualFile: VirtualFile): JPanel(), Disposable {
    val controlPanel = PdfEditorControlPanel(project)
    val controller = PdfPreviewControllerProvider.createViewController(project, virtualFile)

    init {
        Disposer.register(this, controlPanel)
        if (controller != null) {
            Disposer.register(this, controller)
        }
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        add(controlPanel)
        add(controller?.component ?: PdfUnsupportedViewPanel())
    }

    override fun dispose() = Unit
}
