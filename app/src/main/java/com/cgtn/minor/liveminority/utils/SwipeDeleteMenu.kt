package com.cgtn.minor.liveminority.utils

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.cardview.widget.CardView


/**
 * created by yf on 2019-05-13.
 */
class SwipeDeleteMenu @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    CardView(context, attrs, defStyleAttr) {

    private var mContext: Context? = null

    // 右侧菜单测量宽度
    private var mRightMenuWidthMeasured: Int = 0
    // 左侧内容区测量宽度
    private var mContentWidthMeasured: Int = 0
    // 测量高度
    private var mHeightMeasured: Int = 0

    // 滑动显示右侧菜单临界值
    private var mLimitDistance: Int = 0

    // 滑动过滤
    private var mScaledTouchSlop: Int = 0

    private var mMaxFlingVelocity: Int = 0

    private var mPointerId: Int = 0

    // 速度追踪，用于追踪手指在滑动过程中的速度
    private var mTracker: VelocityTracker? = null

    private var mFirstX: Float = 0F
    private var mLastX: Float = 0f
    private var mFirstY:Float = 0F
    private var mLastY:Float = 0F

    // 用户是否点击了内容区
    private var isContentDown = true

    private var mShowAnimator: ValueAnimator? = null
    private var mCloseAnimator: ValueAnimator? = null

    init {
        init(context)
    }

    private fun init(context: Context) {
        this.mContext = context
        this.mScaledTouchSlop = ViewConfiguration.get(mContext).scaledTouchSlop
        this.mMaxFlingVelocity = ViewConfiguration.get(mContext).scaledMaximumFlingVelocity
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        mRightMenuWidthMeasured = 0
        mContentWidthMeasured = 0
        mHeightMeasured = 0

        isClickable = true

        for (i in 0 until this.childCount) {
            val childView = this.getChildAt(i)
            if (childView.visibility != View.GONE) {
                childView.isClickable = true
                measureChild(childView, widthMeasureSpec, heightMeasureSpec)

                mHeightMeasured = this.measuredHeight

                // 内容区
                if (i == 0) {
                    mContentWidthMeasured = childView.measuredWidth
                } else {
                    // 右侧菜单
                    mRightMenuWidthMeasured += childView.measuredWidth
                }

            }
        }

        // 测量完成进行保存
        setMeasuredDimension(
            mContentWidthMeasured + paddingLeft + paddingRight,
            mHeightMeasured + paddingTop + paddingBottom
        )

        mLimitDistance = mRightMenuWidthMeasured * 5 / 10
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {

        if (mTracker == null) {
            mTracker = VelocityTracker.obtain()
        }
        mTracker!!.addMovement(ev)

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {

                isContentDown = true

                mFirstX = ev.rawX
                mLastX = ev.rawX

                if (sMenu != null) {
                    if (sMenu !== this) {
                        sMenu!!.showMenu(false)
                    }
                }

                mPointerId = ev.getPointerId(0)
            }
            MotionEvent.ACTION_MOVE -> {

                val swipeDistance = mLastX - ev.rawX

                // 手指在滑动
                if (Math.abs(swipeDistance) > mScaledTouchSlop) {
                    isContentDown = false
                }

                scrollBy(swipeDistance.toInt(), 0)

                if (scrollX < 0) {
                    scrollTo(0, 0)
                }
                if (scrollX > mRightMenuWidthMeasured) {
                    scrollTo(mRightMenuWidthMeasured, 0)
                }

                mLastX = ev.rawX
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {

                mTracker!!.computeCurrentVelocity(1000, mMaxFlingVelocity.toFloat())
                val velocityX = mTracker!!.getXVelocity(mPointerId)
                if (Math.abs(velocityX) > 1000) {
                    if (velocityX < -1000) {
                        showMenu(true)
                    } else {
                        showMenu(false)
                    }
                } else {
                    if (Math.abs(scrollX) > mLimitDistance) {
                        showMenu(true)
                    } else {
                        showMenu(false)
                    }
                }

                releaseTracker()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
            }
            MotionEvent.ACTION_MOVE -> if (Math.abs(ev.rawX - mFirstX) > mScaledTouchSlop) {
                return true
            }
            MotionEvent.ACTION_UP -> if (scrollX > mScaledTouchSlop) {
                // 判断点击的是右侧删除按钮还是左侧内容区
                if (ev.x < width - scrollX) {
                    if (isContentDown) {
                        showMenu(false)
                    }
                    // 事件不再继续向下传递
                    return true
                }
            }
            MotionEvent.ACTION_CANCEL -> {
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onDetachedFromWindow() {
        if (this === sMenu) {
            sMenu!!.showMenu(false)
            sMenu = null
        }
        super.onDetachedFromWindow()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        var rightMenuLeft = 0

        for (i in 0 until this.childCount) {
            val childView = this.getChildAt(i)
            if (childView.visibility != View.GONE) {
                if (i == 0) {
                    childView.layout(
                        paddingLeft, paddingTop,
                        paddingLeft + childView.measuredWidth,
                        paddingTop + childView.measuredHeight
                    )
                    rightMenuLeft = paddingLeft + childView.measuredWidth
                } else {
                    childView.layout(
                        rightMenuLeft, paddingTop,
                        rightMenuLeft + childView.measuredWidth,
                        paddingTop + childView.measuredHeight
                    )
                    rightMenuLeft += childView.measuredWidth
                }
            }
        }

    }

    private fun showMenu(isShow: Boolean) {
        if (isShow) {
            sMenu = this@SwipeDeleteMenu

            mShowAnimator = ValueAnimator.ofInt(scrollX, mRightMenuWidthMeasured)
            mShowAnimator!!.addUpdateListener { animation -> scrollTo(animation.animatedValue as Int, 0) }
            mShowAnimator!!.interpolator = OvershootInterpolator()
            mShowAnimator!!.setDuration(300).start()
        } else {
            sMenu = null

            mCloseAnimator = ValueAnimator.ofInt(scrollX, 0)
            mCloseAnimator!!.addUpdateListener { animation -> scrollTo(animation.animatedValue as Int, 0) }
            mCloseAnimator!!.interpolator = AccelerateInterpolator()
            mCloseAnimator!!.setDuration(300).start()
        }
    }

    /**
     * 及时释放
     */
    private fun releaseTracker() {
        if (mTracker != null) {
            mTracker!!.clear()
            mTracker!!.recycle()
            mTracker = null
        }
    }

    companion object {

        private var sMenu: SwipeDeleteMenu? = null
    }
}