# ZBanner

## 预览效果


## gradle引入
```
 compile 'com.zhuang.zbanner:zbanner:1.1'
  ```
## 简单使用

 R.layout.activity_example.xml
  ``` 
  <?xml version="1.0" encoding="utf-8"?>
  <com.zhuang.zbannerlibrary.ZBanner xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/zBanner"
    android:layout_width="match_parent"
    android:layout_height="200dp" />
  ```
java
  ```
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhuang.zbannerlibrary.ZBanner;
import com.zhuang.zbannerlibrary.ZBannerAdapter;

public class ExampleActivity extends AppCompatActivity {

    ZBanner zBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        zBanner = findViewById(R.id.zBanner);
        zBanner.setAdapter(new MyBannerAdapter(getSupportFragmentManager()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //每个页面展示时间为1000ms  页面切换持续时间为2000ms
        zBanner.star(1000,2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //停止自动切换
        zBanner.stop();
    }

    private class MyBannerAdapter extends ZBannerAdapter {

        public MyBannerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return BannerFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 5;
        }
    }

    public static class BannerFragment extends Fragment {

        private static final String POSITION = "position";
        int[] colors = {Color.RED, Color.BLUE, Color.GRAY, Color.GREEN, Color.YELLOW};

        public static BannerFragment newInstance(int position) {
            BannerFragment fragment = new BannerFragment();
            Bundle args = new Bundle();
            args.putInt(POSITION, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            TextView textView = new TextView(getContext());
            final int position = getArguments().getInt(POSITION);
            textView.setText(position + "");
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(30);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            textView.setBackgroundColor(colors[position]);
            return textView;
        }
    }
}

  ```
