package com.pumasi.surbay.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pumasi.surbay.MainActivity;
import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.classfile.Post;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class PostListViewAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<Post> listViewItemList;

    // ListViewAdapter의 생성자
    public PostListViewAdapter(ArrayList<Post> listViewItemList) {
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
        TextView authornameTextView = (TextView) convertView.findViewById(R.id.author_info);
        TextView participantsTextView = (TextView) convertView.findViewById(R.id.participants);
        TextView goalParticipantsTextView = (TextView) convertView.findViewById(R.id.goal_participants);
        ImageView withPrizeView = (ImageView) convertView.findViewById(R.id.with_prize);
        TextView DoneView = (TextView) convertView.findViewById(R.id.done);
        TextView ddayView = (TextView) convertView.findViewById(R.id.dday);
        LinearLayout background = (LinearLayout)convertView.findViewById(R.id.list_item_background);
        ImageView profileView = (ImageView) convertView.findViewById(R.id.list_profile);
        TextView commentView = (TextView) convertView.findViewById(R.id.comments);


        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        SimpleDateFormat fm = new SimpleDateFormat("MM.dd");
        Post post = listViewItemList.get(position);
        String[] adminsList = {"SurBay_Admin", "SurBay_dev", "SurBay_dev2", "SurBay_des", "djrobort", "surbaying"};
        ArrayList<String> admins = new ArrayList<>(Arrays.asList(adminsList));
        if(admins.contains(post.getAuthor_userid())){
            profileView.setImageResource(R.drawable.surbay_profile);
        }else{
            profileView.setImageResource(R.drawable.surbay_character_lv1);
        }


        String date = fm.format(post.getDate());
        String deadline = fm.format(post.getDeadline());

        int dday = calc_dday(post.getDeadline());
        Date now = new Date();
        if(now.after(post.getDeadline()) || post.isDone()){
//            background.setBackgroundResource(R.drawable.round_border_gray_list);
            ddayView.setVisibility(View.VISIBLE);
            ddayView.setText("종료");
        }else if (dday <= 3 && dday >= 0 ){
            if (dday==0){
                ddayView.setVisibility(View.VISIBLE);
                ddayView.setText("D-Day");
            } else {
                ddayView.setVisibility(View.VISIBLE);
                ddayView.setText("D-"+dday);
            }
        }
        else {
            ddayView.setVisibility(View.INVISIBLE);
        }

        if(!post.isWith_prize()){
            withPrizeView.setVisibility(View.INVISIBLE);
        }else{
            withPrizeView.setVisibility(View.VISIBLE);
        }
        titleTextView.setText(post.getTitle());

        contentTextView.setText(post.getTarget());
        dateTextView.setText(date+" - "+deadline);

        if (UserPersonalInfo.participations.contains(post.getID()) && !UserPersonalInfo.userID.equals(post.getAuthor_userid())){ //참여했으면 회색
//            background.setBackgroundResource(R.drawable.round_border_gray_list);
            DoneView.setVisibility(View.VISIBLE);
        }else if(!(now.after(post.getDeadline()) || post.isDone())){ //참여 안하고 안끝났으면 민트색
//            background.setBackgroundResource(R.drawable.participants_round_border);
            DoneView.setVisibility(View.GONE);
        }else{
//            background.setBackgroundResource(R.drawable.round_border_gray_list);
            DoneView.setVisibility(View.GONE);
        }

        Integer done = DoneView.getVisibility();
        Drawable back = background.getBackground();
        if (UserPersonalInfo.userID.equals(post.getAuthor_userid())){ //내거면 회색
//            background.setBackgroundResource(R.drawable.round_border_gray_list);
            DoneView.setVisibility(View.GONE);
        }
        else{
            DoneView.setVisibility(done);
//            background.setBackground(back);
        }

        if(post.getAnnonymous()==true){
            authornameTextView.setText("익명");
        }else{
            authornameTextView.setText(post.getAuthor());
        }

        if(post.getParticipants()>999){
            participantsTextView.setText("999+");
        }else {
            participantsTextView.setText(post.getParticipants().toString());
        }

        if(post.getGoal_participants()>999){
            goalParticipantsTextView.setText("999+");
        }else {
            goalParticipantsTextView.setText(post.getGoal_participants().toString());
        }

        if(post.getComments().size()==0){
            commentView.setVisibility(View.GONE);
        }else{
            commentView.setText("["+post.getComments().size()+"]");
            commentView.setVisibility(View.VISIBLE);
        }

        if(post.getComments().size()>0 && (UserPersonalInfo.participations.contains(post.getID()) && !UserPersonalInfo.userID.equals(post.getAuthor_userid()))){
            titleTextView.setMaxEms(11);
        }else if(post.getComments().size()>0){
            titleTextView.setMaxEms(14);
        }else if(UserPersonalInfo.participations.contains(post.getID()) && !UserPersonalInfo.userID.equals(post.getAuthor_userid())){
            titleTextView.setMaxEms(13);
        }else{
            titleTextView.setMaxEms(15);
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
    public void changeItem(){
        try {
            MainActivity.getPosts();
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }
    public void remove(int position){
        listViewItemList.remove(position);
        notifyDataSetChanged();
    }
    public void updateParticipants(int position, int participants){
        Post item = (Post) getItem(position);
        item.setParticipants(participants);
    }

    public int calc_dday(Date goal){
        Date dt = new Date();

        long diff = 0;
        int dday = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MM yyyy");
            SimpleDateFormat fm = new SimpleDateFormat("dd MM yyyy");
            LocalDate date1 = LocalDate.parse(fm.format(goal), dtf);
            LocalDate date2 = LocalDate.parse(fm.format(dt), dtf);
            diff = ChronoUnit.DAYS.between(date2, date1);
            dday = (int) diff;
        }
        else{
            diff = goal.getDate()-dt.getDate();
            dday = (int) diff;
        }
        return dday;
    }
}
