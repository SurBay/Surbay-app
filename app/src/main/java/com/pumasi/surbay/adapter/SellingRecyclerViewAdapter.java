package com.pumasi.surbay.adapter;

import com.bumptech.glide.Glide;
import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.Coupon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.sip.SipSession;
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

public class SellingRecyclerViewAdapter extends RecyclerView.Adapter<SellingRecyclerViewAdapter.MySellingHolder> {

    private Context context;
    LayoutInflater inflater;
    ArrayList<Coupon> selling;
    private RecyclerViewDdayAdapter.OnItemClickListener aListener = null;
    public SellingRecyclerViewAdapter(ArrayList<Coupon> selling, Context ctx) {
        this.selling = selling;
        this.context = ctx;
        inflater = LayoutInflater.from(ctx);
    }
    @NonNull
    @Override
    public SellingRecyclerViewAdapter.MySellingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.selling_item, parent, false);
        MySellingHolder holder = new MySellingHolder(view);
        return holder;
    }
    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    public void setOnItemClickListener(RecyclerViewDdayAdapter.OnItemClickListener listener) {
        this.aListener = listener ;
    }
    public Object getItem(int position) {
        return selling.get(position);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SellingRecyclerViewAdapter.MySellingHolder holder, int position) {
        Coupon coupon = selling.get(position);
        Log.d("date", "onBindViewHolder: " + coupon.getDate());
        holder.tv_coupon_selling_item_name.setText(coupon.getMenu());
        SimpleDateFormat fm = new SimpleDateFormat("yyyy.MM.dd");
        try {
            String end = fm.format(coupon.getDate());
            holder.tv_coupon_selling_item_due.setText("발행일 ~" + end);
        } catch (Exception e) {
            Log.d("date", "onBindViewHolder: " + coupon.getDate());
        }

        holder.tv_coupon_selling_item_price.setText(coupon.getCost() + " 크레딧");
        if (!(coupon.getImage_urls() == null || coupon.getImage_urls().size() == 0)) {
            Glide.with(context).load(coupon.getImage_urls().get(0)).into(holder.iv_coupon_selling_item_image);
        }

    }

    @Override
    public int getItemCount() {
        return selling == null ? 0 : selling.size();
    }

    public class MySellingHolder extends RecyclerView.ViewHolder {

        private TextView tv_coupon_selling_item_name;
        private TextView tv_coupon_selling_item_due;
        private TextView tv_coupon_selling_item_price;
        private ImageView iv_coupon_selling_item_image;
        public MySellingHolder(@NonNull View itemView) {
            super(itemView);
            tv_coupon_selling_item_name = itemView.findViewById(R.id.tv_coupon_selling_item_name);
            tv_coupon_selling_item_due = itemView.findViewById(R.id.tv_coupon_selling_item_due);
            tv_coupon_selling_item_price = itemView.findViewById(R.id.tv_coupon_selling_item_price);
            iv_coupon_selling_item_image = itemView.findViewById(R.id.iv_coupon_selling_item_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (aListener != null) {
                            aListener.onItemClick(v, pos);
                        }
                    }
                }
            });
        }
    }
}
