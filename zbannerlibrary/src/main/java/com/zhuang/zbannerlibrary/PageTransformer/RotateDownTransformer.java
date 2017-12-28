package com.zhuang.zbannerlibrary.PageTransformer;

import android.view.View;

import com.zhuang.zbannerlibrary.ZBanner;

/**
 * Created by zhuang on 2017/12/20.
 */

public class RotateDownTransformer implements ZBanner.ZBannerPageTransformer {

    private static final float ROT_MOD = -15f;

    @Override
    public void transformPage(View page, float position) {
        final float width = page.getWidth();
        final float height = page.getHeight();
        final float rotation = ROT_MOD * position * -1.25f;
        page.setPivotX(width * 0.5f);
        page.setPivotY(height);
        page.setRotation(rotation);
    }
}
