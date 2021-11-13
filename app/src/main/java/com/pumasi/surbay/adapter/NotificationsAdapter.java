package com.pumasi.surbay.adapter;

import android.content.Context;

import android.graphics.Color;
import android.media.Image;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.Notification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.MyViewHolder> {
    private static OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Notification> imageModelArrayList = new ArrayList<>();


    public NotificationsAdapter(Context ctx, ArrayList<Notification> imageModelArrayList){
        this.context = ctx;
        inflater = LayoutInflater.from(ctx);
        this.imageModelArrayList = imageModelArrayList;

    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    @Override
    public NotificationsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.notification_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(NotificationsAdapter.MyViewHolder holder, int position) {

        ArrayList<String> highlight_words = new ArrayList<>(Arrays.asList("댓글", "달성", "종료", "마감", "참여 보상"));
        Notification notification = imageModelArrayList.get(position);

        String title = notification.getTitle();
        holder.tv_my_notification_title.setText(title);

        for (String highlight_word : highlight_words) {
            SpannableString spannableString = new SpannableString(title);
            int start = title.indexOf(highlight_word);
            int end;
            if (start != -1) {
                end = start + highlight_word.length();
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#3AD1BF")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.tv_my_notification_title.setText(spannableString);
                break;
            }

        }

        holder.tv_my_notification_content.setText(imageModelArrayList.get(position).getContent());
        SimpleDateFormat fm = new SimpleDateFormat("MM.dd kk:mm");
        try {
            holder.tv_my_notification_date.setText(fm.format(imageModelArrayList.get(position).getDate()));
        } catch (Exception e) {
            holder.tv_my_notification_date.setText("");
        }

    }

    @Override
    public int getItemCount() {
        return imageModelArrayList == null ? 0 : imageModelArrayList.size();
    }

    public Object getItem(int position) {
        return imageModelArrayList.get(position);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageButton ib_my_notification_cancel;

        private TextView tv_my_notification_title;
        private TextView tv_my_notification_content;
        private TextView tv_my_notification_date;

        public MyViewHolder(View itemView) {
            super(itemView);

            ib_my_notification_cancel = (ImageButton) itemView.findViewById(R.id.ib_my_notification_cancel);

            tv_my_notification_title = (TextView) itemView.findViewById(R.id.tv_my_notification_title);
            tv_my_notification_content = (TextView) itemView.findViewById(R.id.tv_my_notification_content);
            tv_my_notification_date = (TextView) itemView.findViewById(R.id.tv_my_notification_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });
        }

    }

}

