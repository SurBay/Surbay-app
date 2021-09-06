package com.pumasi.surbay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.PostNonSurvey;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class FeedbackListViewAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<PostNonSurvey> listViewItemList = new ArrayList<PostNonSurvey>();
    private String[] categories = {"운영정책", "앱 구동 불편사항","기능 추가/개선", "기타"};

    // ListViewAdapter의 생성자
    public FeedbackListViewAdapter(ArrayList<PostNonSurvey> listViewItemList) {
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
            convertView = inflater.inflate(R.layout.non_survey_list_item, parent, false);
        }
        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView titleTextView = (TextView) convertView.findViewById(R.id.tv_tip_item_title);
        TextView dateTextView = (TextView) convertView.findViewById(R.id.tv_tip_item_date);
        TextView likesTextView = (TextView) convertView.findViewById(R.id.tv_tip_item_likes);
        ImageView likesimgae = (ImageView) convertView.findViewById(R.id.non_recomimage);
        TextView categoryTextView = (TextView) convertView.findViewById(R.id.tv_tip_item_category);
        ImageView profileView = (ImageView) convertView.findViewById(R.id.iv_tip_item_profile);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        SimpleDateFormat fm = new SimpleDateFormat("MM.dd");
        PostNonSurvey post = listViewItemList.get(position);
        String date = fm.format(post.getDate());

        titleTextView.setText(post.getTitle());
        dateTextView.setText(date);

        categoryTextView.setText(categories[post.getCategory()]);

        likesimgae.setVisibility(View.GONE);
        likesTextView.setVisibility(View.GONE);
        String[] adminsList = {"SurBay_Admin", "SurBay_dev", "SurBay_dev2", "SurBay_des", "djrobort", "surbaying"};
        ArrayList<String> admins = new ArrayList<>(Arrays.asList(adminsList));
        if(admins.contains(post.getAuthor_userid())){
            profileView.setImageResource(R.drawable.surbay_profile);
        }else{
            profileView.setImageResource(R.drawable.surbay_character_lv1);
        }

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
    public void addItem(PostNonSurvey item){
        listViewItemList.add(item);
    }

}
