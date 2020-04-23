package com.firsttimeinforever.intellij.pdf.viewer.ui.editor

import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.fileEditor.FileEditorStateLevel


class PdfFileEditorState(val page: Int): FileEditorState {
    override fun canBeMergedWith(otherState: FileEditorState?, level: FileEditorStateLevel?): Boolean {
        return false
    }
}
