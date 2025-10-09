package com.xeta.mws.care

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.xeta.mws.care.ui.HomeFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupSystemBars()
        setStatusBarColor(
            ctx = this,
            activity = this,
            rootLayout = findViewById(R.id.main),
            window = window,

            )

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, HomeFragment())
                .commit()
        }
    }

    private fun setupSystemBars() {
        // Make the content extend behind system bars but add proper padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Don't apply padding here - let fragments handle their own padding
            insets
        }

    }

    @SuppressLint("ResourceAsColor")
    fun setStatusBarColor(ctx: Context, activity: AppCompatActivity, rootLayout: View, window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) { // Android 15+
            ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars())
                val height = insets.top
                // Add top margin so content doesn’t go under the status bar
                v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    topMargin = insets.top
                    bottomMargin = insets.bottom
                }
                // Create a fake status bar view
                val statusBarView = View(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        height
                    )
                    setBackgroundColor(ContextCompat.getColor(ctx, R.color.primary_blue))
                }
                activity.addContentView(statusBarView, statusBarView.layoutParams)

                WindowInsetsCompat.CONSUMED
            }
        } else {
            // For Android 14 and below
            window.statusBarColor = ContextCompat.getColor(ctx, R.color.primary_blue)
        }

    }
}