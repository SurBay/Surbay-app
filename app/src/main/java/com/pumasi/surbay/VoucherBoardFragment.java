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

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class VoucherBoardFragment extends Fragment {

    private View view;
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
                return 2;
            }
        });
        fa_voucher_board.notifyDataSetChanged();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new TabLayoutMediator(tl_voucher_board, vp_voucher_board,
                ((tab, position) -> {
                    if (position == 0) {
                        tab.setText("모바일 교환권");
                    } else if (position == 1) {
                        tab.setText("쿠폰 / 이벤트");
                    }
                })).attach();
    }
}