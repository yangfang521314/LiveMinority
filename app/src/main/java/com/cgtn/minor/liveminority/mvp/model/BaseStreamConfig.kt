package com.cgtn.minor.liveminority.mvp.data

import android.content.pm.ActivityInfo
import com.cgtn.minor.liveminority.BuildConfig
import com.google.gson.GsonBuilder
import com.ksyun.media.streamer.capture.CameraCapture
import com.ksyun.media.streamer.kit.StreamerConstants
import com.ksyun.media.streamer.kit.StreamerConstants.ENCODE_METHOD_SOFTWARE_COMPAT

/**
 * author : yangfang
 * time   : 2019/1/17
 * desc   : 推流基本参数
 * version: 1.0
 */
class BaseStreamConfig {
    var mUrl: String? = null
    //摄像头设置
    var mCameraFacing: Int = CameraCapture.FACING_BACK
    //帧率
    var mFrameRate: Float = 20F
    //码率
    var mVideoKBitrate: Int = 800
    //音频
    var mAudioKBitrate: Int = 48
    //推流分辨率
    var mTargetResolution: Int = StreamerConstants.VIDEO_RESOLUTION_720P
    //方向
    var mOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    //编码方式
    var mEncodeMethod: Int = ENCODE_METHOD_SOFTWARE_COMPAT

    var mAutoStart: Boolean = false

    //debug设置显示推流视频的信息
    var mShowDebugInfo: Boolean = BuildConfig.DEBUG

    fun fromJson(json: String?): BaseStreamConfig {
        return GsonBuilder().create().fromJson(json, this.javaClass)
    }

    fun toJson(): String {
        return GsonBuilder().create().toJson(this)
    }


}