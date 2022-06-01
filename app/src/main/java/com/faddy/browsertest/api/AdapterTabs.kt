package com.faddy.browsertest.api

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.faddy.browsertest.R
import com.google.android.material.card.MaterialCardView

internal class AdapterTabs(
    private val context: Context,
    private val albumController: AlbumController,
    private var browserController: BrowserController
) {
    var albumView: View? = null
        private set
    private var albumTitle: TextView? = null
    private var albumCardView: MaterialCardView? = null
    fun setAlbumTitle(title: String?) {
        albumTitle?.setText(title)
    }

    fun setBrowserController(browserController: BrowserController) {
        this.browserController = browserController
    }

    private fun initUI() {
        albumView = LayoutInflater.from(context).inflate(R.layout.item_list, null, false)
        albumCardView = albumView?.findViewById<MaterialCardView>(R.id.albumCardView)
        albumTitle = albumView?.findViewById<TextView>(R.id.titleView)
        val albumClose = albumView!!.findViewById<Button>(R.id.cancelButton)
        albumClose.visibility = View.VISIBLE
        albumClose.setOnClickListener { view: View? ->
            browserController.removeAlbum(albumController)
            if (BrowserContainer.size() < 2) {
                browserController.hideOverview()
            }
        }
    }

    fun activate() {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.editTextSwitchKey, typedValue, true)
        val color: Int = typedValue.data
        albumCardView?.setCardBackgroundColor(color)
        albumTitle?.setOnClickListener(View.OnClickListener { view: View? ->
            albumCardView?.setCardBackgroundColor(color)
            browserController.hideOverview()
        })
    }

    fun deactivate() {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.editTextSwitchKey, typedValue, true)
        val color: Int = typedValue.data
        albumCardView?.setCardBackgroundColor(color)
        albumTitle?.setOnClickListener(View.OnClickListener { view: View? ->
            browserController.showAlbum(albumController)
            browserController.hideOverview()
        })
    }

    init {
        initUI()
    }
}