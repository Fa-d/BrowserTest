package com.faddy.browsertest.models

import android.os.Parcelable
import android.view.View
import android.webkit.WebView
import kotlinx.parcelize.Parcelize

//@Parcelize
data class NewTabsModel(
    var theTitle: String = "",
  //  var theView: View?
)
    //: Parcelable