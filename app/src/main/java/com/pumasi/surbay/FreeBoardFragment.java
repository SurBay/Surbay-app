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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.pumasi.surbay.pages.boardpage.BoardGeneral;
import com.pumasi.surbay.tools.FirebaseLogging;

import java.util.Arrays;
import java.util.List;


public class FreeBoardFragment extends Fragment {

    private TabLayoutMediator tabLayoutMediator;
    private ImageButton btn_query_free_board;
    public static ViewPager2 vp_free_board;
    private TabLayout tl_free_board;
    public static FragmentStateAdapter adapter2;
    public static int frag_position = -1;

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
                switch (frag_position) {
                    case 0:
                        intent.putExtra("pos", 1);
                        break;
                    case 1:
                        intent.putExtra("pos", 2);
                        break;
                }
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

        final List<String> texts = Arrays.asList("투표 게시판", "콘텐츠 게시판");
        final List<Integer> drawables = Arrays.asList(R.drawable.red_dot, R.drawable.white_dot);
        final List<Integer> textColor = Arrays.asList(R.color.teal_200, R.color.gray2);


        tabLayoutMediator = new TabLayoutMediator(tl_free_board, vp_free_board, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setCustomView(createTabView(drawables.get(position), texts.get(position), getContext().getResources().getColor(textColor.get(position))));
            }
        });
        tabLayoutMediator.attach();

        tl_free_board.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                frag_position = tab.getPosition();
                vp_free_board.setCurrentItem(tab.getPosition());
                TextView textView = tl_free_board.getTabAt(tab.getPosition()).getCustomView().findViewById(R.id.tv_custom_tab);
                textView.setTextColor(getContext().getResources().getColor(R.color.teal_200));
                ImageView imageView = tl_free_board.getTabAt(tab.getPosition()).getCustomView().findViewById(R.id.iv_custom_tab);
                imageView.setImageResource(R.drawable.red_dot);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView textView = tl_free_board.getTabAt(tab.getPosition()).getCustomView().findViewById(R.id.tv_custom_tab);
                textView.setTextColor(getContext().getResources().getColor(R.color.gray2));
                ImageView imageView = tl_free_board.getTabAt(tab.getPosition()).getCustomView().findViewById(R.id.iv_custom_tab);
                imageView.setImageResource(R.drawable.white_dot);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private View createTabView(int drawable, String text, int color) {
        View tabView = LayoutInflater.from(getContext()).inflate(R.layout.custom_tab, null);
        ImageView iv_custom_tab = tabView.findViewById(R.id.iv_custom_tab);
        TextView tv_custom_tab = tabView.findViewById(R.id.tv_custom_tab);
        iv_custom_tab.setImageResource(drawable);
        tv_custom_tab.setTextColor(color);
        tv_custom_tab.setText(text);

        return tabView;
    }
}