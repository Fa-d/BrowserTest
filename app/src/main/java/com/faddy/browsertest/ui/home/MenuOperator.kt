package com.faddy.browsertest.ui.home


import android.content.Context
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import com.faddy.browsertest.R


class MenuOperator(private val theContext: Context, private val webView: WebView) {

    var closeApp: (() -> Unit)? = null
    var openNewTab: (() -> Unit)? = null

    fun showPopUp(view: View) {
        val popup = PopupMenu(theContext, view)
        popup.menuInflater.inflate(
            R.menu.themenu,
            popup.menu
        )
        popup.show()
        (popup.menu as MenuBuilder).setOptionalIconsVisible(true)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.back -> if (webView.canGoBack()) webView.goBack()
                R.id.reload -> webView.reload()
                R.id.previousHis -> if (webView.canGoForward()) webView.goForward()
                R.id.newTab -> openNewTab?.invoke()
                R.id.exit -> closeApp?.invoke()
                R.id.bookmark -> {}
                R.id.helpandfeedabck -> {
                    Toast.makeText(theContext, "helpandfeedabck", Toast.LENGTH_SHORT).show()
                }
                R.id.settingsofMenu -> {
                    Toast.makeText(theContext, "settingsofMenu", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
    }

}