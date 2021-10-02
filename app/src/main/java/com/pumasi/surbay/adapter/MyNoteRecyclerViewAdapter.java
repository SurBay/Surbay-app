package com.pumasi.surbay.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.R;
import com.pumasi.surbay.Tools;
import com.pumasi.surbay.classfile.MessageContent;
import com.pumasi.surbay.classfile.MyMessage;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class MyNoteRecyclerViewAdapter extends RecyclerView.Adapter<MyNoteRecyclerViewAdapter.MyNoteViewHolder> {

    private Context context;
    MyNoteRecyclerViewAdapter.OnItemClickListener aListener = null;
    private ArrayList<MyMessage> myMessages = new ArrayList<>();
    private LayoutInflater inflater;
    private String myself_id;
    private String myself_name;
    private String counter_id;
    private String counter_name;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    public void setOnItemClickListener(MyNoteRecyclerViewAdapter.OnItemClickListener listener) {
        this.aListener = listener ;
    }

    public MyNoteRecyclerViewAdapter(ArrayList<MyMessage> myMessages, Context ctx) {
        this.context = ctx;
        this.myMessages = myMessages;
        this.inflater = LayoutInflater.from(ctx);
    }

    public Object getItem(int position) {
        return myMessages.get(position);
    }
    @NonNull
    @Override
    public MyNoteRecyclerViewAdapter.MyNoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.my_note_item, parent, false);
        MyNoteViewHolder holder = new MyNoteViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull MyNoteRecyclerViewAdapter.MyNoteViewHolder holder, int position) {
        MyMessage myMessage = myMessages.get(position);
        if (UserPersonalInfo.name.equals(myMessage.getTo_name())) {
            myself_id = myMessage.getTo_userID();
            myself_name = myMessage.getTo_name();
            counter_id = myMessage.getFrom_userID();
            counter_name = myMessage.getFrom_name();
        } else {
            myself_id = myMessage.getFrom_userID();
            myself_name = myMessage.getFrom_name();
            counter_id = myMessage.getTo_userID();
            counter_name = myMessage.getTo_name();
        }
        boolean answered = false;
        for (MessageContent messageContent : myMessage.getContent()) {
            if (messageContent.getWriter().equals(counter_id)) {
                answered = true;
            }
        }
        Log.d("counter_id", "onBindViewHolder: " + counter_id);
        if (counter_id.equals("알 수 없음")) {
            holder.tv_my_note_item_counter.setText("알 수 없음");
        }
        else if (answered) {
            holder.tv_my_note_item_counter.setText(counter_name);
        } else {
            holder.tv_my_note_item_counter.setText("익명");
        }

        if (myMessage.getContent() == null || myMessage.getContent().size() == 0) {
            holder.tv_my_note_item_text.setText("");
        } else {
            holder.tv_my_note_item_text.setText(myMessage.getContent().get(myMessage.getContent().size() - 1).getContent());
        }

        if (myMessage.getDate() == null) {
            holder.tv_my_note_item_date.setText("날짜 정보가 없습니다.");
        } else {
            holder.tv_my_note_item_date.setText(new Tools().convertTimeZone(context, myMessage.getDate(), "MM.dd hh:mm"));
        }
    }

    @Override
    public int getItemCount() {
        return myMessages == null ? 0 : myMessages.size();
    }
    public void setItem(ArrayList<MyMessage> myMessages) {
        this.myMessages = myMessages;
    }
    public class MyNoteViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_my_note_item_counter;
        private TextView tv_my_note_item_text;
        private TextView tv_my_note_item_date;

        public MyNoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_my_note_item_counter = itemView.findViewById(R.id.tv_my_note_item_counter);
            tv_my_note_item_text = itemView.findViewById(R.id.tv_my_note_item_text);
            tv_my_note_item_date = itemView.findViewById(R.id.tv_my_note_item_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
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
