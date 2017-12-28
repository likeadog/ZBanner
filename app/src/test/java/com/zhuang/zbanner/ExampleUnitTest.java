package com.zhuang.zbanner;

import com.zhuang.zbanner.zbanner.ItemInfo;
import com.zhuang.zbanner.zbanner.Utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        List<ItemInfo> addItems = new ArrayList<>();
        List<ItemInfo> removeItems = new ArrayList<>();
        ItemInfo i1 = new ItemInfo();
        i1.position = 3;
        ItemInfo i2 = new ItemInfo();
        i2.position = 4;
        addItems.add(i1);
        addItems.add(i2);

        ItemInfo i3 = new ItemInfo();
        i3.position = 2;
        ItemInfo i4 = new ItemInfo();
        i4.position = 3;
        removeItems.add(i3);
        removeItems.add(i4);

        Utils.removeAndAdd(addItems,removeItems);

        for(ItemInfo ii : addItems){
            System.out.println(ii.position);
        }
        for(ItemInfo ii : removeItems){
            System.out.println(ii.position);
        }

    }
}