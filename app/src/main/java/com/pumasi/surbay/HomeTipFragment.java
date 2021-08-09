package com.pumasi.surbay;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class HomeTipFragment extends Fragment {

    private static int TIP_ITEM_NUM;
    private View view;

    private TextView tv_tip_category1;
    private TextView tv_tip_title1;
    private TextView tv_tip_des1;
    private TextView tv_tip_likes1;

    private TextView tv_tip_category2;
    private TextView tv_tip_title2;
    private TextView tv_tip_des2;
    private TextView tv_tip_likes2;

    ArrayList<Surveytip> surveytips;
    public static HomeTipFragment newInstance(int num) {
        TIP_ITEM_NUM = num;
        HomeTipFragment homeTipFragment = new HomeTipFragment();
        return homeTipFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_tip, container, false);

        tv_tip_category1 = view.findViewById(R.id.tv_tip_category1);
        tv_tip_title1 = view.findViewById(R.id.tv_tip_title1);
        tv_tip_des1 = view.findViewById(R.id.tv_tip_des1);
        tv_tip_likes1 = view.findViewById(R.id.tv_tip_likes1);

        tv_tip_category2 = view.findViewById(R.id.tv_tip_category2);
        tv_tip_title2 = view.findViewById(R.id.tv_tip_title2);
        tv_tip_des2 = view.findViewById(R.id.tv_tip_des2);
        tv_tip_likes2 = view.findViewById(R.id.tv_tip_likes2);
        surveytips = MainActivity.surveytipArrayList;
        if (surveytips.size() != 0) {
            tv_tip_category1.setText(surveytips.get(2*TIP_ITEM_NUM).getCategory().toString());
            tv_tip_title1.setText(surveytips.get(2*TIP_ITEM_NUM).getTitle().toString());
            tv_tip_des1.setText(surveytips.get(2*TIP_ITEM_NUM ).getContent().toString());
            tv_tip_likes1.setText(surveytips.get(2*TIP_ITEM_NUM).getLikes().toString());

            tv_tip_category2.setText(surveytips.get(2*TIP_ITEM_NUM + 1).getCategory().toString());
            tv_tip_title2.setText(surveytips.get(2*TIP_ITEM_NUM + 1).getTitle().toString());
            tv_tip_des2.setText(surveytips.get(2*TIP_ITEM_NUM + 1).getContent().toString());
            tv_tip_likes2.setText(surveytips.get(2*TIP_ITEM_NUM + 1).getLikes().toString());
        }



        return view;
    }

}