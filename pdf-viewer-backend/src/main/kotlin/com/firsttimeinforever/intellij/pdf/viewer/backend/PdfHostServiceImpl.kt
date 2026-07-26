package com.firsttimeinforever.intellij.pdf.viewer.backend

import com.firsttimeinforever.intellij.pdf.viewer.common.PdfHostService
import com.firsttimeinforever.intellij.pdf.viewer.common.SynctexInverseSearchResult
import com.firsttimeinforever.intellij.pdf.viewer.common.SynctexScrollEvent
import com.firsttimeinforever.intellij.pdf.viewer.common.events.PdfFileEvent
import com.firsttimeinforever.intellij.pdf.viewer.common.events.PdfSyncTeXEvent
import com.firsttimeinforever.intellij.pdf.viewer.model.tex.SynctexPreciseLocation
import com.firsttimeinforever.intellij.pdf.viewer.model.tex.SynctexViewCoordinates
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.platform.project.ProjectId
import com.intellij.platform.project.findProject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File
import java.nio.file.Paths

class PdfHostServiceImpl : PdfHostService, Disposable {
    private val watchers = mutableMapOf<Pair<ProjectId, String>, DiskFileWatcher>()
    
    override suspend fun getFileEvents(projectId: ProjectId): Flow<String> = callbackFlow {
        val project = projectId.findProject() ?: return@callbackFlow
        val connection = project.messageBus.connect()
        connection.subscribe(PdfFileEvent.TOPIC, object : PdfFileEvent {
            override fun fileChanged(path: String) {
                trySend(path)
            }
        })
        awaitClose { connection.disconnect() }
    }

    override suspend fun getSyncTeXEvents(projectId: ProjectId): Flow<SynctexScrollEvent> = callbackFlow {
        val project = projectId.findProject() ?: return@callbackFlow
        val connection = project.messageBus.connect()
        connection.subscribe(PdfSyncTeXEvent.TOPIC, object : PdfSyncTeXEvent {
            override fun scrollTo(path: String, location: SynctexPreciseLocation) {
                trySend(SynctexScrollEvent(path, location))
            }
        })
        awaitClose { connection.disconnect() }
    }

    override suspend fun isSynctexInstalled(projectId: ProjectId): Boolean {
        return SynctexUtils.isSynctexInstalled()
    }

    override suspend fun isSynctexFileAvailable(projectId: ProjectId, pdfPath: String): Boolean {
        val file = LocalFileSystem.getInstance().findFileByPath(pdfPath) ?: return false
        return SynctexUtils.isSynctexFileAvailable(file)
    }

    override suspend fun inverseSearch(projectId: ProjectId, pdfPath: String, coordinates: SynctexViewCoordinates): SynctexInverseSearchResult? {
        val pdfFile = LocalFileSystem.getInstance().findFileByPath(pdfPath) ?: return null
        val pdfDir = File(pdfFile.parent.presentableUrl)
        val command = GeneralCommandLine(
            "synctex",
            "edit",
            "-o",
            "${coordinates.page}:${coordinates.x}:${coordinates.y}:${pdfFile.presentableUrl}"
        ).withWorkDirectory(pdfDir)
        
        val output = CommandExecutionUtils.getCommandStdoutIfSuccessful(command) ?: return null
        
        val texPath = INPUT_REGEX.find(output)?.groups?.get("file")?.value ?: return null
        val line = LINE_REGEX.find(output)?.groups?.get("line")?.value?.toInt() ?: 1
        val column = COLUMN_REGEX.find(output)?.groups?.get("col")?.value?.toInt() ?: 1
        
        return SynctexInverseSearchResult(texPath.trim(), line, column)
    }

    override suspend fun startWatching(projectId: ProjectId, pdfPath: String) {
        val key = projectId to pdfPath
        if (watchers.containsKey(key)) return
        val project = projectId.findProject() ?: return
        val path = Paths.get(pdfPath)
        val watcher = DiskFileWatcher(path) {
            project.messageBus.syncPublisher(PdfFileEvent.TOPIC).fileChanged(pdfPath)
        }
        watchers[key] = watcher
    }

    override suspend fun stopWatching(projectId: ProjectId, pdfPath: String) {
        watchers.remove(projectId to pdfPath)?.dispose()
    }

    override fun dispose() {
        watchers.values.forEach { it.dispose() }
        watchers.clear()
    }

    companion object {
        private val INPUT_REGEX = "Input:(?<file>[^\\n]+)".toRegex()
        private val LINE_REGEX = "Line:(?<line>\\d+)".toRegex()
        private val COLUMN_REGEX = "Column:(?<col>\\d+)".toRegex()
    }
}
