package com.firsttimeinforever.intellij.pdf.viewer.structureView

import com.firsttimeinforever.intellij.pdf.viewer.model.PdfOutlineNode
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.PdfFileEditor
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.view.PdfOutlineChangedListener
import com.intellij.ide.impl.StructureViewWrapperImpl
import com.intellij.ide.structureView.*
import com.intellij.ide.util.treeView.smartTree.Filter
import com.intellij.ide.util.treeView.smartTree.Grouper
import com.intellij.ide.util.treeView.smartTree.Sorter
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer

class PdfStructureViewModel(
  private val project: Project,
  private val editor: PdfFileEditor
): StructureViewModel, StructureViewModel.ElementInfoProvider, PdfOutlineChangedListener {
  private val modelListeners = mutableListOf<ModelListener>()
  private var tree = PdfStructureViewTreeBuilder.build(editor)

  init {
    val messageBusConnection = project.messageBus.connect()
    Disposer.register(this, messageBusConnection)
    messageBusConnection.subscribe(PdfOutlineChangedListener.TOPIC, this)
  }

  override fun outlineChanged(outline: PdfOutlineNode) {
    tree = PdfStructureViewTreeBuilder.build(editor)
    modelListeners.forEach { it.onModelChanged() }
    // Needed to refresh structure view component
    val wrapper = StructureViewFactoryEx.getInstanceEx(project).structureViewWrapper
    if (wrapper is StructureViewWrapperImpl) {
      ApplicationManager.getApplication().invokeLater {
        wrapper.rebuildNow("Refresh structure view")
      }
    }
  }

  override fun getRoot(): StructureViewTreeElement = tree

  override fun getGroupers(): Array<Grouper> = Grouper.EMPTY_ARRAY

  override fun getSorters(): Array<Sorter> = Sorter.EMPTY_ARRAY

  override fun getFilters(): Array<Filter> = Filter.EMPTY_ARRAY

  override fun dispose() = Unit

  override fun getCurrentEditorElement(): Any? = null

  override fun addEditorPositionListener(listener: FileEditorPositionListener) = Unit

  override fun removeEditorPositionListener(listener: FileEditorPositionListener) = Unit

  override fun addModelListener(modelListener: ModelListener) {
    modelListeners.add(modelListener)
  }

  override fun removeModelListener(modelListener: ModelListener) {
    modelListeners.remove(modelListener)
  }

  override fun shouldEnterElement(element: Any?): Boolean {
    return element is PdfStructureViewElement
  }

  override fun isAlwaysShowsPlus(element: StructureViewTreeElement?): Boolean {
    return element is PdfStructureViewCompositeElement
  }

  override fun isAlwaysLeaf(element: StructureViewTreeElement?): Boolean {
    return element is PdfStructureViewLeafElement
  }
}
