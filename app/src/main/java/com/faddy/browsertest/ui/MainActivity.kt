package com.faddy.browsertest.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.faddy.browsertest.R
import dagger.hilt.android.AndroidEntryPoint
import org.torproject.jni.TorService


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val status = intent.getStringExtra(TorService.EXTRA_STATUS)
                //Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
            }
        }, IntentFilter(TorService.ACTION_STATUS))
    }


    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            //binding
        } else {
            super.onBackPressed()
        }
    }
}