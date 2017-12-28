package com.zhuang.zbannerlibrary.PageTransformer;

import android.view.View;

import com.zhuang.zbannerlibrary.ZBanner;

public class AccordionTransformer1 implements ZBanner.ZBannerPageTransformer {

    @Override
    public void transformPage(View view, float position) {
        final float width = view.getWidth();
        if (position >= 0 && position <= 1) {
            view.setTranslationX(-width * position);
            view.setPivotX(width);
            view.setScaleX(1f - position);
        } else if (position < 0 && position >= -1) {
            view.setTranslationX(-width * position);
            view.setPivotX(0);
            view.setScaleX(1f + position);
        }
    }
}
