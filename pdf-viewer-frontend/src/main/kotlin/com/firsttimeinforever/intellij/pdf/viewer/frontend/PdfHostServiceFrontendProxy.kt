package com.firsttimeinforever.intellij.pdf.viewer.frontend

import com.firsttimeinforever.intellij.pdf.viewer.common.PdfHostService
import com.firsttimeinforever.intellij.pdf.viewer.common.SynctexInverseSearchResult
import com.firsttimeinforever.intellij.pdf.viewer.common.SynctexScrollEvent
import com.firsttimeinforever.intellij.pdf.viewer.model.tex.SynctexViewCoordinates
import com.intellij.openapi.components.service
import com.intellij.platform.project.ProjectId
import com.intellij.platform.rpc.RemoteApiProviderService
import fleet.rpc.remoteApiDescriptor
import kotlinx.coroutines.flow.Flow

class PdfHostServiceFrontendProxy : PdfHostService {
    private suspend fun remoteApi(): PdfHostService {
        return service<RemoteApiProviderService>().resolve(remoteApiDescriptor<PdfHostService>())
    }

    override suspend fun isSynctexInstalled(projectId: ProjectId): Boolean = remoteApi().isSynctexInstalled(projectId)
    
    override suspend fun isSynctexFileAvailable(projectId: ProjectId, pdfPath: String): Boolean = 
        remoteApi().isSynctexFileAvailable(projectId, pdfPath)
    
    override suspend fun inverseSearch(projectId: ProjectId, pdfPath: String, coordinates: SynctexViewCoordinates): SynctexInverseSearchResult? =
        remoteApi().inverseSearch(projectId, pdfPath, coordinates)
    
    override suspend fun startWatching(projectId: ProjectId, pdfPath: String) = remoteApi().startWatching(projectId, pdfPath)
    
    override suspend fun stopWatching(projectId: ProjectId, pdfPath: String) = remoteApi().stopWatching(projectId, pdfPath)

    override suspend fun getFileEvents(projectId: ProjectId): Flow<String> = 
        remoteApi().getFileEvents(projectId)
    
    override suspend fun getSyncTeXEvents(projectId: ProjectId): Flow<SynctexScrollEvent> = 
        remoteApi().getSyncTeXEvents(projectId)
}
