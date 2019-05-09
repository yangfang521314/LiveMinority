package com.cgtn.minor.liveminority.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.cgtn.minor.liveminority.R
import com.example.yangfang.kotlindemo.util.SharedPreferenceUtil
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {

    private var isLogin:Boolean by SharedPreferenceUtil("login", false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        startOtherActivity()
    }

    /**
     * 跳转到其他Activity
     */
    @SuppressLint("CheckResult")
    private fun startOtherActivity() {
        //登录跳转到Main
        if (isLogin) {
            Observable.timer(3L, TimeUnit.SECONDS)
                .subscribe {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    finish()
                }

        } else {
            Observable.timer(3L, TimeUnit.SECONDS)
                .subscribe {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    finish()
                }
        }
    }


}
