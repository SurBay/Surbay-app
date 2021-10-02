package com.pumasi.surbay;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageButton;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.adapter.MyNoteRecyclerViewAdapter;
import com.pumasi.surbay.classfile.MessageContent;
import com.pumasi.surbay.classfile.MyMessage;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.Policy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyNoteActivity extends AppCompatActivity {
    private static final int REQUEST_CHAT = 100;
    private Context context;
    private ArrayList<MyMessage> messages = new ArrayList<>();
    private RecyclerView rv_my_note;
    private MyNoteRecyclerViewAdapter myNoteRecyclerViewAdapter;

    private ImageButton ib_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_note);
        getSupportActionBar().hide();

        context = getApplicationContext();

        ib_back = findViewById(R.id.ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rv_my_note = findViewById(R.id.rv_my_note);
        myNoteRecyclerViewAdapter = new MyNoteRecyclerViewAdapter(messages, context);
        rv_my_note.setAdapter(myNoteRecyclerViewAdapter);
        rv_my_note.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

        myNoteRecyclerViewAdapter.setOnItemClickListener(new MyNoteRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                MyMessage chat = messages.get(position);
                String counter;
                String counter_name;
                Intent intent = new Intent(context, MyNoteDetailActivity.class);
                if (chat.getTo_userID().equals(UserPersonalInfo.email)) {
                    counter = chat.getFrom_userID();
                    counter_name = chat.getFrom_name();
                } else {
                    counter = chat.getTo_userID();
                    counter_name = chat.getTo_name();
                }
                boolean answered = false;
                for (MessageContent messageContent : chat.getContent()) {
                    if (messageContent.getWriter().equals(counter)) {
                        answered = true;
                        break;
                    }
                }
                if (counter.equals("알 수 없음")) intent.putExtra("counter_name", "알 수 없음");
                else if (answered) {
                    intent.putExtra("counter_name", counter_name);
                } else {
                    intent.putExtra("counter_name", "익명");
                }
                intent.putExtra("id", chat.getId());
                intent.putExtra("counter", counter);
                intent.putExtra("position", position);
                startActivityForResult(intent, REQUEST_CHAT);
            }
        });
        getMessages();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHAT) {
            getMessages();
        }
    }

    private void getMessages() {
        try {
            messages.clear();
            myNoteRecyclerViewAdapter.setItem(messages);
            myNoteRecyclerViewAdapter.notifyDataSetChanged();
            String requestURL = context.getResources().getString(R.string.server) + "/api/messages";
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JSONObject params = new JSONObject();
            params.put("userID", UserPersonalInfo.email);
            CustomJsonArrayRequest customJsonArrayRequest = new CustomJsonArrayRequest(
                    Request.Method.PUT, requestURL, params, response -> {
                try {
                    JSONArray responseMessages = new JSONArray(response.toString());
                    for (int i = 0; i < responseMessages.length(); i++) {
                        JSONObject responseMessage = (JSONObject) responseMessages.get(i);
                        String id = responseMessage.getString("_id");
                        int text_num = responseMessage.getInt("text_num");
                        int uncheck_num = responseMessage.getInt("uncheck_num");
                        String from_userID = responseMessage.getString("from_userID");
                        String to_userID = responseMessage.getString("to_userID");
                        String from_name = responseMessage.getString("from_name");
                        String to_name = responseMessage.getString("to_name");
                        ArrayList<MessageContent> content = new ArrayList<>();
                        JSONArray ja = responseMessage.getJSONArray("content");
                        for (int j = 0; j < ja.length(); j++) {
                            JSONObject responseContent = (JSONObject) ja.get(j);
                            String content_id = responseContent.getString("_id");
                            boolean content_check = responseContent.getBoolean("check");
                            String content_writer = responseContent.getString("writer");
                            String content_content = responseContent.getString("content");
                            Date content_date = null;
                            SimpleDateFormat fm = new SimpleDateFormat(context.getResources().getString(R.string.date_format));
                            try {
                                content_date = fm.parse(responseMessage.getString("date"));
                            } catch (ParseException e) {
                                content_date = null;
                            }
                            content.add(new MessageContent(content_id, content_check, content_writer, content_content, content_date));
                        }
                        Date date = null;
                        SimpleDateFormat fm = new SimpleDateFormat(context.getResources().getString(R.string.date_format));
                        try {
                            date = fm.parse(responseMessage.getString("date"));
                        } catch (ParseException e) {
                            date = null;
                        }
                        boolean add = true;
                        for (String block_user : UserPersonalInfo.blocked_users) {
                            if (from_userID.equals(block_user) || to_userID.equals(block_user)) {
                                add = false;
                                break;
                            }
                        }
                        if (add) {
                            messages.add(new MyMessage(id, text_num, uncheck_num, from_userID, to_userID, from_name, to_name, content, date));
                        }
                    }
                    myNoteRecyclerViewAdapter.setItem(messages);
                    myNoteRecyclerViewAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                        error.printStackTrace();
            });
            customJsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(customJsonArrayRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class CustomJsonArrayRequest extends JsonRequest<JSONArray> {

        /**
         * Creates a new request.
         * @param method the HTTP method to use
         * @param url URL to fetch the JSON from
         * @param jsonRequest A {@link JSONObject} to post with the request. Null is allowed and
         *   indicates no parameters will be posted along with request.
         * @param listener Listener to receive the JSON response
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
