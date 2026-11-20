package com.islandultra.app

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private var islandView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#111622"))
            setPadding(40, 40, 40, 40)
        }

        val title = TextView(this).apply {
            text = "جزيرة دجن"
            setTextColor(Color.WHITE)
            textSize = 26f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 30)
        }

        val btnPerm = Button(this).apply {
            text = "1. أعطِ صلاحية الظهور فوق الشاشة"
            setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this@MainActivity)) {
                    startActivity(
                        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                    )
                } else {
                    Toast.makeText(this@MainActivity, "الصلاحية مفعلة ✅", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val btnShow = Button(this).apply {
            text = "2. أظهر الجزيرة"
            setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this@MainActivity)) {
                    Toast.makeText(this@MainActivity, "فعّل الصلاحية من الزر الأول أولاً", Toast.LENGTH_SHORT).show()
                } else {
                    showIsland()
                }
            }
        }

        val btnHide = Button(this).apply {
            text = "3. أخفِ الجزيرة"
            setOnClickListener {
                hideIsland()
            }
        }

        box.addView(title)
        box.addView(btnPerm)
        box.addView(btnShow)
        box.addView(btnHide)
        setContentView(box)
    }

    private fun showIsland() {
        if (islandView != null) return

        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val shape = GradientDrawable().apply {
            cornerRadius = 50f
            setColor(Color.parseColor("#E60D111A"))
            setStroke(2, Color.parseColor("#668AB4F8"))
        }

        val bubble = TextView(this).apply {
            text = "  جزيرة دجن  "
            setTextColor(Color.WHITE)
            textSize = 13f
            background = shape
            setPadding(36, 14, 36, 14)
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = 25
        }

        var startX = 0
        var startY = 0
        var touchX = 0f
        var touchY = 0f

        bubble.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = params.x
                    startY = params.y
                    touchX = event.rawX
                    touchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = startX + (event.rawX - touchX).toInt()
                    params.y = (startY + (event.rawY - touchY).toInt()).coerceAtLeast(0)
                    wm.updateViewLayout(bubble, params)
                    true
                }
                else -> false
            }
        }

        wm.addView(bubble, params)
        islandView = bubble
        Toast.makeText(this, "ظهرت الجزيرة! اسحبها بيدك لمكان الكاميرا", Toast.LENGTH_SHORT).show()
    }

    private fun hideIsland() {
        val view = islandView ?: return
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        try {
            wm.removeView(view)
        } catch (_: Exception) {
        }
        islandView = null
    }
}