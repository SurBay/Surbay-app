package com.pumasi.surbay.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ReplyListViewAdapter extends BaseAdapter {
    private ArrayList<Reply> listViewItemList = new ArrayList<Reply>();
    private String postid;
    final private static String[] report = {"욕설","비하상업적 광고 및 판매낚시","놀람/도배/사기","게시판 성격에 부적절함기타"};

    public ReplyListViewAdapter(ArrayList<Reply> listViewItemList, String postid) {
        this.listViewItemList = listViewItemList;
        this.postid = postid;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.detail_reply_list_item, parent, false);
        }

        TextView replydateview = (TextView)convertView.findViewById(R.id.reply_date);
        TextView replycontentview = (TextView)convertView.findViewById(R.id.reply_context);
        ImageView replymenu = (ImageView)convertView.findViewById(R.id.reply_menu);

        SimpleDateFormat fm = new SimpleDateFormat("MM-dd'T'hh:mm");
        Reply reply = listViewItemList.get(position);
        String date = fm.format(reply.getDate());

        replydateview.setText(date);
        replycontentview.setText(reply.getContent());

        if (reply.getWriter().equals(UserPersonalInfo.userID)){
            replymenu.setVisibility(View.VISIBLE);
            replymenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    AlertDialog dialog = builder.setMessage("댓글을 삭제하겠습니까?")
                            .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    String requestURL = "https://surbay-server.herokuapp.com/api/posts/deletecomment/"+postid;
                                    try{
                                        RequestQueue requestQueue = Volley.newRequestQueue(context);
                                        JSONObject params = new JSONObject();
                                        params.put("_id", reply.getID());
                                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                                (Request.Method.PUT, requestURL, params, response -> {
                                                    Log.d("response is", ""+response);
                                                    listViewItemList.remove(position);
                                                    notifyDataSetChanged();
                                                }, error -> {
                                                    Log.d("exception", "volley error");
                                                    error.printStackTrace();
                                                });
                                        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                        requestQueue.add(jsonObjectRequest);
                                    } catch (Exception e){
                                        Log.d("exception", "failed posting");
                                        e.printStackTrace();
                                    }
                                }
                            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onShow(DialogInterface arg0) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.teal_200);
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.gray);
                        }
                    });
                    dialog.show();
                }
            });
        } else {/*
            replymenu.setVisibility(View.VISIBLE);
            replymenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    AlertDialog dialog = builder.setMessage("댓글을 신고하겠습니까?")
                            .setPositiveButton("신고", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                                    builder2.setTitle("신고 사유");
                                    builder2.setItems(R.array.reportreason, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(context, "신고 사유는 "+report[which], Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    Dialog dialog2 = builder2.create();
                                    dialog2.show();
                                }
                            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onShow(DialogInterface arg0) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.teal_200);
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.gray);
                        }
                    });
                    dialog.show();
                }
            });*/
        }

        return convertView;
    }

    public void addItem(Reply item){
        listViewItemList.add(item);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {return listViewItemList.size();    }

    @Override
    public Object getItem(int position) {return listViewItemList.get(position);    }

    @Override
    public long getItemId(int position) {return position;    }


}
