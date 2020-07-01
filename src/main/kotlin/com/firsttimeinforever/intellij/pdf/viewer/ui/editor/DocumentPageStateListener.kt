package com.firsttimeinforever.intellij.pdf.viewer.ui.editor

import com.intellij.util.messages.Topic

interface DocumentPageStateListener {
    fun pageStateChanged(pageState: DocumentPageState)

    companion object {
        val DOCUMENT_PAGE_STATE =
            Topic.create("some name", DocumentPageStateListener::class.java)
    }
}
