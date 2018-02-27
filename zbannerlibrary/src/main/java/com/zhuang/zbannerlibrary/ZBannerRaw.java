package com.zhuang.zbannerlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhuang on 2017/11/23.
 */

class ZBannerRaw extends ViewGroup {
    private static final int TYPE_ONE_PAGE = 1;//只有一页的情况
    private static final int TYPE_TWO_PAGE = 2;//只有两页的情况
    private static final int TYPE_OTHER_PAGE = 3;//有三页以上的情况

    private ViewDragHelper mDragger;
    private ViewDragHelperCallback mCallback;
    private final ArrayList<ItemInfo> mItems = new ArrayList();
    private ZBannerAdapter mAdapter;
    private int mCurPosition;//当前页位置
    private boolean mFirstLayout = true;
    private int mType;
    private int N;
    private Indicator mIndicator;
    private PagerObserver mObserver;

    private int mPageGap;//页面之间的间隔
    private float mWidthFactor;//页面宽度倍数
    private int mOffscreenPageLimit;//缓存页面

    private int mWidth;
    private int mPageWidth;

    int mCurPageLeft;//静止状态时，当前页的left位置
    int mNextPageLft;//静止状态时，右侧页面的left位置
    int mPrePageLeft;//静止状态时，左侧页面的left位置

    private ZBanner.ZBannerPageTransformer mPageTransformer;
    private static final int DRAW_ORDER_DEFAULT = 0;
    private static final int DRAW_ORDER_FORWARD = 1;
    private static final int DRAW_ORDER_REVERSE = 2;
    private int mDrawingOrder;
    private static final ViewPositionComparator sPositionComparator = new ViewPositionComparator();
    private ArrayList<View> mDrawingOrderedChildren;
    static final int[] LAYOUT_ATTRS = new int[]{android.R.attr.layout_gravity};

    private Timer mTimer;
    private long mRecentTouchTime;
    private boolean mAutoPlay;
    private int mAnimalDuration = 2000;//ms
    private int mDisplayDuration = 2000;//ms
    private int mDuration = mAnimalDuration + mDisplayDuration;//ms

    private ZBanner.OnPageChangeLister mOnPageChangeLister;

     ZBannerRaw(Context context, Builder builder) {
        super(context);
        mPageGap = builder.pageGap;
        mWidthFactor = builder.widthFactor;
        mOffscreenPageLimit = builder.offscreenPageLimit;
        initView();
    }

     ZBannerRaw(Context context) {
        super(context);
        initView();
    }

