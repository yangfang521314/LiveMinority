package com.cgtn.minor.liveminority.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.cgtn.minor.liveminority.R
import com.google.gson.Gson
import okhttp3.RequestBody

/**
 * created by yf on 2018/7/6.
 */


fun Activity.toast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}

fun Any.toast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}

fun getGson(data: Any): RequestBody {
    val gson = Gson()
    val entity = gson.toJson(data)
    return RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), entity)
}

/**
 * @param context
 * @param cs
 * @param duration //时间
 */
fun Any.showToast(context: Context, cs: CharSequence, duration: Int) {
    var mToast: Toast? = null
    mToast?.cancel()
    val inflater = LayoutInflater.from(context)
    val layout = inflater.inflate(R.layout.custom_toast, null)

    val pro = layout.findViewById<View>(R.id.progressIconToast)
    pro.visibility = View.GONE

    // 设置toast文字
    val tv = layout.findViewById<TextView>(R.id.tvTextToast)
    tv.text = cs
    mToast = Toast(context)
    mToast.setGravity(Gravity.CENTER, 0, 0)
    mToast.duration = duration
    mToast.view = layout
    mToast.show()
}


//开启事务
inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
    beginTransaction().commitAllowingStateLoss()
}

//增加fragment
fun FragmentActivity.addFragment(fragment: Fragment, content: Int, flag: String) {
    supportFragmentManager.inTransaction { add(content, fragment, flag).hide(fragment) }
}

fun FragmentActivity.replaceFragment(fragment: Fragment, content: Int, flag: String) {
    supportFragmentManager.inTransaction { replace(content, fragment, flag) }
}

fun FragmentActivity.showFragment(fragment: Fragment) {
    supportFragmentManager.inTransaction { show(fragment) }
}

fun FragmentActivity.hideFragment(fragment: Fragment) {
    supportFragmentManager.inTransaction { hide(fragment) }
}

fun commonStartActivity(forActivity: Activity, toActivity: Class<Any>) {
    val intent = Intent()
    intent.setClass(forActivity, toActivity)
    forActivity.startActivity(intent)
}




