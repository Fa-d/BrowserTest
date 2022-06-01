package com.faddy.browsertest.api

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.webkit.CookieManager
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import com.faddy.browsertest.R
import com.faddy.browsertest.ui.MainActivity
import java.io.File
import java.util.*
import java.util.regex.Pattern

object BrowserUnit {
    const val PROGRESS_MAX = 100
    const val LOADING_STOPPED = 101 //Must be > PROGRESS_MAX !
    const val MIME_TYPE_TEXT_PLAIN = "text/plain"
    const val URL_ENCODING = "UTF-8"
    const val URL_SCHEME_ABOUT = "about:"
    const val URL_SCHEME_MAIL_TO = "mailto:"
    private const val SEARCH_ENGINE_GOOGLE = "https://www.google.com/search?q="
    private const val SEARCH_ENGINE_DUCKDUCKGO = "https://duckduckgo.com/?q="
    private const val SEARCH_ENGINE_STARTPAGE = "https://startpage.com/do/search?query="
    private const val SEARCH_ENGINE_BING = "https://www.bing.com/search?q="
    private const val SEARCH_ENGINE_BAIDU = "https://www.baidu.com/s?wd="
    private const val SEARCH_ENGINE_QWANT = "https://www.qwant.com/?q="
    private const val SEARCH_ENGINE_ECOSIA = "https://www.ecosia.org/search?q="
    private const val SEARCH_ENGINE_Metager = "https://metager.org/meta/meta.ger3?eingabe="
    private const val SEARCH_ENGINE_STARTPAGE_DE =
        "https://startpage.com/do/search?lui=deu&language=deutsch&query="
    private const val SEARCH_ENGINE_SEARX = "https://searx.be/?q="
    private const val URL_ABOUT_BLANK = "about:blank"
    private const val URL_SCHEME_FILE = "file://"
    private const val URL_SCHEME_HTTPS = "https://"
    private const val URL_SCHEME_HTTP = "http://"
    private const val URL_SCHEME_FTP = "ftp://"
    private const val URL_SCHEME_INTENT = "intent://"
    fun isURL(url: String): Boolean {
        var url = url
        url = url.lowercase(Locale.getDefault())
        if (url.startsWith(URL_ABOUT_BLANK)
            || url.startsWith(URL_SCHEME_MAIL_TO)
            || url.startsWith(URL_SCHEME_FILE)
            || url.startsWith(URL_SCHEME_HTTP)
            || url.startsWith(URL_SCHEME_HTTPS)
            || url.startsWith(URL_SCHEME_FTP)
            || url.startsWith(URL_SCHEME_INTENT)
        ) {
            return true
        }
        val regex = ("^((ftp|http|https|intent)?://)" // support scheme
                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" // ftp的user@
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL -> 199.194.52.184
                + "|" // 允许IP和DOMAIN（域名）
                + "([0-9a-z_!~*'()-]+\\.)*" // 域名 -> www.
                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名
                + "[a-z]{2,6})" // first level domain -> .com or .museum
                + "(:[0-9]{1,4})?" // 端口 -> :80
                + "((/?)|" // a slash isn't required if there is no file name
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$")
        val pattern = Pattern.compile(regex)
        return pattern.matcher(url).matches()
    }

    @JvmStatic
    fun queryWrapper(context: Context?, query: String): String {
        var query = query
        if (isURL(query)) {
            if (query.startsWith(URL_SCHEME_ABOUT) || query.startsWith(URL_SCHEME_MAIL_TO)) {
                return query
            }
            if (!query.contains("://")) {
                query = URL_SCHEME_HTTPS + query
            }
            return query
        }
        val sp = PreferenceManager.getDefaultSharedPreferences(
            context!!
        )
        val customSearchEngine = sp.getString("sp_search_engine_custom", "")!!

        //Override UserAgent if own UserAgent is defined
        if (!sp.contains("searchEngineSwitch")) {  //if new switch_text_preference has never been used initialize the switch
            if (customSearchEngine == "") {
                sp.edit().putBoolean("searchEngineSwitch", false).apply()
            } else {
                sp.edit().putBoolean("searchEngineSwitch", true).apply()
            }
        }
        return if (sp.getBoolean(
                "searchEngineSwitch",
                false
            )
        ) {  //if new switch_text_preference has never been used initialize the switch
            customSearchEngine + query
        } else {
            val i =
                Objects.requireNonNull(sp.getString("sp_search_engine", "0"))?.toInt()
            when (i) {
                1 -> SEARCH_ENGINE_STARTPAGE_DE + query
                2 -> SEARCH_ENGINE_BAIDU + query
                3 -> SEARCH_ENGINE_BING + query
                4 -> SEARCH_ENGINE_DUCKDUCKGO + query
                5 -> SEARCH_ENGINE_GOOGLE + query
                6 -> SEARCH_ENGINE_SEARX + query
                7 -> SEARCH_ENGINE_QWANT + query
                8 -> SEARCH_ENGINE_ECOSIA + query
                9 -> SEARCH_ENGINE_Metager + query
                else -> SEARCH_ENGINE_STARTPAGE + query
            }
        }
    }

    fun clearHome(context: Context?) {
        val action = RecordAction(context)
        action.open(true)
        action.clearTable(RecordUnit.TABLE_START)
        action.close()
    }

    fun clearCache(context: Context) {
        try {
            val dir = context.cacheDir
            if (dir != null && dir.isDirectory) deleteDir(dir)
        } catch (exception: Exception) {
            Log.w("browser", "Error clearing cache")
        }
    }

    fun clearCookie() {
        val cookieManager = CookieManager.getInstance()
        cookieManager.flush()
        cookieManager.removeAllCookies { value: Boolean? -> }
    }

    @JvmStatic
    fun clearBookmark(context: Context) {
        val action = RecordAction(context)
        action.open(true)
        action.clearTable(RecordUnit.TABLE_BOOKMARK)
        action.close()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val shortcutManager = context.getSystemService(
                ShortcutManager::class.java
            )
            Objects.requireNonNull(shortcutManager).removeAllDynamicShortcuts()
        }
    }

