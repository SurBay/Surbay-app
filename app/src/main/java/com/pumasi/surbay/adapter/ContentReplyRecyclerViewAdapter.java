package com.pumasi.surbay.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.ContentDetailCommentsActivity;
import com.pumasi.surbay.R;
import com.pumasi.surbay.Tools;
import com.pumasi.surbay.classfile.ContentReReply;
import com.pumasi.surbay.classfile.ContentReply;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.Notification;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ContentReplyRecyclerViewAdapter extends RecyclerView.Adapter<ContentReplyRecyclerViewAdapter.ContentReplyViewHolder> {

    private Tools tools = new Tools();
    private ContentReplyHandler handler = new ContentReplyHandler();
    private boolean get_done = false;
    private boolean reply_report_done = false;
    private boolean reply_delete_done = false;
    private boolean reply_block_done = false;
    private boolean person_done = false;
    private String content_id;
    private Context context;
    private LayoutInflater inflater;
    private CustomDialog customDialog;
    private ArrayList<ContentReply> contentReplies = new ArrayList<>();
    private ContentReplyRecyclerViewAdapter.OnReplyClickListener aListener= null;
    private ContentReplyRecyclerViewAdapter.OnMenuClickListener bListener= null;
    private ContentReReplyRecyclerViewAdapter contentReReplyRecyclerViewAdapter;
    private int click_position = -1;

    public ContentReplyRecyclerViewAdapter(ArrayList<ContentReply> contentReplies, Context ctx, String content_id) {
        this.context = ctx;
        this.contentReplies = contentReplies;
        this.content_id = content_id;
        inflater = LayoutInflater.from(ctx);
    }
    public interface OnReplyClickListener {
        void onReplyClick(View v, int position) ;
    }
    public interface OnMenuClickListener {
        void onMenuClick(View v, int position);
    }
    public void setOnItemClickListener(ContentReplyRecyclerViewAdapter.OnReplyClickListener listener) {
        this.aListener = listener ;
    }
    public void setOnItemClickListener(ContentReplyRecyclerViewAdapter.OnMenuClickListener listener) {
        this.bListener = listener ;
    }

    public Object getItem(int position) {
        return contentReplies.get(position);
    }
    public void Click(int click_position) {
        this.click_position = click_position;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContentReplyRecyclerViewAdapter.ContentReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.detail_reply_list_item, parent, false);
        ContentReplyViewHolder holder = new ContentReplyViewHolder(view);
        return holder;
    }

    public void setItem(ArrayList<ContentReply> contentReplies) {
        this.contentReplies = contentReplies;
    }
    @Override
    public void onBindViewHolder(@NonNull ContentReplyRecyclerViewAdapter.ContentReplyViewHolder holder, int position) {
        if (click_position == position) {
            holder.comment_holder.setBackgroundColor(context.getResources().getColor(R.color.teal_200));
            holder.reply_reply.setImageResource(R.drawable.reply_teal);
        } else {
            holder.comment_holder.setBackgroundColor(context.getResources().getColor(R.color.gray));
            holder.reply_reply.setImageResource(R.drawable.reply);
        }
        ContentReply contentReply = contentReplies.get(position);
        holder.reply_name.setText(contentReply.getWriter_name());
        holder.reply_date.setText(tools.convertTimeZone(context, contentReply.getDate(), "MM.dd HH:mm"));
        if (UserPersonalInfo.blocked_users.contains(contentReply.getWriter())) {
            holder.reply_context.setText("(차단한 유저의 댓글입니다.)");
        } else if (contentReply.getReports().size() > 4) {
            holder.reply_context.setText("(신고가 누적된 댓글입니다.)");
        } else {
            holder.reply_context.setText(contentReply.getContent());
        }
        if (contentReply.getReply() != null && contentReply.getReply().size() > 0) {
            holder.reply_context.setPadding(0, 0 ,0, (int) context.getResources().getDisplayMetrics().density * 14);
        }
        contentReReplyRecyclerViewAdapter = new ContentReReplyRecyclerViewAdapter(contentReply.getReply(), context);
        holder.rv_reply_reply.setAdapter(contentReReplyRecyclerViewAdapter);
        holder.rv_reply_reply.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        contentReReplyRecyclerViewAdapter.setOnItemClickListener(new OnMenuClickListener() {
            @Override
            public void onMenuClick(View v, int pos) {
                if (UserPersonalInfo.userID.equals("nonMember")) {
                    customDialog = new CustomDialog(context, null);
                    customDialog.show();
                    customDialog.setMessage("비회원은 이용할 수 없는 기능입니다.");
                    customDialog.setNegativeButton("확인");
                } else {
                    ContentReReply contentReReply = contentReply.getReply().get(pos);
                    PopupMenu popupMenu = new PopupMenu(context, v);
                    popupMenu.getMenuInflater().inflate(R.menu.content_reply_popup_menu, popupMenu.getMenu());
                    if (UserPersonalInfo.email.equals(contentReReply.getWriter())) {
                        popupMenu.getMenu().getItem(1).setVisible(false);
                        popupMenu.getMenu().getItem(1).setEnabled(false);
                        popupMenu.getMenu().getItem(2).setVisible(false);
                        popupMenu.getMenu().getItem(2).setEnabled(false);
                    } else {
                        popupMenu.getMenu().getItem(0).setVisible(false);
                        popupMenu.getMenu().getItem(0).setEnabled(false);
                    }

                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete:
                                    customDialog = new CustomDialog(context, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            BackgroundReplyDeleteThread backgroundReplyDeleteThread = new BackgroundReplyDeleteThread(contentReply.getId(), contentReReply.getId());
                                            Log.d("reply", "onMenuItemClick: " + contentReply.getId() + ", " + contentReReply.getId());
                                            backgroundReplyDeleteThread.start();
                                            customDialog.dismiss();
                                        }
                                    });
                                    customDialog.show();
                                    customDialog.setMessage("댓글을 삭제하겠습니까?");
                                    customDialog.setPositiveButton("삭제");
                                    customDialog.setNegativeButton("취소");
                                    return true;
                                case R.id.report:
                                    if (contentReReply.getReports().contains(UserPersonalInfo.email)) {
                                        customDialog = new CustomDialog(context, null);
                                        customDialog.show();
                                        customDialog.setMessage("이미 신고한 댓글입니다");
                                        customDialog.setNegativeButton("확인");
                                    } else {
                                        customDialog = new CustomDialog(context, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                ArrayList<String> reports = new ArrayList<>(Arrays.asList("욕설/비하", "상업적 광고 및 판매", "낚시/놀람/도배/사기", "게시판 성격에 부적절함", "기타"));
                                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                builder.setTitle("신고 사유");
                                                builder.setItems(R.array.reportreason, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        BackgroundReplyReportThread backgroundReplyReportThread = new BackgroundReplyReportThread(contentReply.getId(), contentReReply.getId(), reports.get(which));
                                                        backgroundReplyReportThread.start();
                                                        Toast.makeText(context, "신고가 접수되었습니다", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                Dialog dialog = builder.create();
                                                dialog.show();
                                                customDialog.dismiss();

                                            }
                                        });
                                        customDialog.show();
                                        customDialog.setMessage("댓글을 신고하시겠습니까?");
                                        customDialog.setPositiveButton("신고");
                                        customDialog.setNegativeButton("취소");
                                    }
                                    return true;
                                case R.id.block:
                                    if (UserPersonalInfo.blocked_users.contains(contentReReply.getWriter())) {
                                        customDialog = new CustomDialog(context, null);
                                        customDialog.show();
                                        customDialog.setMessage("이미 차단한 유저입니다.");
                                        customDialog.setNegativeButton("확인");
                                    } else {
                                        customDialog = new CustomDialog(context, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                BackgroundReplyBlockThread backgroundReplyBlockThread = new BackgroundReplyBlockThread(contentReReply.getWriter());
                                                backgroundReplyBlockThread.start();
                                                customDialog.dismiss();
                                            }
                                        });
                                        customDialog.show();
                                        customDialog.setMessage("상대를 차단하겠습니까?\n상대를 차단하시면 더 이상 상대방이 보낸 쪽지를 볼 수 없습니다.");
                                        customDialog.setPositiveButton("차단");
                                        customDialog.setNegativeButton("취소");
                                    }

                                    return true;
                            }
                            return false;
                        }
                    });
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return contentReplies == null ? 0 : contentReplies.size();
    }

    public class ContentReplyViewHolder extends RecyclerView.ViewHolder {
        private ImageView reply_reply;
        private ImageView reply_menu;
        private TextView reply_name;
        private TextView reply_date;
        private TextView reply_context;
        private RecyclerView rv_reply_reply;
        private LinearLayout comment_holder;

        public ContentReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            reply_reply = itemView.findViewById(R.id.reply_reply);
            reply_name = itemView.findViewById(R.id.reply_name);
            reply_menu = itemView.findViewById(R.id.reply_menu);
            reply_date = itemView.findViewById(R.id.reply_date);
            reply_context = itemView.findViewById(R.id.reply_context);
            rv_reply_reply = itemView.findViewById(R.id.rv_reply_reply);
            comment_holder = itemView.findViewById(R.id.comment_holder);

            reply_reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (aListener != null) {
                            aListener.onReplyClick(v, pos) ;
                        }
                    }
                }
            });
            reply_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (bListener != null) {
                            bListener.onMenuClick(v, pos); ;
                        }
                    }
                }
            });
        }
    }


    public void getComments(String content_object_id) {
        try {
            String requestURL = context.getResources().getString(R.string.server) + "/api/content/getcontent/" + content_object_id;
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET, requestURL, null, response -> {
                try {
                    JSONObject responseContent = new JSONObject(response.toString());
                    String id = responseContent.getString("_id");
                    ArrayList<String> image_urls = new ArrayList<>();
                    JSONArray ja = responseContent.getJSONArray("image_urls");
                    for (int j = 0; j < ja.length(); j++) {
                        image_urls.add(ja.getString(j));
                    }
                    int likes = responseContent.getInt("likes");
                    int visit = responseContent.getInt("visit");
                    boolean hide = responseContent.getBoolean("hide");
                    String title = responseContent.getString("title");
                    String author = responseContent.getString("author");
                    String content_ = responseContent.getString("content");
                    SimpleDateFormat fm = new SimpleDateFormat(context.getResources().getString(R.string.date_format));
                    Date date = null;
                    try {
                        date = fm.parse(responseContent.getString("date"));
                    } catch (ParseException e) {
                        date = null;
                    }
                    ArrayList<String> liked_users = new ArrayList<>();
                    JSONArray ja2 = responseContent.getJSONArray("liked_users");;
                    for (int j = 0; j < ja2.length(); j++) {
                        liked_users.add(ja.getString(j));
                    }
                    ArrayList<ContentReply> comments = new ArrayList<>();
                    JSONArray responseComments = responseContent.getJSONArray("comments");
                    for (int j = 0; j < responseComments.length(); j++) {
                        JSONObject responseComment = (JSONObject) responseComments.get(j);
                        String _id = responseComment.getString("_id");
                        ArrayList<ContentReReply> contentReplies = new ArrayList<>();
                        JSONArray responseReplies = (JSONArray) responseComment.get("reply");
                        for (int k = 0; k < responseReplies.length(); k++) {
                            JSONObject responseReply = (JSONObject) responseReplies.get(k);
                            String __id = responseReply.getString("_id");
                            ArrayList<String> __reports = new ArrayList<>();
                            JSONArray ua = responseReply.getJSONArray("reports");
                            for (int u = 0; u < ua.length(); u++) {
                                __reports.add(ua.getString(u));
                            }
                            boolean __hide = responseReply.getBoolean("hide");
                            ArrayList<String> __report_reasons = new ArrayList<>();
                            JSONArray ua2 = responseReply.getJSONArray("report_reasons");
                            for (int u = 0; u < ua2.length(); u++) {
                                __report_reasons.add(ua.getString(u));
                            }
                            String __writer = responseReply.getString("writer");
                            String __writer_name = responseReply.getString("writer_name");
                            String __replyID = responseReply.getString("replyID");
                            String __content = responseReply.getString("content");
                            Date __date = null;
                            try {
                                __date = fm.parse(responseReply.getString("date"));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            contentReplies.add(new ContentReReply(__id, __reports, __hide, __report_reasons, __writer, __writer_name, __replyID, __content, __date));

                        }
                        boolean _hide = responseComment.getBoolean("hide");
                        ArrayList<String> _report_reasons = new ArrayList<>();
                        JSONArray ka = responseComment.getJSONArray("report_reasons");
                        for (int k = 0; k < ka.length(); k++) {
                            _report_reasons.add(ka.getString(k));
                        }
                        ArrayList<String> _reports = new ArrayList<>();
                        JSONArray ka2 = responseComment.getJSONArray("reports");
                        for (int k = 0; k < ka2.length(); k++) {
                            _reports.add(ka2.getString(k));
                        }
                        String _writer = responseComment.getString("writer");
                        String _writer_name = responseComment.getString("writer_name");
                        Date _date = null;
                        try {
                            _date = fm.parse(responseComment.getString("date"));
                        } catch (ParseException e) {
                            _date = null;
                        }
                        String _content = responseComment.getString("content");

                        comments.add(new ContentReply(_id, contentReplies, _hide, _reports, _report_reasons, _writer, _writer_name, _date, _content));
                    }
                    this.contentReplies = comments;
                    get_done = true;

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }, error -> {
                error.printStackTrace();
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    class BackgroundReplyDeleteThread extends Thread {
        private String comment_id;
        private String reply_id;
        public BackgroundReplyDeleteThread(String comment_id, String reply_id) {
            this.comment_id = comment_id;
            this.reply_id = reply_id;
        }
        public void run() {
            deleteReply(comment_id, reply_id);
            while (!reply_delete_done) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            getComments(content_id);
            while (!get_done) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putInt("thread", 0);
            message.setData(bundle);
            handler.sendMessage(message);

        }
    }
    class BackgroundReplyReportThread extends Thread {
        private String comment_id;
        private String reply_id;
        private String report_reason;
        public BackgroundReplyReportThread(String comment_id, String reply_id, String report_reason) {
            this.comment_id = comment_id;
            this.reply_id = reply_id;
            this.report_reason = report_reason;
        }
        public void run() {
            reportReply(comment_id, reply_id, report_reason);
            while (!reply_report_done) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            getComments(content_id);
            while (!get_done) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putInt("thread", 0);
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }
    class BackgroundReplyBlockThread extends Thread {
        private String counter;
        public BackgroundReplyBlockThread(String counter) {
            this.counter = counter;
        }
        public void run() {
            blockUser(counter);
            while(!reply_block_done) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            getPersonalInfo();
            while(!person_done) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            getComments(content_id);
            while(!get_done) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putInt("thread", 0);
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }
    public void reportReply(String contentcomment_object_id, String reply_object_id, String report_reason) {
        try {
            String requestURL = context.getResources().getString(R.string.server) + "/api/content/reportreply";
            JSONObject params = new JSONObject();
            params.put("userID", UserPersonalInfo.email);
            params.put("contentcomment_object_id", contentcomment_object_id);
            params.put("reply_object_id", reply_object_id);
            params.put("reason", report_reason);
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.PUT, requestURL, params, response -> {
                reply_report_done = true;
            }, error -> {
                error.printStackTrace();
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void deleteReply(String contentcomment_object_id, String reply_object_id) {
        try {
            String requestURL = context.getResources().getString(R.string.server) + "/api/content/deletereply";
            JSONObject params = new JSONObject();
            params.put("userID", UserPersonalInfo.email);
            params.put("contentcomment_object_id", contentcomment_object_id);
            params.put("reply_object_id", reply_object_id);
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.PUT, requestURL, params, response -> {
                reply_delete_done = true;
            }, error -> {
                error.printStackTrace();
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ContentReplyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            notifyDataSetChanged();
            reply_delete_done = false;
            reply_report_done = false;
            get_done = false;
            person_done = false;
            reply_block_done = false;
        }
    }
    public void blockUser(String counter) {
        Log.d("counter", "blockUser: " + counter);
        String token = UserPersonalInfo.token;
        try {
            String requestURL = context.getResources().getString(R.string.server) + "/api/users/blockuser";
            JSONObject params = new JSONObject();
            params.put("userID", counter);
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.PUT, requestURL, params, response -> {
                reply_block_done = true;
            }, error -> {
                error.printStackTrace();
            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }
            };

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getPersonalInfo() {
        if (UserPersonalInfo.token == null) {
            return;
        }
        String token = UserPersonalInfo.token;
        try{
            String requestURL = context.getResources().getString(R.string.server) + "/personalinfo";
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest
                    (Request.Method.GET, requestURL, null, response -> {
                        try {
                            JSONObject res = new JSONObject(response.toString());
                            Log.d("response is", ""+response);
                            JSONObject user = res.getJSONObject("data");
                            UserPersonalInfo.name = user.getString("name");
                            UserPersonalInfo.email = user.getString("email");
                            UserPersonalInfo.points = user.getInt("points");
                            UserPersonalInfo.level = user.getInt("level");
                            UserPersonalInfo.userID = user.getString("userID");
                            UserPersonalInfo.userPassword = user.getString("userPassword");
                            UserPersonalInfo.gender = user.getInt("gender");
                            UserPersonalInfo.yearBirth = user.getInt("yearBirth");
                            JSONArray ja = (JSONArray)user.get("participations");

                            ArrayList<String> partiarray = new ArrayList<String>();
                            for (int j = 0; j<ja.length(); j++){
                                partiarray.add(ja.getString(j));
                            }

                            UserPersonalInfo.participations = partiarray;
                            Log.d("partiarray", ""+UserPersonalInfo.participations.toString());

                            JSONArray ja2 = (JSONArray)user.get("prizes");
                            ArrayList<String> prizearray = new ArrayList<String>();
                            for (int j = 0; j<ja2.length(); j++){
                                prizearray.add(ja2.getString(j));
                            }
                            UserPersonalInfo.prizes = prizearray;
                            Log.d("prizearray", ""+UserPersonalInfo.prizes.toString());
                            ArrayList<Notification> notifications = new ArrayList<>();
                            try{
                                SimpleDateFormat fm = new SimpleDateFormat(context.getResources().getString(R.string.date_format));
                                JSONArray na = (JSONArray)user.get("notifications");
                                if (na.length() != 0){
                                    for (int j = 0; j<na.length(); j++){
                                        JSONObject notification = na.getJSONObject(j);
                                        String title = notification.getString("title");
                                        String content = notification.getString("content");
                                        String post_id = notification.getString("post_id");
                                        Date date = null;
                                        try {
                                            date = fm.parse(notification.getString("date"));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        Integer post_type = notification.getInt("post_type");
                                        Notification newNotification = new Notification(title, content, post_id, date, post_type);
                                        notifications.add(newNotification);
                                    }
                                }

                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            UserPersonalInfo.notifications = notifications;
                            UserPersonalInfo.notificationAllow = user.getBoolean("notification_allow");
                            UserPersonalInfo.prize_check = user.getInt("prize_check");
                            try {
                                ArrayList<String> blockedUsers = new ArrayList<>();
                                JSONArray ja7 = (JSONArray)user.get("blocked_users");
                                for (int j = 0; j < ja7.length(); j++) {
                                    blockedUsers.add(ja7.getString(j));
                                }
                                UserPersonalInfo.blocked_users = blockedUsers;
                            } catch (Exception e) {
                                UserPersonalInfo.blocked_users = new ArrayList<>();
                            }
                            person_done = true;
                        } catch (JSONException e) {
                            Log.d("exception", "JSON error");
                            e.printStackTrace();
                        }
                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }
            };
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed getting response");
            e.printStackTrace();
        }
    }

}
