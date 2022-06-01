package com.faddy.browsertest.api

data class Record(
    val ordinal: Int = 0,
    var desktopMode: Boolean = false,
    var nightMode: Boolean = false,
    var iconColor: Long = 0,
    var title: String = "",
    var uRL: String = "",
    var time: Long = 0,
    var type: Int = 0
)


/*
class Record {
    val ordinal: Int
    var desktopMode: Boolean?
    var nightMode: Boolean? = null
    var iconColor: Long
    var title: String?
    var uRL: String?
    var time: Long
    var type //0 History, 1 Start site, 2 Bookmark
            : Int

    constructor() {
        title = null
        uRL = null
        time = 0L
        ordinal = -1
        type = -1
        desktopMode = null
        iconColor = 0L
    }

    constructor(
        title: String?,
        url: String?,
        time: Long,
        ordinal: Int,
        type: Int,
        DesktopMode: Boolean?,
        NightMode: Boolean?,
        iconColor: Long
    ) {
        this.title = title
        uRL = url
        this.time = time
        this.ordinal = ordinal
        this.type = type
        desktopMode = DesktopMode
        nightMode = NightMode
        this.iconColor = iconColor
    }
}*/
