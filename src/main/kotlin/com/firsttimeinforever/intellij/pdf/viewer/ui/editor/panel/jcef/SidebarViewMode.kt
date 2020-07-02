package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef

import java.lang.IllegalArgumentException

enum class SidebarViewMode(val displayName: String) {
    THUMBNAILS("thumbs"),
    BOOKMARKS("bookmarks"),
    ATTACHMENTS("attachments");
//    HIDDEN("none")

    companion object {
        fun from(value: String): SidebarViewMode {
            return when (value) {
                "thumbs" -> THUMBNAILS
                "bookmarks" -> BOOKMARKS
                "attachments" -> ATTACHMENTS
                "none" -> throw IllegalArgumentException("Hidden mode should not be used here!")
                else -> throw IllegalArgumentException("Could not construct SidebarViewState!")
            }
        }
    }
}

