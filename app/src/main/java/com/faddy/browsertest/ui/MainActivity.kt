package com.faddy.browsertest.ui
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.faddy.browsertest.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
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