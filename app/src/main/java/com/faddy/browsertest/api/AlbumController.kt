package com.faddy.browsertest.api

import android.view.View

interface AlbumController {
    val albumView: View?
    fun activate()
    fun deactivate()
}