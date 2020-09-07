package com.example.jolly.visitormanagement;

/*import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

*//**
 * Created by Jolly on 27-Mar-18.
 *//*

public class MyAdapter extends FragmentStatePagerAdapter {
    int notabs;

    public MyAdapter(FragmentManager fm,int notabs)
    {
        super(fm);
        this.notabs = notabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new PermissionFragment();
            case 1:
                return new QrFragment();
            case 2:
                return new FindFragment();
            case 3:
                return new GalleryFragment();
            case 4:
                return new AboutFragment();
        }
        return null;
    }

    @Override
    public int getCount() {


        return notabs;
    }

    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "Permissions";
            case 1:
                return "QR Code";
            case 2:
                return "Find Me";
            case 3:
                return "Campus Gallery";
            case 4:
                return "About";
        }

        return null;
    }
}*/
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class MyAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public MyAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    @Override
    public int getItemPosition(Object object){
        return super.getItemPosition(object);

    }


}
