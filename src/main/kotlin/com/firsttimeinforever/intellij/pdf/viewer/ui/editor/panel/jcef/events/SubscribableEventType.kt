package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events

enum class SubscribableEventType(val displayName: String) {
    PAGE_CHANGED("pageChanged"),
    DOCUMENT_INFO("documentInfo"),
    PRESENTATION_MODE_ENTER_READY("presentationModeEnterReady"),
    PRESENTATION_MODE_ENTER("presentationModeEnter"),
    PRESENTATION_MODE_EXIT("presentationModeExit"),
    FRAME_FOCUSED("frameFocused"),
    PAGES_COUNT("pagesCount"),
    DOCUMENT_LOAD_ERROR("documentLoadError"),
    UNHANDLED_ERROR("unhandledError"),
    SIDEBAR_VIEW_STATE_CHANGED("sidebarViewStateChanged"),
    SIDEBAR_AVAILABLE_VIEWS_CHANGED("sidebarAvailableViewsChanged"),
    SYNC_EDITOR("syncEditor"),
    ASK_FORWARD_SEARCH_DATA("askForwardSearchData"),
}
