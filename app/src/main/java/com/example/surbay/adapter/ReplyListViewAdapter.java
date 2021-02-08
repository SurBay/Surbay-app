package com.example.surbay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.surbay.R;
import com.example.surbay.classfile.Reply;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ReplyListViewAdapter extends BaseAdapter {

    private ArrayList<Reply> listViewItemList = new ArrayList<Reply>();

    public ReplyListViewAdapter(ArrayList<Reply> listViewItemList) {
        this.listViewItemList = listViewItemList;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.detail_reply_list_item, parent, false);
        }

        TextView replydateview = (TextView)convertView.findViewById(R.id.reply_date);
        TextView replycontentview = (TextView)convertView.findViewById(R.id.reply_context);

        SimpleDateFormat fm = new SimpleDateFormat("MM-dd'T'hh:mm");
        Reply reply = listViewItemList.get(position);
        String date = fm.format(reply.getDate());

        replydateview.setText(date);
        replycontentview.setText(reply.getContent());

        return convertView;
    }

    public void addItem(Reply item){
        listViewItemList.add(item);
    }

    @Override
    public int getCount() {return listViewItemList.size();    }

    @Override
    public Object getItem(int position) {return listViewItemList.get(position);    }

    @Override
    public long getItemId(int position) {return position;    }

}
