package com.cgtn.minor.liveminority.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.TextView
import com.cgtn.minor.liveminority.R
import com.cgtn.minor.liveminority.base.BaseMVPActivity
import com.cgtn.minor.liveminority.contants.Constants
import com.cgtn.minor.liveminority.contants.Constants.Companion.HEADER
import com.cgtn.minor.liveminority.db.DBHelper
import com.cgtn.minor.liveminority.mvp.contract.TaskContract
import com.cgtn.minor.liveminority.mvp.model.CommonListEntity
import com.cgtn.minor.liveminority.mvp.model.TaskEntity
import com.cgtn.minor.liveminority.mvp.presenter.TaskPresenter
import com.cgtn.minor.liveminority.ui.adapter.TaskAdapter
import com.cgtn.minor.liveminority.utils.LogUtil
import com.cgtn.minor.liveminority.utils.SpaceItemDecoration
import com.cgtn.minor.liveminority.utils.toast
import com.cgtn.minor.liveminority.widget.EditPopupWindow
import com.cgtn.minor.liveminority.widget.listener.OnEditClickListener
import com.cgtn.minor.liveminority.widget.listener.OnItemClickListener
import com.cgtn.minor.liveminority.widget.stickyitemdecoration.OnStickyChangeListener
import com.cgtn.minor.liveminority.widget.stickyitemdecoration.StickyHeadContainer
import com.cgtn.minor.liveminority.widget.stickyitemdecoration.StickyItemDecoration
import com.example.yangfang.kotlindemo.util.SharedPreferenceUtil
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseMVPActivity<TaskContract.TaskView, TaskPresenter>(),
    View.OnClickListener, OnItemClickListener, TaskContract.TaskView {

    private var mExitTime = 0L

    private var mTaskAdapter: TaskAdapter? = null

    private var mDispose: Disposable? = null

    //用户token
    private var _token by SharedPreferenceUtil("token", "")

    private var _username by SharedPreferenceUtil("user", "")

    //判断是否登录
    private var _login by SharedPreferenceUtil("login", false)

    private var _mClickFlag = true

    private val data = ArrayList<CommonListEntity>()

    private var mCreateList: List<TaskEntity> = ArrayList<TaskEntity>()

    private val headers: List<String> = listOf("My Task", "Create")

    override fun setLayoutId(): Int = R.layout.activity_main

    override fun initData() {
        super.initData()
        if (!_login) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        initCreateData()
        mTaskAdapter = TaskAdapter()
        mPresenter = TaskPresenter()
        mPresenter!!.attach(this)
    }

    override fun initView() {
        setToolBar()
        setRcyBar()
        rcy.layoutManager = LinearLayoutManager(this)
        rcy.addItemDecoration(SpaceItemDecoration(34))
        rcy.adapter = mTaskAdapter
        mTaskAdapter!!.setOnClickListener(this)
        swipe_refresh.setOnRefreshListener {
            swipe_refresh.isRefreshing = true
            mPresenter!!.getTaskData(_username, _token)
        }

    }

    /**
     * 设置recyclerview的粘性bar
     */
    private fun setRcyBar() {
        val container = findViewById<StickyHeadContainer>(R.id.header)
        val tvStockName = container.findViewById<TextView>(R.id.tv_header)
        container.setDataCallback {
            val item = mTaskAdapter!!.getData()!![it].title
            tvStockName.text = item
        }
        val stickyItemDecoration = StickyItemDecoration(container, HEADER)

        stickyItemDecoration.setOnStickyChangeListener(object : OnStickyChangeListener {
            override fun onScrollable(offset: Int) {
                container.scrollChild(offset)
                container.visibility = View.VISIBLE
            }

            override fun onInVisible() {
                container.reset()
                container.visibility = View.INVISIBLE
            }
        })
        rcy.addItemDecoration(stickyItemDecoration)
    }

    /**
     * 设置toolbar的属性
     */
    private fun setToolBar() {
        toolbar.title = ""
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * it 服务器返回的数据
     */
    override fun showTaskData(it: List<TaskEntity>?) {
        if (swipe_refresh.isRefreshing) {
            swipe_refresh.isRefreshing = false
        }
        if (data.isNotEmpty()) {
            data.clear()
        }
        if (it != null && it.isNotEmpty()) {
            data.add(CommonListEntity(HEADER, headers[0], null))
            for (i in it.indices) {
                data.add(CommonListEntity(Constants.TASK, "My Task", it[i]))
            }
            //数据可以初始化为null
            if (mCreateList.isNotEmpty()) {
                data.add(CommonListEntity(HEADER, headers[1], null))
                for (i in mCreateList.indices) {
                    data.add(CommonListEntity(Constants.CREATE, "Create", mCreateList[i]))
                }
            }
            LogUtil.e("${data.size}")
            mTaskAdapter!!.setData(data)
            mTaskAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onStart() {
        super.onStart()
        mPresenter!!.getTaskData(_username, _token)
    }


    override fun showErrorMsg(s: String) {
        super.showErrorMsg(s)
        if (swipe_refresh.isRefreshing) {
            swipe_refresh.isRefreshing = false
        }
        toast(this, s)
        setCreateData()
    }

    private fun setCreateData() {
        //数据可以初始化为null
        if (data.isNotEmpty()) {
            data.clear()
        }
        if (mCreateList.isNotEmpty()) {
            data.add(CommonListEntity(HEADER, headers[1], null))
            for (i in mCreateList.indices) {
                data.add(CommonListEntity(Constants.CREATE, "Create", mCreateList[i]))
            }
        }
        LogUtil.e("${data.size}")
        mTaskAdapter!!.setData(data)
        mTaskAdapter!!.notifyDataSetChanged()
    }


    /**
     * 初始化数据库数据
     */
    @SuppressLint("CheckResult")
    private fun initCreateData() {
        mDispose = Observable.create(ObservableOnSubscribe<List<TaskEntity>> {
            val taskList = DBHelper.mInstance.getTaskDao().getTaskData()
            it.onNext(taskList)
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it != null && it.isNotEmpty()) {
                    mCreateList = it
                }
            }
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.task_add && _mClickFlag) {
            onShowMenuPop()
        }
        return true
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
     * 任务栏推流url的回调处理,点击进入详情页面
     */
    override fun onClickListener(view: View?, json: Any) {
        val task = json as TaskEntity
        val intent = Intent(this@MainActivity, LiveVideoActivity::class.java)
        intent.putExtra("task", task.toJson())
        startActivity(intent)
    }

    /**
     * 显示popupwindow
     */
    override fun onShowMenuPop() {
        _mClickFlag = false
        getCreateList()
        val editPopupWindow = EditPopupWindow(this)
        editPopupWindow.showAtLocation(main_task, Gravity.CENTER, 0, 0)
        editPopupWindow.setWindowBgAlpha(Constants.CHANGE_ALPHA)
        editPopupWindow.setOnSaveListener(object : OnEditClickListener {
            override fun onSaveTaskListener(mTaskTile: String, mTaskUrl: String) {
                toast(this@MainActivity, "保存")
                editPopupWindow.dismiss()
                editPopupWindow.setWindowBgAlpha(Constants.NORMAL_ALPHA)
                val mSaveTaskEntity: TaskEntity
                if (mCreateList.isEmpty()) {
                    mSaveTaskEntity = TaskEntity(1, mTaskTile, mTaskUrl, "", "")
                    data.add(CommonListEntity(HEADER, headers[1], null))
                    data.add(CommonListEntity(Constants.CREATE, "Create", mSaveTaskEntity))
                } else {
                    mSaveTaskEntity = TaskEntity(mCreateList.size + 1, mTaskTile, mTaskUrl, "", "")
                    data.add(CommonListEntity(Constants.CREATE, "Create", mSaveTaskEntity))
                }
                mTaskAdapter!!.notifyDataSetChanged()
                mDispose = Completable.complete()
                    .observeOn(Schedulers.io())
                    .subscribe {
                        DBHelper.mInstance.getTaskDao()
                            .addTaskData(mSaveTaskEntity)
                    }

            }

        })
        editPopupWindow.setOnDismissListener {
            if (!editPopupWindow.isShowing) {
                editPopupWindow.setWindowBgAlpha(Constants.NORMAL_ALPHA)
                _mClickFlag = true
            }
        }
    }

    private fun getCreateList() {
        mDispose = Observable.create(ObservableOnSubscribe<List<TaskEntity>> {
            val taskList = DBHelper.mInstance.getTaskDao().getTaskData()
            it.onNext(taskList)
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                mCreateList = it
            }
    }


    /**
     * 退出程序
     *
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
