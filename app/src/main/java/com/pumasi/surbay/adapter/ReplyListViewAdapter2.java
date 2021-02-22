package com.pumasi.surbay.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.MainActivity;
import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ReplyListViewAdapter2 extends RecyclerView.Adapter<ReplyListViewAdapter2.MyViewHolder> {
    private static ReplyListViewAdapter2.OnItemClickListener mListener = null ;
    private ArrayList<Reply> listViewItemList = new ArrayList<Reply>();
    private Post post;
    final private static String[] report = {"욕설","비하상업적 광고 및 판매낚시","놀람/도배/사기","게시판 성격에 부적절함기타"};
    private Context context;
    private LayoutInflater inflater;

    public ReplyListViewAdapter2(Context ctx, ArrayList<Reply> listViewItemList) {
        Log.d("list size is", ""+listViewItemList.size());
        inflater = LayoutInflater.from(ctx);
        context = ctx;
        this.listViewItemList = listViewItemList;
    }
    public void setPost(Post post){
        this.post = post;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    @Override
    public ReplyListViewAdapter2.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.detail_reply_list_item, parent, false);
        ReplyListViewAdapter2.MyViewHolder holder = new ReplyListViewAdapter2.MyViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(ReplyListViewAdapter2.MyViewHolder holder, int position) {

        SimpleDateFormat fm = new SimpleDateFormat("MM.dd hh:mm");
        Reply reply = listViewItemList.get(position);
        String date = fm.format(reply.getDate());

        holder.replydateview.setText(date);
        holder.replycontentview.setText(reply.getContent());
        Log.d("on listview", "ading "+reply.getContent());

        if (reply.getWriter().equals(UserPersonalInfo.userID)){
            holder.replymenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    AlertDialog dialog = builder.setMessage("댓글을 삭제하겠습니까?")
                            .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    String requestURL = context.getString(R.string.server)+"/api/posts/deletecomment/"+post.getID();
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
        } else {
            holder.replymenu.setVisibility(View.VISIBLE);
            holder.replymenu.setOnClickListener(new View.OnClickListener() {
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
                                            try {
                                                updateReplyReports(position, reply.getID());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
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
            });
        }

    }

    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }

    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView replydateview;
        TextView replycontentview;
        ImageView replymenu;

        public MyViewHolder(View itemView) {
            super(itemView);

            replydateview = (TextView)itemView.findViewById(R.id.reply_date);
            replycontentview = (TextView)itemView.findViewById(R.id.reply_context);
            replymenu = (ImageView)itemView.findViewById(R.id.reply_menu);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });
        }

    }

    public void addItem(Reply item){
        listViewItemList.add(item);
        notifyDataSetChanged();
    }

    private void updateReplyReports(int position, String comment_id) throws Exception {
        String requestURL = context.getString(R.string.server) + "/api/posts/reportcomment/" + post.getID();
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JSONObject params = new JSONObject();
            params.put("userID", UserPersonalInfo.userID);
            params.put("comment_id", comment_id);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, requestURL, params, response -> {
                        Log.d("response is", "" + response);
                            listViewItemList.get(position).setHide(true);
                            listViewItemList.remove(position);
                            notifyDataSetChanged();
                            try {
                                MainActivity.getPosts();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            notifyDataSetChanged();
                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            Log.d("exception", "failed posting");
            e.printStackTrace();
        }
    }
}
