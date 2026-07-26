package com.firsttimeinforever.intellij.pdf.viewer.frontend.ui.editor

import com.firsttimeinforever.intellij.pdf.viewer.common.events.PdfFileEvent
import com.firsttimeinforever.intellij.pdf.viewer.common.events.PdfSyncTeXEvent

import com.firsttimeinforever.intellij.pdf.viewer.common.icons.PdfViewerIcons

import com.firsttimeinforever.intellij.pdf.viewer.common.PdfHostService
import com.firsttimeinforever.intellij.pdf.viewer.common.SynctexScrollEvent
import com.firsttimeinforever.intellij.pdf.viewer.common.settings.PdfViewerSettings
import com.firsttimeinforever.intellij.pdf.viewer.common.settings.PdfViewerSettingsListener
import com.firsttimeinforever.intellij.pdf.viewer.frontend.structureView.PdfStructureViewBuilder
import com.firsttimeinforever.intellij.pdf.viewer.model.tex.SynctexPreciseLocation
import com.firsttimeinforever.intellij.pdf.viewer.frontend.ui.editor.view.PdfEditorViewComponent
import com.intellij.diff.util.FileEditorBase
import com.intellij.ide.structureView.StructureViewBuilder
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.platform.project.projectId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.awt.Window
import java.awt.event.HierarchyListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JComponent
import javax.swing.SwingUtilities

// TODO: Implement state persistence
class PdfFileEditor(private val project: Project, private val virtualFile: VirtualFile) : FileEditorBase(), DumbAware {
  private val scope = CoroutineScope(Dispatchers.Default)
  val viewComponent = PdfEditorViewComponent(project, virtualFile)
  private val messageBusConnection = project.messageBus.connect()
  private val fileChangedListener = FileChangedListener(PdfViewerSettings.instance.enableDocumentAutoReload)
  private var lastKnownFileTimestamp = virtualFile.timeStamp
  private val hierarchyListener = HierarchyListener {
    attachWindowFocusListener()
  }
  private val windowFocusListener = object : WindowAdapter() {
    override fun windowGainedFocus(e: WindowEvent) {
      scheduleRefreshCheck()
    }
  }
  private val fileWatcherLock = Any()
  private var isRemoteWatcherEnabled = false
  private var attachedWindow: Window? = null

  init {
    Disposer.register(this, viewComponent)
    Disposer.register(this, messageBusConnection)
    Disposer.register(this) {
      detachWindowFocusListener()
      viewComponent.removeHierarchyListener(hierarchyListener)
      setFileWatcherEnabled(false)
      scope.cancel()
    }
    viewComponent.addHierarchyListener(hierarchyListener)
    ApplicationManager.getApplication().invokeLater {
      attachWindowFocusListener()
    }
    messageBusConnection.subscribe(VirtualFileManager.VFS_CHANGES, fileChangedListener)
    scope.launch {
      val hostService = service<PdfHostService>()
      val projectId = project.projectId()
      hostService.getFileEvents(projectId).collect { path ->
        if (path == virtualFile.path) {
          scheduleRefreshCheck()
        }
      }
    }
    scope.launch {
      val hostService = service<PdfHostService>()
      val projectId = project.projectId()
      hostService.getSyncTeXEvents(projectId).collect { event ->
        if (event.pdfPath == virtualFile.path) {
          viewComponent.controller?.setForwardSearchData(event.location)
        }
      }
    }
    messageBusConnection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, object : FileEditorManagerListener {
      override fun selectionChanged(event: FileEditorManagerEvent) {
        if (event.newEditor === this@PdfFileEditor) {
          scheduleRefreshCheck()
        }
      }
    })
    messageBusConnection.subscribe(PdfViewerSettings.TOPIC, PdfViewerSettingsListener {
      fileChangedListener.isEnabled = it.enableDocumentAutoReload
      setFileWatcherEnabled(it.enableFileWatcher)
    })
    setFileWatcherEnabled(PdfViewerSettings.instance.enableFileWatcher)
  }

  override fun getName(): String = NAME

  override fun getFile(): VirtualFile = virtualFile

  override fun getComponent(): JComponent = viewComponent

  override fun getPreferredFocusedComponent(): JComponent = viewComponent.controlPanel

  private fun scheduleRefreshCheck() {
    ApplicationManager.getApplication().executeOnPooledThread {
      refreshAndReloadIfChanged()
    }
  }

  private fun setFileWatcherEnabled(isEnabled: Boolean) {
    synchronized(fileWatcherLock) {
      if (isRemoteWatcherEnabled == isEnabled) return
      isRemoteWatcherEnabled = isEnabled
      val hostService = service<PdfHostService>()
      val projectId = project.projectId()
      scope.launch {
        if (isEnabled) {
          hostService.startWatching(projectId, virtualFile.path)
        } else {
          hostService.stopWatching(projectId, virtualFile.path)
        }
      }
    }
  }

  private fun attachWindowFocusListener() {
    val window = SwingUtilities.getWindowAncestor(viewComponent) ?: return
    if (window === attachedWindow) {
      return
    }
    detachWindowFocusListener()
    attachedWindow = window
    window.addWindowFocusListener(windowFocusListener)
  }

  private fun detachWindowFocusListener() {
    attachedWindow?.removeWindowFocusListener(windowFocusListener)
    attachedWindow = null
  }

  private fun refreshAndReloadIfChanged() {
    val controller = viewComponent.controller ?: run {
      logger.warn("Target file ${virtualFile.path} changed on disk, but the PDF controller is not ready yet.")
      return
    }

    logger.warn("Target file ${virtualFile.path} changed on disk. Reloading current view.")
    ApplicationManager.getApplication().executeOnPooledThread {
      controller.reload(tryToPreserveState = true)
    }
  }

  private inner class FileChangedListener(var isEnabled: Boolean = true) : BulkFileListener {
    override fun after(events: MutableList<out VFileEvent>) {
      if (!isEnabled) {
        return
      }
      if (events.any { it.file?.path == virtualFile.path || it.path == virtualFile.path }) {
        scheduleRefreshCheck()
      }
    }
  }

  override fun getStructureViewBuilder(): StructureViewBuilder {
    return PdfStructureViewBuilder(this)
  }

  companion object {
    private const val NAME = "Pdf Viewer File Editor"
    private val logger = logger<PdfFileEditor>()
  }
}
