package com.faddy.browsertest.webViews

import android.graphics.Bitmap
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.lifecycle.MutableLiveData
import com.faddy.browsertest.ui.home.HomeViewModel
import com.faddy.browsertest.utils.imageToBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GenericWebViewChromeClient() : WebChromeClient() {
    var onFavIconRecieved: ((icon: Bitmap?) -> Unit)? = null
    val progresse = MutableLiveData<Int>(0)
    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        progresse.value = newProgress
        super.onProgressChanged(view, newProgress)
    }

    override fun onReceivedIcon(view: WebView, icon: Bitmap?) {
        onFavIconRecieved?.invoke(icon)
        Log.d("THeDebugggingIcon 1", "$icon")
        super.onReceivedIcon(view, icon)
      //  viewModel.setFavionToDB(imageToBitmap(icon), view.url ?: "")
        /*CoroutineScope(Dispatchers.IO).launch {
            viewModel.setFavionToDB(imageToBitmap(icon), view.url ?: "")
        }*/
    }
}
