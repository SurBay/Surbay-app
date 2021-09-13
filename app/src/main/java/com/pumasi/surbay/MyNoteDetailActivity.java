package com.pumasi.surbay;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

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
import com.pumasi.surbay.classfile.MessageContent;
import com.pumasi.surbay.classfile.MyMessage;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.pages.boardpage.PostDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_note_detail);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        context = getApplicationContext();

        id = getIntent().getStringExtra("id");
        counter = getIntent().getStringExtra("counter");
        counter_name = getIntent().getStringExtra("counter_name");
        position = getIntent().getIntExtra("position", 0);
        ib_back = findViewById(R.id.ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(position);
                finish();
            }
        });
        ib_my_note_detail_refresh = findViewById(R.id.ib_my_note_detail_refresh);
        ib_my_note_detail_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadChat();
            }
        });

        ib_my_note_detail_menu = findViewById(R.id.ib_my_note_detail_menu);
        ib_my_note_detail_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tv_my_note_detail_counter = findViewById(R.id.tv_my_note_detail_counter);
        tv_my_note_detail_counter.setText(counter_name);

        rv_my_note_detail = findViewById(R.id.rv_my_note_detail);
        myChatRecyclerViewAdapter = new MyChatRecyclerViewAdapter(messageContents, context);
        rv_my_note_detail.setAdapter(myChatRecyclerViewAdapter);
        rv_my_note_detail.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        reloadChat();

        et_my_note_reply = findViewById(R.id.et_my_note_reply);
        ib_my_note_reply = findViewById(R.id.ib_my_note_reply);

        ib_my_note_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reply = et_my_note_reply.getText().toString();
                et_my_note_reply.getText().clear();
                messageContents.add(new MessageContent("mock", false, UserPersonalInfo.email, reply, new Date(SystemClock.currentThreadTimeMillis())));
                myChatRecyclerViewAdapter.setItem(messageContents);
                myChatRecyclerViewAdapter.notifyItemChanged(messageContents.size() - 1);
                postReply(reply);
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
            myChatRecyclerViewAdapter.setItem(messageContents);
            myChatRecyclerViewAdapter.notifyDataSetChanged();

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
                        myChatRecyclerViewAdapter.setItem(messageContents);
                        myChatRecyclerViewAdapter.notifyDataSetChanged();
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