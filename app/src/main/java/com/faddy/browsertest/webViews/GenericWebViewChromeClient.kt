package com.faddy.browsertest.webViews

import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.lifecycle.MutableLiveData

class GenericWebViewChromeClient() : WebChromeClient() {
    val progresse = MutableLiveData<Int>(0)
    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        progresse.value = newProgress
        super.onProgressChanged(view, newProgress)
    }
}