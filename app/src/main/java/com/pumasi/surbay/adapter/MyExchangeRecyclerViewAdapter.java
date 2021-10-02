package com.pumasi.surbay.adapter;

import com.bumptech.glide.Glide;
import com.pumasi.surbay.R;
import com.pumasi.surbay.Tools;
import com.pumasi.surbay.classfile.MyCoupon;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

public class MyExchangeRecyclerViewAdapter extends RecyclerView.Adapter<MyExchangeRecyclerViewAdapter.MyVoucherViewHolder> {
    Tools tools = new Tools();
    LayoutInflater inflater;
    private Context context;
    private ArrayList<MyCoupon> myCoupons = new ArrayList<>();
    private MyExchangeRecyclerViewAdapter.OnItemClickListener aListener = null;

    public MyExchangeRecyclerViewAdapter(ArrayList<MyCoupon> myCoupons, Context ctx) {
        this.context = ctx;
        this.myCoupons = myCoupons;
        inflater = LayoutInflater.from(ctx);
    }
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(MyExchangeRecyclerViewAdapter.OnItemClickListener onItemClickListener) {
        this.aListener = onItemClickListener;
    }
    public Object getItem(int position) {
        return myCoupons.get(position);
    }
    @NonNull
    @Override
    public MyVoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.my_voucher_item, parent, false);
        MyVoucherViewHolder holder = new MyVoucherViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyVoucherViewHolder holder, int position) {
        MyCoupon myCoupon = myCoupons.get(position);
        if (!(myCoupon.getImage_urls() == null || myCoupon.getImage_urls().size() == 0)) {
            Glide.with(context).load(myCoupon.getImage_urls().get(0)).into(holder.iv_my_coupon_item_image);
        }
        holder.tv_my_coupon_item_name.setText(myCoupon.getStore() + " " + myCoupon.getMenu());
//        holder.tv_my_coupon_item_date.setText(); 나중에 추가
        holder.tv_my_coupon_item_publish_date.setText(tools.convertTimeZone(context, myCoupon.getDate(), "yyyy.MM.dd"));
    }
    public void setItems(ArrayList<MyCoupon> myCoupons) {
        this.myCoupons = myCoupons;
    }
    @Override
    public int getItemCount() {
        return myCoupons == null ? 0 : myCoupons.size();
    }

    public class MyVoucherViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_my_coupon_item_image;
        private TextView tv_my_coupon_item_name;
        private TextView tv_my_coupon_item_date;
        private TextView tv_my_coupon_item_publish_date;
        public MyVoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_my_coupon_item_image = itemView.findViewById(R.id.iv_my_coupon_item_image);
            tv_my_coupon_item_name = itemView.findViewById(R.id.tv_my_coupon_item_name);
            tv_my_coupon_item_date = itemView.findViewById(R.id.tv_my_coupon_item_date);
            tv_my_coupon_item_publish_date = itemView.findViewById(R.id.tv_my_coupon_item_publish_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (aListener != null) {
                            aListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });
        }
    }
}
