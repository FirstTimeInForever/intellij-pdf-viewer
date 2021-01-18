package com.firsttimeinforever.intellij.pdf.viewer.ui.editor

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.util.Url
import com.intellij.util.Urls.parseEncoded
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.QueryStringDecoder
import org.jetbrains.ide.BuiltInServerManager
import org.jetbrains.ide.HttpRequestHandler
import org.jetbrains.io.FileResponses
import org.jetbrains.io.response
import org.jetbrains.io.send
import java.io.File

class StaticServer: HttpRequestHandler() {
    companion object {
        private val logger = thisLogger()

        val instance
            get() = EP_NAME.findExtension(StaticServer::class.java)?:
                error("Could not find StaticServer extension")

        val BASE_DIRECTORY = File("/web-view/")
        private const val URL_UUID = "64fa8636-e686-4c63-9956-132d9471ce77"
    }

    private val serverUrl = "http://localhost:${BuiltInServerManager.getInstance().port}/$URL_UUID"

    init {
        logger.debug("Starting static server with url: $serverUrl")
    }

    override fun process(urlDecoder: QueryStringDecoder, request: FullHttpRequest, context: ChannelHandlerContext): Boolean {
        logger.debug(
            """Incoming request with path:\n
            path: ${urlDecoder.path()}\n
            parameters: ${urlDecoder.parameters()}"""
        )
        // Check if current request is actually ours
        if (!urlDecoder.path().contains(URL_UUID)) {
            logger.debug("Current url is not ours. Passing it to the next handler.")
            return false
        }
        val requestPath = urlDecoder.path().removePrefix("/$URL_UUID")
        if (requestPath == "/get-file") {
            val targetFile = File(urlDecoder.parameters()["localFile"]!!.first())
            logger.debug("Trying to send file: $targetFile")
            FileResponses.sendFile(request, context.channel(), targetFile.toPath())
            return true
        }
        val targetFile = File(BASE_DIRECTORY, requestPath)
        val contentType = FileResponses.getContentType(targetFile.toString())
        logger.debug("Trying to send viewer source file: $targetFile with contentType: $contentType")
        val resultBuffer = Unpooled.wrappedBuffer(ResourceLoader.load(targetFile))
        val response = response(contentType, resultBuffer)
        response.send(context.channel(), request)
        return true
    }

    private fun parseEncodedPath(target: String): Url {
        return parseEncoded(target)?: error("parseEncoded failed for \"$target\"")
    }

    fun getFilePreviewUrl(path: String): Url {
        return getIndexUrl().addParameters(mapOf("path" to getLocalFileUrl(path).toString()))
    }

    private fun getLocalFileUrl(path: String): Url {
        val parsed = parseEncodedPath("$serverUrl/get-file")
        return parsed.addParameters(mapOf("localFile" to path))
    }

    private fun getIndexUrl() = parseEncodedPath("$serverUrl/index.html")
}
