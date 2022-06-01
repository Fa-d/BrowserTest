package com.faddy.browsertest.api

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.PorterDuff
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.CookieManager
import android.webkit.URLUtil
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceManager
import com.faddy.browsertest.R
import com.faddy.browsertest.api.BackupUnit.checkPermissionStorage
import com.faddy.browsertest.api.BackupUnit.requestPermission
import com.faddy.browsertest.ui.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*

object HelperUnit {
    private const val REQUEST_CODE_ASK_PERMISSIONS_1 = 1234
    private const val REQUEST_CODE_ASK_PERMISSIONS_2 = 12345
    private const val REQUEST_CODE_ASK_PERMISSIONS_3 = 123456
    private var sp: SharedPreferences? = null

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun grantPermissionsLoc(activity: Activity) {
        val hasACCESS_FINE_LOCATION =
            activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        if (hasACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
            val builder = MaterialAlertDialogBuilder(activity)
            builder.setIcon(R.drawable.icon_alert)
            builder.setTitle(R.string.setting_title_location)
            builder.setMessage(R.string.app_permission)
            builder.setPositiveButton(R.string.app_ok) { dialog: DialogInterface?, whichButton: Int ->
                activity.requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), REQUEST_CODE_ASK_PERMISSIONS_1
                )
            }
            builder.setNegativeButton(R.string.app_cancel) { dialog: DialogInterface, whichButton: Int -> dialog.cancel() }
            val dialog = builder.create()
            dialog.show()
            setupDialog(activity, dialog)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun grantPermissionsCamera(activity: Activity) {
        val camera = activity.checkSelfPermission(Manifest.permission.CAMERA)
        if (camera != PackageManager.PERMISSION_GRANTED) {
            val builder = MaterialAlertDialogBuilder(activity)
            builder.setIcon(R.drawable.icon_alert)
            builder.setTitle(R.string.setting_title_camera)
            builder.setMessage(R.string.app_permission)
            builder.setPositiveButton(R.string.app_ok) { dialog: DialogInterface?, whichButton: Int ->
                activity.requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA
                    ), REQUEST_CODE_ASK_PERMISSIONS_2
                )
            }
            builder.setNegativeButton(R.string.app_cancel) { dialog: DialogInterface, whichButton: Int -> dialog.cancel() }
            val dialog = builder.create()
            dialog.show()
            setupDialog(activity, dialog)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun grantPermissionsMic(activity: Activity) {
        val mic = activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO)
        if (mic != PackageManager.PERMISSION_GRANTED) {
            val builder = MaterialAlertDialogBuilder(activity)
            builder.setIcon(R.drawable.icon_alert)
            builder.setTitle(R.string.setting_title_microphone)
            builder.setMessage(R.string.app_permission)
            builder.setPositiveButton(R.string.app_ok) { dialog: DialogInterface?, whichButton: Int ->
                activity.requestPermissions(
                    arrayOf(
                        Manifest.permission.RECORD_AUDIO
                    ), REQUEST_CODE_ASK_PERMISSIONS_3
                )
            }
            builder.setNegativeButton(R.string.app_cancel) { dialog: DialogInterface, whichButton: Int -> dialog.cancel() }
            val dialog = builder.create()
            dialog.show()
            setupDialog(activity, dialog)
        }
    }

    fun saveAs(dialogToCancel: AlertDialog, activity: Activity, url: String?) {
        try {
            val builder = MaterialAlertDialogBuilder(activity)
            val dialogView = View.inflate(activity, R.layout.dialog_edit_extension, null)
            val editTitle = dialogView.findViewById<EditText>(R.id.dialog_edit_1)
            val editExtension = dialogView.findViewById<EditText>(R.id.dialog_edit_2)
            val filename = URLUtil.guessFileName(url, null, null)
            editTitle.setText(fileName(url))
            val extension = filename.substring(filename.lastIndexOf("."))
            if (extension.length <= 8) {
                editExtension.setText(extension)
            }
            builder.setView(dialogView)
            builder.setTitle(R.string.menu_save_as)
            builder.setIcon(R.drawable.icon_alert)
            builder.setMessage(url)
            val dialog = builder.create()
            val ib_cancel = dialogView.findViewById<Button>(R.id.ib_cancel)
            ib_cancel.setOnClickListener { view: View? ->
                hideSoftKeyboard(editExtension, activity)
                dialog.cancel()
            }
            val ib_ok = dialogView.findViewById<Button>(R.id.ib_ok)
            ib_ok.setOnClickListener { view12: View? ->
                val title = editTitle.text.toString().trim { it <= ' ' }
                val extension1 = editExtension.text.toString().trim { it <= ' ' }
                val filename1 = title + extension1
                if (title.isEmpty() || extension1.isEmpty() || !extension1.startsWith(".")) {
                    // toast(activity, activity.getString(R.string.toast_input_empty));
                } else {
                    if (checkPermissionStorage(activity)) {
                        val source = Uri.parse(url)
                        val request = DownloadManager.Request(source)
                        request.addRequestHeader(
                            "List_protected",
                            CookieManager.getInstance().getCookie(url)
                        )
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) //Notify client once download is completed!
                        request.setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS,
                            filename1
                        )
                        val dm =
                            (activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager)
                        dm.enqueue(request)
                        hideSoftKeyboard(editExtension, activity)
                        dialogToCancel.cancel()
                    } else {
                        requestPermission(activity)
                    }
                }
                dialog.cancel()
            }
            dialog.show()
            setupDialog(activity, dialog)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun createShortcut(context: Context, title: String?, url: String?) {
        val icon: Icon
        val browserController = NinjaWebView.getBrowserController()
        icon = Icon.createWithBitmap(browserController.favicon())
        try {
            val i = Intent()
            i.action = Intent.ACTION_VIEW
            i.data = Uri.parse(url)
            i.setPackage("de.baumann.browser")
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                // code for adding shortcut on pre oreo device
                val installer = Intent()
                installer.putExtra("android.intent.extra.shortcut.INTENT", i)
                installer.putExtra("android.intent.extra.shortcut.NAME", title)
                installer.putExtra(
                    Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(
                        context.applicationContext,
                        R.drawable.ic_baseline_bookmark_border_24
                    )
                )
                installer.action = "com.android.launcher.action.INSTALL_SHORTCUT"
                context.sendBroadcast(installer)
            } else {
                val shortcutManager = context.getSystemService(
                    ShortcutManager::class.java
                )!!
                if (shortcutManager.isRequestPinShortcutSupported) {
                    val pinShortcutInfo = ShortcutInfo.Builder(context, url)
                        .setShortLabel(title!!)
                        .setLongLabel(title)
                        .setIcon(icon)
                        .setIntent(
                            Intent(context, MainActivity::class.java).setAction(Intent.ACTION_VIEW)
                                .setData(
                                    Uri.parse(url)
                                )
                        )
                        .build()
                    shortcutManager.requestPinShortcut(pinShortcutInfo, null)
                } else {
                    println("failed_to_add")
                }
            }
        } catch (e: Exception) {
            println("failed_to_add")
        }
    }

    fun fileName(url: String?): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
        val currentTime = sdf.format(Date())
        val domain =
            Objects.requireNonNull(Uri.parse(url).host)?.replace("www.", "")?.trim { it <= ' ' }
        return domain?.replace(".", "_")?.trim { it <= ' ' } + "_" + currentTime.trim { it <= ' ' }
    }

    @JvmStatic
    fun domain(url: String?): String {
        return if (url == null) {
            ""
        } else {
            try {
                Objects.requireNonNull(Uri.parse(url).host)?.replace("www.", "")?.trim { it <= ' ' }
                    ?: ""
            } catch (e: Exception) {
                ""
            }
        }
    }

    fun initTheme(context: Activity) {
        sp = PreferenceManager.getDefaultSharedPreferences(context)
        when (Objects.requireNonNull(sp?.getString("sp_theme", "1"))) {
            "2" -> context.setTheme(R.style.AppTheme_day)
            "3" -> context.setTheme(R.style.AppTheme_night)
            "4" -> context.setTheme(R.style.AppTheme_wallpaper)
            "5" -> context.setTheme(R.style.AppTheme_OLED)
            else -> context.setTheme(R.style.AppTheme)
        }
    }

    fun showSoftKeyboard(view: View?, context: Activity) {
        assert(view != null)
        val handler = Handler()
        handler.postDelayed({
            if (view!!.requestFocus()) {
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            }
        }, 50)
    }

    fun hideSoftKeyboard(view: View?, context: Context) {
        assert(view != null)
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    fun setupDialog(context: Context, dialog: Dialog) {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            com.google.android.material.R.attr.colorSurfaceVariant,
            typedValue,
            true
        )
        val color = typedValue.data
        val imageView = dialog.findViewById<ImageView>(android.R.id.icon)
        imageView?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        Objects.requireNonNull(dialog.window)?.setGravity(Gravity.BOTTOM)
    }

    fun triggerRebirth(context: Context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context)
        sp?.edit()?.putInt("restart_changed", 0)?.apply()
        sp?.edit()?.putBoolean("restoreOnRestart", true)?.apply()
        val builder = MaterialAlertDialogBuilder(context)
        builder.setTitle(R.string.menu_restart)
        builder.setIcon(R.drawable.icon_alert)
        builder.setMessage(R.string.toast_restart)
        builder.setPositiveButton(R.string.app_ok) { dialog: DialogInterface?, whichButton: Int ->
            val packageManager = context.packageManager
            val intent = packageManager.getLaunchIntentForPackage(context.packageName)!!
            val componentName = intent.component
            val mainIntent = Intent.makeRestartActivityTask(componentName)
            context.startActivity(mainIntent)
            System.exit(0)
        }
        builder.setNegativeButton(R.string.app_cancel) { dialog: DialogInterface, whichButton: Int -> dialog.cancel() }
        val dialog = builder.create()
        dialog.show()
        setupDialog(context, dialog)
    }

    fun convertDpToPixel(dp: Float, context: Context): Int {
        return Math.round(dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT))
    }
}