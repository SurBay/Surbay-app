package com.pumasi.surbay.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.pumasi.surbay.classfile.PostNonSurvey;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class FeedbackReplyListViewAdapter  extends RecyclerView.Adapter<FeedbackReplyListViewAdapter.MyViewHolder> {
    private static ReplyListViewAdapter2.OnItemClickListener mListener = null ;
    private ArrayList<Reply> listViewItemList = new ArrayList<Reply>();
    private LayoutInflater inflater;
    private PostNonSurvey post;
    final private static String[] report = {"욕설","비하상업적 광고 및 판매낚시","놀람/도배/사기","게시판 성격에 부적절함기타"};
    private Context context;

    public FeedbackReplyListViewAdapter(Context ctx,ArrayList<Reply> listViewItemList, PostNonSurvey post) {
        inflater = LayoutInflater.from(ctx);
        context = ctx;
        this.listViewItemList = listViewItemList;
        this.post = post;
        Log.d("made", "feedbbackreplyadapterismade"+this.listViewItemList.size());
    }
    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    @Override
    public FeedbackReplyListViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.detail_reply_list_item, parent, false);
        FeedbackReplyListViewAdapter.MyViewHolder holder = new FeedbackReplyListViewAdapter.MyViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(FeedbackReplyListViewAdapter.MyViewHolder holder, int position) {

        SimpleDateFormat fm = new SimpleDateFormat("MM.dd kk:mm", Locale.KOREA);
        Reply reply = listViewItemList.get(position);
        String date = fm.format(reply.getDate());

        holder.replymenu.setVisibility(View.GONE);
        holder.replydateview.setText(date);
        holder.replycontentview.setText(reply.getContent());
        holder.replyauthorview.setText("관리자");
        Log.d("replycontentis", ""+reply.getContent());
        return;
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
        TextView replyauthorview;
        ImageView replymenu;


        public MyViewHolder(View itemView) {
            super(itemView);

            replydateview = (TextView)itemView.findViewById(R.id.reply_date);
            replycontentview = (TextView)itemView.findViewById(R.id.reply_context);
            replyauthorview = (TextView)itemView.findViewById(R.id.reply_name);
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
    @Override
    public long getItemId(int position) {return position;    }

//    private void updateReplyReports(int position) throws Exception {
//        String requestURL = context.getString(R.string.server) + "/api/posts/reportcomment/" + post.getID();
//        try {
//            RequestQueue requestQueue = Volley.newRequestQueue(context);
//            JSONObject params = new JSONObject();
//            params.put("userID", UserPersonalInfo.userID);
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
//                    (Request.Method.PUT, requestURL, params, response -> {
//                        Log.d("response is", "" + response);
//                        if (post.getReports().size() == 3){
//                            post.setHide(true);
//                            listViewItemList.remove(position);
//                            try {
//                                MainActivity.getPosts();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            notifyDataSetChanged();
//                        }
//                    }, error -> {
//                        Log.d("exception", "volley error");
//                        error.printStackTrace();
//                    });
//            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            requestQueue.add(jsonObjectRequest);
//        } catch (Exception e) {
//            Log.d("exception", "failed posting");
//            e.printStackTrace();
//        }
//    }
}
