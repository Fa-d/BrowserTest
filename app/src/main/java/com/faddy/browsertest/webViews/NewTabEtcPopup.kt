package com.faddy.browsertest.webViews

import android.content.Context
import android.util.DisplayMetrics
import android.view.Display
import android.view.LayoutInflater
import android.view.WindowManager
import com.faddy.browsertest.databinding.DialogMenuOverflowBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class NewTabEtcPopup() {


    fun showETCPopUp2(context: Context, layoutInflater: LayoutInflater) {
        var popUpBinding: DialogMenuOverflowBinding?
        //   val view = DialogMenuOverflowBinding.inflate(inflater, container, false).also { popUpBinding = it }.root
        val view = DialogMenuOverflowBinding.inflate(layoutInflater).also { popUpBinding = it }.root
        val builder = MaterialAlertDialogBuilder(context)

        builder.setView(view)

        val dialog = builder.create()
        dialog.window?.attributes?.width =
            (getDeviceMetrics(context).widthPixels.times(0.80)).toInt()

        dialog.show()
        /*      popUpBinding?.complainBtn?.setOnClickListener {
                  dialog.dismiss()
              }
              popUpBinding?.complainHistoryBtn?.setOnClickListener {

                  dialog.dismiss()
              }*/
    }

    private fun getDeviceMetrics(context: Context): DisplayMetrics {
        val metrics = DisplayMetrics()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display = wm.defaultDisplay
        display.getMetrics(metrics)
        return metrics
    }

}