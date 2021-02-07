package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events

enum class TriggerableEventType(val displayName: String) {
    SET_SCALE("setScale"),
    GOTO_NEXT_PAGE("nextPage"),
    GOTO_PREVIOUS_PAGE("previousPage"),
    GET_DOCUMENT_INFO("getDocumentInfo"),
    PRINT_DOCUMENT("printDocument"),
    TOGGLE_SCROLL_DIRECTION("toggleScrollDirection"),
    SPREAD_NONE("spreadNonePages"),
    SPREAD_EVEN_PAGES("spreadEvenPages"),
    SPREAD_ODD_PAGES("spreadOddPages"),
    ROTATE_CLOCKWISE("rotateClockwise"),
    ROTATE_COUNTERCLOCKWISE("rotateCounterclockwise"),
    TOGGLE_PRESENTATION_MODE("togglePresentationMode"),
    FIND_NEXT("findNext"),
    FIND_PREVIOUS("findPrevious"),
    SET_THEME_COLORS("setThemeColors"),
    SET_PAGE("pageSet"),
    TOGGLE_SIDEBAR("toggleSidebar"),
    SET_SIDEBAR_VIEW_MODE("setSidebarViewState"),
    INVERT_DOCUMENT_COLORS("invertDocumentColors"),
    SET_SYNCTEX_AVAILABLE("setSynctexAvailable"),
    FORWARD_SEARCH("forwardSearch")
}
