package com.pumasi.surbay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pumasi.surbay.adapter.SellerRecyclerViewAdapter;
import com.pumasi.surbay.pages.MainActivity;
import com.pumasi.surbay.pages.SellingActivity;

public class    MobileCouponFragment extends Fragment {

    private View view;
    private Context context;
    private SellerRecyclerViewAdapter sellerRecyclerViewAdapter;

    private RecyclerView rv_coupon_seller_supplier;
    private Button btn_coupon_seller_filter_all;
    private Button btn_coupon_seller_filter_cafe;
    private Button btn_coupon_seller_filter_food;
    private Button btn_coupon_seller_filter_etc;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_mobile_coupon, container, false);
        context = getActivity().getApplicationContext();
        rv_coupon_seller_supplier = view.findViewById(R.id.rv_coupon_seller_supplier);
        btn_coupon_seller_filter_all = view.findViewById(R.id.btn_coupon_seller_filter_all);
        btn_coupon_seller_filter_cafe = view.findViewById(R.id.btn_coupon_seller_filter_cafe);
        btn_coupon_seller_filter_food = view.findViewById(R.id.btn_coupon_seller_filter_food);
        btn_coupon_seller_filter_etc = view.findViewById(R.id.btn_coupon_seller_filter_etc);
        sellerRecyclerViewAdapter = new SellerRecyclerViewAdapter(context);
        rv_coupon_seller_supplier.setAdapter(sellerRecyclerViewAdapter);
        rv_coupon_seller_supplier.setLayoutManager(new GridLayoutManager(context, 3));
        btn_coupon_seller_filter_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelected(0);
            }
        });
        btn_coupon_seller_filter_cafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelected(1);
            }
        });
        btn_coupon_seller_filter_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelected(2);
            }
        });
        btn_coupon_seller_filter_etc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelected(3);
                Intent intent = new Intent(getActivity().getApplicationContext(), SellingActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    public void setSelected(int num) {
        btn_coupon_seller_filter_all.setTextAppearance(R.style.TabButtonNotSelect);
        btn_coupon_seller_filter_all.setBackgroundResource(R.drawable.ic_tabnoselect);
        btn_coupon_seller_filter_cafe.setTextAppearance(R.style.TabButtonNotSelect);
        btn_coupon_seller_filter_cafe.setBackgroundResource(R.drawable.ic_tabnoselect);
        btn_coupon_seller_filter_food.setTextAppearance(R.style.TabButtonNotSelect);
        btn_coupon_seller_filter_food.setBackgroundResource(R.drawable.ic_tabnoselect);
        btn_coupon_seller_filter_etc.setTextAppearance(R.style.TabButtonNotSelect);
        btn_coupon_seller_filter_etc.setBackgroundResource(R.drawable.ic_tabnoselect);
        switch (num) {
            case 0:
                btn_coupon_seller_filter_all.setBackgroundResource(R.drawable.ic_tabselect);
                btn_coupon_seller_filter_all.setTextAppearance(R.style.TabButtonSelect);
                break;
            case 1:
                btn_coupon_seller_filter_cafe.setBackgroundResource(R.drawable.ic_tabselect);
                btn_coupon_seller_filter_cafe.setTextAppearance(R.style.TabButtonSelect);
                break;
            case 2:
                btn_coupon_seller_filter_food.setBackgroundResource(R.drawable.ic_tabselect);
                btn_coupon_seller_filter_food.setTextAppearance(R.style.TabButtonSelect);
                break;
            case 3:
                btn_coupon_seller_filter_etc.setBackgroundResource(R.drawable.ic_tabselect);
                btn_coupon_seller_filter_etc.setTextAppearance(R.style.TabButtonSelect);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + num);
        }
    }
}