package com.pumasi.surbay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.adapter.ContentReplyRecyclerViewAdapter;
import com.pumasi.surbay.adapter.ReplyRecyclerViewAdapter;
import com.pumasi.surbay.classfile.Content;
import com.pumasi.surbay.classfile.ContentReReply;
import com.pumasi.surbay.classfile.ContentReply;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.ReReply;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentDetailCommentsActivity extends AppCompatActivity {

    private Context context;
    ContentDetailHandler handler = new ContentDetailHandler();
    private String reply;
    private boolean post_done = false;
    private boolean get_done = false;
    private boolean delete_done = false;
    private boolean reply_delete_done = false;
    private boolean report_done = false;
    private boolean block_done = false;
    private boolean reply_report_done = false;
    private String content_id;
    private boolean is_reply = false;
    private String reply_id;
    private ArrayList<ContentReply> comments;
    private ContentReplyRecyclerViewAdapter contentReplyRecyclerViewAdapter;
    private CustomDialog customDialog;

    private ImageButton ib_back;
    private RecyclerView rv_content_detail_comment;
    public ImageView iv_content_detail_comment_reply;
    private EditText et_content_detail_comment_reply;
    private ImageButton ib_content_detail_comment_reply;
    private LinearLayout ll_content_detail_comment_reply;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_detail_comments);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        context = ContentDetailCommentsActivity.this;
        content_id = getIntent().getStringExtra("content_id");
        comments = getIntent().getParcelableArrayListExtra("comments");

        new BackgroundInitiateThread().start();

        ib_back = findViewById(R.id.ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int comment_counts = 0;
                for (ContentReply contentReply : comments) {
                    comment_counts++;
                    comment_counts += contentReply.getReply().size();
                }
                setResult(comment_counts);
                finish();
            }
        });

        iv_content_detail_comment_reply = findViewById(R.id.iv_content_detail_comment_reply);
        et_content_detail_comment_reply = findViewById(R.id.et_content_detail_comment_reply);
        ib_content_detail_comment_reply = findViewById(R.id.ib_content_detail_comment_reply);
        ib_content_detail_comment_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reply = et_content_detail_comment_reply.getText().toString();
                et_content_detail_comment_reply.getText().clear();
                if (reply.length() > 0) {
                    BackgroundReplyThread backgroundReplyThread = new BackgroundReplyThread();
                    backgroundReplyThread.start();
                }
            }
        });
        ll_content_detail_comment_reply = findViewById(R.id.ll_content_detail_comment_reply);
        if (UserPersonalInfo.userID.equals("nonMember")) ll_content_detail_comment_reply.setVisibility(View.GONE);
    }

    public void postContentReply(String content) {
        try {
            String requestURL = getResources().getString(R.string.server) + "/api/content/postcomment";
            JSONObject params = new JSONObject();
            params.put("userID", UserPersonalInfo.email);
            params.put("content_object_id", content_id);
            params.put("content", content);
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.PUT, requestURL, params, response -> {
                        post_done = true;
            }, error -> {
                        error.printStackTrace();
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {

        }
    }

    public void postContentReReply(String content_comment_object_id, String content) {
        try {
            String requestURL = getResources().getString(R.string.server) + "/api/content/postreply";
            JSONObject params = new JSONObject();
            params.put("userID", UserPersonalInfo.email);
            params.put("contentcomment_object_id", content_comment_object_id);
            params.put("content", content);
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.PUT, requestURL, params, response -> {
                        post_done = true;
            }, error -> {
                        error.printStackTrace();
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void setReplyVisible(boolean visible) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_content_detail_comment_reply.getLayoutParams();
        if (!visible) {
            params.width = 0;
            params.height = 0;
        } else {
            params.width = (int) (context.getResources().getDisplayMetrics().density * 23);
            params.height = (int) (context.getResources().getDisplayMetrics().density * 23);
        }
        iv_content_detail_comment_reply.setLayoutParams(params);
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
                    SimpleDateFormat fm = new SimpleDateFormat(getResources().getString(R.string.date_format));
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
                    this.comments = comments;
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
    public void deleteComment(String contentcomment_object_id) {
        try {
            String requestURL = context.getResources().getString(R.string.server) + "/api/content/deletecomment";
            JSONObject params = new JSONObject();
            params.put("userID", UserPersonalInfo.email);
            params.put("contentcomment_object_id", contentcomment_object_id);
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.PUT, requestURL, params, response -> {
                        delete_done = true;
            }, error -> {
               error.printStackTrace();
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void reportComment(String contentcomment_object_id, String report_reason) {
        try {
            Log.d("report", "reportComment: activiated" );
            String requestURL = context.getResources().getString(R.string.server) + "/api/content/reportcomment";
            JSONObject params = new JSONObject();
            params.put("userID", UserPersonalInfo.email);
            params.put("contentcomment_object_id", contentcomment_object_id);
            params.put("reason", report_reason);
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.PUT, requestURL, params, response -> {
                        report_done = true;
            }, error -> {
               error.printStackTrace();
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class BackgroundInitiateThread extends Thread {
        public void run() {
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
            bundle.putInt("thread", 1);
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }
    class BackgroundReplyThread extends Thread {

        public void run() {
            if (is_reply) {
                postContentReReply(reply_id, reply);
            } else {
                postContentReply(reply);
            }
            while (!post_done) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
            getComments(content_id);
            while (!get_done) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }

            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putInt("thread", 0);
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }

    class BackgroundDeleteThread extends Thread {
        private String id;

        public BackgroundDeleteThread(String id) {
            this.id = id;
        }

        public void run() {
            deleteComment(id);
            while (!delete_done) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {}

            }
            getComments(content_id);

            while (!get_done) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putInt("thread", 0);
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }

    class BackgroundReportThread extends Thread {
        private String id;
        private String reason;
        public BackgroundReportThread(String id, String reason) {
            this.id = id;
            this.reason = reason;
        }
        public void run() {
            reportComment(id, reason);
            while (!report_done) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            getComments(content_id);
            while (!get_done) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putInt("thread", 0);
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }

    class BackgroundBlockThread extends Thread {
        private String counter;
        public BackgroundBlockThread(String counter) {
            this.counter = counter;
        }
        public void run() {
            blockUser(counter);
            while (!block_done) {
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
                } catch (Exception e) {}
            }
            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putInt("thread", 0);
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }


    private class ContentDetailHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.getData().getInt("thread") == 1) {
                rv_content_detail_comment = findViewById(R.id.rv_content_detail_comment);
                contentReplyRecyclerViewAdapter = new ContentReplyRecyclerViewAdapter(comments, ContentDetailCommentsActivity.this, content_id);
                contentReplyRecyclerViewAdapter.setOnItemClickListener(new ContentReplyRecyclerViewAdapter.OnReplyClickListener() {
                    @Override
                    public void onReplyClick(View v, int position) {
                        if (UserPersonalInfo.userID.equals("nonMember")) {
                            customDialog = new CustomDialog(ContentDetailCommentsActivity.this, null);
                            customDialog.show();
                            customDialog.setMessage("비회원은 이용할 수 없는 기능입니다.");
                            customDialog.setNegativeButton("확인");
                        } else {
                            if (is_reply) {
                                is_reply = false;
                                setReplyVisible(false);

                                contentReplyRecyclerViewAdapter.Click(-1);
                            } else {
                                et_content_detail_comment_reply.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        et_content_detail_comment_reply.setFocusableInTouchMode(true);
                                        et_content_detail_comment_reply.requestFocus();
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                                    }
                                });
                                contentReplyRecyclerViewAdapter.Click(position);
                                ContentReply contentReply = (ContentReply) contentReplyRecyclerViewAdapter.getItem(position);
                                reply_id = contentReply.getId();
                                is_reply = true;
                                setReplyVisible(true);
                            }
                        }


                    }
                });
                contentReplyRecyclerViewAdapter.setOnItemClickListener(new ContentReplyRecyclerViewAdapter.OnMenuClickListener() {
                    @Override
                    public void onMenuClick(View v, int position) {
                        if (UserPersonalInfo.userID.equals("nonMember")) {
                            customDialog = new CustomDialog(ContentDetailCommentsActivity.this, null);
                            customDialog.show();
                            customDialog.setMessage("비회원은 이용할 수 없는 기능입니다.");
                            customDialog.setNegativeButton("확인");
                        } else {
                            ContentReply contentReply = (ContentReply) contentReplyRecyclerViewAdapter.getItem(position);
                            PopupMenu popupMenu = new PopupMenu(context, v);
                            popupMenu.getMenuInflater().inflate(R.menu.content_reply_popup_menu, popupMenu.getMenu());
                            popupMenu.show();
                            if (UserPersonalInfo.email.equals(contentReply.getWriter())) {
                                popupMenu.getMenu().getItem(1).setVisible(false);
                                popupMenu.getMenu().getItem(1).setEnabled(false);
                                popupMenu.getMenu().getItem(2).setVisible(false);
                                popupMenu.getMenu().getItem(2).setEnabled(false);
                            } else {
                                popupMenu.getMenu().getItem(0).setVisible(false);
                                popupMenu.getMenu().getItem(0).setEnabled(false);
                            }
                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.delete:
                                            customDialog = new CustomDialog(ContentDetailCommentsActivity.this, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    BackgroundDeleteThread backgroundDeleteThread = new BackgroundDeleteThread(contentReply.getId());
                                                    backgroundDeleteThread.start();
                                                    customDialog.dismiss();
                                                }
                                            });
                                            customDialog.show();
                                            customDialog.setMessage("댓글을 삭제하겠습니까?");
                                            customDialog.setPositiveButton("삭제");
                                            customDialog.setNegativeButton("취소");
                                            return true;
                                        case R.id.report:
                                            if (contentReply.getReports().contains(UserPersonalInfo.email)) {
                                                customDialog = new CustomDialog(ContentDetailCommentsActivity.this, null);
                                                customDialog.show();
                                                customDialog.setMessage("이미 신고한 댓글입니다");
                                                customDialog.setMessage("확인");
                                            } else {
                                                customDialog = new CustomDialog(ContentDetailCommentsActivity.this, new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        ArrayList<String> reports = new ArrayList<>(Arrays.asList("욕설/비하", "상업적 광고 및 판매", "낚시/놀람/도배/사기", "게시판 성격에 부적절함", "기타"));
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(ContentDetailCommentsActivity.this);
                                                        builder.setTitle("신고 사유");
                                                        builder.setItems(R.array.reportreason, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                BackgroundReportThread backgroundReportThread = new BackgroundReportThread(contentReply.getId(), reports.get(which));
                                                                backgroundReportThread.start();
                                                                Toast.makeText(ContentDetailCommentsActivity.this, "신고가 접수되었습니다", Toast.LENGTH_SHORT).show();
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
                                            customDialog = new CustomDialog(context, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    BackgroundBlockThread backgroundBlockThread = new BackgroundBlockThread(contentReply.getWriter());
                                                    backgroundBlockThread.start();
                                                    customDialog.dismiss();
                                                }
                                            });
                                            customDialog.show();
                                            customDialog.setMessage("상대를 차단하겠습니까?\n상대를 차단하시면 더 이상 상대방이 보낸 쪽지를 볼 수 없습니다.");
                                            customDialog.setPositiveButton("차단");
                                            customDialog.setNegativeButton("취소");
                                            return true;
                                    }
                                    return false;
                                }
                            });
                        }

                    }
                });
                rv_content_detail_comment.setAdapter(contentReplyRecyclerViewAdapter);
                rv_content_detail_comment.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
            }
            if (!is_reply) {
                rv_content_detail_comment.scrollToPosition(contentReplyRecyclerViewAdapter.getItemCount() - 1);
            }
            contentReplyRecyclerViewAdapter.setItem(comments);
            contentReplyRecyclerViewAdapter.notifyDataSetChanged();
            post_done = false;
            get_done = false;
            delete_done = false;
            reply_delete_done = false;
            report_done = false;
            reply_report_done = false;
            block_done = false;
        }
    }
    @Override
    public void onBackPressed() {
        if (is_reply) {
            contentReplyRecyclerViewAdapter.Click(-1);
            is_reply = false;
            setReplyVisible(false);
        } else {
            int comment_counts = 0;
            for (ContentReply contentReply : comments) {
                comment_counts++;
                comment_counts += contentReply.getReply().size();
            }
            setResult(comment_counts);
            finish();
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
                block_done = true;
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
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}