package com.cgtn.minor.liveminority.http.manager

import com.cgtn.minor.liveminority.http.api.CommonService
import com.cgtn.minor.liveminority.http.base.BaseRetrofit
import com.cgtn.minor.liveminority.mvp.model.TaskEntity
import com.cgtn.minor.liveminority.utils.RxUtils
import io.reactivex.Observable
import okhttp3.RequestBody

/**
 * created by yf on 2019/1/7.
 * 登录的逻辑接口
 */
class CommonRetrofitManager private constructor() : BaseRetrofit() {
    /**
     * 设置baseUrl的地址
     */
    override fun getBaseUrl(): String {
        return "http://52.81.40.136:8081"
    }

    private var mCommonService: CommonService? = null

    init {
        mCommonService = getRetrofit()!!.create(CommonService::class.java)
    }

    companion object {
        val commonRetrofitManager: CommonRetrofitManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            CommonRetrofitManager()
        }

    }

    /**
     * 登录函数
     */
    fun login(value: RequestBody): Observable<String> {
        return mCommonService!!.login(value)
            .compose(RxUtils.handleObservableResult())
    }

    /**
     * 获取任务函数
     */
    fun getTaskData(body: RequestBody): Observable<List<TaskEntity>> {
        return mCommonService!!.getTaskData(body).compose(RxUtils.handleObservableResult())
    }

    /**
     * 性能测试
     */
    fun testData(body: RequestBody): Observable<String> {
        return mCommonService!!.postData(body).compose(RxUtils.handleObservableResult())
    }


}