package com.firsttimeinforever.intellij.pdf.viewer.common

import com.firsttimeinforever.intellij.pdf.viewer.model.tex.SynctexPreciseLocation
import com.firsttimeinforever.intellij.pdf.viewer.model.tex.SynctexViewCoordinates
import com.intellij.platform.project.ProjectId
import fleet.rpc.RemoteApi
import fleet.rpc.Rpc
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

@Rpc
interface PdfHostService : RemoteApi<Unit> {
    suspend fun isSynctexInstalled(projectId: ProjectId): Boolean
    suspend fun isSynctexFileAvailable(projectId: ProjectId, pdfPath: String): Boolean
    suspend fun inverseSearch(projectId: ProjectId, pdfPath: String, coordinates: SynctexViewCoordinates): SynctexInverseSearchResult?
    suspend fun startWatching(projectId: ProjectId, pdfPath: String)
    suspend fun stopWatching(projectId: ProjectId, pdfPath: String)
    
    suspend fun getFileEvents(projectId: ProjectId): Flow<String>
    suspend fun getSyncTeXEvents(projectId: ProjectId): Flow<SynctexScrollEvent>
}

@Serializable
data class SynctexScrollEvent(
    val pdfPath: String,
    val location: SynctexPreciseLocation
)

@Serializable
data class SynctexInverseSearchResult(
    val texPath: String,
    val line: Int,
    val column: Int
)
