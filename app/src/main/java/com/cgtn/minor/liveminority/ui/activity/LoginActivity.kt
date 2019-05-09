package com.cgtn.minor.liveminority.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.text.TextUtils
import com.cgtn.minor.liveminority.R
import com.cgtn.minor.liveminority.base.BaseMVPActivity
import com.cgtn.minor.liveminority.mvp.contract.LoginContract
import com.cgtn.minor.liveminority.mvp.presenter.LoginPresenter
import com.cgtn.minor.liveminority.utils.LogUtil
import com.cgtn.minor.liveminority.utils.toast
import com.cgtn.minor.liveminority.widget.CustomHint
import com.example.yangfang.kotlindemo.util.SharedPreferenceUtil
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : BaseMVPActivity<LoginContract.LoginView, LoginPresenter>(), LoginContract.LoginView {
    //储存用户是否登录
    private var _login by SharedPreferenceUtil("login", false)
    //存储用户token
    private var _token by SharedPreferenceUtil("token", "")

    //存储用户name
    private var _username by SharedPreferenceUtil("user", "")

    private var mUserName: String? = null

    private var mPwd: String? = null

    companion object {
        private const val PERMISSION_REQUEST: Int = 101

    }

    override fun initData() {
        requestPermission()
        mPresenter = LoginPresenter()
        mPresenter!!.attach(this)

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initView() {
        login.setOnClickListener {
            mUserName = login_username.text.toString()
            mPwd = login_pwd.text.toString()
            if (checkUtils()) {
                _username = mUserName!!
                mPresenter!!.login(mUserName!!, mPwd!!)
            }

        }
        val userNameHint = CustomHint(Typeface.DEFAULT, "User name", Typeface.ITALIC)
        login_username.hint = userNameHint
        val pwdHint = CustomHint(Typeface.DEFAULT, "Password", Typeface.ITALIC)
        login_pwd.hint = pwdHint

    }

    @SuppressLint("CheckResult")
    private fun requestPermission() {
        val permissions = RxPermissions(this)
        permissions.requestEachCombined(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
            .subscribe({
                when {
                    it.granted -> {
                        // `permission.name` is granted !
                    }
                    it.shouldShowRequestPermissionRationale -> {
                        toast(this, "Denied permission，don't open the application ")
                        finish()
                        // Denied permission without ask never again
                    }
                    else -> {
                        // Denied permission with ask never again
                        // Need to go to the settings
                        AlertDialog.Builder(this)
                            .setMessage(
                                "You do not allow the application to access the recording rights of the mobile phone," +
                                        " the application can not be used normally, you can open it in the system settings."
                            )
                            .setNegativeButton(
                                "Not"
                            ) { dialog, _ ->
                                dialog.dismiss()
                                finish()
                            }
                            .setPositiveButton(
                                "Go to system settings"
                            ) { dialog, _ ->
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                startActivityForResult(intent, PERMISSION_REQUEST)
                                dialog.dismiss()

                            }
                            .create()
                            .show()

                    }
                }

            }, {
            })
    }


    /**
     * 判断用户名和密码的相关正确性
     */
    private fun checkUtils(): Boolean {
        if(TextUtils.isEmpty(mUserName) && TextUtils.isEmpty(mPwd)){
            toast(this,"Please input user name and password")
            return false
        }
        if (TextUtils.isEmpty(mUserName)) {
            toast(this, "Please input user name")
            return false
        }
        if (TextUtils.isEmpty(mPwd)) {
            toast(this, "Please input password")
            return false
        }
        return true
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_login
    }


    override fun loginSuccess(it: String) {
        LogUtil.e(it)
        _login = true
        _token = it
        toast(this, "登录成功")
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSION_REQUEST) {
            LogUtil.e("$requestCode")
            val permissions = RxPermissions(this)
            permissions.request(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
                .subscribe({
                    if (it) {

                    } else {
                        finish()
                    }
                }, {
                    finish()
                })
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }


}
