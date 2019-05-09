package com.cgtn.minor.liveminority.ui.activity

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.cgtn.minor.liveminority.R
import com.cgtn.minor.liveminority.base.BaseMVPActivity
import com.cgtn.minor.liveminority.mvp.contract.TaskContract
import com.cgtn.minor.liveminority.mvp.model.TaskEntity
import com.cgtn.minor.liveminority.mvp.presenter.TaskPresenter
import com.cgtn.minor.liveminority.ui.adapter.TaskAdapter
import com.cgtn.minor.liveminority.utils.LogUtil
import com.cgtn.minor.liveminority.utils.toast
import com.cgtn.minor.liveminority.widget.OnItemClickListener
import com.example.yangfang.kotlindemo.util.SharedPreferenceUtil
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseMVPActivity<TaskContract.TaskView, TaskPresenter>(),
    View.OnClickListener, OnItemClickListener, TaskContract.TaskView {

    private var mExitTime = 0L

    private var mTaskAdapter: TaskAdapter? = null

    private var mDispose: Disposable? = null

    private var rtmp_url: String? = null

    //用户token
    private var _token by SharedPreferenceUtil("token", "")

    private var _username by SharedPreferenceUtil("user", "")

    //判断是否登录
    private var _login by SharedPreferenceUtil("login", false)


    override fun setLayoutId(): Int =
        R.layout.activity_main

    override fun initData() {
        super.initData()
        if (!_login) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        mPresenter = TaskPresenter()
        mPresenter!!.attach(this)
        mPresenter!!.getTaskData(_username, _token)
        //测试上传速度的接口
//        mPresenter!!.postData()
    }

    override fun initView() {
        rcy_task.layoutManager = LinearLayoutManager(this)
        val taskEntity = TaskEntity(1, "Live:30 day countdown to 2019 Beijing international H", "", "")
        val data: List<TaskEntity> = mutableListOf(taskEntity, taskEntity, taskEntity)
        LogUtil.e("${data.size}")
        mTaskAdapter = TaskAdapter(data)
        mTaskAdapter!!.setOnClickListener(this)
        rcy_task.adapter = mTaskAdapter
        setToolBar()

        rcy_create.layoutManager = LinearLayoutManager(this)

    }


    private fun setToolBar() {
        toolbar.title = ""
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            toast(this, "fuck you ")
        }
    }

    /**
     * it 服务器返回的数据
     */
    override fun showTaskData(it: List<TaskEntity>) {

        //todo  假数据

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.task_add) {
            toast(this, "hello")
        }
        return super.onOptionsItemSelected(item)
    }

    // check HW encode white list
//    private fun isHw264EncoderSupported(): Boolean {
//        val deviceInfo = DeviceInfoTools.getInstance().deviceInfo
//        if (deviceInfo != null) {
//            LogUtil.e("$deviceInfo.printDeviceInfo()")
//            return deviceInfo.encode_h264 == DeviceInfo.ENCODE_HW_SUPPORT
//        }
//        return false
//    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


    override fun onClick(v: View?) {
        when (v!!.id) {

        }

    }

    /**
     * 性能测试接口
     */
    override fun showPerformanceTest(it: String?) {

    }

    /**
     * 任务栏推流url的回调处理,推流地址
     */
    override fun onClickListener(view: View, json: Any) {
        LogUtil.e(json.toString())
        rtmp_url = json as String
    }


    /**
     * 退出程序
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - mExitTime > 2000) {
                toast(this, "再次点击退出App")
                mExitTime = System.currentTimeMillis()
            } else {
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mDispose != null && !mDispose!!.isDisposed) {
            mDispose!!.dispose()
        }
    }

}
