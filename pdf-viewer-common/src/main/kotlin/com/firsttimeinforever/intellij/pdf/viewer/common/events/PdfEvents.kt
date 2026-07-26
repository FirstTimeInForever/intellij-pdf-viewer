package com.firsttimeinforever.intellij.pdf.viewer.common.events

import com.firsttimeinforever.intellij.pdf.viewer.model.tex.SynctexPreciseLocation
import com.intellij.util.messages.Topic

interface PdfFileEvent {
    fun fileChanged(path: String)

    companion object {
        @Topic.ProjectLevel
        val TOPIC = Topic.create("PDF File Changed", PdfFileEvent::class.java)
    }
}

interface PdfSyncTeXEvent {
    fun scrollTo(path: String, location: SynctexPreciseLocation)

    companion object {
        @Topic.ProjectLevel
        val TOPIC = Topic.create("PDF SyncTeX Scroll", PdfSyncTeXEvent::class.java)
    }
}
