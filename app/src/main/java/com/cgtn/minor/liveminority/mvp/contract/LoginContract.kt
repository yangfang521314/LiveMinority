package com.cgtn.minor.liveminority.mvp.contract

import com.cgtn.minor.liveminority.base.BaseContract

/**
 * created by yf on 2019/1/6.
 */
interface LoginContract {

    interface LoginView : BaseContract.BaseView {
        fun loginSuccess(it: String)
    }

    interface Presenter : BaseContract.Presenter<LoginView> {
        //登录
        fun login(username: String, pwd: String)
    }
}