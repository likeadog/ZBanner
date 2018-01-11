package com.zhuang.zbannerlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhuang on 2017/12/13.
 */

public class ZBanner extends FrameLayout {
    private final static int INDICATOR_GRAVITY_BOTTOM_LEFT = 0;
    private final static int INDICATOR_GRAVITY_BOTTOM_CENTER = 1;
    private final static int INDICATOR_GRAVITY_BOTTOM_RIGHT = 2;
    private static final int DURATION_ANIMAL = 1000; // ms
    private static final int DURATION_DISPLAY = 2000; // ms

    private ZBannerRaw zBannerRaw;
    private Indicator indicator;

    /**
     * xml中可配置的变量
     */
    private int mPageGap;//页面之间的间隔
    private float mWidthFactor = 1f;//页面宽度倍数
    private int mOffscreenPageLimit = 2;//缓存页面
    private Drawable indicatorSelectIcon;//指示器被选中时的图标
    private Drawable indicatorUnSelectIcon;//指示器未被选中时的图标
    private int indicatorGravity = INDICATOR_GRAVITY_BOTTOM_CENTER;//指示器的位置
    private int indicatorIconSize = 12;//指示器的图标大小
    private boolean showIndicator = true;//是否显示指示器
    private int indicatorMargin = 5;//dp
    private int mIndicatorGap = 5;//指示器图标之间的间隔
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    public ZBanner(@NonNull Context context) {
        super(context);
        initView(null, 0);
    }

    public ZBanner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(attrs, 0);
    }

    public ZBanner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs, defStyleAttr);
    }

    void initView(AttributeSet attrs, int defStyle) {

        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ZBanner, defStyle, 0);
        mPageGap = a.getDimensionPixelSize(R.styleable.ZBanner_pageGap, mPageGap);
        mWidthFactor = a.getFloat(R.styleable.ZBanner_widthFactor, mWidthFactor);
        mOffscreenPageLimit = a.getInt(R.styleable.ZBanner_offscreenPageLimit, mOffscreenPageLimit);
        indicatorSelectIcon = a.getDrawable(R.styleable.ZBanner_indicatorSelectIcon);
        indicatorUnSelectIcon = a.getDrawable(R.styleable.ZBanner_indicatorUnSelectIcon);
        indicatorGravity = a.getInt(R.styleable.ZBanner_indicatorGravity, indicatorGravity);
        indicatorIconSize = a.getDimensionPixelSize(R.styleable.ZBanner_indicatorIconSize, dpToPx(indicatorIconSize));
        showIndicator = a.getBoolean(R.styleable.ZBanner_showIndicator, showIndicator);
        indicatorMargin = a.getDimensionPixelSize(R.styleable.ZBanner_indicatorMargin, dpToPx(indicatorMargin));
        mIndicatorGap = a.getDimensionPixelSize(R.styleable.ZBanner_indicatorGap, dpToPx(mIndicatorGap));
        a.recycle();

        if (mWidthFactor < 0.5f || mWidthFactor > 1f) {
            throw new RuntimeException("mWidthFactor的区间只能是[0.5f,1f]");
        }
        if (mOffscreenPageLimit < 1) {
            throw new RuntimeException("mOffscreenPageLimit必须>=1");
        }
        if (indicatorSelectIcon == null) {
            indicatorSelectIcon = getResources().getDrawable(R.drawable.ic_indicator_select);
        }
        if (indicatorUnSelectIcon == null) {
            indicatorUnSelectIcon = getResources().getDrawable(R.drawable.ic_indicator_unselect);
        }

        initBanner();
        initIndicator();
    }

    void initIndicator() {
        if (!showIndicator) return;
        indicator = new Indicator.Builder()
                .indicatorSelectIcon(indicatorSelectIcon)
                .indicatorUnSelectIcon(indicatorUnSelectIcon)
                .indicatorIconSize(indicatorIconSize)
                .indicatorGap(mIndicatorGap)
                .build(getContext());

        LayoutParams params = generateDefaultLayoutParams();
        switch (indicatorGravity) {
            case (INDICATOR_GRAVITY_BOTTOM_LEFT):
                params.gravity = Gravity.BOTTOM;
                break;
            case (INDICATOR_GRAVITY_BOTTOM_CENTER):
                params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                break;
            case (INDICATOR_GRAVITY_BOTTOM_RIGHT):
                params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                break;
        }
        params.setMargins(indicatorMargin, indicatorMargin, indicatorMargin, indicatorMargin);
        indicator.setLayoutParams(params);
        addView(indicator);
        zBannerRaw.setIndicator(indicator);
    }

    void initBanner() {
        zBannerRaw = new ZBannerRaw.Builder()
                .pageGap(mPageGap)
                .widthFactor(mWidthFactor)
                .offscreenPageLimit(mOffscreenPageLimit)
                .build(getContext());
        zBannerRaw.setId(generateBannerViewId());
        addView(zBannerRaw);
    }

    private int generateBannerViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public void setAdapter(ZBannerAdapter adapter) {
        zBannerRaw.setAdapter(adapter);
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public void setPageTransformer(ZBannerPageTransformer pageTransformer) {
        zBannerRaw.setPageTransformer(true, pageTransformer);
    }

    /**
     * @param displayDuration 页面展示的时间 ms
     * @param animalDuration  页面滑动的时间 ms
     */
    public void star(int displayDuration, int animalDuration) {
        zBannerRaw.setDisplayDuration(displayDuration);
        zBannerRaw.setAnimalDuration(animalDuration);
        zBannerRaw.star();
    }

    public void star() {
        zBannerRaw.setDisplayDuration(DURATION_DISPLAY);
        zBannerRaw.setAnimalDuration(DURATION_ANIMAL);
        zBannerRaw.star();
    }

    public void stop() {
        zBannerRaw.stop();
    }

    /**
     * 提供一个页面切换时的接口，可以自定义转换动画
     */
    public interface ZBannerPageTransformer {
        /**
         * @param page     在切换的页面
         * @param position 正在切换的页面相对于当前显示在正中间的页面的位置
         *                 0表示当前页，1表示右侧一页，-1表示左侧一页。
         *                 注意，这几个都是临界值，position在页面切换过程中会一直改变
         *                 例如左移的话，当前页的position会从0减少到-1，切换完成后就变成左侧一页了。
         */
        void transformPage(View page, float position);
    }

}
