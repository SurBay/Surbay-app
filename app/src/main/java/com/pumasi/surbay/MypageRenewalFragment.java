package com.pumasi.surbay;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.pumasi.surbay.pages.mypage.MypageSettingMain;

public class MypageRenewalFragment extends Fragment {

    private View view;
    private ImageButton ib_my_setting;
    private ImageButton ib_my_exchange;
    private ImageButton ib_my_gift;
    private ImageButton ib_my_note;
    private ImageButton ib_my_announce;
    private ImageButton ib_my_suggest;
    private ImageButton ib_my_report;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mypage_renewal, container, false);
        ib_my_setting = view.findViewById(R.id.ib_my_setting);
        ib_my_exchange = view.findViewById(R.id.ib_my_exchange);
        ib_my_gift = view.findViewById(R.id.ib_my_gift);
        ib_my_note = view.findViewById(R.id.ib_my_note);
        ib_my_announce = view.findViewById(R.id.ib_my_announce);
        ib_my_suggest = view.findViewById(R.id.ib_my_suggest);
        ib_my_report = view.findViewById(R.id.ib_my_report);

        ib_my_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), MypageSettingMain.class);
                startActivity(intent);
            }
        });

        return view;
    }
}