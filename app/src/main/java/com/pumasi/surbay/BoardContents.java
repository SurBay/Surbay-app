package com.pumasi.surbay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.adapter.ContentRecyclerViewAdapter;
import com.pumasi.surbay.classfile.Content;
import com.pumasi.surbay.classfile.ContentReReply;
import com.pumasi.surbay.classfile.ContentReply;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class BoardContents extends Fragment {

    private View view;
    private Context context;
    private ArrayList<Content> contentsArrayList = new ArrayList<>();
    private RecyclerView rv_board_content;
    private ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_board_contents, container, false);
        context = getActivity().getApplicationContext();

        rv_board_content = view.findViewById(R.id.rv_board_content);
        contentRecyclerViewAdapter = new ContentRecyclerViewAdapter(contentsArrayList, context);
        rv_board_content.setAdapter(contentRecyclerViewAdapter);
        rv_board_content.setLayoutManager(new GridLayoutManager(context, 2, RecyclerView.VERTICAL, false));

        getContents();
        contentRecyclerViewAdapter.setOnItemClickListener(new ContentRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(context, ContentDetailActivity.class);
                Content content = (Content) contentRecyclerViewAdapter.getItem(position);
                intent.putExtra("content", content);
                startActivity(intent);
            }
        });

        return view;
    }

    private void getContents() {
        Log.d("", "getContents: initiate" );
        try {
            String requestURL = getResources().getString(R.string.server) + "/api/contents";
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            CustomJsonArrayRequest customJsonArrayRequest = new CustomJsonArrayRequest(
                    Request.Method.GET, requestURL, null, response -> {
                try {
                    contentsArrayList.clear();
                    JSONArray responseContents = new JSONArray(response.toString());
                    for (int i = 0; i < responseContents.length(); i++) {
                        JSONObject responseContent = (JSONObject) responseContents.get(i);
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
                        String content = responseContent.getString("content");
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
                            String _writer = responseComment.getString("writer");
                            String _writer_name = responseComment.getString("writer_name");
                            Date _date = null;
                            try {
                                _date = fm.parse(responseComment.getString("date"));
                            } catch (ParseException e) {
                                _date = null;
                            }
                            String _content = responseComment.getString("content");

                            comments.add(new ContentReply(_id, contentReplies, _hide, _report_reasons, _writer, _writer_name, _date, _content));
                        }
                        contentsArrayList.add(new Content(id, image_urls, likes, visit, hide, title, author, content, date, comments, liked_users));

                        Log.d("contentsArrayList", "getContents: " + contentsArrayList);

                    }
                    contentRecyclerViewAdapter.setItem(contentsArrayList);
                    contentRecyclerViewAdapter.notifyDataSetChanged();
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