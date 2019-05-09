package com.cgtn.minor.liveminority.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.widget.TextView;
import com.cgtn.minor.liveminority.R;

import java.lang.reflect.Field;

/**
 * created by yf on 2019/5/7.
 */
public class SuperEditText extends AppCompatEditText {
    /*
     * 定义属性变量
     * */
    private Paint mPaint; // 画笔

    private int cursor; // 光标

    // 分割线变量
    private int lineColor_click,lineColor_unclick;// 点击时 & 未点击颜色
    private int color;
    private int linePosition;

    public SuperEditText(Context context) {
        super(context);

    }

    public SuperEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SuperEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 步骤1：初始化属性
     */

    private void init(Context context, AttributeSet attrs) {

        // 获取控件资源
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SuperEditText);

        // setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom)介绍
        // 作用：在EditText上、下、左、右设置图标（相当于android:drawableLeft=""  android:drawableRight=""）
        // 备注：传入的Drawable对象必须已经setBounds(x,y,width,height)，即必须设置过初始位置、宽和高等信息
        // x:组件在容器X轴上的起点 y:组件在容器Y轴上的起点 width:组件的长度 height:组件的高度
        // 若不想在某个地方显示，则设置为null

        // 另外一个相似的方法：setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom)
        // 作用：在EditText上、下、左、右设置图标
        // 与setCompoundDrawables的区别：setCompoundDrawablesWithIntrinsicBounds（）传入的Drawable的宽高=固有宽高（自动通过getIntrinsicWidth（）& getIntrinsicHeight（）获取）
        // 不需要设置setBounds(x,y,width,height)

        /**
         * 初始化光标（颜色 & 粗细）
         */
        // 原理：通过 反射机制 动态设置光标
        // 1. 获取资源ID
        cursor = typedArray.getResourceId(R.styleable.SuperEditText_cursor, R.drawable.login_bg);
        try {

            // 2. 通过反射 获取光标属性
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            // 3. 传入资源ID
            f.set(this, cursor);

        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * 初始化分割线（颜色、粗细、位置）
         */
        // 1. 设置画笔
        mPaint = new Paint();
        mPaint.setStrokeWidth(2.0f); // 分割线粗细

        // 2. 设置分割线颜色（使用十六进制代码，如#333、#8e8e8e）
        int lineColorClick_default = context.getResources().getColor(R.color.blue); // 默认 = 蓝色#1296db
        int lineColorunClick_default = context.getResources().getColor(R.color.yellow); // 默认 = 灰色#9b9b9b
        lineColor_click = typedArray.getColor(R.styleable.SuperEditText_lineColor_click, lineColorClick_default);
        lineColor_unclick = typedArray.getColor(R.styleable.SuperEditText_lineColor_unclick, lineColorunClick_default);
        color = lineColor_unclick;

        mPaint.setColor(lineColor_unclick); // 分割线默认颜色 = 灰色
        setTextColor(color); // 字体默认颜色 = 灰色

        // 3. 分割线位置
        linePosition = typedArray.getInteger(R.styleable.SuperEditText_linePosition, 1);
        // 消除自带下划线
        setBackground(null);


    }

    /**
     * 复写EditText本身的方法：onTextChanged（）
     * 调用时刻：当输入框内容变化时
     */
    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        setDeleteIconVisible(hasFocus() && text.length() > 0,hasFocus());
        // hasFocus()返回是否获得EditTEXT的焦点，即是否选中
        // setDeleteIconVisible（） = 根据传入的是否选中 & 是否有输入来判断是否显示删除图标->>关注1
    }

    /**
     * 复写EditText本身的方法：onFocusChanged（）
     * 调用时刻：焦点发生变化时
     */
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        setDeleteIconVisible(focused && length() > 0,focused);
        // focused = 是否获得焦点
        // 同样根据setDeleteIconVisible（）判断是否要显示删除图标->>关注1
    }

//
//    /**
//     * 作用：对删除图标区域设置为"点击 即 清空搜索框内容"
//     * 原理：当手指抬起的位置在删除图标的区域，即视为点击了删除图标 = 清空搜索框内容
//     */
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//        // 原理：当手指抬起的位置在删除图标的区域，即视为点击了删除图标 = 清空搜索框内容
//        switch (event.getAction()) {
//            // 判断动作 = 手指抬起时
//            case MotionEvent.ACTION_UP:
//                Drawable drawable =  ic_delete;
//
//                if (drawable != null && event.getX() <= (getWidth() - getPaddingRight())
//                        && event.getX() >= (getWidth() - getPaddingRight() - drawable.getBounds().width())) {
//
//                    // 判断条件说明
//                    // event.getX() ：抬起时的位置坐标
//                    // getWidth()：控件的宽度
//                    // getPaddingRight():删除图标图标右边缘至EditText控件右边缘的距离
//                    // 即：getWidth() - getPaddingRight() = 删除图标的右边缘坐标 = X1
//                    // getWidth() - getPaddingRight() - drawable.getBounds().width() = 删除图标左边缘的坐标 = X2
//                    // 所以X1与X2之间的区域 = 删除图标的区域
//                    // 当手指抬起的位置在删除图标的区域（X2=<event.getX() <=X1），即视为点击了删除图标 = 清空搜索框内容
//                    setText("");
//
//                }
//                break;
//        }
//        return super.onTouchEvent(event);
//    }

    /**
     * 关注1
     * 作用：判断是否显示删除图标 & 设置分割线颜色
     */
    private void setDeleteIconVisible(boolean deleteVisible,boolean leftVisible) {
        color = leftVisible ? lineColor_click : lineColor_unclick;
        setTextColor(color);
        invalidate();
    }

    /**
     * 作用：绘制分割线
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(color);
        setTextColor(color);
        // 绘制分割线
        // 需要考虑：当输入长度超过输入框时，所画的线需要跟随着延伸
        // 解决方案：线的长度 = 控件长度 + 延伸后的长度
        int x=this.getScrollX(); // 获取延伸后的长度
        int w=this.getMeasuredWidth(); // 获取控件长度

        // 传入参数时，线的长度 = 控件长度 + 延伸后的长度
        canvas.drawLine(0, this.getMeasuredHeight()- linePosition, w+x,
                this.getMeasuredHeight() - linePosition, mPaint);

    }
}
