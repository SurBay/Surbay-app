package com.pumasi.surbay.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import com.pumasi.surbay.Tools;
import com.pumasi.surbay.classfile.Notification;
import com.pumasi.surbay.classfile.Poll;
import com.pumasi.surbay.classfile.ReReply;
import com.pumasi.surbay.pages.MainActivity;
import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.General;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.skyhope.showmoretextview.ShowMoreTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GeneralReplyRecyclerViewAdapter extends RecyclerView.Adapter<GeneralReplyRecyclerViewAdapter.MyViewHolder> {
    private Tools tools = new Tools();
    private GeneralReplyRecyclerViewAdapter.OnReplyClickListener aListener = null;
    private GeneralReplyRecyclerViewAdapter.OnMenuClickListener bListener = null;
    private GeneralReReplyRecyclerViewAdapter generalReReplyRecyclerViewAdapter;
    private ArrayList<Reply> replyArrayList = new ArrayList<Reply>();
    private General post;
    final private static String[] report = {"??????","??????????????? ?????? ??? ????????????","??????/??????/??????","????????? ????????? ??????????????????"};
    private Context context;
    private String vote_id;
    private LayoutInflater inflater;
    private CustomDialog customDialog;
    private boolean deleteDone = false;
    private boolean reportDone = false;
    private boolean getDone = false;
    private boolean blockDone = false;
    private boolean person_done = false;
    private int click_position = -1;
    ReplyHandler handler = new ReplyHandler();

    public GeneralReplyRecyclerViewAdapter(Context ctx, ArrayList<Reply> listViewItemList, String vote_id) {
        inflater = LayoutInflater.from(ctx);
        context = ctx;
        this.vote_id = vote_id;
        this.replyArrayList = listViewItemList;
    }

    public void setPost(General post){
        this.post = post;
    }
    public void setItem(ArrayList<Reply> replyArrayList) {
        this.replyArrayList = replyArrayList;
    }
    public interface OnReplyClickListener {
        void onReplyClick(View v, int position) ;
    }
    public interface OnMenuClickListener {
        void onMenuClick(View v, int position);
    }
    public void setOnItemClickListener(GeneralReplyRecyclerViewAdapter.OnReplyClickListener listener) {
        this.aListener = listener ;
    }
    public void setOnItemClickListener(GeneralReplyRecyclerViewAdapter.OnMenuClickListener listener) {
        this.bListener = listener ;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    public void Click(int click_position) {
        this.click_position = click_position;
        notifyDataSetChanged();
    }
    @Override
    public GeneralReplyRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.detail_reply_list_item, parent, false);
        GeneralReplyRecyclerViewAdapter.MyViewHolder holder = new GeneralReplyRecyclerViewAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(GeneralReplyRecyclerViewAdapter.MyViewHolder holder, int position) {


        Reply reply = replyArrayList.get(position);
        if (click_position == position) {
            holder.comment_holder.setBackgroundColor(context.getResources().getColor(R.color.teal_200));
            holder.reply_reply.setImageResource(R.drawable.reply_teal);
        } else {
            holder.comment_holder.setBackgroundColor(context.getResources().getColor(R.color.gray_FB));
            holder.reply_reply.setImageResource(R.drawable.reply);
        }
        generalReReplyRecyclerViewAdapter = new GeneralReReplyRecyclerViewAdapter(reply.getReply(), context, post);
        holder.rv_reply_reply.setAdapter(generalReReplyRecyclerViewAdapter);
        holder.rv_reply_reply.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        generalReReplyRecyclerViewAdapter.notifyDataSetChanged();

        holder.replydateview.setText(tools.convertTimeZone(context, reply.getDate(), "MM.dd kk:mm"));
        if (UserPersonalInfo.blocked_users.contains(reply.getWriter())) {
            holder.replycontentview.setText("(????????? ????????? ???????????????.)");
        } else {
            holder.replycontentview.setText(reply.getContent());
        }
        if (reply.getReply() != null && reply.getReply().size() > 0) {
            holder.replycontentview.setPadding(0, 0 ,0, (int) context.getResources().getDisplayMetrics().density * 14);
        }
        String[] adminsList = {"SurBay_Admin", "SurBay_dev", "SurBay_dev2", "SurBay_des", "djrobort", "surbaying"};
        ArrayList<String> admins = new ArrayList<>(Arrays.asList(adminsList));
        if(reply.getWriter().equals(post.getAuthor_userid())) {
            holder.replywriter.setText("?????????");
            holder.replywriter.setTextColor(context.getColor(R.color.teal_200));
        }else if((admins.contains(reply.getWriter()))&&(reply.getWriter_name()!=null)){
            holder.replywriter.setText("?????????");
            holder.replywriter.setTextColor(context.getColor(R.color.teal_200));
        }else{
            holder.replywriter.setText(reply.getWriter_name());
            holder.replywriter.setTextColor(context.getColor(R.color.text_black_70));
        }

        generalReReplyRecyclerViewAdapter.setOnItemClickListener(new GeneralReReplyRecyclerViewAdapter.OnMenuClickListener() {
            @Override
            public void onMenuClick(View v, int pos) {
                if (UserPersonalInfo.userID.equals("nonMember")) {
                    customDialog = new CustomDialog(context, null);
                    customDialog.show();
                    customDialog.setMessage("???????????? ????????? ??? ?????? ???????????????.");
                    customDialog.setNegativeButton("??????");
                } else {
                    ReReply reReply = reply.getReply().get(pos);
                    PopupMenu popupMenu = new PopupMenu(context, v);
                    popupMenu.getMenuInflater().inflate(R.menu.reply_pop_menu, popupMenu.getMenu());
                    popupMenu.show();
                    popupMenu.getMenu().getItem(2).setVisible(false);
                    popupMenu.getMenu().getItem(2).setEnabled(false);
                    if (UserPersonalInfo.email.equals(reReply.getWriter())) {
                        popupMenu.getMenu().getItem(1).setVisible(false);
                        popupMenu.getMenu().getItem(1).setEnabled(false);
                        popupMenu.getMenu().getItem(3).setVisible(false);
                        popupMenu.getMenu().getItem(3).setEnabled(false);
                    } else {
                        popupMenu.getMenu().getItem(0).setVisible(false);
                        popupMenu.getMenu().getItem(0).setEnabled(false);

                    }
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete:
                                    customDialog = new CustomDialog(context, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            BackgroundReReplyDeleteThread backgroundReReplyDeleteThread = new BackgroundReReplyDeleteThread(reply.getID(), reReply.getId());
                                            backgroundReReplyDeleteThread.start();
                                            customDialog.dismiss();
                                        }
                                    });
                                    customDialog.show();
                                    customDialog.setMessage("????????? ??????????????????????");
                                    customDialog.setPositiveButton("??????");
                                    customDialog.setNegativeButton("??????");
                                    return true;
                                case R.id.report:
                                    if (reReply.getReports().contains(UserPersonalInfo.email)) {
                                        customDialog = new CustomDialog(context, null);
                                        customDialog.show();
                                        customDialog.setMessage("?????? ????????? ???????????????.");
                                        customDialog.setNegativeButton("??????");
                                    } else {
                                        customDialog = new CustomDialog(context, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                ArrayList<String> reports = new ArrayList<>(Arrays.asList("??????/??????", "????????? ?????? ??? ??????", "??????/??????/??????/??????", "????????? ????????? ????????????", "??????"));
                                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                builder.setTitle("?????? ??????");
                                                builder.setItems(R.array.reportreason, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        BackgroundReReplyReportThread backgroundReReplyReportThread = new BackgroundReReplyReportThread(reply.getID(), reReply.getId(), reports.get(which));
                                                        backgroundReReplyReportThread.start();
                                                        Toast.makeText(context, "????????? ?????????????????????", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                Dialog dialog = builder.create();
                                                dialog.show();
                                                customDialog.dismiss();
                                            }
                                        });
                                        customDialog.show();
                                        customDialog.setMessage("????????? ?????????????????????????");
                                        customDialog.setPositiveButton("??????");
                                        customDialog.setNegativeButton("??????");
                                    }

                                    return true;
                                case R.id.edit:
                                    return true;
                                case R.id.block:
                                    if (UserPersonalInfo.blocked_users.contains(reReply.getWriter())) {
                                        customDialog = new CustomDialog(context, null);
                                        customDialog.show();
                                        customDialog.setMessage("?????? ????????? ???????????????.");
                                        customDialog.setNegativeButton("??????");
                                    } else {
                                        customDialog = new CustomDialog(context, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                BackgroundReReplyBlockThread backgroundReReplyBlockThread = new BackgroundReReplyBlockThread(reReply.getWriter());
                                                backgroundReReplyBlockThread.start();
                                                customDialog.dismiss();
                                            }
                                        });
                                        customDialog.show();
                                        customDialog.setMessage("????????? ??????????????????????\n????????? ??????????????? ??? ?????? ???????????? ?????? ????????? ??? ??? ????????????.");
                                        customDialog.setPositiveButton("??????");
                                        customDialog.setNegativeButton("??????");
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
        return replyArrayList.size();
    }

    public Object getItem(int position) {
        return replyArrayList.get(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView replydateview;
        TextView replycontentview;
        private ImageView reply_reply;
        private ImageView reply_menu;
        LinearLayout comment;
        TextView replywriter;
        private RecyclerView rv_reply_reply;
        private LinearLayout comment_holder;

        public MyViewHolder(View itemView) {
            super(itemView);

            replydateview = (TextView)itemView.findViewById(R.id.reply_date);
            replycontentview = (TextView) itemView.findViewById(R.id.reply_context);
            reply_reply = itemView.findViewById(R.id.reply_reply);
            reply_menu = (ImageView)itemView.findViewById(R.id.reply_menu);
            replywriter = (TextView) itemView.findViewById(R.id.reply_name);
            rv_reply_reply = itemView.findViewById(R.id.rv_reply_reply);
            comment_holder = itemView.findViewById(R.id.comment_holder);




            reply_reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // ????????? ????????? ????????? ??????.
                        if (aListener != null) {
                            aListener.onReplyClick(v, pos); ;
                        }
                    }
                }
            });
            reply_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // ????????? ????????? ????????? ??????.
                        if (bListener != null) {
                            bListener.onMenuClick(v, pos); ;
                        }
                    }
                }
            });
        }

    }


    public void deleteReply(String generalcomment_object_id, String reply_object_id) {
        try {
            Log.d("TAG", "deleteReply: " + generalcomment_object_id + ", " + reply_object_id);
            String requestURL = context.getResources().getString(R.string.server) + "/api/generals/comment/deletereply";
            JSONObject params = new JSONObject();
            params.put("userID", UserPersonalInfo.email);
            params.put("generalcomment_object_id", generalcomment_object_id);
            params.put("reply_object_id", reply_object_id);
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.PUT, requestURL, params, response -> {
                        deleteDone = true;
            }, error -> {
               error.printStackTrace();
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reportReply(String generalcomment_object_id, String reply_object_id, String reason) {
        try {
            String requestURL = context.getResources().getString(R.string.server) + "/api/generals/reportreply";
            JSONObject params = new JSONObject();
            params.put("userID", UserPersonalInfo.email);
            params.put("generalcomment_object_id", generalcomment_object_id);
            params.put("reply_object_id", reply_object_id);
            params.put("reason", reason);
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.PUT, requestURL, params, response -> {
                        reportDone = true;
            }, error -> {
                error.printStackTrace();
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void editReply(String generalcomment_object_id, String reply_object_id, String content) {
//        try {
//            String requestURL = context.getResources().getString(R.string.server) + "/api/generals/comment/updatereply";
//            JSONObject params = new JSONObject();
//            params.put("userID", UserPersonalInfo.email);
//            params.put("generalcomment_object_id", generalcomment_object_id);
//            params.put("reply_object_id", reply_object_id);
//            params.put("content", content);
//            RequestQueue requestQueue = Volley.newRequestQueue(context);
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
//                    Request.Method.PUT, requestURL, params, response -> {
//            }, error -> {
//                error.printStackTrace();
//            });
//            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            requestQueue.add(jsonObjectRequest);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    private void getGeneral(String id) {
        try{
            String requestURL = context.getResources().getString(R.string.server) + "/api/generals/getpost/"+ id;
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest
                    (Request.Method.GET, requestURL, null, response -> {
                        try {
                            JSONObject newgeneral = new JSONObject(response.toString());
                            String _id = newgeneral.getString("_id");
                            String title = newgeneral.getString("title");
                            String author = newgeneral.getString("author");
                            Integer author_lvl = newgeneral.getInt("author_lvl");
                            String content = newgeneral.getString("content");
                            SimpleDateFormat fm = new SimpleDateFormat(context.getResources().getString(R.string.date_format));
                            Date date = null;
                            try {
                                date = fm.parse(newgeneral.getString("date"));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Date deadline = null;
                            try {
                                deadline = fm.parse(newgeneral.getString("deadline"));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            ArrayList<Reply> comments = new ArrayList<>();
                            try{
                                JSONArray ja = (JSONArray)newgeneral.get("comments");
                                if (ja.length() != 0){
                                    for (int j = 0; j<ja.length(); j++){
                                        JSONObject comment = ja.getJSONObject(j);
                                        String reid = comment.getString("_id");
                                        String writer = comment.getString("writer");
                                        String contetn = comment.getString("content");
                                        Date datereply = null;
                                        try {
                                            datereply = fm.parse(comment.getString("date"));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        Boolean replyhide = comment.getBoolean("hide");
                                        JSONArray ua = (JSONArray)comment.get("reports");


                                        ArrayList<String> replyreports = new ArrayList<String>();
                                        for (int u = 0; u<ua.length(); u++){
                                            replyreports.add(ua.getString(u));
                                        }
                                        String writer_name = null;
                                        try {
                                            writer_name = comment.getString("writer_name");
                                        }catch (Exception e){
                                            writer_name = null;
                                        }
                                        ArrayList<ReReply> reReplies = new ArrayList<>();
                                        try {
                                            JSONArray jk = (JSONArray) comment.get("reply");
                                            if (jk.length() != 0) {
                                                for (int k = 0; k < jk.length(); k++) {
                                                    JSONObject reReply = jk.getJSONObject(k);
                                                    String id_ = reReply.getString("_id");
                                                    ArrayList<String> reports_ = new ArrayList<>();
                                                    JSONArray jb = (JSONArray) reReply.get("reports");
                                                    for (int b = 0; b < jb.length(); b++) {
                                                        reports_.add(jb.getString(b));
                                                    }
                                                    ArrayList<String> report_reasons_ = new ArrayList<>();
                                                    JSONArray jc = (JSONArray) reReply.get("report_reasons");
                                                    for (int c = 0; c < jc.length(); c++) {
                                                        report_reasons_.add(jc.getString(c));
                                                    }
                                                    boolean hide_ = reReply.getBoolean("hide");
                                                    String writer_ = reReply.getString("writer");
                                                    String writer_name_ = "";
                                                    try {
                                                        writer_name_ = reReply.getString("writer_name");
                                                    } catch (Exception e) {
                                                        writer_name_ = "??????";
                                                    }
                                                    String content_ = reReply.getString("content");
                                                    Date date_ = fm.parse(reReply.getString("date"));
                                                    String replyID_ = reReply.getString("replyID");

                                                    ReReply newReReply = new ReReply(id_, reports_, report_reasons_, hide_, writer_, writer_name_, content_, date_, replyID_);
                                                    reReplies.add(newReReply);
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        Reply re = new Reply(reid, writer, contetn, datereply,replyreports,replyhide, writer_name, reReplies);
                                        re.setWriter_name(writer_name);
                                        if ((!replyhide )&& (!replyreports.contains(UserPersonalInfo.userID))){
                                            comments.add(re);
                                        }
                                    }
                                }

                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            Boolean done = newgeneral.getBoolean("done");
                            String author_userid = newgeneral.getString("author_userid");
                            JSONArray ka = (JSONArray)newgeneral.get("reports");
                            ArrayList<String> reports = new ArrayList<String>();
                            for (int j = 0; j<ka.length(); j++){
                                reports.add(ka.getString(j));
                            }
                            Boolean multi_response = newgeneral.getBoolean("multi_response");
                            Integer participants = newgeneral.getInt("participants");
                            JSONArray ia = (JSONArray)newgeneral.get("participants_userids");
                            ArrayList<String> participants_userids = new ArrayList<String>();
                            for (int j = 0; j<ia.length(); j++){
                                participants_userids.add(ia.getString(j));
                            }
                            Boolean with_image = newgeneral.getBoolean("with_image");
                            ArrayList<Poll> polls = new ArrayList<>();
                            try{
                                JSONArray ja = (JSONArray)newgeneral.get("polls");
                                if (ja.length() != 0){
                                    for (int j = 0; j<ja.length(); j++){
                                        JSONObject poll = ja.getJSONObject(j);
                                        String poll_id = poll.getString("_id");
                                        String poll_content = poll.getString("content");
                                        ArrayList<String> poll_participants_userids = new ArrayList<String>();
                                        JSONArray ua = (JSONArray)poll.get("participants_userids");
                                        for (int u = 0; u<ua.length(); u++){
                                            poll_participants_userids.add(ua.getString(u));
                                        }
                                        String image = poll.getString("image");
                                        Poll newpoll = new Poll(poll_id, poll_content, poll_participants_userids, image);
                                        polls.add(newpoll);
                                    }
                                }

                            } catch (Exception e){
                                e.printStackTrace();
                            }

                            JSONArray la = (JSONArray)newgeneral.get("liked_users");
                            ArrayList<String> liked_users = new ArrayList<String>();
                            for (int j = 0; j<la.length(); j++){
                                liked_users.add(la.getString(j));
                            }

                            Integer likes = newgeneral.getInt("likes");
                            Boolean hide = newgeneral.getBoolean("hide");

                            General newGeneral = new General(_id, title, author, author_lvl, content,
                                    date, deadline, comments, done, author_userid, reports, multi_response,
                                    participants, participants_userids, with_image, polls, liked_users, likes, hide);

                            this.replyArrayList = comments;
                            getDone = true;
                        } catch (JSONException e) {
                            Log.d("exception", "JSON error");
                            e.printStackTrace();
                        }
                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed getting response");
            e.printStackTrace();
        }
    }

    class BackgroundReReplyDeleteThread extends Thread{
        private String comment_id;
        private String reply_id;
        public BackgroundReReplyDeleteThread(String comment_id, String reply_id) {
            this.comment_id = comment_id;
            this.reply_id = reply_id;
            Log.d("ids", "BackgroundReReplyDeleteThread: " + comment_id + ", " + reply_id);
        }
        public void run() {
            deleteReply(comment_id, reply_id);
            while (!deleteDone) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            getGeneral(vote_id);
            while (!getDone) {
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
    class BackgroundReReplyReportThread extends Thread{
        private String comment_id;
        private String reply_id;
        private String report_reason;
        public BackgroundReReplyReportThread(String comment_id, String reply_id, String report_reason) {
            this.comment_id = comment_id;
            this.reply_id = reply_id;
            this.report_reason = report_reason;
        }
        public void run() {
            reportReply(comment_id, reply_id, report_reason);
            while (!reportDone) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            getGeneral(vote_id);
            while (!getDone) {
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
//    class BackgroundReReplyEditThread extends Thread{
//        private String comment_id;
//        private String reply_id;
//        private String content;
//        public BackgroundReReplyEditThread(String comment_id, String reply_id, String content) {
//            this.comment_id = comment_id;
//            this.reply_id = reply_id;
//            this.content = content;
//        }
//        public void run() {
//            editReply(comment_id, reply_id, content);
//            while (!editDone) {
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            getGeneral(vote_id);
//            while (!getDone) {
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            Message message = handler.obtainMessage();
//            Bundle bundle = new Bundle();
//            bundle.putInt("thread", 0);
//            message.setData(bundle);
//            handler.sendMessage(message);
//        }
//    }
    class BackgroundReReplyBlockThread extends Thread{
        private String counter;
        public BackgroundReReplyBlockThread(String counter) {
            this.counter = counter;
        }
        public void run() {
            blockUser(counter);
            while(!blockDone) {
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
            getGeneral(vote_id);
            while(!getDone) {
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
    private class ReplyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            deleteDone = false;
            reportDone = false;
            getDone = false;
            blockDone = false;
            person_done = false;
            notifyDataSetChanged();
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
                blockDone = true;
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
