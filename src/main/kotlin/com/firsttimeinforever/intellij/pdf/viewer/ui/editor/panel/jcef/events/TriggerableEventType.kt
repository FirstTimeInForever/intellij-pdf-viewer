package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events

enum class TriggerableEventType(val displayName: String) {
    INCREASE_SCALE("increaseScale"),
    DECREASE_SCALE("decreaseScale"),
    GOTO_NEXT_PAGE("nextPage"),
    GOTO_PREVIOUS_PAGE("previousPage"),
    TOGGLE_PDFJS_TOOLBAR("toggleToolbar"),
    GET_DOCUMENT_INFO("getDocumentInfo"),
    PRINT_DOCUMENT("printDocument"),
    TOGGLE_SIDEBAR("toggleSidebar"),
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
    SET_PAGE("pageSet")
}
