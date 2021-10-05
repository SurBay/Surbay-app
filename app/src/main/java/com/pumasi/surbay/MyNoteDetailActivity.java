package com.pumasi.surbay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.adapter.MyChatRecyclerViewAdapter;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.MessageContent;
import com.pumasi.surbay.classfile.MyMessage;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.pages.boardpage.PostDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
public class MyNoteDetailActivity extends AppCompatActivity {


    private static final int RESPONSE = 100;
    private Context context;
    private String id;
    private int position;
    private ArrayList<MessageContent> messageContents = new ArrayList<>();

    private RecyclerView rv_my_note_detail;
    private MyChatRecyclerViewAdapter myChatRecyclerViewAdapter;

    private ImageButton ib_back;
    private ImageButton ib_my_note_detail_refresh;
    private ImageButton ib_my_note_detail_menu;
    private TextView tv_my_note_detail_counter;

    private EditText et_my_note_reply;
    private ImageButton ib_my_note_reply;

    private String counter;
    private String counter_name;
    private CustomDialog customDialog;

    private NoteHandler handler = new NoteHandler();
    private boolean block_done = false;
    private boolean delete_done = false;
    private boolean reload_done = false;
    private boolean reply_done = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_note_detail);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        context = MyNoteDetailActivity.this;

        id = getIntent().getStringExtra("id");
        counter = getIntent().getStringExtra("counter");
        counter_name = getIntent().getStringExtra("counter_name");
        position = getIntent().getIntExtra("position", 0);
        ib_back = findViewById(R.id.ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != -1) {
                    setResult(position);
                }
                finish();
            }
        });
        ib_my_note_detail_refresh = findViewById(R.id.ib_my_note_detail_refresh);
        ib_my_note_detail_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ReloadThread().start();
            }
        });

        ib_my_note_detail_menu = findViewById(R.id.ib_my_note_detail_menu);
        ib_my_note_detail_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems((R.array.my_chat), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {

                            // 채팅방 나가기
                            case 0:
                                customDialog = new CustomDialog(context, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DeleteThread deleteThread = new DeleteThread(id, counter);
                                        deleteThread.start();
                                    }
                                });
                                customDialog.show();
                                customDialog.setMessage("쪽지를 삭제하시겠습니까?\n차단하시면 그 이후 상대방의 채팅을\n확인하실 수 없습니다.");
                                customDialog.setPositiveButton("삭제");
                                customDialog.setNegativeButton("취소");
                                break;
                            // 쪽지 삭제하기
                            case 1:
                                customDialog = new CustomDialog(context, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        BlockThread blockThread = new BlockThread(id, counter);
                                        blockThread.start();
                                    }
                                });
                                customDialog.show();
                                customDialog.setMessage("상대를 차단하시겠습니까?\n차단하시면 그 이후 상대방의 채팅을\n확인하실 수 없습니다.");
                                customDialog.setPositiveButton("차단");
                                customDialog.setNegativeButton("취소");
                                break;
                            // 신고하기
                            case 2:
                                ArrayList<String> reports = new ArrayList<>(Arrays.asList("욕설/비하", "상업적 광고 및 판매", "낚시/놀람/도배/사기", "게시판 성격에 부적절함", "기타"));
                                AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                                builder2.setItems(R.array.reportreason, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(MyNoteDetailActivity.this, reports.get(which), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Dialog dialog2 = builder2.create();
                                dialog2.show();
                                break;

                            // 차단하기
                            case 3:
                                break;

                        }
                    }
                });
                Dialog dialog = builder.create();
                ListView listView = ((AlertDialog) dialog).getListView();
                listView.setDivider(context.getResources().getDrawable(R.color.BDBDBD));
                listView.setDividerHeight(2);
                listView.setFooterDividersEnabled(false);
                dialog.show();
            }
        });

        tv_my_note_detail_counter = findViewById(R.id.tv_my_note_detail_counter);

        tv_my_note_detail_counter.setText(counter_name);

        rv_my_note_detail = findViewById(R.id.rv_my_note_detail);
        myChatRecyclerViewAdapter = new MyChatRecyclerViewAdapter(messageContents, context);
        rv_my_note_detail.setAdapter(myChatRecyclerViewAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        rv_my_note_detail.setLayoutManager(linearLayoutManager);
        new ReloadThread().start();

        et_my_note_reply = findViewById(R.id.et_my_note_reply);
        ib_my_note_reply = findViewById(R.id.ib_my_note_reply);

        ib_my_note_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reply = et_my_note_reply.getText().toString();
                et_my_note_reply.getText().clear();
                new ReplyThread(reply).start();
            }
        });
    }


    public void postReply(String reply) {
        try {
            String requestURL = getResources().getString(R.string.server) + "/api/messages/postmessage";
            JSONObject params = new JSONObject();
            params.put("to_userID", counter);
            params.put("from_userID", UserPersonalInfo.userID);
            params.put("content", reply);

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST, requestURL, params, response -> {
                        reply_done = true;
            }, error -> {
                        error.printStackTrace();
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void reloadChat() {
        try {
            messageContents.clear();
            String requestURL = getResources().getString(R.string.server) + "/api/messages/getmessage";
            JSONObject params = new JSONObject();
            params.put("userID", UserPersonalInfo.email);
            params.put("message_object_id", id);
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            CustomJsonArrayRequest customJsonArrayRequest= new CustomJsonArrayRequest(
                    Request.Method.PUT, requestURL, params, response -> {
                    try {
                        JSONObject responseMessage = (JSONObject) new JSONArray(response.toString()).get(0);
                        JSONArray responseContents = responseMessage.getJSONArray("content");
                        for (int i = 0; i < responseContents.length(); i++) {
                            JSONObject responseContent = (JSONObject) responseContents.get(i);
                            String id = responseContent.getString("_id");
                            boolean check = responseContent.getBoolean("check");
                            String writer = responseContent.getString("writer");
                            String content = responseContent.getString("content");
                            Date date = null;
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat fm = new SimpleDateFormat(getResources().getString(R.string.date_format));
                            try {
                                date = fm.parse(responseContent.getString("date"));
                            } catch (ParseException e) {
                                date = null;
                            }
                            messageContents.add(new MessageContent(id, check, writer, content, date));
                        }

                        reload_done = true;
                        Log.d("reload", "reloadChat: " + messageContents);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }, error -> {
                error.printStackTrace();
            });
            customJsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(customJsonArrayRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    public void Dialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(MyNoteDetailActivity.this, R.style.CustomDialog);
//        LayoutInflater inflater = getLayoutInflater();
//        View view = inflater.inflate(R.layout.share_dialog, null);
//
//        final ListView listview = (ListView)view.findViewById(R.id.listview_alterdialog_list);
//        final AlertDialog dialog = builder.create();
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getColor(R.color.transparent)));
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//
//    }

    class ReloadThread extends Thread {
        public void run() {
            reloadChat();
            while(!reload_done) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            handler.sendEmptyMessage(1);
        }
    }
    class ReplyThread extends Thread {
        private String reply;
        public ReplyThread(String reply) {
            this.reply = reply;
        }
        public void run() {
            postReply(reply);
            while(!reply_done) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            new ReloadThread().start();
        }
    }
    class DeleteThread extends Thread {
        private String message_id;
        private String counter;
        public DeleteThread(String message_id, String counter) {
            this.message_id = message_id;
            this.counter = counter;
        }
        public void run() {
            deleteUser(message_id, counter);
            while(!delete_done) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            handler.sendEmptyMessage(0);
        }
    }
    class BlockThread extends Thread {
        private String message_id;
        private String counter;
        public BlockThread(String message_id, String counter) {
            this.message_id = message_id;
            this.counter = counter;
        }
        public void run() {
            blockUser(counter);
            deleteUser(message_id, counter);
            while(!block_done || !delete_done) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            handler.sendEmptyMessage(0);
        }

    }

    class NoteHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                delete_done = false;
                block_done = false;
                finish();
            } else if (msg.what == 1) {
                myChatRecyclerViewAdapter.setItem(messageContents);
                myChatRecyclerViewAdapter.notifyDataSetChanged();
                rv_my_note_detail.scrollToPosition(myChatRecyclerViewAdapter.getItemCount() - 1);
                reload_done = false;
                reply_done = false;
            }
        }
    }

    public void blockUser(String counter) {
        Log.d("counter", "blockUser: " + counter);
        String token = UserPersonalInfo.token;
        try {
            String requestURL = context.getResources().getString(R.string.server) + "/api/users/blockuser";
            JSONObject params = new JSONObject();
            params.put("", counter);
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

    public void deleteUser(String message_object_id, String counter) {
        try {
            String requestURL = context.getResources().getString(R.string.server) + "/api/messages/deletemessage";
            JSONObject params = new JSONObject();
            params.put("userID", UserPersonalInfo.userID);
            params.put("message_object_id", message_object_id);
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

    private class CustomJsonArrayRequest extends JsonRequest<JSONArray> {

        /**
         * Creates a new request.
         *
         * @param method        the HTTP method to use
         * @param url           URL to fetch the JSON from
         * @param jsonRequest   A {@link JSONObject} to post with the request. Null is allowed and
         *                      indicates no parameters will be posted along with request.
         * @param listener      Listener to receive the JSON response
         * @param errorListener Error listener, or null to ignore errors.
         */
        public CustomJsonArrayRequest(int method, String url, JSONObject jsonRequest,
                                      Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
            super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener,
                    errorListener);
        }

        @Override
        protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
            try {
                String jsonString = new String(response.data,
                        HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                return Response.success(new JSONArray(jsonString),
                        HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            } catch (JSONException je) {
                return Response.error(new ParseError(je));
            }
        }
    }
}