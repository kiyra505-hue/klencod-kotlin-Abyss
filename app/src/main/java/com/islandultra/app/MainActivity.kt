package com.islandultra.app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // نصنع واجهة بسيطة برمجياً (بدون ملفات تصميم معقدة)
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 150, 50, 50)

        val title = TextView(this)
        title.text = "أهلاً بك في جزيرتك التفاعلية!\nاضغط الزر بالأسفل للبدء:"
        title.textSize = 20f
        layout.addView(title)

        val btnStart = Button(this)
        btnStart.text = "تشغيل الجزيرة 🚀"
        btnStart.setOnClickListener {
            // نطلب إذن الظهور فوق التطبيقات أولاً
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivity(intent)
            } else {
                // إذا الإذن موجود، نشغل الخدمة
                startIslandService()
            }
        }
        layout.addView(btnStart)

        setContentView(layout)
    }

    // دالة تشغيل الجزيرة
    private fun startIslandService() {
        val serviceIntent = Intent(this, IslandService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }
}