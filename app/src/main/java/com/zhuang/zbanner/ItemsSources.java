package com.zhuang.zbanner;

import com.zhuang.zbannerlibrary.PageTransformer.AccordionTransformer;
import com.zhuang.zbannerlibrary.PageTransformer.AccordionTransformer1;
import com.zhuang.zbannerlibrary.PageTransformer.DepthPageTransformer;
import com.zhuang.zbannerlibrary.PageTransformer.DrawerTransformer;
import com.zhuang.zbannerlibrary.PageTransformer.Flip3DTransformer;
import com.zhuang.zbannerlibrary.PageTransformer.FlipHorizontalTransformer;
import com.zhuang.zbannerlibrary.PageTransformer.RotateDownTransformer;
import com.zhuang.zbannerlibrary.PageTransformer.StackTransformer;
import com.zhuang.zbannerlibrary.PageTransformer.ZoomOutTransformer;
import com.zhuang.zbannerlibrary.ZBanner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuang on 2018/1/5.
 */

public class ItemsSources {

    public final static String TYPE_CLASSIC = "classic";
    public final static String TYPE_WIDTH_FACTOR = "widthFactor";
    public final static String TYPE_INDICATOR = "indicator";

    public static List<ExampleItem> getItems() {
        List<ExampleItem> list = new ArrayList();
        list.add(new ExampleItem("Classic",null,R.layout.activity_banner_classic));
        list.add(new ExampleItem("WidthFactor",null,R.layout.activity_banner_widthfactor));
        list.add(new ExampleItem("Indicator",null,R.layout.activity_banner_indicator));
        list.add(new ExampleItem("AccordionTransformer",new AccordionTransformer()));
        list.add(new ExampleItem("AccordionTransformer1",new AccordionTransformer1()));
        list.add(new ExampleItem("DepthPageTransformer",new DepthPageTransformer()));
        list.add(new ExampleItem("DrawerTransformer",new DrawerTransformer()));
        list.add(new ExampleItem("Flip3DTransformer",new Flip3DTransformer()));
        list.add(new ExampleItem("FlipHorizontalTransformer",new FlipHorizontalTransformer()));
        list.add(new ExampleItem("RotateDownTransformer",new RotateDownTransformer()));
        list.add(new ExampleItem("StackTransformer",new StackTransformer()));
        list.add(new ExampleItem("ZoomOutTransformer",new ZoomOutTransformer()));
        return list;
    }

    static class ExampleItem {
        String title;
        ZBanner.ZBannerPageTransformer transformer;
        int layoutId;

        public ExampleItem(String title, ZBanner.ZBannerPageTransformer transformer, int layoutId) {
            this.title = title;
            this.transformer = transformer;
            this.layoutId = layoutId;
        }

        public ExampleItem(String title, ZBanner.ZBannerPageTransformer transformer) {
            this.title = title;
            this.transformer = transformer;
            this.layoutId = R.layout.activity_banner_transformer;
        }
    }

}