    fun clearHistory(context: Context) {
        val action = RecordAction(context)
        action.open(true)
        action.clearTable(RecordUnit.TABLE_HISTORY)
        action.close()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val shortcutManager = context.getSystemService(
                ShortcutManager::class.java
            )
            Objects.requireNonNull(shortcutManager).removeAllDynamicShortcuts()
        }
    }

    fun intentURL(context: Context, uri: Uri?) {
        val browserIntent = Intent(Intent.ACTION_VIEW)
        browserIntent.data = uri
        browserIntent.setPackage("de.baumann.browser")
        val chooser =
            Intent.createChooser(browserIntent, context.getString(R.string.menu_open_with))
        context.startActivity(chooser)
    }

    fun openInBackground(activity: Activity, intent: Intent, url: String?) {
        val sp = PreferenceManager.getDefaultSharedPreferences(activity)
        if (sp.getBoolean(
                "sp_tabBackground",
                false
            ) && intent.getPackage() != "de.baumann.browser"
        ) {
            val intentP = Intent(activity, MainActivity::class.java)
            val pendingIntent =
                PendingIntent.getActivity(activity, 0, intentP, PendingIntent.FLAG_IMMUTABLE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Opened Link"
                val description = "url of links opened in the background -> click to open"
                val importance =
                    NotificationManager.IMPORTANCE_HIGH //Important for heads-up notification
                val channel = NotificationChannel("1", name, importance)
                channel.description = description
                channel.setShowBadge(true)
                channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                val notificationManager = activity.getSystemService(
                    NotificationManager::class.java
                )
                notificationManager.createNotificationChannel(channel)
            }
            val mBuilder = NotificationCompat.Builder(activity, "1")
                .setSmallIcon(R.drawable.ic_baseline_language_24)
                .setContentTitle(activity.getString(R.string.main_menu_new_tab))
                .setContentText(url)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE) //Important for heads-up notification
                .setPriority(Notification.PRIORITY_MAX) //Important for heads-up notification
                .setContentIntent(pendingIntent) //Set the intent that will fire when the user taps the notification
            val buildNotification = mBuilder.build()
            val mNotifyMgr =
                activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotifyMgr.notify(1, buildNotification)
            activity.moveTaskToBack(true)
        }
    }

    fun clearIndexedDB(context: Context) {
        val data = Environment.getDataDirectory()
        val blob_storage =
            "//data//" + context.packageName + "//app_webview//" + "//Default//" + "//blob_storage"
        val databases =
            "//data//" + context.packageName + "//app_webview//" + "//Default//" + "//databases"
        val indexedDB =
            "//data//" + context.packageName + "//app_webview//" + "//Default//" + "//IndexedDB"
        val localStorage =
            "//data//" + context.packageName + "//app_webview//" + "//Default//" + "//Local Storage"
        val serviceWorker =
            "//data//" + context.packageName + "//app_webview//" + "//Default//" + "//Service Worker"
        val sessionStorage =
            "//data//" + context.packageName + "//app_webview//" + "//Default//" + "//Session Storage"
        val shared_proto_db =
            "//data//" + context.packageName + "//app_webview//" + "//Default//" + "//shared_proto_db"
        val VideoDecodeStats =
            "//data//" + context.packageName + "//app_webview//" + "//Default//" + "//VideoDecodeStats"
        val QuotaManager =
            "//data//" + context.packageName + "//app_webview//" + "//Default//" + "//QuotaManager"
        val QuotaManager_journal =
            "//data//" + context.packageName + "//app_webview//" + "//Default//" + "//QuotaManager-journal"
        val webData =
            "//data//" + context.packageName + "//app_webview//" + "//Default//" + "//Web Data"
        val WebDataJournal =
            "//data//" + context.packageName + "//app_webview//" + "//Default//" + "//Web Data-journal"
        val blob_storage_file = File(data, blob_storage)
        val databases_file = File(data, databases)
        val indexedDB_file = File(data, indexedDB)
        val localStorage_file = File(data, localStorage)
        val serviceWorker_file = File(data, serviceWorker)
        val sessionStorage_file = File(data, sessionStorage)
        val shared_proto_db_file = File(data, shared_proto_db)
        val VideoDecodeStats_file = File(data, VideoDecodeStats)
        val QuotaManager_file = File(data, QuotaManager)
        val QuotaManager_journal_file = File(data, QuotaManager_journal)
        val webData_file = File(data, webData)
        val WebDataJournal_file = File(data, WebDataJournal)
        deleteDir(blob_storage_file)
        deleteDir(databases_file)
        deleteDir(indexedDB_file)
        deleteDir(localStorage_file)
        deleteDir(serviceWorker_file)
        deleteDir(sessionStorage_file)
        deleteDir(shared_proto_db_file)
        deleteDir(VideoDecodeStats_file)
        deleteDir(QuotaManager_file)
        deleteDir(QuotaManager_journal_file)
        deleteDir(webData_file)
        deleteDir(WebDataJournal_file)
    }

    fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (aChildren in Objects.requireNonNull(children)) {
                val success = deleteDir(File(dir, aChildren))
                if (!success) {
                    return false
                }
            }
        }
        return dir != null && dir.delete()
    }
}