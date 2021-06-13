package com.pumasi.surbay.pages.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.GeneralDetailActivity;
import com.pumasi.surbay.MainActivity;
import com.pumasi.surbay.PostDetailActivity;
import com.pumasi.surbay.R;
import com.pumasi.surbay.adapter.NotificationsAdapter;
import com.pumasi.surbay.classfile.General;
import com.pumasi.surbay.classfile.Notification;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.UserPersonalInfo;

public class NotificationsActivity extends AppCompatActivity {

    RecyclerView notificationsRecyclerView;
    NotificationsAdapter notificationsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        this.getSupportActionBar().hide();
        ImageButton back = findViewById(R.id.btnBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        notificationsRecyclerView = findViewById(R.id.notification_recyclerview);
        notificationsAdapter = new NotificationsAdapter(NotificationsActivity.this, UserPersonalInfo.notifications);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(NotificationsActivity.this, RecyclerView.VERTICAL, false));
        notificationsRecyclerView.setAdapter(notificationsAdapter);
        notificationsAdapter.setOnItemClickListener(new NotificationsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Notification notification = (Notification)notificationsAdapter.getItem(position);
                if(notification.getPost_type()==0){
                    for(int i=0; i<MainActivity.notreportedpostArrayList.size();i++){
                        Post item = MainActivity.notreportedpostArrayList.get(i);
                        if(item.getID().equals(notification.getPost_id())){
                            Intent intent = new Intent(NotificationsActivity.this, PostDetailActivity.class);
                            intent.putExtra("post", item);
                            intent.putExtra("position", i);
                            startActivityForResult(intent, 2);
                        }
                    }

                }else if(notification.getPost_type()==1){
                    for(int i=0; i<MainActivity.generalArrayList.size();i++){
                        General item = MainActivity.generalArrayList.get(i);
                        if(item.getID().equals(notification.getPost_id())){
                            Intent intent = new Intent(NotificationsActivity.this, GeneralDetailActivity.class);
                            intent.putExtra("general", item);
                            intent.putParcelableArrayListExtra("reply", item.getComments());
                            intent.putParcelableArrayListExtra("polls", item.getPolls());
                            intent.putExtra("position", i);
                            startActivityForResult(intent, 2);
                        }
                    }
                }
            }
        });

    }
}
