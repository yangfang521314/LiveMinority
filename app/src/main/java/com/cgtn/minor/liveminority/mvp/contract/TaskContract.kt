package com.cgtn.minor.liveminority.mvp.contract

import com.cgtn.minor.liveminority.base.BaseContract
import com.cgtn.minor.liveminority.mvp.model.TaskEntity

/**
 * created by yf on 2019/4/19.
 */
interface TaskContract {
    interface TaskView : BaseContract.BaseView {
        fun showTaskData(it: List<TaskEntity>)


        fun showPerformanceTest(it: String?)
    }

    interface Presenter : BaseContract.Presenter<TaskView> {
        fun getTaskData(username: String, token: String)
    }
}