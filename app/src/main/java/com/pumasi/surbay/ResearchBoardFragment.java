 package com.pumasi.surbay;

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
import com.pumasi.surbay.pages.boardpage.BoardGeneral;
import com.pumasi.surbay.pages.boardpage.BoardPost;

public class ResearchBoardFragment extends Fragment  {
    private View view;
    public static ViewPager2 vp_research_board;
    private TabLayout tabLayout;
    public static final Integer NEW = 1;
    public static final Integer DEADLINE = 2;
    public static final Integer GOAL = 3;

    public static FragmentStateAdapter adapter;
    ImageButton btn_query_research_board;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_research_board, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        vp_research_board = (ViewPager2) view.findViewById(R.id.vp_research_board);
        vp_research_board.setAdapter(adapter = new FragmentStateAdapter(getActivity()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
//                if(position==0) {
//                    return new BoardPost();
//                }
                return new BoardPost();

//                else if(position==1){
//                    return new BoardGeneral();
//                }else if(position==2){
//                    return new BoardSurveyTip();
//                }
//                return new BoardPost();
            }

            @Override
            public int getItemCount() {
                return 1;
            }

        });
        adapter.notifyDataSetChanged();

        btn_query_research_board = view.findViewById(R.id.btn_query_research_board);
        btn_query_research_board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), BoardsSearchActivity.class);
                intent.putExtra("pos", vp_research_board.getCurrentItem());
                startActivity(intent);
            }
        });


        return view;
    }
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        new TabLayoutMediator(tabLayout, viewPager,
//                (tab, position) -> {
//                    if(position==0){
//                        tab.setText("품앗이");
//                    }
//                    else if(position==1){tab.setText("SurBay");}
//                    else if(position==2){tab.setText("설문TIP");}
//                }
//        ).attach();
//    }
}
