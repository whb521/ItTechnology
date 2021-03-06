package com.safetyhuge.chanlian.safetyhuge.UIActivity.MineActivityInfo.FananFragmentInfo;

import android.support.v4.app.Fragment;

import java.util.HashMap;
import java.util.Map;

/**
 * FragmentFactory工厂类
 */
public class FananFragmentFactory {
    private static boolean flage = true;
    private static int i;
    public FananFragmentFactory(int i) {
        this.i = i;
    }

    public static void setFlage(boolean flage) {
        FananFragmentFactory.flage = flage;
    }

    /**
     * 用于缓存Fragment实例的Map集合
     */
    public static Map<Integer, Fragment> mFragments = new HashMap<Integer, Fragment>();

    /**
     * 根据不同的position生产对应的Fragment对象(不缓存Fragment实例)
     */
    public static Fragment create(int position) {
        Fragment fragment = mFragments.get(position);
        if (fragment == null) {
           if(i==0){
                switch (position) {
                    case 0:
                        fragment = new FananFragment(0);
                        break;
                    case 1:
                        fragment = new FananFragment(1);
                        break;
                    case 2:
                        fragment = new FananFragment(2);
                        break;
                }
                //投标管理
            }
            //保存到集合
            mFragments.put(position, fragment);
        }
        return fragment;
    }
}
