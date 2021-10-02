package com.pumasi.surbay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.Content;
import com.pumasi.surbay.pages.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class ContentRecyclerViewAdapter extends RecyclerView.Adapter<ContentRecyclerViewAdapter.MyContentViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Content> contentArrayList = new ArrayList<>();
    private ContentRecyclerViewAdapter.OnItemClickListener aListener = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    public void setOnItemClickListener(ContentRecyclerViewAdapter.OnItemClickListener listener) {
        this.aListener = listener ;
    }
    public ContentRecyclerViewAdapter(ArrayList<Content> contentArrayList, Context ctx) {
        this.context = ctx;
        this.contentArrayList = contentArrayList;
        inflater = LayoutInflater.from(ctx);
    }

    public Object getItem(int position) {
        return contentArrayList.get(position);
    }
    @NonNull
    @Override
    public MyContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.content_item, parent, false);
        MyContentViewHolder holder = new MyContentViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyContentViewHolder holder, int position) {
        Content content = contentArrayList.get(position);
        int size = (int) (MainActivity.screen_width_px / 2.19786096257);

        holder.iv_board_content_item_image.getLayoutParams().height = size;
        holder.iv_board_content_item_image.getLayoutParams().width = size;
        Glide.with(context).load(content.getImage_urls().get(0)).override(size, size).into(holder.iv_board_content_item_image);
        holder.tv_board_content_item_title.setText(content.getTitle());
        SimpleDateFormat fm = new SimpleDateFormat("MM.dd");
        holder.tv_board_content_item_date.setText(fm.format(content.getDate()));
    }

    @Override
    public int getItemCount() {
        return contentArrayList == null ? 0 : contentArrayList.size();

    }

    public void setItem(ArrayList<Content> contentArrayList) {
        this.contentArrayList = contentArrayList;
    }



    public class MyContentViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_board_content_item_image;
        TextView tv_board_content_item_title;
        TextView tv_board_content_item_date;
        public MyContentViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_board_content_item_image = itemView.findViewById(R.id.iv_board_content_item_image);
            tv_board_content_item_title = itemView.findViewById(R.id.tv_board_content_item_title);
            tv_board_content_item_date = itemView.findViewById(R.id.tv_board_content_item_date);

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
