package com.cgtn.minor.liveminority.utils

import android.Manifest.permission.ACCESS_NETWORK_STATE
import android.annotation.TargetApi
import android.content.Context
import android.net.*
import android.os.Build
import android.support.annotation.RequiresPermission
import com.cgtn.minor.liveminority.widget.listener.NetWorkListener


/**
 * created by yf on 2018/7/23.
 */

class NetworkUtils private constructor() {

    companion object {
        /**
         * 获取网络状况信息
         */
        @RequiresPermission(ACCESS_NETWORK_STATE)
        fun isConnected(context: Context): Boolean {
            val cm: ConnectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (cm.activeNetworkInfo == null || !cm.activeNetworkInfo.isConnected) {
                return false
            }
            return true

        }


        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        fun initNetWorkCallBack(
            context: Context,
            netWorkListener: NetWorkListener
        ) {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.registerNetworkCallback(
                NetworkRequest.Builder().build(),
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        LogUtil.d("onAvailable")
                        netWorkListener.onNetOn()
                    }

                    override fun onLosing(network: Network, maxMsToLive: Int) {
                        LogUtil.d(" onLosing")
                    }

                    override fun onLost(network: Network) {
                        LogUtil.d(" onLost")
                        netWorkListener.onNetOff()
                    }

                    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                        LogUtil.d(" onCapabilitiesChanged")
                    }

                    override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                        LogUtil.d("onLinkPropertiesChanged ")
                    }
                })
        }

    }

}