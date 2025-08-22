package com.intellij.diff.tools.holders

import com.firsttimeinforever.intellij.pdf.viewer.lang.PdfFileType
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditor
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditorProvider
import com.intellij.diff.DiffContext
import com.intellij.diff.contents.DiffContent
import com.intellij.diff.contents.FileContent
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.util.Disposer
import java.awt.event.FocusListener
import javax.swing.JComponent

class PdfEditorHolder(
  private val myEditor: FileEditor,
  private val myEditorProvider: FileEditorProvider?
) : EditorHolder() {

  val editor: FileEditor
    get() = myEditor

  override fun dispose() {
    if (myEditorProvider != null) {
      myEditorProvider.disposeEditor(myEditor)
    } else {
      Disposer.dispose(myEditor)
    }
  }

  override fun getComponent(): JComponent = myEditor.component

  override fun installFocusListener(listener: FocusListener) {
    myEditor.component.addFocusListener(listener)
  }

  override fun getPreferredFocusedComponent(): JComponent? = myEditor.preferredFocusedComponent

  class Factory() : EditorHolderFactory<PdfEditorHolder>() {
    companion object {
      val INSTANCE = Factory()
    }

    override fun create(content: DiffContent, context: DiffContext): PdfEditorHolder {
      val project = context.project ?: error("Project required")
      if (content !is FileContent || content.file.fileType !is PdfFileType) {
        error("Content must be a FileContent with a PDF file type")
      }
      val editor = PdfFileEditor(project, content.file)
      val provider = PdfFileEditorProvider()
      return PdfEditorHolder(editor, provider)
    }

    override fun canShowContent(content: DiffContent, context: DiffContext): Boolean =
      content is FileContent && content.file.fileType is PdfFileType

    override fun wantShowContent(content: DiffContent, context: DiffContext): Boolean = canShowContent(content, context)
  }

}
