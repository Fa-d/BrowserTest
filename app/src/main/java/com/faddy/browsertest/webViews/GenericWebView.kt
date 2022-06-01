package com.faddy.browsertest.webViews

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.annotation.RequiresApi
import kotlin.properties.Delegates


class GenericWebView : WebView {

   private lateinit var theWebView: WebView
    var isBackPressed by Delegates.notNull<Boolean>()
    private var onScrollChangeListener: OnScrollChangeListener? = null

    constructor(context: Context?) : super(context!!)

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)

    fun initView(): GenericWebView {
     theWebView = WebView(context)
        with(theWebView) {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
        }
        return this
    }

    private val foreground = false

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        onScrollChangeListener?.onScrollChange(t, oldt)
    }

    fun setOnScrollChangeListener(l: OnScrollChangeListener?) {
        onScrollChangeListener = l
    }

    interface OnScrollChangeListener {
        fun onScrollChange(scrollY: Int, oldScrollY: Int)
    }

    fun setIsBackPressed(isBP: Boolean) {
        isBackPressed = isBP
    }

    fun isForeground(): Boolean {
        return foreground
    }

    @Synchronized
    fun getRequestHeaders(): HashMap<String, String> {
        val requestHeaders = HashMap<String, String>()
        requestHeaders["DNT"] = "1"
        //  Server-side detection for GlobalPrivacyControl
        requestHeaders["Sec-GPC"] = "1"
        requestHeaders["X-Requested-With"] = "com.duckduckgo.mobile.android"
        /*    profile = sp.getString("profile", "profileStandard")
            if (sp.getBoolean(profile + "_saveData", false)) requestHeaders["Save-Data"] = "on"*/
        return requestHeaders
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @Synchronized
    fun initPreferences(url: String?) {

        val webSettings: WebSettings = this.settings
        val userAgent: String = getUserAgent(false)
        webSettings.userAgentString = userAgent
        if (Build.VERSION.SDK_INT >= 26) webSettings.safeBrowsingEnabled = true
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false
        webSettings.setSupportMultipleWindows(true)
        webSettings.textZoom = 100
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        webSettings.mediaPlaybackRequiresUserGesture = true
        webSettings.blockNetworkImage = false
        webSettings.setGeolocationEnabled(true)
        webSettings.javaScriptEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.domStorageEnabled = true

    }

    fun getUserAgent(desktopMode: Boolean): String {
        val mobilePrefix = "Mozilla/5.0 (Linux; Android " + Build.VERSION.RELEASE + ")"
        val desktopPrefix = "Mozilla/5.0 (X11; Linux " + System.getProperty("os.arch") + ")"
        var newUserAgent = WebSettings.getDefaultUserAgent(context)
        val prefix = newUserAgent!!.substring(0, newUserAgent.indexOf(")") + 1)
        if (desktopMode) {
            try {
                newUserAgent = newUserAgent.replace(prefix, desktopPrefix)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            try {
                newUserAgent = newUserAgent.replace(prefix, mobilePrefix)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        //Override UserAgent if own UserAgent is defined
        return newUserAgent
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (this == null && canGoBack()) {
                goBack()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}