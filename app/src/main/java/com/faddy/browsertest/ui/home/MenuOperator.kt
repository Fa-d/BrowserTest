package com.faddy.browsertest.ui.home


import android.content.Context
import android.view.View
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import com.faddy.browsertest.R


class MenuOperator(private val theContext: Context, private val webView: WebView) {

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
                R.id.back -> {
                    Toast.makeText(theContext, "back", Toast.LENGTH_SHORT).show()
                    if(webView.canGoBack()){
                        webView.goBack()
                    }
                }
                R.id.reload -> {
                    webView.reload()
                    Toast.makeText(theContext, "reload", Toast.LENGTH_SHORT).show()
                }
                R.id.previousHis -> {
                   if(webView.canGoForward()){
                        webView.goForward()
                    }
                    Toast.makeText(theContext, "previousHis", Toast.LENGTH_SHORT).show()
                }
                R.id.newTab -> {
                    Toast.makeText(theContext, "newTab", Toast.LENGTH_SHORT).show()
                }
                R.id.exit -> {

                    Toast.makeText(theContext, "exit", Toast.LENGTH_SHORT).show()
                }
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