package com.cgtn.minor.liveminority.mvp.contract

import com.cgtn.minor.liveminority.base.BaseContract

/**
 * created by yf on 2019/1/6.
 */
interface LoginContract {

    interface LoginView : BaseContract.View {
        fun loginSuccess()
    }

    interface Presenter : BaseContract.Presenter<LoginView> {
        //登录
        fun login()
    }
}