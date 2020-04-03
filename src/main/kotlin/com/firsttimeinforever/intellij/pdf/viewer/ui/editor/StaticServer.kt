package com.firsttimeinforever.intellij.pdf.viewer.ui.editor

import com.intellij.openapi.diagnostic.logger
import com.intellij.util.Url
import com.intellij.util.Urls.parseEncoded
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.QueryStringDecoder
import org.jetbrains.ide.BuiltInServerManager
import org.jetbrains.ide.HttpRequestHandler
import org.jetbrains.io.*
import java.io.File
import java.net.URI


class StaticServer: HttpRequestHandler() {
    companion object {
        fun getInstance(): StaticServer? {
            return EP_NAME.findExtension(StaticServer::class.java)
        }

        val BASE_DIRECTORY = File("/web-view/")
    }

    private val logger = logger<StaticServer>();

    override fun process(urlDecoder: QueryStringDecoder, request: FullHttpRequest, context: ChannelHandlerContext): Boolean {
        val requestPath = urlDecoder.path()
        logger.debug(requestPath)
        if (urlDecoder.uri().contains("get-file")) {
            val targetFile = File(URI(urlDecoder.uri().drop("/get-file/".length)).path)
            logger.debug("Trying to send file: $targetFile")
            FileResponses.sendFile(request, context.channel(), targetFile.toPath())
            return true
        }
        val targetFile = File(BASE_DIRECTORY, requestPath)
        val contentType = FileResponses.getContentType(targetFile.toString())
        val resultBuffer = Unpooled.wrappedBuffer(ResourceLoader.load(targetFile))
        val response = response(contentType, resultBuffer)
        response.send(context.channel(), request)
        return true
    }

    fun getFileUrl(file: String): Url? {
        return getServerUrl()?.resolve(file)
    }

    private fun getServerUrl(): Url? {
        return parseEncoded("http://localhost:" + BuiltInServerManager.getInstance().port)
    }
}
