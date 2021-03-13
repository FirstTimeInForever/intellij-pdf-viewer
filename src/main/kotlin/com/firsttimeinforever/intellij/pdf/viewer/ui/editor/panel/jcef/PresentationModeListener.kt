package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef

fun interface PresentationModeListener {
    fun presentationModeChanged(controller: PresentationModeController): Boolean
}
