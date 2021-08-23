package com.pumasi.surbay;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.pumasi.surbay.pages.boardpage.BoardGeneral;


public class FreeBoardFragment extends Fragment {

    private ImageButton btn_query_free_board;
    public ViewPager2 vp_free_board;
    private TabLayout tl_free_board;
    public static FragmentStateAdapter adapter2;
    public static int free_position;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_free_board, container, false);

        btn_query_free_board = view.findViewById(R.id.btn_query_free_board);
        vp_free_board = view.findViewById(R.id.vp_free_board);
        tl_free_board = view.findViewById(R.id.tl_free_board);

        btn_query_free_board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), BoardsSearchActivity.class);
                intent.putExtra("pos", 1);
                startActivity(intent);
            }
        });

        vp_free_board.setAdapter(adapter2 = new FragmentStateAdapter(getActivity()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return new BoardGeneral();
                    case 1:
                        return new BoardContents();
                    default:
                        throw new IllegalStateException("Unexpected value: " + position);
                }
            }

            @Override
            public int getItemCount() {
                return 2;
            }


        });
        adapter2.notifyDataSetChanged();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new TabLayoutMediator(tl_free_board, vp_free_board,
                ((tab, position) -> {
                    if (position == 0) {
                        tab.setText("투표 게시판");
                    } else if (position  == 1) {
                        tab.setText("콘텐츠 게시판");
                    }
                })).attach();

    }
    @Override
    public void onStart() {
        super.onStart();
        vp_free_board.setCurrentItem(free_position);
    }

}