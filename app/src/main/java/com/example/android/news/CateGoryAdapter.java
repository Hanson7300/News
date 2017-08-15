package com.example.android.news;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Hansson on 2017/8/15.
 */

public class CateGoryAdapter extends FragmentPagerAdapter{

    private Context mContext;

    public CateGoryAdapter(Context context,FragmentManager fm) {
        super(fm);
        mContext = context;
    }
    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return new ArtFragment();
            case 1:
                return new BusinessFragment();
            case 2:
                return new EconomicFragment();
            default:
                return new TechnologyFragment();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
    switch (position){
        case 0:
            return mContext.getString(R.string.art);
        case 1:
            return mContext.getString(R.string.business);
        case 2:
            return mContext.getString(R.string.economic);
        default:
            return mContext.getString(R.string.technology);

    }
    }
}
