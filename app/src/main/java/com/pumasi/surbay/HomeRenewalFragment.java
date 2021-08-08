package com.pumasi.surbay;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.pumasi.surbay.adapter.BannerViewPagerAdapter;

import java.util.ArrayList;

public class HomeRenewalFragment extends Fragment {

    private static Context mContext;
    private static final Integer[] IMAGES= {R.drawable.renewal_banner, R.drawable.tutorialbanner2};
    private ArrayList<Integer> ImagesArray = new ArrayList<>();
    private ViewPager vp_banner;
    private View view;

    private static BannerViewPagerAdapter bannerAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_renewal, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        mContext = getActivity();


        setVp_banner();
        return view;
    }
    private void setVp_banner() {
        if (view != null) {
            vp_banner = view.findViewById(R.id.vp_banner);
            for (int i=0;i<IMAGES.length;i++) ImagesArray.add(IMAGES[i]);
            bannerAdapter = new BannerViewPagerAdapter(getActivity().getApplicationContext(), ImagesArray);
            vp_banner.setAdapter(bannerAdapter);
        }

    }
}