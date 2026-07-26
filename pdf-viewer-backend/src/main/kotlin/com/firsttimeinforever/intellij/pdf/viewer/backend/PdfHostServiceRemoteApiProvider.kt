package com.firsttimeinforever.intellij.pdf.viewer.backend

import com.firsttimeinforever.intellij.pdf.viewer.common.PdfHostService
import com.intellij.openapi.project.Project
import com.intellij.platform.rpc.backend.RemoteApiProvider
import fleet.rpc.remoteApiDescriptor

class PdfHostServiceRemoteApiProvider : RemoteApiProvider {
    override fun RemoteApiProvider.Sink.remoteApis() {
        remoteApi(remoteApiDescriptor<PdfHostService>()) { PdfHostServiceImpl() }
    }
}
