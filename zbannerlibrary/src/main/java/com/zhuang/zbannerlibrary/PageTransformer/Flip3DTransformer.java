package com.zhuang.zbannerlibrary.PageTransformer;

import android.view.View;

import com.zhuang.zbannerlibrary.ZBanner;

public class Flip3DTransformer implements ZBanner.ZBannerPageTransformer {

    @Override
    public void transformPage(View page, float position) {
        final float width = page.getWidth();
        if (position >= 0 && position <= 1) {
            page.setPivotY(0);
            page.setRotationY(0);
            page.setPivotX(0);

            page.setPivotY(page.getHeight() * 0.5f);
            page.setRotationY(90f * position);

        } else if (position < 0 && position >= -1) {
            page.setPivotX(0);
            page.setPivotY(0);
            page.setRotationY(0);

            page.setPivotX(width);
            page.setPivotY(page.getHeight() * 0.5f);
            page.setRotationY(90f * position);
        }

    }
}
