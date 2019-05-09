package com.cgtn.minor.liveminority.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * created by yf on 2019/1/4.
 */
abstract class BasePresenter<T : BaseContract.BaseView> : BaseContract.Presenter<T> {


    protected var mView: T? = null
    private var mCompositeDisposable: CompositeDisposable? = null


    fun subscribe(disposable: Disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        mCompositeDisposable!!.add(disposable)
    }

    override fun attach(view: BaseContract.BaseView) {
        mView = view as T

    }

    fun unsubscribe() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable!!.clear()
        }
    }


    override fun detach() {
        if (mView != null) {
            mView = null
        }
    }

}