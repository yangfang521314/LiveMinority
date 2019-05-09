package com.cgtn.minor.liveminority.base

import com.cgtn.minor.liveminority.utils.toast

/**
 * created by yf on 2019/1/4.
 * 基类Activity
 */
abstract class BaseMVPActivity<V : BaseContract.BaseView, T : BaseContract.Presenter<V>> : BaseActivity(),
    BaseContract.BaseView {
    protected var mPresenter: T? = null


    override fun showErrorMsg(s: String) {
        toast(this,s)
    }

    override fun showLoading(s: String) {

    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.detach()
        mPresenter = null
    }

}