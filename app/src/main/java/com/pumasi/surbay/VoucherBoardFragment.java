package com.pumasi.surbay;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import javax.crypto.spec.IvParameterSpec;

public class VoucherBoardFragment extends Fragment {

    private View view;
    private TabLayoutMediator tabLayoutMediator;
    private ViewPager2 vp_voucher_board;
    private TabLayout tl_voucher_board;
    private FragmentStateAdapter fa_voucher_board;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_voucher_board, container, false);
        vp_voucher_board = view.findViewById(R.id.vp_voucher_board);
        tl_voucher_board = view.findViewById(R.id.tl_voucher_board);
        vp_voucher_board.setAdapter(fa_voucher_board = new FragmentStateAdapter(getActivity()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0: return new MobileCouponFragment();
                    case 1: return new EventFragment();
                    default:
                        throw new IllegalStateException("Unexpected value: " + position);
                }
            }

            @Override
            public int getItemCount() {
                return 1;
            }
        });
        fa_voucher_board.notifyDataSetChanged();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final List<String> texts = Arrays.asList("모바일 교환권", "쿠폰 / 이벤트");
        final List<Integer> drawables = Arrays.asList(R.drawable.red_dot, R.drawable.white_dot);
        final List<Integer> textColor = Arrays.asList(R.color.teal_200, R.color.gray2);

        tabLayoutMediator = new TabLayoutMediator(tl_voucher_board, vp_voucher_board, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setCustomView(createTabView(drawables.get(position), texts.get(position), getContext().getResources().getColor(textColor.get(position))));
            }
        });
        tabLayoutMediator.attach();


        tl_voucher_board.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp_voucher_board.setCurrentItem(tab.getPosition());
                TextView textView = tl_voucher_board.getTabAt(tab.getPosition()).getCustomView().findViewById(R.id.tv_custom_tab);
                textView.setTextColor(getContext().getResources().getColor(R.color.teal_200));
                ImageView imageView = tl_voucher_board.getTabAt(tab.getPosition()).getCustomView().findViewById(R.id.iv_custom_tab);
                imageView.setImageResource(R.drawable.red_dot);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView textView = tl_voucher_board.getTabAt(tab.getPosition()).getCustomView().findViewById(R.id.tv_custom_tab);
                textView.setTextColor(getContext().getResources().getColor(R.color.gray2));
                ImageView imageView = tl_voucher_board.getTabAt(tab.getPosition()).getCustomView().findViewById(R.id.iv_custom_tab);
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