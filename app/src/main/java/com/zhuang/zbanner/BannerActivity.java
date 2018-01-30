package com.zhuang.zbanner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.zhuang.zbannerlibrary.Indicator;
import com.zhuang.zbannerlibrary.PageTransformer.Flip3DTransformer;
import com.zhuang.zbannerlibrary.ZBanner;
import com.zhuang.zbannerlibrary.ZBannerAdapter;

import java.util.List;

public class BannerActivity extends AppCompatActivity {

    int[] imgResId = {
            R.drawable.jackson,
            R.drawable.jordan,
            R.drawable.kobe,
            R.drawable.stephen,
            R.drawable.android
    };
    String[] name = {
            "jackson",
            "jordan",
            "kobe",
            "stephen",
            "android",
    };
    ZBanner zBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int position = getIntent().getIntExtra("position", 0);
        List<ItemsSources.ExampleItem> list = ItemsSources.getItems();
        int layoutId = list.get(position).layoutId;
        setContentView(layoutId);

        zBanner = findViewById(R.id.zBanner);
        if (layoutId == R.layout.activity_banner_widthfactor) {
            zBanner.setAdapter(new MyBannerAdapter1(getSupportFragmentManager()));
        } else {
            zBanner.setAdapter(new MyBannerAdapter(getSupportFragmentManager()));
        }
        ZBanner.ZBannerPageTransformer transformer = list.get(position).transformer;
        if (transformer != null) {
            zBanner.setPageTransformer(transformer);
        }

        if (findViewById(R.id.indicator) != null) {
            Indicator indicator = findViewById(R.id.indicator);
            zBanner.setIndicator(indicator);
        }

        if (findViewById(R.id.title) != null) {
            final TextView title = findViewById(R.id.title);
            zBanner.setOnPageChangeLister(new ZBanner.OnPageChangeLister() {
                @Override
                public void change(int position) {
                    title.setText(position + ".  " + name[position]);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        zBanner.star();
    }

    @Override
    protected void onPause() {
        super.onPause();
        zBanner.stop();
    }

    private class MyBannerAdapter extends ZBannerAdapter {

        public MyBannerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return BannerFragment.newInstance(imgResId[position], position);
        }

        @Override
        public int getCount() {
            return imgResId.length;
        }
    }

    private class MyBannerAdapter1 extends ZBannerAdapter {

        public MyBannerAdapter1(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return BannerFragmentRound.newInstance(imgResId[position], position);
        }

        @Override
        public int getCount() {
            return imgResId.length;
        }
    }

}
