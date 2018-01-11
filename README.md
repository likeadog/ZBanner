# ZBanner

## 预览效果
![](https://github.com/likeadog/Zbanner/blob/master/screenshot/1.gif)
![](https://github.com/likeadog/Zbanner/blob/master/screenshot/2.gif)
![](https://github.com/likeadog/Zbanner/blob/master/screenshot/3.gif)
![](https://github.com/likeadog/Zbanner/blob/master/screenshot/4.gif)
![](https://github.com/likeadog/Zbanner/blob/master/screenshot/5.gif)
![](https://github.com/likeadog/Zbanner/blob/master/screenshot/6.gif)
![](https://github.com/likeadog/Zbanner/blob/master/screenshot/7.gif)
![](https://github.com/likeadog/Zbanner/blob/master/screenshot/8.gif)
![](https://github.com/likeadog/Zbanner/blob/master/screenshot/9.gif)
![](https://github.com/likeadog/Zbanner/blob/master/screenshot/10.gif)
![](https://github.com/likeadog/Zbanner/blob/master/screenshot/11.gif)

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
## 转换动画
```
zBanner.setPageTransformer(new Flip3DTransformer());
```
ZBanner提供了多种转换动画效果：AccordionTransformer、AccordionTransformer1、DepthPageTransformer、DrawerTransformer、Flip3DTransformer、FlipHorizontalTransformer、RotateDownTransformer、StackTransformer、ZoomOutTransformer  
ZBanner允许用户自定义Transformer，只需实现接口ZBannerPageTransformer，例如AccordionTransformer的实现如下：
```
public class AccordionTransformer implements ZBanner.ZBannerPageTransformer {

   /**
    * @param page     在切换的页面
    * @param position 正在切换的页面相对于当前显示在正中间的页面的位置
    *                 0表示当前页，1表示右侧一页，-1表示左侧一页。
    *                 注意，这几个都是临界值，position在页面切换过程中会一直改变
    *                 例如左移的话，当前页的position会从0减少到-1，切换完成后就变成左侧一页了。
    */

    @Override
    public void transformPage(View view, float position) {
        final float width = view.getWidth();
        if (position >= 0 && position <= 1) {
            view.setTranslationX(-width * position);
            view.setPivotX(width);
            view.setScaleX(1f - position);
        } else if (position < 0 && position >= -1) {
            view.setTranslationX(0);
            view.setPivotX(0);
            view.setScaleX(1f);
        }
    }
}
```
用一张图来说明transformPage的position参数

![](https://github.com/likeadog/Zbanner/blob/master/screenshot/page_transformer1.png)

如有不清楚的可以查阅ViewPager的PageTransformer,因为ZBanner提供的PageTransformer接口与ViewPager的方式是一致的。

## 可以在Xml文件中设置的属性
| 属性        | 描述           | 示例  |
| ------------- |:-------------:| -----:|
| widthFactor      | 设置ZBanner中页面宽度倍数，例如设置为0.8，则每个页面都占用ZBanner的0.8倍 |zbanner:widthFactor=".8"|
| pageGap      | 页面间隔 |zbanner:pageGap="10dp" |
| offscreenPageLimit | 左右两侧分别可缓存的页面数 | zbanner:offscreenPageLimit="2"|
| indicatorSelectIcon    |可自定义指示器被选中时的图标|zbanner:indicatorSelectIcon="@drawable/ic_indicator_line_select"|
| indicatorUnSelectIcon    |可自定义指示器未被选中时的图标|zbanner:indicatorUnSelectIcon="@drawable/ic_indicator_line_unselect"|
| indicatorGravity    |指示器的位置|zbanner:indicatorGravity="bottomRight"|
| indicatorIconSize    |指示器的大小|zbanner:indicatorIconSize="10dp"|
| showIndicator    |是否显示指示器|zbanner:showIndicator="false"|
|indicatorMargin|指示器的margin|zbanner:indicatorMargin="10dp"|
|indicatorGap|指示器中各个图标的间隔|zbanner:indicatorGap="3dp"|

## 点击事件

从上面的介绍可以看到ZBanner的每个页面其实就是一个Fragment，如需为页面设置点击事件，只需在对应的Fragment中设置点击事件即可
```
 @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_banner1, container, false);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
        return rootView;
    }
```

## License
```
Copyright 2018 likeadog

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
