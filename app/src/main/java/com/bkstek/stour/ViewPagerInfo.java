package com.bkstek.stour;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bkstek.stour.fragment.FragmentInfo;
import com.bkstek.stour.fragment.FragmentMessage;

/**
 * Created by acebk on 9/6/2017.
 */

public class ViewPagerInfo extends FragmentPagerAdapter {


    public ViewPagerInfo(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FragmentInfo fragmentInfo = new FragmentInfo();
                return fragmentInfo;
            case 1:
                FragmentMessage fragmentMessage = new FragmentMessage();
                return fragmentMessage;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "THÔNG BÁO";
            case 1:
                return "TIN NHẮN";
            default:
                return null;

        }
    }
}
