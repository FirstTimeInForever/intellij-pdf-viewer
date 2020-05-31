package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events

enum class SubscribableEventType(val displayName: String) {
    PAGE_CHANGED("pageChanged"),
    DOCUMENT_INFO("documentInfo"),
    PRESENTATION_MODE_ENTER_READY("presentationModeEnterReady"),
    PRESENTATION_MODE_ENTER("presentationModeEnter"),
    PRESENTATION_MODE_EXIT("presentationModeExit"),
    FRAME_FOCUSED("frameFocused"),
    PAGES_COUNT("pagesCound"),
    DOCUMENT_LOAD_ERROR("documentLoadError"),
    UNHANDLED_ERROR("unhandledError")
}
