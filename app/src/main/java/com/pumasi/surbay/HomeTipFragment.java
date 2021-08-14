package com.pumasi.surbay;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.classfile.Notice;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.PostNonSurvey;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.Surveytip;
import com.pumasi.surbay.pages.MainActivity;
import com.pumasi.surbay.pages.boardpage.TipdetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static com.pumasi.surbay.BoardsSearchActivity.LIKE_SURVEY;
import static com.pumasi.surbay.adapter.HomeTipPagerAdapter.home_surveytips;


public class HomeTipFragment extends Fragment {

    private int TIP_ITEM_NUM;
    private View view;

    private LinearLayout ll_home_tip_item1;
    private TextView tv_tip_category1;
    private TextView tv_tip_title1;
    private TextView tv_tip_des1;
    private TextView tv_tip_likes1;

    private LinearLayout ll_home_tip_item2;
    private TextView tv_tip_category2;
    private TextView tv_tip_title2;
    private TextView tv_tip_des2;
    private TextView tv_tip_likes2;


    public HomeTipFragment(int num) {
        this.TIP_ITEM_NUM = num;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_tip, container, false);

        ll_home_tip_item1 = view.findViewById(R.id.ll_home_tip_item1);
        tv_tip_category1 = view.findViewById(R.id.tv_tip_category1);
        tv_tip_title1 = view.findViewById(R.id.tv_tip_title1);
        tv_tip_des1 = view.findViewById(R.id.tv_tip_des1);
        tv_tip_likes1 = view.findViewById(R.id.tv_tip_likes1);

        ll_home_tip_item2 = view.findViewById(R.id.ll_home_tip_item2);
        tv_tip_category2 = view.findViewById(R.id.tv_tip_category2);
        tv_tip_title2 = view.findViewById(R.id.tv_tip_title2);
        tv_tip_des2 = view.findViewById(R.id.tv_tip_des2);
        tv_tip_likes2 = view.findViewById(R.id.tv_tip_likes2);


        if (home_surveytips != null && home_surveytips.size() != 0) {
            tv_tip_category1.setText(home_surveytips.get(2*TIP_ITEM_NUM).getCategory().toString());
            tv_tip_title1.setText(home_surveytips.get(2*TIP_ITEM_NUM).getTitle().toString());
            tv_tip_des1.setText(home_surveytips.get(2*TIP_ITEM_NUM ).getContent().toString());
            tv_tip_likes1.setText(home_surveytips.get(2*TIP_ITEM_NUM).getLikes().toString());

            tv_tip_category2.setText(home_surveytips.get(2*TIP_ITEM_NUM + 1).getCategory().toString());
            tv_tip_title2.setText(home_surveytips.get(2*TIP_ITEM_NUM + 1).getTitle().toString());
            tv_tip_des2.setText(home_surveytips.get(2*TIP_ITEM_NUM + 1).getContent().toString());
            tv_tip_likes2.setText(home_surveytips.get(2*TIP_ITEM_NUM + 1).getLikes().toString());

            ll_home_tip_item1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Surveytip surveytip = (Surveytip) home_surveytips.get(2 * TIP_ITEM_NUM);
                    Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), TipdetailActivity.class);
                    intent.putExtra("post", surveytip);
                    intent.putExtra("position", 2 * TIP_ITEM_NUM);
                    startActivityForResult(intent, LIKE_SURVEY);
                }
            });

            ll_home_tip_item2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Surveytip surveytip = (Surveytip) home_surveytips.get(2 * TIP_ITEM_NUM + 1);
                    Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), TipdetailActivity.class);
                    intent.putExtra("post", surveytip);
                    intent.putExtra("position", 2 * TIP_ITEM_NUM + 1);
                    startActivityForResult(intent, LIKE_SURVEY);
                }
            });
        }



        return view;
    }

}