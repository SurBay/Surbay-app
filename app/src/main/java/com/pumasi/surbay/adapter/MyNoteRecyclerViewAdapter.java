package com.pumasi.surbay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.MyMessage;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class MyNoteRecyclerViewAdapter extends RecyclerView.Adapter<MyNoteRecyclerViewAdapter.MyNoteViewHolder> {

    private Context context;
    MyNoteRecyclerViewAdapter.OnItemClickListener aListener = null;
    private ArrayList<MyMessage> myMessages = new ArrayList<>();
    private LayoutInflater inflater;

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
            holder.tv_my_note_item_counter.setText(myMessage.getFrom_name());
        } else {
            holder.tv_my_note_item_counter.setText(myMessage.getTo_name());
        }

        if (myMessage.getContent() == null || myMessage.getContent().size() == 0) {
            holder.tv_my_note_item_text.setText("");
        } else {
            holder.tv_my_note_item_text.setText(myMessage.getContent().get(myMessage.getContent().size() - 1).getContent());
        }

        SimpleDateFormat fm = new SimpleDateFormat("MM.dd hh:mm");
        if (myMessage.getDate() == null) {
            holder.tv_my_note_item_date.setText("날짜 정보가 없습니다.");
        } else {
            holder.tv_my_note_item_date.setText(fm.format(myMessage.getDate()));
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
