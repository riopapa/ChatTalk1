package com.urrecliner.chattalk;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PagerAdapter extends FragmentStateAdapter {

public PagerAdapter(FragmentActivity fm) {
    super(fm);
}

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                return new Fragment_0Table();
            case 1:
                return new Fragment_1Que();
            case 2:
                return new Fragment_2Saved();
            case 3:
                return new Fragment_3Stock();
            case 4:
                return new Fragment_4Alert();
            case 5:
                return new Fragment_5Chat();
        }
        return new Fragment_1Que();
    }

    @Override
    public int getItemCount() {
        return 6;
    }
}