     ZBannerRaw(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

     ZBannerRaw(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mCallback = new ViewDragHelperCallback();
        mDragger = ViewDragHelper.create(this, 1.0f, mCallback);
    }

    public void setIndicator(Indicator mIndicator) {
        this.mIndicator = mIndicator;
        this.mIndicator.setCount(N);
    }

    public void setAdapter(ZBannerAdapter adapter) {
        mAdapter = adapter;
        if (mAdapter != null) {
            if (mObserver == null) {
                mObserver = new PagerObserver();
            }
            mAdapter.setViewPagerObserver(mObserver);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void dataSetChanged() {
        for (int i = 0; i < mItems.size(); i++) {
            mAdapter.destroyItem(mItems.get(i).fragment);
        }
        mAdapter.finishUpdate();
        mItems.clear();
        mCurPosition = 0;
        mFirstLayout = true;
        N = mAdapter.getCount();
        if (mIndicator != null) {
            mIndicator.setCount(N);
        }

        switch (N) {
            case 1:
                mType = TYPE_ONE_PAGE;
                mWidthFactor = 1f;//只有一个页面时，mWidthFactor无效，恒为1f
                break;
            case 2:
                mType = TYPE_TWO_PAGE;
                mWidthFactor = 1f;//只有两个页面时，mWidthFactor无效,恒为1f
                break;
            default:
                mType = TYPE_OTHER_PAGE;
                break;
        }
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getClientWidth();
        mPageWidth = (int) (mWidth * mWidthFactor);

        if (mFirstLayout) {
            mFirstLayout = false;
            mCurPageLeft = (int) (mWidth * (1 - mWidthFactor) / 2);
            mPrePageLeft = mCurPageLeft - mPageWidth - mPageGap;
            mNextPageLft = mCurPageLeft + mPageWidth + mPageGap;
            switch (mType) {
                case TYPE_ONE_PAGE:
                case TYPE_TWO_PAGE:
                    addItem();
                    break;
                case TYPE_OTHER_PAGE:
                    popuateItem();
                default:
                    break;
            }

            if (mOnPageChangeLister != null) {
                mOnPageChangeLister.change(mCurPosition);
            }
        }

        int childWidthSize = mPageWidth;
        int childHeightSize = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        int size = getChildCount();
        for (int i = 0; i < size; ++i) {
            final View child = getChildAt(i);
            final int widthSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
            final int heightSpec = MeasureSpec.makeMeasureSpec(childHeightSize, MeasureSpec.EXACTLY);
            child.measure(widthSpec, heightSpec);
        }
    }

    private int getClientWidth() {
        return getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    void popuateItem() {
        popuateItem(mCurPageLeft);
    }

    void popuateItem(int curViewLeft) {
        if (N <= 0) return;
        int curIndex = -1;
        ItemInfo curItem = null;
        for (curIndex = 0; curIndex < mItems.size(); curIndex++) {
            final ItemInfo ii = mItems.get(curIndex);
            if (ii.position == mCurPosition) {
                curItem = ii;
                break;
            }
        }

        //新增当前页
        if (curItem == null) {
            curItem = new ItemInfo();
            curItem.position = mCurPosition;
            curItem.prePosition = curItem.position == 0 ? N - 1 : curItem.position - 1;
            curItem.nextPosition = curItem.position == N - 1 ? 0 : curItem.position + 1;
            curItem.fragment = mAdapter.instantiateItem(this, mCurPosition);
            curItem.left = curViewLeft;
            mItems.add(curIndex, curItem);
        } else {
            curItem.left = curViewLeft;
        }

        if (curItem != null) {
            int rightCount;//左侧总共有多少个页面
            int leftCount;//右侧总共有多少个页面
            //页面总数为双数
            if (N % 2 == 0) {
                rightCount = N / 2;
                leftCount = rightCount - 1;
            }
            //页面总数为单数
            else {
                rightCount = leftCount = (N - 1) / 2;
            }

            rightCount = Math.min(rightCount, mOffscreenPageLimit);
            leftCount = Math.min(leftCount, mOffscreenPageLimit);
            int leftNum = Math.max(leftCount, curIndex);
            int rightNum = Math.max(rightCount, mItems.size() - curIndex - 1);

            boolean needRequestLayoutLeft;
            boolean needRequestLayoutRight;
            if (curIndex == 0) {
                needRequestLayoutRight = populateRight(curItem, curIndex, rightCount, rightNum);
                needRequestLayoutLeft = populateLeft(curItem, curIndex, leftCount, leftNum);
            } else {
                needRequestLayoutLeft = populateLeft(curItem, curIndex, leftCount, leftNum);
                needRequestLayoutRight = populateRight(curItem, curIndex, rightCount, rightNum);
            }
            if (needRequestLayoutLeft || needRequestLayoutRight) {
                requestLayout();
            }
        }
        mAdapter.finishUpdate();
        sortChildDrawingOrder();
    }

    private boolean populateRight(ItemInfo curItem, int curIndex, int rightCount, int rightNum) {
        boolean needRequestLayout = false;
        int itemIndex = curIndex + 1;
        ItemInfo ii = itemIndex < mItems.size() ? mItems.get(itemIndex) : null;
        for (int i = 1; i <= rightNum; i++) {
            int offset = i * (mPageWidth + mPageGap);
            if (ii != null) {
                if (rightCount <= 0) {
                    mItems.remove(ii);
                    if (N > 2 * mOffscreenPageLimit + 1) {
                        mAdapter.destroyItem(ii.fragment);
                    } else {
                        needRequestLayout = true;
                    }
                } else {
                    itemIndex++;
                    ii.left = curItem.left + offset;
                }
                ii = itemIndex < mItems.size() ? mItems.get(itemIndex) : null;
            } else {
                ItemInfo rightii = new ItemInfo();
                int position = mCurPosition + i;
                if (position >= N) {
                    position -= N;
                }
                rightii.position = position;
                rightii.prePosition = rightii.position == 0 ? N - 1 : rightii.position - 1;
                rightii.nextPosition = rightii.position == N - 1 ? 0 : rightii.position + 1;
                rightii.left = curItem.left + offset;
                rightii.fragment = mAdapter.instantiateItem(this, rightii.position);
                mItems.add(rightii);
            }
            rightCount--;
        }
        return needRequestLayout;
    }

    private boolean populateLeft(ItemInfo curItem, int curIndex, int leftCount, int leftNum) {
        boolean needRequestLayout = false;
        int itemIndex = curIndex - 1;
        ItemInfo ii = itemIndex >= 0 ? mItems.get(itemIndex) : null;
        for (int i = 1; i <= leftNum; i++) {
            int offset = i * (mPageWidth + mPageGap);
            if (ii != null) {
                if (leftCount <= 0) {
                    mItems.remove(ii);
                    if (N > 2 * mOffscreenPageLimit + 1) {
                        mAdapter.destroyItem(ii.fragment);
                    } else {
                        needRequestLayout = true;
                    }
                } else {
                    ii.left = curItem.left - offset;
                }
                itemIndex--;
                ii = itemIndex >= 0 ? mItems.get(itemIndex) : null;
            } else {
                ItemInfo leftii = new ItemInfo();
                int position = mCurPosition - i;
                if (position < 0) {
                    position += N;
                }
                leftii.position = position;
                leftii.prePosition = leftii.position == 0 ? N - 1 : leftii.position - 1;
                leftii.nextPosition = leftii.position == N - 1 ? 0 : leftii.position + 1;
                leftii.left = curItem.left - offset;
                leftii.fragment = mAdapter.instantiateItem(this, leftii.position);
                mItems.add(0, leftii);
            }
            leftCount--;
        }
        return needRequestLayout;
    }

    private void addItem() {
        int size = mItems.size();
        if (size == 0) {
            for (int i = 0; i < N; i++) {
                ItemInfo ii = new ItemInfo();
                ii.position = i;
                ii.prePosition = Math.abs(i - 1);
                ii.nextPosition = Math.abs(i - 1);
                ii.fragment = mAdapter.instantiateItem(this, ii.position);
                ii.left = i * (mWidth + mPageGap);
                mItems.add(ii);
            }
            mAdapter.finishUpdate();
            sortChildDrawingOrder();
        }
    }

    ItemInfo infoForChild(View child) {
        for (int i = 0; i < mItems.size(); i++) {
            ItemInfo ii = mItems.get(i);
            if (mAdapter.isViewFromObject(child, ii.fragment)) {
                return ii;
            }
        }
        return null;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            ItemInfo ii = infoForChild(childView);
            childView.layout(ii.left, 0,
                    ii.left + childView.getMeasuredWidth(), childView.getMeasuredHeight());
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mDragger.shouldInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragger.processTouchEvent(event);
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mAutoPlay = false;
                mDragger.setAutoPlaying(false);
                break;
            case MotionEvent.ACTION_MOVE:
                //防止与父组件滑动事件冲突
                ViewParent parent = getParent();
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                mAutoPlay = true;
                mDragger.setAutoPlaying(true);
                mRecentTouchTime = System.currentTimeMillis();
                break;
        }

        return true;
    }

    private class ViewDragHelperCallback extends ViewDragHelper.Callback {
        View anotherView;//只有两个页面时需要用到
        int anotherViewOldLeft;//只有两个页面时需要用到

        ItemInfo firstItem;
        View firstView;
        ItemInfo lastItem;
        View lastView;

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            switch (mType) {
                case TYPE_ONE_PAGE:
                    return false;
                case TYPE_TWO_PAGE:
                    for (int i = 0; i < getChildCount(); i++) {
                        View view = getChildAt(i);
                        if (view != child) {
                            anotherView = view;
                            anotherViewOldLeft = view.getLeft();
                        }
                    }
                    return true;
                case TYPE_OTHER_PAGE:
                    setFirstAndLast();
                    return true;
            }
            return true;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return 1;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            switch (mType) {
                case TYPE_TWO_PAGE:
                    if (left < 0) {
                        anotherView.layout(changedView.getRight() + mPageGap, 0,
                                changedView.getRight() + anotherView.getMeasuredWidth() + mPageGap, anotherView.getBottom() + dy);
                    } else {
                        anotherView.layout(left - anotherView.getMeasuredWidth() - mPageGap, 0,
                                left - mPageGap, anotherView.getBottom());
                    }

                    if (anotherViewOldLeft * anotherView.getLeft() < 0) {
                        anotherViewOldLeft = anotherView.getLeft();
                        ItemInfo ii = mItems.get(0);
                        mItems.remove(0);
                        mItems.add(ii);
                        sortChildDrawingOrder();
                    }

                    for (int i = 0; i < getChildCount(); i++) {
                        View view = getChildAt(i);
                        if (mPageTransformer != null) {
                            float position = (float) view.getLeft() / mPageWidth;
                            mPageTransformer.transformPage(view, position);
                        }
                    }
                    break;
                case TYPE_OTHER_PAGE:
                    triggerPositionChange();
                    for (int i = 0; i < getChildCount(); i++) {
                        View view = getChildAt(i);
                        if (view != changedView) {
                            ViewCompat.offsetLeftAndRight(view, dx);
                        }
                        if (mPageTransformer != null) {
                            float position = (float) (view.getLeft() - mCurPageLeft) / mPageWidth;
                            mPageTransformer.transformPage(view, position);
                        }
                    }
                    break;
            }
        }

        private void triggerPositionChange() {
            if (firstView.getRight() > mWidth / 2) {
                mCurPosition = firstItem.position;
                popuateItem(firstView.getLeft());
                setFirstAndLast();
            } else if (lastView.getLeft() < mWidth / 2) {
                mCurPosition = lastItem.position;
                popuateItem(lastView.getLeft());
                setFirstAndLast();
            }
        }

        private void setFirstAndLast() {
            firstItem = mItems.get(0);
            firstView = firstItem.fragment.getView();
            lastItem = mItems.get(mItems.size() - 1);
            lastView = lastItem.fragment.getView();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            ItemInfo ii = infoForChild(releasedChild);
            if (ii == null) return;
            int left = releasedChild.getLeft();
            if (left < mCurPageLeft - mPageWidth / 2) {
                if (xvel <= 0) {
                    smoothMoveLeft(ii);
                } else {
                    smoothMoveBack(ii);
                }
            } else if (left > mCurPageLeft - mPageWidth / 2 && left < mCurPageLeft) {
                if (xvel >= 0) {
                    smoothMoveBack(ii);
                } else if (xvel < 0) {
                    smoothMoveLeft(ii);
                }
            } else if (left > mWidth / 2) {
                if (xvel >= 0) {
                    smoothMoveRight(ii);
                } else if (xvel < 0) {
                    smoothMoveBack(ii);
                }
            } else if (left > mCurPageLeft && left < mWidth / 2) {
                if (xvel <= 0) {
                    smoothMoveBack(ii);
                } else {
                    smoothMoveRight(ii);
                }
            } else {
                smoothMoveBack(ii);
            }
            if (mIndicator != null) {
                mIndicator.setSelectPosition(mCurPosition);
            }
            if (mOnPageChangeLister != null) {
                mOnPageChangeLister.change(mCurPosition);
            }
            ViewCompat.postInvalidateOnAnimation(ZBannerRaw.this);
        }

        //页面向左移动一页
        void smoothMoveLeft(ItemInfo ii) {
            mDragger.settleCapturedViewAt(mPrePageLeft, 0);
            mCurPosition = ii.nextPosition;
        }

        //页面向右移动一页
        void smoothMoveRight(ItemInfo ii) {
            mDragger.settleCapturedViewAt(mNextPageLft, 0);
            mCurPosition = ii.prePosition;
        }

        //页面恢复到原来的位置
        void smoothMoveBack(ItemInfo ii) {
            mDragger.settleCapturedViewAt(mCurPageLeft, 0);
            mCurPosition = ii.position;
        }
    }

    //跳转到指定页面
    public void setCurrentItem(int position) {
        mCallback.setFirstAndLast();
        mCurPosition = position;
        View view = getViewFromPosition(position);
        mDragger.smoothSlideViewTo(view, mCurPageLeft, 0);
        ViewCompat.postInvalidateOnAnimation(ZBannerRaw.this);
        if (mIndicator != null) {
            mIndicator.setSelectPosition(mCurPosition);
        }
        if (mOnPageChangeLister != null) {
            mOnPageChangeLister.change(position);
        }
    }

    /**
     * 获取指定位置的view
     *
     * @param position
     * @return
     */
    private View getViewFromPosition(int position) {
        final int size = mItems.size();
        for (int i = 0; i < size; i++) {
            if (mItems.get(i).position == position) {
                return mItems.get(i).fragment.getView();
            }
        }
        return null;
    }

    @Override
    public void computeScroll() {
        if (mDragger.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(ZBannerRaw.this);
        }
    }

    public void setPageTransformer(boolean reverseDrawingOrder, ZBanner.ZBannerPageTransformer pageTransformer) {
        mDrawingOrder = reverseDrawingOrder ? DRAW_ORDER_REVERSE : DRAW_ORDER_FORWARD;
        setChildrenDrawingOrderEnabled(true);
        this.mPageTransformer = pageTransformer;
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        final int layerType = mPageTransformer == null ? View.LAYER_TYPE_NONE : View.LAYER_TYPE_HARDWARE;
        child.setLayerType(layerType, null);
    }

    /**
     * 得到view在mItems中的位置
     */
    private int viewPosition(View child) {
        final int size = mItems.size();
        for (int i = 0; i < size; i++) {
            if (mItems.get(i).fragment.getView() == child) {
                return i;
            }
        }
        return 0;
    }

    private void sortChildDrawingOrder() {
        if (mDrawingOrder != DRAW_ORDER_DEFAULT) {
            if (mDrawingOrderedChildren == null) {
                mDrawingOrderedChildren = new ArrayList();
            } else {
                mDrawingOrderedChildren.clear();
            }

            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                lp.childIndex = i;
                lp.position = viewPosition(child);
                mDrawingOrderedChildren.add(child);
            }
            Collections.sort(mDrawingOrderedChildren, sPositionComparator);
        }
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        final int index = mDrawingOrder == DRAW_ORDER_REVERSE ? childCount - 1 - i : i;
        final int result = ((LayoutParams) mDrawingOrderedChildren.get(index).getLayoutParams()).childIndex;
        return result;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return generateDefaultLayoutParams();
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams && super.checkLayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    private class PagerObserver extends DataSetObserver {
        PagerObserver() {
        }

        @Override
        public void onChanged() {
            dataSetChanged();
        }

        @Override
        public void onInvalidated() {
            dataSetChanged();
        }
    }

    public static class Builder {
        int pageGap;
        float widthFactor;
        int offscreenPageLimit;

        public ZBannerRaw build(Context context) {
            ZBannerRaw zBannerRaw = new ZBannerRaw(context, this);
            return zBannerRaw;
        }

        public Builder pageGap(int pageGap) {
            this.pageGap = pageGap;
            return this;
        }

        public Builder widthFactor(float widthFactor) {
            this.widthFactor = widthFactor;
            return this;
        }

        public Builder offscreenPageLimit(int offscreenPageLimit) {
            this.offscreenPageLimit = offscreenPageLimit;
            return this;
        }
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        public int gravity;
        int position;
        int childIndex;

        public LayoutParams() {
            super(MATCH_PARENT, MATCH_PARENT);
        }

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
            gravity = a.getInteger(0, Gravity.TOP);
            a.recycle();
        }
    }

    static class ViewPositionComparator implements Comparator<View> {
        @Override
        public int compare(View lhs, View rhs) {
            final LayoutParams llp = (LayoutParams) lhs.getLayoutParams();
            final LayoutParams rlp = (LayoutParams) rhs.getLayoutParams();
            return llp.position - rlp.position;
        }
    }

    void star() {
        if (mTimer == null) {
            mDuration = mAnimalDuration + mDisplayDuration;
            mDragger.setAutoPlaying(true);
            mDragger.setDuration(mAnimalDuration);
            mAutoPlay = true;
            mTimer = new Timer();
            mTimer.schedule(new ScrollerTask(), mDisplayDuration, mDuration);
        }
    }

    void stop() {
        if (mTimer != null) {
            mDragger.setAutoPlaying(false);
            mTimer.cancel();
            mTimer = null;
        }
    }

    public void setAnimalDuration(int duration) {
        this.mAnimalDuration = duration;
    }

    public void setDisplayDuration(int duration) {
        this.mDisplayDuration = duration;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mCurPosition++;
            if (mCurPosition == N) {
                mCurPosition = 0;
            }
            setCurrentItem(mCurPosition);
        }
    };

    private class ScrollerTask extends TimerTask {

        public ScrollerTask() {
        }

        public void run() {
            if (mAutoPlay && (System.currentTimeMillis() - mRecentTouchTime >= mDisplayDuration)) {
                handler.sendEmptyMessage(0);
            }
        }
    }

    public void setOnPageChangeLister(ZBanner.OnPageChangeLister mOnPageChangeLister) {
        this.mOnPageChangeLister = mOnPageChangeLister;
    }

}
