package com.zhuang.zbanner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.zhuang.zbannerlibrary.PageTransformer.DrawerTransformer;
import com.zhuang.zbannerlibrary.PageTransformer.Flip3DTransformer;
import com.zhuang.zbannerlibrary.ZBanner;
import com.zhuang.zbannerlibrary.ZBannerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ZBanner banner1;
    List<String> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fresco.initialize(this);

        String[] imgSrc = {
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1512714572853&di=2b3ed18a830c9bd79c519886afe019b3&imgtype=0&src=http%3A%2F%2Fpic27.photophoto.cn%2F20130506%2F0005018340710650_b.jpg",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1512714572852&di=456e12e98305218a43719ab2770853c2&imgtype=0&src=http%3A%2F%2Fpic.baike.soso.com%2Fp%2F20131220%2F20131220194153-1583037037.jpg",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1512714572842&di=bc50aa70299d795f94ae9227f352b9f6&imgtype=0&src=http%3A%2F%2Fimg5.duitang.com%2Fuploads%2Fitem%2F201507%2F01%2F20150701183422_4f8YU.jpeg",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1512714626990&di=ab41bb4cc23113f6e86675d4dcc21dc4&imgtype=jpg&src=http%3A%2F%2Fimg0.imgtn.bdimg.com%2Fit%2Fu%3D3370893190%2C2229984371%26fm%3D214%26gp%3D0.jpg",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1512714572881&di=ecdede81218cde2df4e10d73ee1856e4&imgtype=0&src=http%3A%2F%2Fpic.58pic.com%2F58pic%2F16%2F03%2F90%2F27P58PICXBw_1024.jpg",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1512714572879&di=e63062290ff50fe51848e9cc9c431189&imgtype=0&src=http%3A%2F%2Fd.hiphotos.baidu.com%2Fzhidao%2Fpic%2Fitem%2F18d8bc3eb13533fa995d378fabd3fd1f40345ba3.jpg",
        };
        for (int i = 0; i < imgSrc.length; i++) {
            mList.add(imgSrc[i]);
        }

        banner1 = findViewById(R.id.zBanner1);
        banner1.setAdapter(new MyBannerAdapter(getSupportFragmentManager()));
        banner1.setPageTransformer(new Flip3DTransformer());
    }

    @Override
    protected void onResume() {
        super.onResume();
        banner1.star(2000,1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        banner1.stop();
    }

    private class MyBannerAdapter extends ZBannerAdapter {

        public MyBannerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return BannerFragment.newInstance(mList.get(position),position);
        }

        @Override
        public int getCount() {
            return 6;//mList.size();
        }
    }
}
