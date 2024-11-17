package com.cs407.memora

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 设置启动页面的布局
        setContentView(R.layout.activity_splash)

        // 设定延迟 3 秒钟后跳转到主页面
        Handler().postDelayed({
            // 启动主页面（SubjectActivity）
            val intent = Intent(this, SubjectActivity::class.java)
            startActivity(intent)

            // 关闭 SplashActivity，这样用户不能返回到它
            finish()
        }, 2000) // 3000 毫秒 = 3 秒
    }
}

