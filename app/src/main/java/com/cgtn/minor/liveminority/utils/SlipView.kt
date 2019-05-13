package com.cgtn.minor.liveminority.utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.method.Touch.scrollTo
import android.util.AttributeSet
import android.view.*
import android.widget.Scroller
import androidx.cardview.widget.CardView


/**
 * created by yf on 2019-05-13.
 */
class SlipView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    CardView(context, attrs, defStyleAttr) {

    private var scroller: Scroller? = null
    private var velocityTracker: VelocityTracker? = null

    private var lastX: Int = 0          //最近一次MotionEvent的x坐标
    private var lastY: Int = 0          //最近一次MotionEvent的y坐标
    private var menuWidth: Int = 0      //菜单宽度
    private var touchSlop: Int = 0      //超过此距离，认为手指正在滑动

    private var scrollable = true                      //是否允许滚动
    private var isScrolling = false                    //是否正在滚动
    private var hasConsumeDownEventByChild = true      //child是否消费了down事件

    private var onScrollListener: OnScrollListener? = null

    private val xVelocity: Int
        get() {
            velocityTracker!!.computeCurrentVelocity(1000)
            return velocityTracker!!.xVelocity.toInt()
        }

    /**
     * 侧滑菜单是否正在显示
     * @return
     */
    val isMenuShowing: Boolean
        get() = scrollX > 0

    interface OnScrollListener {

        fun onScrollStart()

        fun onScrollEnd()
    }

    init {
        init(context)
    }

    private fun init(context: Context) {
        scroller = Scroller(context)
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (childCount <= 1) {
            throw AssertionError("The child-count of SlipView must be greater than 1!")
        }

        val firstChild = getChildAt(0)
        val params = firstChild.layoutParams as MarginLayoutParams
        if (params.width != ViewGroup.LayoutParams.MATCH_PARENT) {
            throw AssertionError("The width of the first child in SlipView must be MATCH_PARENT!")
        }

        if (firstChild.visibility == View.GONE) {
            throw AssertionError("The visibility of the first child of SlipView can not be GONE!")
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)      //MeasureSpec.EXACTLY对应match_parent和100dp
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)    //MeasureSpec.AT_MOST对应wrap_content

//        if (widthMode == MeasureSpec.AT_MOST) {
//            //SlipView的宽度不应该是wrap_content，否则侧滑菜单可能无法隐藏
//            throw AssertionError("The width of SlipView can not be wrap_content!")
//        }

