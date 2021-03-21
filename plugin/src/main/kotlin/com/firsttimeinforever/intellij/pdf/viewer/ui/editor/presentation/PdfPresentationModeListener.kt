package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.presentation

import com.intellij.util.messages.Topic

fun interface PdfPresentationModeListener {
    fun presentationModeChanged(controller: PdfPresentationController)

    companion object {
        val TOPIC: Topic<PdfPresentationModeListener> = Topic.create(
            "PdfPresentationModeListener",
            PdfPresentationModeListener::class.java
        )
    }
}
