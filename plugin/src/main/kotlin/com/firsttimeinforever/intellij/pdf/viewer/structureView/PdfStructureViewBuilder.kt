package com.firsttimeinforever.intellij.pdf.viewer.structureView

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditor
import com.intellij.ide.structureView.*
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer

class PdfStructureViewBuilder(private val editor: PdfFileEditor): StructureViewBuilder {
  override fun createStructureView(fileEditor: FileEditor?, project: Project): StructureView {
    val model = PdfStructureViewModel(project, editor)
    val factory = StructureViewFactory.getInstance(project)
    val view = factory.createStructureView(fileEditor, model, project, false)
    Disposer.register(view, model)
    return view
  }
}
