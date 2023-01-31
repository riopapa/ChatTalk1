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
                return new Fragment_1Log();
            case 2:
                return new Fragment_2Saved();
            case 3:
                return new Fragment_3Alert();
            case 4:
                return new Fragment_4Chat();
        }
        return new Fragment_1Log();
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}