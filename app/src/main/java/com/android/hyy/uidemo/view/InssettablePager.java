package com.android.hyy.uidemo.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by hyy on 2016/3/18.
 */
public class InssettablePager extends ViewPager implements Insettable{
    protected Rect mInsets = new Rect();
    public InssettablePager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public void setChildInsets(View child, Rect newInsets, Rect oldInsets) {
        if (child instanceof Insettable) {
            ((Insettable) child).setInsets(newInsets);
        }
    }

    @Override
    public void setInsets(Rect insets) {
        final int n = getChildCount();
        for (int i = 0; i < n; i++) {
            final View child = getChildAt(i);
            setChildInsets(child, insets, mInsets);
        }
        mInsets.set(insets);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setInsets(mInsets);
    }
}
