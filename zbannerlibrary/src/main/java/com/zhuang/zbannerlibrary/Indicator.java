package com.zhuang.zbannerlibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuang on 2017/12/12.
 */

public class Indicator extends View {

    private List<ItemInfo> mItems = new ArrayList<>();
    private int mOldPosition;
    private int mCount;

    private Drawable mIndicatorSelectIcon;//指示器被选中时的图标
    private Drawable mIndicatorUnSelectIcon;//指示器未被选中时的图标
    private int mIndicatorIconSize;//指示器的图标大小
    private int mIndicatorGap;//指示器图标之间的间隔

    public Indicator(Context context, Builder builder) {
        super(context);
        mIndicatorSelectIcon = builder.indicatorSelectIcon;
        mIndicatorUnSelectIcon = builder.indicatorUnSelectIcon;
        mIndicatorIconSize = builder.indicatorIconSize;
        mIndicatorGap = builder.mIndicatorGap;
        initView();
    }

    public Indicator(Context context) {
        super(context);
        initView();
    }

    public Indicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public Indicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    void initView() {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = mCount * mIndicatorIconSize + (mCount-1)*mIndicatorGap;
        int height = mIndicatorIconSize;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int left = 0;
        for (int i = 0; i < mItems.size(); i++) {
            ItemInfo itemInfo = mItems.get(i);
            if (itemInfo.isSelect) {
                mIndicatorSelectIcon.setBounds(left, 0, left + mIndicatorIconSize, mIndicatorIconSize);
                mIndicatorSelectIcon.draw(canvas);
            } else {
                mIndicatorUnSelectIcon.setBounds(left, 0, left + mIndicatorIconSize, mIndicatorIconSize);
                mIndicatorUnSelectIcon.draw(canvas);
            }
            left += mIndicatorIconSize + mIndicatorGap;
        }
    }

    public void setCount(int count) {
        this.mCount = count;
        mOldPosition = 0;
        mItems.clear();
        for (int i = 0; i < mCount; i++) {
            ItemInfo itemInfo = new ItemInfo();
            if (i == 0) {
                itemInfo.isSelect = true;
            }
            mItems.add(itemInfo);
        }
        invalidate();
        requestLayout();
    }

    public void setSelectPosition(int position) {
        if (mOldPosition == position) return;
        mItems.get(position).isSelect = true;
        mItems.get(mOldPosition).isSelect = false;
        mOldPosition = position;
        invalidate();
    }

    private class ItemInfo {
        boolean isSelect;
    }

    public static class Builder {
        Drawable indicatorSelectIcon;//指示器被选中时的图标
        Drawable indicatorUnSelectIcon;//指示器未被选中时的图标
        int indicatorIconSize;//指示器的图标大小
        int mIndicatorGap;//指示器图标之间的间隔

        public Indicator build(Context context) {
            Indicator indicator = new Indicator(context, this);
            return indicator;
        }

        public Builder indicatorGap(int indicatorGap) {
            this.mIndicatorGap = indicatorGap;
            return this;
        }

        public Builder indicatorSelectIcon(Drawable indicatorSelectIcon) {
            this.indicatorSelectIcon = indicatorSelectIcon;
            return this;
        }

        public Builder indicatorUnSelectIcon(Drawable indicatorUnSelectIcon) {
            this.indicatorUnSelectIcon = indicatorUnSelectIcon;
            return this;
        }

        public Builder indicatorIconSize(int indicatorIconSize) {
            this.indicatorIconSize = indicatorIconSize;
            return this;
        }
    }
}
