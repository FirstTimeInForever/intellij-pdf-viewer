package com.firsttimeinforever.intellij.pdf.viewer.mpi.events

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@ExperimentalJsExport
@JsExport
object IdeEventTypes {
    val SET_SCALE by PropertyNameDelegate
    val GOTO_NEXT_PAGE by PropertyNameDelegate
    val GOTO_PREVIOUS_PAGE by PropertyNameDelegate
    val GET_DOCUMENT_INFO by PropertyNameDelegate
    val PRINT_DOCUMENT by PropertyNameDelegate
    val TOGGLE_SCROLL_DIRECTION by PropertyNameDelegate
    val SPREAD_NONE by PropertyNameDelegate
    val SPREAD_EVEN_PAGES by PropertyNameDelegate
    val SPREAD_ODD_PAGES by PropertyNameDelegate
    val ROTATE_CLOCKWISE by PropertyNameDelegate
    val ROTATE_COUNTERCLOCKWISE by PropertyNameDelegate
    val TOGGLE_PRESENTATION_MODE by PropertyNameDelegate
    val FIND_NEXT by PropertyNameDelegate
    val FIND_PREVIOUS by PropertyNameDelegate
    val SET_THEME_COLORS by PropertyNameDelegate
    val SET_PAGE by PropertyNameDelegate
    val TOGGLE_SIDEBAR by PropertyNameDelegate
    val SET_SIDEBAR_VIEW_MODE by PropertyNameDelegate
    val INVERT_DOCUMENT_COLORS by PropertyNameDelegate
}
