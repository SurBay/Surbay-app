package com.pumasi.surbay.pages.mypage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.ContentDetailActivity;
import com.pumasi.surbay.classfile.Content;
import com.pumasi.surbay.classfile.ContentReReply;
import com.pumasi.surbay.classfile.ContentReply;
import com.pumasi.surbay.classfile.Poll;
import com.pumasi.surbay.classfile.PostNonSurvey;
import com.pumasi.surbay.classfile.ReReply;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.pages.boardpage.GeneralDetailActivity;
import com.pumasi.surbay.pages.MainActivity;
import com.pumasi.surbay.pages.boardpage.PostDetailActivity;
import com.pumasi.surbay.R;
import com.pumasi.surbay.adapter.NotificationsAdapter;
import com.pumasi.surbay.classfile.General;
import com.pumasi.surbay.classfile.Notification;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.tools.ServerTransport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class NotificationsActivity extends AppCompatActivity {

    RecyclerView notificationsRecyclerView;
    NotificationsAdapter notificationsAdapter;
    private Context context;
    private ImageButton ib_back;
    private ArrayList<Notification> notifications = new ArrayList<>();
    private ServerTransport st;
    private final int RESEARCH = 0;
    private final int VOTE = 1;
    private final int GIFT = 2;
    private final int START = 3;
    private final int CONTENT = 4;
    private final int NOTE = 5;
    private final int FEEDBACK = 6;
    private boolean item_clickable = true;
    // 0: 설문, 1: 자게, 2: 경품, 3: 시작화면, 4: content, 5. 쪽지함, 6. 피드백

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        getSupportActionBar().hide();
        context = getApplicationContext();
        st = new ServerTransport(context);

        notifications.addAll(UserPersonalInfo.notifications);
        Collections.reverse(notifications);
        ib_back = findViewById(R.id.ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        notificationsRecyclerView = findViewById(R.id.notification_recyclerview);
        notificationsAdapter = new NotificationsAdapter(NotificationsActivity.this, notifications);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(NotificationsActivity.this, RecyclerView.VERTICAL, false));
        notificationsRecyclerView.setAdapter(notificationsAdapter);
        notificationsAdapter.setOnItemClickListener(new NotificationsAdapter.OnItemClickListener() {
            // 0: 설문, 1: 자게, 2: 경품, 3: 시작화면, 4: content, 5. 쪽지함, 6. 피드백
            @Override
            public void onItemClick(View v, int position) {
                Notification notification = (Notification)notificationsAdapter.getItem(position);
                int notification_type = notification.getPost_type();
                if (item_clickable) {
                    item_clickable  = false;
                    switch (notification_type) {
                        case RESEARCH:
                            item_clickable = true;
                            st.getExecute(notification.getPost_id(), 0, 1);
                            break;
                        case VOTE:
                            item_clickable = true;
                            st.getExecute(notification.getPost_id(), 1, 0);
                            break;
                        case GIFT:
                            Intent intent = new Intent(context, IGotGifts.class);
                            item_clickable = true;
                            startActivity(intent);
                            break;
                        case START:
                            item_clickable = true;
                            break;
                        case CONTENT:
                            item_clickable = true;
                            st.getExecute(notification.getPost_id(), 2, 0);
                            break;
                        case NOTE:
                            item_clickable = true;
                            break;
                        case FEEDBACK:
                            for (PostNonSurvey feedback : MainActivity.feedbackArrayList) {
                                if (notification.getPost_id().equals(feedback.getID())) {
                                    Intent intent2 = new Intent(context, Feedbackdetail.class);
                                    intent2.putExtra("post", feedback);
                                    intent2.putExtra("position", position);
                                    intent2.putParcelableArrayListExtra("reply", feedback.getComments());
                                    startActivity(intent2);
                                    break;
                                }
                            }
                            item_clickable = true;
                            break;
                    }
                }


            }
        });
    }
}
