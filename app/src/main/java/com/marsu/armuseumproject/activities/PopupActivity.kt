package com.marsu.armuseumproject.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import com.marsu.armuseumproject.R

class PopupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popup)

        supportActionBar?.hide()

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        window.setLayout((width * .8).toInt(), (height * 0.6).toInt())
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }
}