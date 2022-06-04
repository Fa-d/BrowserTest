package com.faddy.browsertest.ui

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.faddy.browsertest.R
import com.faddy.browsertest.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.torproject.jni.TorService


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<HomeViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        initService()
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val status = intent.getStringExtra(TorService.EXTRA_STATUS)
                //Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
            }
        }, IntentFilter(TorService.ACTION_STATUS))
    }



    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (viewModel.genericWebView.canGoBack()) {
                        viewModel.genericWebView.goBack()
                    } else {
                        finish()
                    }
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun initService() {
        bindService(
            Intent(this@MainActivity, TorService::class.java),
            object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName, service: IBinder) {
                    val torService = (service as TorService.LocalBinder).service
                    val conn = torService.torControlConnection
                    while ((conn == torService.torControlConnection) == null) {
                        try {
                            Thread.sleep(500)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                    if (conn != null) {
                        Toast.makeText(
                            this@MainActivity,
                            "Got Tor control connection",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onServiceDisconnected(name: ComponentName) {}
            },
            AppCompatActivity.BIND_AUTO_CREATE
        )
    }
}