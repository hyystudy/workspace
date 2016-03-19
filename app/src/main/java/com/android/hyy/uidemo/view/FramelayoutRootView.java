package com.android.hyy.uidemo.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;

/**
 * Created by hyy on 2016/3/18.
 */
public class FramelayoutRootView extends InsettableFrameLayout {
    private  int statusBarHeight = 0;

    public FramelayoutRootView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {

        /**
         * 因为我们需要在程序运行过程中自动隐藏状态栏, 但隐藏状态栏的时候不希望界面发生抖动.
         * 同时, 对于虚拟键盘, 我们又要支持. 因此, 当设置第一个非0的 top 值时, 我们将他记录下来,
         * 作为 statusbar 的高度, 并且再今后都不改变.
         */
        if(statusBarHeight < insets.top) {
            statusBarHeight = insets.top;
        }

        /**
         * if status bar is hidden.
         */
        if(insets.top < statusBarHeight) {
            insets.top = statusBarHeight;
        }

        setInsets(insets);
        return true; // I'll take it from here
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
