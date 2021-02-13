package com.example.surbay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.surbay.R;
import com.example.surbay.UserPersonalInfo;
import com.example.surbay.classfile.Post;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ListViewAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<Post> listViewItemList = new ArrayList<Post>();

    // ListViewAdapter의 생성자
    public ListViewAdapter(ArrayList<Post> listViewItemList) {
        this.listViewItemList = listViewItemList;
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    // position : 리턴 할 자식 뷰의 위치
    // convertView : 메소드 호출 시 position에 위치하는 자식 뷰 ( if == null 자식뷰 생성 )
    // parent : 리턴할 부모 뷰, 어댑터 뷰
    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }
        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView titleTextView = (TextView) convertView.findViewById(R.id.title);
        TextView contentTextView = (TextView) convertView.findViewById(R.id.content);
        TextView dateTextView = (TextView) convertView.findViewById(R.id.date);
        TextView authornameTextView = (TextView) convertView.findViewById(R.id.level);
        TextView participantsTextView = (TextView) convertView.findViewById(R.id.participants);
        TextView goalParticipantsTextView = (TextView) convertView.findViewById(R.id.goal_participants);
        ImageView withPrizeView = (ImageView) convertView.findViewById(R.id.with_prize);
        TextView DoneView = (TextView) convertView.findViewById(R.id.done);
        TextView ddayView = (TextView) convertView.findViewById(R.id.dday);
        RelativeLayout background = (RelativeLayout)convertView.findViewById(R.id.list_item_background);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        SimpleDateFormat fm = new SimpleDateFormat("MM.dd");
        Post post = listViewItemList.get(position);
        String date = fm.format(post.getDate());
        String deadline = fm.format(post.getDeadline());
        int dday = calc_dday(post.getDeadline());

        if (dday <= 2 && dday >= 0 ){
            if (dday==0){
                ddayView.setVisibility(View.VISIBLE);
                ddayView.setText("D-Day");
            } else {
                ddayView.setVisibility(View.VISIBLE);
                ddayView.setText("D-"+(dday+1));
            }
        } else {
            ddayView.setVisibility(View.INVISIBLE);
        }

        if(!post.isWith_prize()){
            withPrizeView.setVisibility(View.INVISIBLE);
        }
        titleTextView.setText(post.getTitle());
        contentTextView.setText(post.getTarget());
        dateTextView.setText(date+"~"+deadline);

        if (UserPersonalInfo.name.equals(post.getAuthor())){
            background.setBackgroundResource(R.drawable.round_border_gray_list);
        }
        if (UserPersonalInfo.participations.contains(post.getID())){
            background.setBackgroundResource(R.drawable.round_border_gray_list);
            DoneView.setVisibility(View.VISIBLE);
        }

        authornameTextView.setText(post.getAuthor());
        participantsTextView.setText(post.getParticipants().toString());
        goalParticipantsTextView.setText(post.getGoal_participants().toString());

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    // 아이템 데이터 추가를 위한 함수.
//    public void addItem(String id, String title, String author, Integer author_lvl, String content, Integer participants, Integer goal_participants, String url, Date date, Date deadline, Boolean with_prize, String prize, Integer est_time, String target) {
//        Post item = new Post(id, title, author, author_lvl, content, participants, goal_participants, url, date, deadline, with_prize, prize, est_time, target);
//        listViewItemList.add(item);
//    }
    public void addItem(Post item){
        listViewItemList.add(item);
    }
    public void updateParticipants(int position, int participants){
        Post item = (Post) getItem(position);
        item.setParticipants(participants);
    }

    public int calc_dday(Date goal){
        Date dt = new Date();

        long diff = (goal.getTime() - dt.getTime()) / (24*60*60*1000);
        int dday = (int)diff;

        return dday;
    }
}
