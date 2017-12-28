package com.zhuang.zbannerlibrary.PageTransformer;

import android.view.View;

import com.zhuang.zbannerlibrary.ZBanner;

/**
 * Created by dkzwm on 2017/3/2.
 *
 * @author dkzwm
 */
public class DrawerTransformer implements ZBanner.ZBannerPageTransformer {

    @Override
    public void transformPage(View page, float position) {
        if ((position >= -1 && position <= 0) || (position > 1 && position <= 2)) {
            page.setTranslationX(0);
        } else if (position > 0 && position <= 1) {
            page.setTranslationX(-page.getWidth() / 2 * position);
        }
    }
}
