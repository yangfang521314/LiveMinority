package com.cgtn.minor.liveminority.mvp.presenter

import android.annotation.SuppressLint
import com.cgtn.minor.liveminority.base.BasePresenter
import com.cgtn.minor.liveminority.http.manager.CommonRetrofitManager
import com.cgtn.minor.liveminority.mvp.contract.LoginContract
import com.cgtn.minor.liveminority.utils.LogUtil
import com.cgtn.minor.liveminority.utils.RxUtils
import com.cgtn.minor.liveminority.utils.getGson

/**
 * created by yf on 2019/1/6.
 * 登录逻辑
 */
class LoginPresenter : BasePresenter<LoginContract.LoginView>(), LoginContract.Presenter {

    /**
     * 登录函数
     */
    @SuppressLint("CheckResult")
    override fun login(username: String, pwd: String) {
        val map = HashMap<String, String>()
        map["userAccount"] = username
        map["password"] = pwd
        CommonRetrofitManager.commonRetrofitManager.login(getGson(map))
            .compose(RxUtils.rxObScheduleHelper())
            .subscribe({
                mView!!.loginSuccess(it)
            }, { t: Throwable? ->
                LogUtil.e("$t")
                mView!!.showErrorMsg(t?.message!!) })

    }


}