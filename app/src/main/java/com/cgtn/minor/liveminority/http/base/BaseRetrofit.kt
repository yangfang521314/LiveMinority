package com.cgtn.minor.liveminority.http.base

import com.cgtn.minor.liveminority.app.LiveApplication
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * created by yf on 2019/1/6.
 */
abstract class BaseRetrofit {

    /**
     * 设置retrofit2
     */
    fun getRetrofit(): Retrofit? {
        return Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(getOkHttpClient())
            .baseUrl(getBaseUrl())
            .build()
    }

    abstract fun getBaseUrl(): String


    /**
     * OkHttp适配器
     * 超时时间
     * 缓存设置时间
     */
    private fun getOkHttpClient(): OkHttpClient {
        val mInterceptor = HttpLoggingInterceptor()
        mInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(mInterceptor)
            .connectTimeout(10000L, TimeUnit.MILLISECONDS)       //设置连接超时
            .readTimeout(10000L, TimeUnit.MILLISECONDS)          //设置读取超时
            .writeTimeout(10000L, TimeUnit.MILLISECONDS)
            .cache(Cache(LiveApplication.mInstance!!.cacheDir, 10 * 1024 * 1024))
            .build()
    }


}