        var height = 0
        menuWidth = 0

        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) {
                continue
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            val lp = child.layoutParams as MarginLayoutParams
            if (i == 0) {
                val dstWidth = child.measuredWidth - lp.leftMargin - lp.rightMargin
                val childWidthSpec = MeasureSpec.makeMeasureSpec(dstWidth, MeasureSpec.EXACTLY)
                val childHeightSpec =
                    ViewGroup.getChildMeasureSpec(heightMeasureSpec, paddingTop + paddingBottom, lp.height)
                child.measure(childWidthSpec, childHeightSpec)
            }
            val childWidth = child.measuredWidth + lp.leftMargin + lp.rightMargin
            val childHeight = child.measuredHeight + lp.topMargin + lp.bottomMargin
            height = Math.max(height, childHeight)
            if (i > 0) {
                menuWidth += childWidth
            }
        }

        setMeasuredDimension(
            widthSize,
            if (heightMode == MeasureSpec.EXACTLY) heightSize else height + paddingTop + paddingBottom
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var left = paddingLeft
        val top = paddingTop
        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) {
                continue
            }

            val lp = child.layoutParams as MarginLayoutParams
            val lc = left + lp.leftMargin
            val tc = top + lp.topMargin
            val rc = lc + child.measuredWidth
            val bc = tc + child.measuredHeight
            child.layout(lc, tc, rc, bc)

            left += child.measuredWidth + lp.leftMargin + lp.rightMargin
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (!scrollable) {
            return super.onInterceptTouchEvent(event)
        }
        val x = event.x.toInt()
        val y = event.y.toInt()

        trackEvent(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = x
                lastY = y
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = x - lastX
                val deltaY = y - lastY
                LogUtil.e("intercept")
                if (!isScrolling && Math.abs(deltaX) > touchSlop && Math.abs(deltaX) > Math.abs(deltaY)) {
                    parent.requestDisallowInterceptTouchEvent(true)
                    return true
                }

                lastX = x
            }
        }

        return super.onInterceptTouchEvent(event)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        //如果禁止滚动，或者正在滚动，那么不要自己处理event事件了。
        if (!scrollable || isScrolling) {
            return super.onTouchEvent(event)
        }

        val x = event.x.toInt()
        val y = event.y.toInt()

        trackEvent(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

                /**
                 * 如果down事件走到了SlipView的onTouchEvent，说明SlipView的子View并没有消费down事件，其子View的clickable为false
                 * 而且没有setOnClickListener设置过点击监听。由于子View没有消费down事件，那么后续的move事件不会传递到子View，所以
                 * SlipView的onInterceptTouchEvent就不会再进行拦截了（down事件进来后，move事件就不会来了，所以根本不会走到我们代码
                 * 拦截那里）。
                 *
                 * 由于不会走到我们在onInterceptTouchEvent拦截代码那里，所以我们要在onTouchEvent进行拦截，拦截之后要求parent不要拦截
                 * 后续的事件，把后续的事件都发到SlipView。
                 */
                lastX = x
                lastY = y
                hasConsumeDownEventByChild = false
            }
            MotionEvent.ACTION_MOVE -> {
//                LogUtil.e("$x,$y")
                val deltaX = lastX - x
                val deltaY = lastY - y
//                Log.e("TAG","$deltaX,$deltaY")
                //如果子View没有消费down事件，则在此处继续拦截并请求SlipView的父类不要拦截，把事件统统传递到SlipView上来进行滑动。
                if (!hasConsumeDownEventByChild && Math.abs(deltaX) > touchSlop
                    && Math.abs(deltaX) > Math.abs(deltaY)
                ) {
                    parent.requestDisallowInterceptTouchEvent(true)
                }

                val targetScrollX = scrollX + deltaX
                LogUtil.e("$deltaX")
                LogUtil.e("$scrollX")
                LogUtil.e("$menuWidth")
                when {
                    targetScrollX <= 0 -> scrollTo(0, 0)
                    targetScrollX > menuWidth -> scrollTo(menuWidth, 0)
                    else -> scrollBy(deltaX, 0)
                }
                lastX = x
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
//                val dx: Int
//                hasConsumeDownEventByChild = true
//                val deltaX = lastX - x
//                val xVelocity = xVelocity          //通过加速度方向来判断手指抬起时正在朝哪边滚动
//                LogUtil.e("$xVelocity")
//                dx = when {
//                    (xVelocity <= 0 && deltaX < 0) -> menuWidth - scrollX
//                    (xVelocity > 0) -> -scrollX
//                    scrollX >= menuWidth / 2 -> menuWidth - scrollX
//                    else -> -scrollX
//                }
//                LogUtil.e("$dx")
//
//                if (onScrollListener != null) {
//                    isScrolling = true
//                    onScrollListener!!.onScrollStart()
//                }
//                scroller!!.startScroll(scrollX, 0, dx, 0)
//                invalidate()
//
//                clearTracker()
//                lastX = x
                if (Math.abs(xVelocity) > 1000) {
                    if (xVelocity < -1000) {
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
                clearTracker()
            }
        }

        return true
    }

    private fun trackEvent(event: MotionEvent) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain()
        }
        velocityTracker!!.addMovement(event)
    }

    private fun clearTracker() {
        velocityTracker!!.recycle()
        velocityTracker = null
    }

    override fun computeScroll() {
        if (scroller!!.computeScrollOffset()) {
            scrollTo(scroller!!.currX, scroller!!.currY)
            invalidate()
        } else if (onScrollListener != null && isScrolling) {
            isScrolling = false
            onScrollListener!!.onScrollEnd()
        }
    }

    /**
     * 关闭侧滑菜单（没有动画）
     *
     * @param duration 单位ms
     */
    fun closeMenu(duration: Int) {
        if (duration == 0) {
            scrollTo(0, 0)
        } else {
            val x = scrollX
            scroller!!.startScroll(x, 0, -x, 0, duration)
            invalidate()
        }
    }

    /**
     * 是否允许侧滑，默认为true
     * @param scrollable ture-允许 false-禁止
     */
    fun enableScroll(scrollable: Boolean) {
        this.scrollable = scrollable
    }

    fun setOnScrollListener(listener: OnScrollListener) {
        this.onScrollListener = listener
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return super.generateLayoutParams(attrs)
    }
}