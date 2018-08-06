package cn.yanjingtp.sleeprecord.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import cn.yanjingtp.sleeprecord.R
import java.util.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

//        Toast.makeText(this@SplashActivity,"chenggongle",Toast.LENGTH_LONG).show()
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                val intent = Intent()
                startActivity(intent.setClass(this@SplashActivity, MainActivity::class.java))
                finish()
            }

        }, 3500)
    }
}
