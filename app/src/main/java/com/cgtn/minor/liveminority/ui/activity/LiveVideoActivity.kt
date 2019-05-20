package com.cgtn.minor.liveminority.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.cgtn.minor.liveminority.R
import com.cgtn.minor.liveminority.mvp.model.TaskEntity
import com.cgtn.minor.liveminority.utils.LogUtil
import com.cgtn.minor.liveminority.utils.toast
import com.ksyun.media.streamer.kit.KSYStreamer
import com.ksyun.media.streamer.kit.StreamerConstants
import com.ksyun.media.streamer.kit.StreamerConstants.*
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_live.*
import kotlinx.android.synthetic.main.base_camera_actionbar.*
import java.util.concurrent.TimeUnit

/**
 * created by yf on 2019/4/18.
 */
open class LiveVideoActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    companion object {
        protected const val PERMISSION_REQUEST_CAMERA_AUDIOREC = 1
        private val TAG = LiveVideoActivity::class.java.simpleName

    }

    private lateinit var mTaskEntity: TaskEntity
    private var mStreamer: KSYStreamer? = null
    private var mStreaming: Boolean = true
    private var mIsFlashOpened: Boolean = false
    private var mSWEncoderUnsupported: Boolean = false
    private var compositeDisposable: CompositeDisposable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_live)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // 4.4以上系统，自动隐藏x导航栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
        initTaskConfig()
        compositeDisposable = CompositeDisposable()
        mStreamer = KSYStreamer(this)
        initConfig()
    }

    private fun initTaskConfig() {
        if (intent.extras != null) {
            val bundle = intent.extras
            if (bundle!!["task"] != null) {
                val data = bundle["task"]
                mTaskEntity = TaskEntity(0, "", "", "", "")
                    .fromJson(data!!.toString())
                LogUtil.e("$mTaskEntity")
            }
        }
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // 4.4以上系统，自动隐藏导航栏
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            }
        }
    }

    private fun initConfig() {
//        Log.e("LiveCamera", mConfig.toJson())
//        if (!TextUtils.isEmpty(mConfig.mUrl)) {
//            mStreamer!!.url = mConfig.mUrl
//        } else {
//            toast(this, "地址为空")
//        }
//        // 设置推流分辨率
//        mStreamer!!.setPreviewResolution(mConfig.mTargetResolution)
//        mStreamer!!.setTargetResolution(mConfig.mTargetResolution)
//
//        mStreamer!!.videoEncodeMethod = mConfig.mEncodeMethod
//        mStreamer!!.cameraFacing = mConfig.mCameraFacing
//
//        // 硬编模式下默认使用高性能模式(high profile)
//        if (mConfig.mEncodeMethod == StreamerConstants.ENCODE_METHOD_HARDWARE) {
//            mStreamer!!.videoEncodeProfile = VideoEncodeFormat.ENCODE_PROFILE_HIGH_PERFORMANCE
//        }
//        // 设置推流帧率
//        if (mConfig.mFrameRate > 0) {
//            mStreamer!!.previewFps = mConfig.mFrameRate
//            mStreamer!!.targetFps = mConfig.mFrameRate
//        }
//        // 设置推流视频码率，三个参数分别为初始码率、最高码率、最低码率
//        val videoBitrate = mConfig.mVideoKBitrate
//        if (videoBitrate > 0) {
//            mStreamer!!.setVideoKBitrate(videoBitrate * 3 / 4, videoBitrate, videoBitrate / 4)
//        }
//
//        // 设置音频码率
//        if (mConfig.mAudioKBitrate > 0) {
//            mStreamer!!.setAudioKBitrate(mConfig.mAudioKBitrate)
//        }
//
//        // 设置视频方向（横屏、竖屏）
//        if (mConfig.mOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
////            mIsLandscape = true
//            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//            mStreamer!!.rotateDegrees = 90
//        } else if (mConfig.mOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
////            mIsLandscape = false
//            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//            mStreamer!!.rotateDegrees = 0
//        }

        // 设置预览View
        setDisplayPreview()
        // 设置回调处理函数
        mStreamer!!.onInfoListener = mOnInfoListener
        mStreamer!!.onErrorListener = mOnErrorListener
        // 禁用后台推流时重复最后一帧的逻辑（这里我们选择切后台使用背景图推流的方式）
        mStreamer!!.enableRepeatLastFrame = false

        start_stream_tv.setOnClickListener {
            if (mStreaming) {
                startStream()
            } else {
                stopStream()
            }
        }

        switch_cam.setOnClickListener {
            //切换摄像头
            mStreamer!!.switchCamera()

        }

        flash.setOnClickListener {
            mIsFlashOpened = if (mIsFlashOpened) {
                // 关闭闪光灯
                mStreamer!!.toggleTorch(false)
                false
            } else {
                // 开启闪光灯
                mStreamer!!.toggleTorch(true)
                true
            }
        }
    }


    /**
     * 开始推流
     */
    private fun startStream() {
        mStreaming = false
        mStreamer!!.startStream()
        start_stream_tv.setImageResource(R.mipmap.stop)
    }

    /**
     * 停止推流
     */
    private fun stopStream() {
        mStreamer!!.stopStream()
        mStreaming = true
        start_stream_tv.setImageResource(R.mipmap.play)
    }

    override fun onResume() {
        super.onResume()
        handleOnResume()
    }

    override fun onPause() {
        super.onPause()
        handleOnPause()
    }

    private fun handleOnResume() {
        // 调用KSYStreamer的onResume接口
        mStreamer!!.onResume()
        // 停止背景图采集
        mStreamer!!.stopImageCapture()
        // 开启摄像头采集
        startCameraPreviewWithPermCheck()
        // 如果onPause中切到了DummyAudio模块，可以在此恢复
        mStreamer!!.setUseDummyAudioCapture(false)

    }


    private fun handleOnPause() {
        // 调用KSYStreamer的onPause接口
        mStreamer!!.onPause()
        // 停止摄像头采集，然后开启背景图采集，以实现后台背景图推流功能
        mStreamer!!.stopCameraPreview()
//        mStreamer.startImageCapture(mBgImagePath)
        // 如果希望App切后台后，停止录制主播端的声音，可以在此切换为DummyAudio采集，
        // 该模块会代替mic采集模块产生静音数据，同时释放占用的mic资源
        mStreamer!!.setUseDummyAudioCapture(true)
    }


    private fun setDisplayPreview() {
        mStreamer!!.setDisplayPreview(surfaceView)
    }

    private fun startCameraPreviewWithPermCheck() {
        val cameraPerm = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val audioPerm = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        if (cameraPerm != PackageManager.PERMISSION_GRANTED || audioPerm != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Log.e(TAG, "No CAMERA or AudioRecord permission, please check")
                Toast.makeText(
                    applicationContext, "No CAMERA or AudioRecord permission, please check",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val permissions = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE
                )
                ActivityCompat.requestPermissions(
                    this, permissions,
                    PERMISSION_REQUEST_CAMERA_AUDIOREC
                )
            }
        } else {
            mStreamer!!.startCameraPreview()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CAMERA_AUDIOREC -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mStreamer!!.startCameraPreview()
                } else {
                    Log.e(TAG, "No CAMERA or AudioRecord permission")
                    Toast.makeText(
                        applicationContext, "No CAMERA or AudioRecord permission",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private val mOnInfoListener: KSYStreamer.OnInfoListener =
        KSYStreamer.OnInfoListener { what, msg1, msg2 ->
            onStreamerInfo(what, msg1, msg2)

        }

    /**
     * 推流成功的信息
     */
    private fun onStreamerInfo(what: Int, msg1: Int, msg2: Int) {
        when (what) {
            KSY_STREAMER_OPEN_STREAM_SUCCESS -> {
                toast(this, "推流成功")
            }
            KSY_STREAMER_FRAME_SEND_SLOW -> {
                toast(this, "网络状态不佳，数据发送可能有延迟")
            }

        }
    }

    private val mOnErrorListener = KSYStreamer.OnErrorListener { what, msg1, msg2 ->
        onStreamerError(what, msg1, msg2)

    }

    /**
     * 推流失败的处理
     */
    private fun onStreamerError(what: Int, msg1: Int, msg2: Int) {
        Log.e(TAG, "streaming error: what=$what msg1=$msg1 msg2=$msg2")
        when (what) {
            StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_START_FAILED,
            StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_UNKNOWN -> {
            }
            StreamerConstants.KSY_STREAMER_CAMERA_ERROR_UNKNOWN,
            StreamerConstants.KSY_STREAMER_CAMERA_ERROR_START_FAILED,
            StreamerConstants.KSY_STREAMER_CAMERA_ERROR_EVICTED,
            StreamerConstants.KSY_STREAMER_CAMERA_ERROR_SERVER_DIED -> mStreamer!!.stopCameraPreview()
            StreamerConstants.KSY_STREAMER_VIDEO_ENCODER_ERROR_UNSUPPORTED,
            StreamerConstants.KSY_STREAMER_VIDEO_ENCODER_ERROR_UNKNOWN -> {
                handleEncodeError()
                reStreaming(what)
            }
            KSY_STREAMER_ERROR_CONNECT_BREAKED -> {
                toast(this, "网络连接断开")
            }
            KSY_STREAMER_ERROR_DNS_PARSE_FAILED -> {
                toast(this, "url错误")
            }
            KSY_STREAMER_RTMP_ERROR_UNKNOWN -> {
                toast(this, "推流未知错误")
            }
            else -> {
            }
        }
    }

    /**
     * 出错误重新推送
     */
    @SuppressLint("CheckResult")
    private fun reStreaming(what: Int) {
        val disposable = Observable.timer(3000L, TimeUnit.MILLISECONDS)
            .subscribe {
                startStream()
            }
        compositeDisposable!!.add(disposable)

    }

    /**
     * 编码出错的修改
     */
    private fun handleEncodeError() {
        val encodeMethod = mStreamer!!.videoEncodeMethod
        if (encodeMethod == StreamerConstants.ENCODE_METHOD_HARDWARE) {
            if (mSWEncoderUnsupported) {
                mStreamer!!.setEncodeMethod(
                    StreamerConstants.ENCODE_METHOD_SOFTWARE_COMPAT
                )
                Log.e(TAG, "Got HW encoder error, switch to SOFTWARE_COMPAT mode")
            } else {
                mStreamer!!.setEncodeMethod(StreamerConstants.ENCODE_METHOD_SOFTWARE)
                Log.e(TAG, "Got HW encoder error, switch to SOFTWARE mode")
            }
        } else if (encodeMethod == StreamerConstants.ENCODE_METHOD_SOFTWARE) {
            mSWEncoderUnsupported = true
            mStreamer!!.setEncodeMethod(StreamerConstants.ENCODE_METHOD_SOFTWARE_COMPAT)
            Log.e(TAG, "Got SW encoder error, switch to SOFTWARE_COMPAT mode")
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable!!.dispose()
        mStreamer!!.release()

    }

}