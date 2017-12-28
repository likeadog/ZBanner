package com.zhuang.zbannerlibrary.PageTransformer;

import android.view.View;

import com.zhuang.zbannerlibrary.ZBanner;

/**
 * Created by zhuang on 2017/12/20.
 */

public class FlipHorizontalTransformer implements ZBanner.ZBannerPageTransformer {
    @Override
    public void transformPage(View view, float position) {
        if (position >= -1 && position <= 1) {
            float rotation = 180f * position;
            view.setTranslationX(view.getWidth() * -position);
            view.setAlpha(rotation > 90f || rotation < -90f ? 0 : 1);
            view.setPivotX(view.getWidth() * 0.5f);
            view.setPivotY(view.getHeight() * 0.5f);
            view.setRotationY(rotation);
        }

    }
}
