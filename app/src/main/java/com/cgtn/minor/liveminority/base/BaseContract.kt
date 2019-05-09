package com.cgtn.minor.liveminority.base

/**
 * created by yf on 2019/1/10.
 */
class BaseContract {
    interface BaseView {
        fun showErrorMsg(s: String)

        fun showLoading(s: String)
    }


    interface Presenter<T:BaseView> {
//        fun subscribe()
//        fun unsubscribe()
        fun attach(view: BaseView)
        fun detach()
    }
}