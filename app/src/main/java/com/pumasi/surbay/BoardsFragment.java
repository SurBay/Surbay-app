package com.pumasi.surbay;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class BoardsFragment extends Fragment  {
    private View view;
    public static ViewPager2 viewPager;
    private TabLayout tabLayout;
    public static final Integer NEW = 1;
    public static final Integer DEADLINE = 2;
    public static final Integer GOAL = 3;

    public static FragmentStateAdapter adapter;
    ImageButton boards_search_button;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_boards, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);

        viewPager = (ViewPager2) view.findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter = new FragmentStateAdapter(getActivity()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                if(position==0) {
                    return new BoardFragment1();
                }else if(position==1){
                    return new BoardFragment2();
                }else if(position==2){
                    return new BoardFragment3();
                }
                return new BoardFragment1();
            }

            @Override
            public int getItemCount() {
                return 3;
            }

        });
        adapter.notifyDataSetChanged();

        boards_search_button = view.findViewById(R.id.boards_search_button);
        boards_search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), BoardsSearchActivity.class);
                intent.putExtra("pos", viewPager.getCurrentItem());
                startActivity(intent);
            }
        });


        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if(position==0){
                        tab.setText("SurBay");
                    }
                    else if(position==1){tab.setText("설문 TIP");}
                    else if(position==2){tab.setText("건의/의견");}
                }
        ).attach();
    }
}
