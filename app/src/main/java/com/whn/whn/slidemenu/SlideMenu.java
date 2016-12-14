package com.whn.whn.slidemenu;

import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by whn on 2016/12/13.
 */

public class SlideMenu extends FrameLayout {

    private View menu;
    private View main;
    private int maxLeft;
    private ViewDragHelper viewDragHelper;

    FloatEvaluator floatEvaluator = new FloatEvaluator();//浮点计算对象
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();//颜色计算器

    //在构造方法中使用ViewDragHelper
    public SlideMenu(Context context) {
        this(context, null);
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        viewDragHelper = ViewDragHelper.create(this, mCallback);
    }

    /**
     * 将触摸事件交给ViewDragHelper处理
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    /**
     * viewDragHelper帮助我们判断是否应该拦截
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = viewDragHelper.shouldInterceptTouchEvent(ev);
        return result;
    }


    /**
     * 获取到两个子view
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        menu = getChildAt(0);
        main = getChildAt(1);
    }

    /**
     * 获取到子view的数据,可以移动的最大距离
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        maxLeft = (int) (getMeasuredWidth() * 0.6f);
    }


    /**
     * ViewDragHelper的回调
     */
    ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {

        //获取到view
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == main || child == menu;
        }

        //修正子view的水平位置
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //// TODO: 2016/12/13
            if (child == main) {
                left = clampLeft(left);
            }
            return left;
        }

        private int clampLeft(int left) {
            if (left >= maxLeft) {
                left = maxLeft;
            } else if (left <= 0) {
                left = 0;
            }
            return left;
        }

        //控制拖拽范围,无效
        @Override
        public int getViewHorizontalDragRange(View child) {
            return 1;
        }

        //当view位置改变
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            //滑动menu的时候,让menu固定不动,让main滑动,并伴随动画
            if (changedView == menu) {
                menu.layout(0, 0, menu.getMeasuredWidth(), menu.getMeasuredHeight());
                int newLeft = clampLeft(main.getLeft() + dx);
                main.layout(newLeft, 0, newLeft + main.getMeasuredWidth(), main.getMeasuredHeight());
            }

            //获取百分比(当前main左边距的位置占总屏幕的位置),
            float fraction = main.getLeft() * 1f / maxLeft;
            //执行动画
            executeAnim(fraction);

            //回调方法
            if (main.getLeft() == 0) {
                if (listener != null) {
                    listener.onClose();
                }
            } else if (main.getLeft() == maxLeft) {
                if (listener != null) {
                    listener.onOpen();
                }
            }

            if (listener != null) {
                listener.onDragint(fraction);
            }


        }

        //手指抬起
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            //根据当前位置,选择性关闭或者打开,动画效果
            if (main.getLeft() > maxLeft / 2) {
                //应该打开
                openMenu();
            } else {
                //应该关闭
                closeMenu();
            }
        }
    };

    /**
     * 执行动画(打开的过程fraction 0-1)
     */
    private void executeAnim(float fraction) {
        //main 缩小
        Float scale = floatEvaluator.evaluate(fraction, 1.0f, 0.8f);
        main.setScaleX(scale);
        main.setScaleY(scale);

        //menu 放大
        Float scale2 = floatEvaluator.evaluate(fraction, 0.3f, 1f);
        menu.setScaleX(scale2);
        menu.setScaleY(scale2);

        //menu 平移动画
        Float translation = floatEvaluator.evaluate(fraction, -menu.getMeasuredWidth() / 2, 0);
        menu.setTranslationX(translation);

        //给整个slidingmenu背景图片设置色遮罩
        int color = (int) argbEvaluator.evaluate(fraction, Color.BLACK, Color.TRANSPARENT);
        getBackground().setColorFilter(color, PorterDuff.Mode.SRC_OVER);
    }

    /**
     * 关闭menu
     */
    private void closeMenu() {
        viewDragHelper.smoothSlideViewTo(main, 0, 0);
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }


    /**
     * 打开mennu
     */
    private void openMenu() {
        viewDragHelper.smoothSlideViewTo(main, maxLeft, 0);
        //view兼容,调用方法computeScroll()
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }


    @Override
    public void computeScroll() {
        super.computeScroll();
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
        }
    }


    /**
     * 实现接口回调
     */

    private onSlideChangeListener listener;

    public void setonSlideChangeListener(onSlideChangeListener listener) {
        this.listener = listener;

    }

    public interface onSlideChangeListener {
        void onOpen();

        void onClose();

        void onDragint(float fraction);
    }


}
