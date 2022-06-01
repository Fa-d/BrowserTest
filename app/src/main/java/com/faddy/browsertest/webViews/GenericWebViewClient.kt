package com.faddy.browsertest.webViews

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.util.Log
import android.webkit.*
import androidx.annotation.RequiresApi
import java.io.*
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.URL

class GenericWebViewClient : WebViewClient() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        //return super.shouldOverrideUrlLoading(view, request)
        val url = request?.url
        view?.loadUrl(url.toString())
        return true
        // Url base logic here
        /*val url = request?.url?.path
        if (url?.startsWith("intent://scan/") == true) {
            // Do Stuff
            return true
        }*/
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse {
        val urlString = request.url.toString().split("#").toTypedArray()[0]
        Log.d("wowoowow", "1 ${request.url}")
        Log.d("wowoowow", "2 ${urlString}")
        try {
            val connection: HttpURLConnection
            val proxied = true
            connection = if (proxied) {
                val proxy = Proxy(Proxy.Type.SOCKS, InetSocketAddress("localhost", 9050))
                URL(urlString).openConnection(proxy) as HttpURLConnection
            } else {
                URL(urlString).openConnection() as HttpURLConnection
            }

            connection.requestMethod = request.method
            for ((key, value) in request.requestHeaders.entries) {
                connection.setRequestProperty(key, value)
            }

            // transform response to required format for WebResourceResponse parameters

            // transform response to required format for WebResourceResponse parameters
            val inputStream: InputStream = BufferedInputStream(connection.inputStream)
            val encoding = connection.contentEncoding
            connection.headerFields
            val responseHeaders: MutableMap<String, String> = java.util.HashMap()
            for (key in connection.headerFields.keys) {
                //responseHeaders[key] = connection.getHeaderField(key)
            }

            var mimeType = "text/plain"
            if (connection.contentType != null && connection.contentType.isNotEmpty()) {
                mimeType = connection.contentType.split("; ").toTypedArray()[0]
            }

            return WebResourceResponse(
                mimeType,
                encoding,
                connection.responseCode,
                connection.responseMessage,
                responseHeaders,
                inputStream
            )

        } catch (e: UnsupportedEncodingException) {
        } catch (e: IOException) {
        }
        // failed doing proxied http request: return empty response
        return WebResourceResponse(
            "text/plain",
            "UTF-8",
            204,
            "No Content",
            HashMap(),
            ByteArrayInputStream(byteArrayOf())
        )
    }


    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
    }

    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
    }

    override fun onReceivedSslError(
        view: WebView?,
        handler: SslErrorHandler?,
        error: SslError?
    ) {
        super.onReceivedSslError(view, handler, error)
        handler?.proceed()

    }
}