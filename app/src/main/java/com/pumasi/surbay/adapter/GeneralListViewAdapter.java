package com.pumasi.surbay.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pumasi.surbay.MainActivity;
import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.General;
import com.pumasi.surbay.classfile.Poll;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.classfile.Post;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class GeneralListViewAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<General> listViewItemList;

    // ListViewAdapter의 생성자
    public GeneralListViewAdapter(ArrayList<General> listViewItemList) {
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
        General general = listViewItemList.get(pos);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.general_list_item, parent, false);
        }


        TextView title = convertView.findViewById(R.id.title);
        TextView done = convertView.findViewById(R.id.done);
        TextView participants = convertView.findViewById(R.id.participants);
        TextView date = convertView.findViewById(R.id.date);
        TextView likes = convertView.findViewById(R.id.likes);
        TextView comments = convertView.findViewById(R.id.comments);

        Log.d("onadapter", ""+listViewItemList.size()+"   ");
        title.setText("Q. "+ general.getTitle());

        Date now = new Date();
        if(general.getDone()==true || now.after(general.getDeadline())){
            done.setVisibility(View.VISIBLE);
        }else{
            done.setVisibility(View.GONE);
        }
        participants.setText(general.getParticipants().toString());
        SimpleDateFormat fm = new SimpleDateFormat("MM.dd kk:mm", Locale.KOREA);
        Log.d("realdateis", ""+general.getDate());
        Log.d("dateis", ""+fm.format(general.getDate()));
        date.setText(fm.format(general.getDate()));
        likes.setText(general.getLikes().toString());
        comments.setText(String.valueOf(general.getComments().size()));

        ArrayList<Poll> polls = general.getPolls();
        FrameLayout poll2content = convertView.findViewById(R.id.poll_2contents);
        FrameLayout poll3content = convertView.findViewById(R.id.poll_3contents);
        FrameLayout poll4content = convertView.findViewById(R.id.poll_4contents);

        if(polls.size()==2){
            poll2content.setVisibility(View.VISIBLE);
            poll3content.setVisibility(View.GONE);
            poll4content.setVisibility(View.GONE);
            TextView content2_1 = convertView.findViewById(R.id.content2_1);
            TextView content2_2 = convertView.findViewById(R.id.content2_2);
            String content1 = polls.get(0).getContent();
            if(content1.length()>5)
                content1 = content1.substring(0,4) + "...";
            String content2 = polls.get(1).getContent();
            if(content2.length()>5)
                content2 = content2.substring(0,4) + "...";
            content2_1.setText(content1);
            content2_2.setText(content2);

        }else if(polls.size()==3){
            poll2content.setVisibility(View.GONE);
            poll3content.setVisibility(View.VISIBLE);
            poll4content.setVisibility(View.GONE);
            TextView content3_1 = convertView.findViewById(R.id.content3_1);
            TextView content3_2 = convertView.findViewById(R.id.content3_2);
            TextView content3_3 = convertView.findViewById(R.id.content3_3);
            String content1 = polls.get(0).getContent();
            if(content1.length()>5)
                content1 = content1.substring(0,4) + "...";
            String content2 = polls.get(1).getContent();
            if(content2.length()>5)
                content2 = content2.substring(0,4) + "...";
            String content3 = polls.get(2).getContent();
            if(content3.length()>5)
                content3 = content2.substring(0,4) + "...";
            content3_1.setText(content1);
            content3_2.setText(content2);
            content3_3.setText(content3);

        }else if(polls.size()==4){
            poll2content.setVisibility(View.GONE);
            poll3content.setVisibility(View.GONE);
            poll4content.setVisibility(View.VISIBLE);
            TextView content4_1 = convertView.findViewById(R.id.content4_1);
            TextView content4_2 = convertView.findViewById(R.id.content4_2);
            TextView content4_3 = convertView.findViewById(R.id.content4_3);
            TextView content4_4 = convertView.findViewById(R.id.content4_4);
            String content1 = polls.get(0).getContent();
            if(content1.length()>7)
                content1 = content1.substring(0,6) + "...";
            String content2 = polls.get(1).getContent();
            if(content2.length()>7)
                content2 = content2.substring(0,6) + "...";
            String content3 = polls.get(2).getContent();
            if(content3.length()>7)
                content3 = content1.substring(0,6) + "...";
            String content4 = polls.get(3).getContent();
            if(content4.length()>7)
                content4 = content2.substring(0,6) + "...";
            content4_1.setText(content1);
            content4_2.setText(content2);
            content4_3.setText(content3);
            content4_4.setText(content4);
            TextView extra_contents = convertView.findViewById(R.id.content4_extra);
            extra_contents.setVisibility(View.GONE);

        }else if(polls.size()>4){
            poll2content.setVisibility(View.GONE);
            poll3content.setVisibility(View.GONE);
            poll4content.setVisibility(View.VISIBLE);
            TextView content4_1 = convertView.findViewById(R.id.content4_1);
            TextView content4_2 = convertView.findViewById(R.id.content4_2);
            TextView content4_3 = convertView.findViewById(R.id.content4_3);
            TextView content4_4 = convertView.findViewById(R.id.content4_4);
            String content1 = polls.get(0).getContent();
            if(content1.length()>7)
                content1 = content1.substring(0,6) + "...";
            String content2 = polls.get(1).getContent();
            if(content2.length()>7)
                content2 = content2.substring(0,6) + "...";
            String content3 = polls.get(2).getContent();
            if(content3.length()>7)
                content3 = content1.substring(0,6) + "...";
            String content4 = polls.get(3).getContent();
            if(content4.length()>7)
                content4 = content2.substring(0,6) + "...";
            content4_1.setText(content1);
            content4_2.setText(content2);
            content4_3.setText(content3);
            content4_4.setText(content4);
            TextView extra_contents = convertView.findViewById(R.id.content4_extra);
            extra_contents.setVisibility(View.VISIBLE);
            extra_contents.setText("+"+(polls.size()-4));
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

    public void remove(int position){
        listViewItemList.remove(position);
        notifyDataSetChanged();
    }

    public void changeItem(){
        try {
            MainActivity.getGenerals();
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }
}
