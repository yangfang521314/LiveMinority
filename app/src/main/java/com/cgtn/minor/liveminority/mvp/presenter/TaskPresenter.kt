package com.cgtn.minor.liveminority.mvp.presenter

import android.annotation.SuppressLint
import com.cgtn.minor.liveminority.base.BasePresenter
import com.cgtn.minor.liveminority.http.manager.CommonRetrofitManager
import com.cgtn.minor.liveminority.mvp.contract.TaskContract
import com.cgtn.minor.liveminority.utils.LogUtil
import com.cgtn.minor.liveminority.utils.RxUtils
import com.cgtn.minor.liveminority.utils.getGson

/**
 * created by yf on 2019/4/19.
 */
class TaskPresenter : BasePresenter<TaskContract.TaskView>(), TaskContract.Presenter {
    //获取用户指定的推流地址
    @SuppressLint("CheckResult")
    override fun getTaskData(username: String, token: String) {
        val map = HashMap<String, String>()
        map["username"] = username
        map["token"] = token
        CommonRetrofitManager.commonRetrofitManager.getTaskData(getGson(map))
            .compose(RxUtils.rxObScheduleHelper())
            .subscribe({
                mView!!.showTaskData(it)
            }, { t: Throwable? ->
                LogUtil.e("$t")
                mView!!.showErrorMsg(t!!.message!!) })

    }

    @SuppressLint("CheckResult")
    fun postData() {
        val testString = "com,comsdsdm.ddsvcscssssdsfsaldsl;"
        val map = HashMap<String, String>()
        map["data"] = testString
        CommonRetrofitManager.commonRetrofitManager.testData(getGson(map))
            .compose(RxUtils.rxObScheduleHelper())
            .subscribe({
                mView!!.showPerformanceTest(it)
            }, { t: Throwable? -> mView!!.showErrorMsg(t!!.message!!) })

    }


}