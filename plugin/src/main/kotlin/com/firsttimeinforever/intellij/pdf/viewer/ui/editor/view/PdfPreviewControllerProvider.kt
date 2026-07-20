package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.view

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

object PdfPreviewControllerProvider {
  fun createViewController(project: Project, virtualFile: VirtualFile): PdfJcefPreviewController? {
    if (!isJcefSupported()) return null
    return runCatching { PdfJcefPreviewController(project, virtualFile) }
      .onFailure { logger.warn("Could not initialize JCEF preview controller", it) }
      .getOrNull()
  }

  private fun isJcefSupported(): Boolean {
    return runCatching {
      val jcefAppClass = Class.forName("com.intellij.ui.jcef.JBCefApp")
      val isSupportedMethod = jcefAppClass.getMethod("isSupported")
      isSupportedMethod.invoke(null) as? Boolean ?: false
    }.getOrElse {
      logger.debug("JCEF is not available in current runtime", it)
      false
    }
  }

  private val logger = logger<PdfPreviewControllerProvider>()
}
