package com.cgtn.minor.liveminority.ui.activity

import android.support.v7.widget.LinearLayoutManager
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.cgtn.minor.liveminority.R
import com.cgtn.minor.liveminority.base.BaseMVPActivity
import com.cgtn.minor.liveminority.contants.Constants.Companion.CREATE
import com.cgtn.minor.liveminority.contants.Constants.Companion.HEADER
import com.cgtn.minor.liveminority.contants.Constants.Companion.TASK
import com.cgtn.minor.liveminority.mvp.contract.TaskContract
import com.cgtn.minor.liveminority.mvp.model.CommonListEntity
import com.cgtn.minor.liveminority.mvp.model.TaskEntity
import com.cgtn.minor.liveminority.mvp.presenter.TaskPresenter
import com.cgtn.minor.liveminority.ui.adapter.TaskAdapter
import com.cgtn.minor.liveminority.utils.LogUtil
import com.cgtn.minor.liveminority.utils.SpaceItemDecoration
import com.cgtn.minor.liveminority.utils.toast
import com.cgtn.minor.liveminority.widget.OnItemClickListener
import com.cgtn.minor.liveminority.widget.stickyitemdecoration.OnStickyChangeListener
import com.cgtn.minor.liveminority.widget.stickyitemdecoration.StickyItemDecoration
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

    private val data = ArrayList<CommonListEntity>()


    override fun setLayoutId(): Int =
        com.cgtn.minor.liveminority.R.layout.activity_main

    override fun initData() {
        super.initData()
//        if (!_login) {
//            startActivity(Intent(this, LoginActivity::class.java))
//            finish()
//            return
//        }
        mPresenter = TaskPresenter()
        mPresenter!!.attach(this)
        mPresenter!!.getTaskData(_username, _token)
        //测试上传速度的接口
//        mPresenter!!.postData()
        if (data.isNotEmpty()) {
            data.clear()
        }
        val taskEntity = TaskEntity(1, "Live:30 day countdown to 2019 Beijing international H", "", "")
        val taskList: List<TaskEntity> = mutableListOf(
            taskEntity,
            taskEntity,
            taskEntity,
            taskEntity,
            taskEntity,
            taskEntity,
            taskEntity,
            taskEntity,
            taskEntity
        )
        var count = 0

        for (i in taskList.indices) {
            if (count == 0) {
                val commonListEntity = CommonListEntity(HEADER, "Task", null)
                data.add(commonListEntity)
                count++
            } else {
                val commonListEntity = CommonListEntity(TASK, "", taskList[i])
                data.add(commonListEntity)
                count++

            }
        }
        count = 0
        for (i in taskList.indices) {
            if (count == 0) {
                val commonListEntity = CommonListEntity(HEADER, "Create", null)
                data.add(commonListEntity)
                count++
            } else {
                val commonListEntity = CommonListEntity(CREATE, "", taskList[i])
                data.add(commonListEntity)
                count++
            }
        }
    }

    override fun initView() {
        mTaskAdapter = TaskAdapter(data)

        header.setDataCallback { pos ->
            val item = mTaskAdapter!!.getData()?.get(pos)?.title
            header.findViewById<TextView>(R.id.tv_header).text = item

        }

        val stickyItemDecoration = StickyItemDecoration(header, HEADER)

        stickyItemDecoration.setOnStickyChangeListener(object : OnStickyChangeListener {
            override fun onScrollable(offset: Int) {
                header.scrollChild(offset)
                header.visibility = View.VISIBLE
            }

            override fun onInVisible() {
                header.reset()
                header.visibility = View.INVISIBLE
            }
        })
        rcy.addItemDecoration(stickyItemDecoration)
        rcy.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rcy.addItemDecoration(stickyItemDecoration)
        rcy.addItemDecoration(SpaceItemDecoration(34))
        LogUtil.e("${data.size}")
        rcy.adapter = mTaskAdapter

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
        if (item!!.itemId == com.cgtn.minor.liveminority.R.id.task_add) {
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
        menuInflater.inflate(com.cgtn.minor.liveminority.R.menu.main, menu)
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
        toast(this, "live task")
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
