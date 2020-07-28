package id.co.myproject.yukkajian.adapter;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import id.co.myproject.yukkajian.model.Kategori;
import id.co.myproject.yukkajian.view.home.HomePagerFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    List<Kategori> kategoriList = new ArrayList<>();
    Context context;
    private ArrayList<Fragment> fragments;
    private ArrayList<String> titles;
    public ViewPagerAdapter(@NonNull FragmentManager fm, Context context, List<Kategori> kategoriList) {
        super(fm);
        this.context = context;
        this.kategoriList.clear();
        this.kategoriList.addAll(kategoriList);

        fragments = new ArrayList<>();
        titles = new ArrayList<>();


        fragments.add(new HomePagerFragment("0"));
        titles.add("Semua");
        for (Kategori kategori : kategoriList){
            fragments.add(new HomePagerFragment(kategori.getIdKategori()));
            titles.add(kategori.getNamaKategori());
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
