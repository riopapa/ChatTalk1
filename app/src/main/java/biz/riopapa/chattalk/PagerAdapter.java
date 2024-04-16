package biz.riopapa.chattalk;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import biz.riopapa.chattalk.fragments.Fragment_0Logs;
import biz.riopapa.chattalk.fragments.Fragment_1Table;
import biz.riopapa.chattalk.fragments.Fragment_2Saved;
import biz.riopapa.chattalk.fragments.Fragment_3Stock;
import biz.riopapa.chattalk.fragments.Fragment_4Alert;
import biz.riopapa.chattalk.fragments.Fragment_5Chat;
import biz.riopapa.chattalk.fragments.Fragment_6Work;

public class PagerAdapter extends FragmentStateAdapter {

public PagerAdapter(FragmentActivity fm) {
    super(fm);
}

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                return new Fragment_0Logs();
            case 1:
                return new Fragment_1Table();
            case 2:
                return new Fragment_2Saved();
            case 3:
                return new Fragment_3Stock();
            case 4:
                return new Fragment_4Alert();
            case 5:
                return new Fragment_5Chat();
            case 6:
                return new Fragment_6Work();
        }
        return new Fragment_0Logs();
    }

    @Override
    public int getItemCount() {
        return 7;
    }
}