package com.cgtn.minor.liveminority.app

import android.app.Application


/**
 * created by yf on 2019/1/4.
 */
class LiveApplication : Application() {
    companion object {
        var mInstance: LiveApplication? = null
        //操作数据库

    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
//        DeviceInfoTools.getInstance().init(this)

    }

}