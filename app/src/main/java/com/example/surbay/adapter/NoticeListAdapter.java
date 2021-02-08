package com.example.surbay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.surbay.R;
import com.example.surbay.classfile.PostNonSurvey;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class NoticeListAdapter extends BaseAdapter {

    private ArrayList<PostNonSurvey> listViewItemList = new ArrayList<PostNonSurvey>();

    public NoticeListAdapter(ArrayList<PostNonSurvey> listViewItemList) {
        this.listViewItemList = listViewItemList;
    }



    @Override
    public int getCount() {return listViewItemList.size();    }

    @Override
    public long getItemId(int position) {        return position;    }

    @Override
    public Object getItem(int position) {        return listViewItemList.get(position);    }

    public void addItem(PostNonSurvey item){        listViewItemList.add(item);    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.notice_listitem, parent, false);
        }
        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView titleTextView = (TextView) convertView.findViewById(R.id.notice_title);
        TextView contentTextView = (TextView) convertView.findViewById(R.id.notice_content);
        TextView dateTextView = (TextView) convertView.findViewById(R.id.notice_date);
        TextView authorTextView = (TextView) convertView.findViewById(R.id.notice_auther);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        SimpleDateFormat fm = new SimpleDateFormat("MM.dd");
        PostNonSurvey post = listViewItemList.get(position);
        String date = fm.format(post.getDate());

        titleTextView.setText(post.getTitle());
        contentTextView.setText(post.getContent());
        dateTextView.setText(date);
        authorTextView.setText(post.getAuthor());

        return convertView;
    }
}
