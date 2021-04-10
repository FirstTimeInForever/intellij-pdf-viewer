package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.view

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.jcef.JBCefApp

object PdfPreviewControllerProvider {
  fun createViewController(project: Project, virtualFile: VirtualFile): PdfJcefPreviewController? {
    return when {
      JBCefApp.isSupported() -> PdfJcefPreviewController(project, virtualFile)
      else -> null
    }
  }
}
