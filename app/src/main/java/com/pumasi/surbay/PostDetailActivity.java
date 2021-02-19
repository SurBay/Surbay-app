package com.pumasi.surbay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.adapter.ReplyListViewAdapter;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostDetailActivity extends AppCompatActivity {
    static final int START_SURVEY = 1;
    static final int DONE = 1;
    static final int NOT_DONE = 0;
    static final int FIX = 2;
    static final int FIX_DONE = 3;

    private TextView participants;
    private TextView participants_percent;
    TextView author;
    TextView level;
    TextView title;
    TextView target;
    TextView content;

    TextView est_time;
    TextView deadline;
    TextView dday;
    EditText reply_enter;
    LinearLayout prizeLayout;
    TextView prize;

    LinearLayout partilayout;
    LinearLayout authorlayout;
    Button surveyEx;
    Button surveyEnd;

    private AlertDialog dialog;
    ImageButton reply_enter_button;

    private Post post;
    private int position;
    String surveyURL;


    private static ReplyListViewAdapter detail_reply_Adapter;
    private static ListView detail_reply_listView;
    private static ArrayList<Reply> replyArrayList;

    static Button surveyButton;

    List<Map<String, Object>> dialogItemList;


    private static final String TAG_TEXT = "text";
    private static final String TAG_IMAGE = "image";
    int[] image = {R.drawable.kakaotalk, R.drawable.googleform};
    String[] text = {" SNS로 공유하기 ", "구글폼 url 복사하기"};
    final String[] spinner_esttime = {"선택해주세요", "1분 미만", "1~2분", "2~3분", "3~5분", "5~7분", "7~10분", "10분 초과"};

    Date today;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_detail);
        setResult(NOT_DONE);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        today = new Date();

        author = findViewById(R.id.author);
        level = findViewById(R.id.level);
        title = findViewById(R.id.title);
        target = findViewById(R.id.target);
        content = findViewById(R.id.content);;

        est_time = findViewById(R.id.detail_est_time);
        deadline = findViewById(R.id.deadline);
        dday = findViewById(R.id.detail_dday);
        participants = findViewById(R.id.participants);
        participants_percent = findViewById(R.id.participants_percent);

        reply_enter = findViewById(R.id.reply_enter);
        reply_enter_button = findViewById(R.id.reply_enter_button);

        surveyButton = findViewById(R.id.surveyButton);
        prizeLayout = findViewById(R.id.prize_layout);
        prize = findViewById(R.id.prize);
        detail_reply_listView = findViewById(R.id.detail_reply_list);

        partilayout = findViewById(R.id.detail_parti_layout);
        authorlayout = findViewById(R.id.detail_author_layout);
        surveyEx = findViewById(R.id.surveyExButton);
        surveyEnd = findViewById(R.id.surveyEndButton);

        post = (Post)intent.getParcelableExtra("post");
        position = intent.getIntExtra("position", -1);
        loading_detail(post);
        replyArrayList = post.getComments();
        Log.d("comments size", post.getComments().size()+"");
        detail_reply_Adapter = new ReplyListViewAdapter(replyArrayList, post.getID());
        detail_reply_listView.setAdapter(detail_reply_Adapter);

        surveyButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent newIntent = new Intent(getApplicationContext(), SurveyWebActivity.class);
                        newIntent.putExtra("url", surveyURL);
                        startActivityForResult(newIntent, START_SURVEY);
                    }
                }
        );

        setbuttonunable();


        reply_enter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reply = reply_enter.getText().toString();
                if (reply.length() > 0 ){
                    postReply(reply);
                }
            }
        });

        surveyEx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SurveyExDialog();
            }
        });

        surveyEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SurveyEndDialog();
            }
        });

        dialogItemList = new ArrayList<>();

        for(int i=0;i<image.length;i++)
        {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put(TAG_IMAGE, image[i]);
            itemMap.put(TAG_TEXT, text[i]);

            dialogItemList.add(itemMap);
        }

        Log.d("reports", post.getReports().toString());
    }

    private void postReply(String reply) {
        String requestURL = getString(R.string.server)+"/api/posts/writecomment/"+post.getID();
        try{
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("writer", UserPersonalInfo.userID);
            params.put("content",reply);

            Date date = new Date();
            SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
            params.put("date", fm.format(date));

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, requestURL, params, response -> {
                        Log.d("response is", ""+response);
                        try {
                            JSONObject resultObj = new JSONObject(response.toString());
                            String id = resultObj.getString("id");
                            Reply re = new Reply(id, UserPersonalInfo.userID, reply, date, new ArrayList<>(), false);
                            replyArrayList.add(re);
                            detail_reply_Adapter.addItem(re);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("result code", "result code is " +resultCode);
        switch (resultCode){
            case DONE:
                try {
                    int updatedParticipants = post.getParticipants()+1;
                    updateParticipants(updatedParticipants);
                    surveyButton.setClickable(false);
                    surveyButton.setText("이미 참여한 설문입니다");
                    Intent resultIntent = new Intent(getApplicationContext(), BoardFragment1.class);
                    resultIntent.putExtra("position", position);
                    resultIntent.putExtra("participants", updatedParticipants);
                    setResult(DONE, resultIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            case NOT_DONE:
                setResult(NOT_DONE);
                return;
            case FIX_DONE:
                Intent resultIntent = new Intent(getApplicationContext(), BoardFragment1.class);
                Post post = data.getParcelableExtra("post");
                resultIntent.putExtra("post", post);
                resultIntent.putExtra("position", position);
                setResult(FIX_DONE, resultIntent);
                finish();
        }
    }
    private void getPersonalInfo() {
        if (UserPersonalInfo.token == null) {
            return;
        }
        String token = UserPersonalInfo.token;
        try{
            String requestURL = getString(R.string.server) + "/personalinfo";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
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
                            UserPersonalInfo.phoneNumber = user.getString("phoneNumber");
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


                            SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoLogin = auto.edit();
                            autoLogin.putString("name", user.getString("name"));
                            autoLogin.commit();
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
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed getting response");
            e.printStackTrace();
        }
    }

    private void updateParticipants(int updatedParticipants) throws Exception{
        participants.setText(""+updatedParticipants+"/"+post.getGoal_participants());
        String requestURL = getString(R.string.server)+"/api/posts/updatepost/" + post.getID();
        try{
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("participants", updatedParticipants);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, requestURL, params, response -> {
                        Log.d("response is", ""+response);
                        getPersonalInfo();
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
        updateUserParticipation(post.getID());
        if (updatedParticipants == post.getGoal_participants()){
            requestURL = getString(R.string.server)+"/api/posts/done/" + post.getID();
            try{
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                JSONObject params = new JSONObject();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.PUT, requestURL, params, response -> {
                            Log.d("response is", ""+response);
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
    }


    private void updateReports() throws Exception {
        String requestURL = getString(R.string.server) + "/api/posts/report/" + post.getID();
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("userID", UserPersonalInfo.userID);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, requestURL, params, response -> {
                        Log.d("response is", "" + response);
                        if (post.getReports().size() == 3){
                            post.setHide(true);
                            MainActivity.postArrayList.set(position, post);
                            try {
                                MainActivity.getPosts();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.post_detail_bar, menu);
        if (UserPersonalInfo.userID.equals(post.getAuthor())){
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);

            menu.getItem(3).setVisible(true);
            menu.getItem(4).setVisible(true);
        } else {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);

            menu.getItem(3).setVisible(false);
            menu.getItem(4).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.share:
                ShareDialog();
                break;
            case R.id.report:
                //select back button
                ReportDialog();
                break;
            case R.id.fix:
                Intent intent = new Intent(this, WriteActivity.class);
                intent.putExtra("purpose", FIX);
                intent.putExtra("post", post);
                startActivityForResult(intent, FIX);
                break;
            case R.id.remove:
                RemoveDialog();
                break;
            case R.id.note:
                break;
            case R.id.resultrequest:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void setbuttonunable(){
        if (post.isDone()) {
            surveyButton.setClickable(false);
            surveyButton.setText("마감되었습니다");
        } else {
                if (post.getAuthor().equals(UserPersonalInfo.userID)){
                    partilayout.setVisibility(View.GONE);
                    authorlayout.setVisibility(View.VISIBLE);
                } else if (UserPersonalInfo.participations.contains(post.getID())){
                    surveyButton.setClickable(false);
                    surveyButton.setText("이미 참여한 설문입니다");

            }
        }
    }


    public int calc_dday(Date goal){
        Date dt = new Date();

        long diff = (goal.getTime() - dt.getTime()) / (24*60*60*1000);
        int dday = (int)diff;

        return (int)diff;
    }

    public void updateUserParticipation(String id){
        String token = UserPersonalInfo.token;
        String requestURL = getString(R.string.server)+"/user/survey";
        try{
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("post_id",id);
            params.put("userID", UserPersonalInfo.userID);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, requestURL, params, response -> {
                        Log.d("partiarray", ""+response);
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
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed posting");
            e.printStackTrace();
        }
    }

    public void loading_detail(Post post){
        author.setText(post.getAuthor());
        level.setText("Lv "+post.getAuthor_lvl());
        est_time.setText(spinner_esttime[post.getEst_time()]);

        deadline.setText(new SimpleDateFormat("MM.dd").format(post.getDate()) + " - " + new SimpleDateFormat("MM.dd").format(post.getDeadline()));
        int dday_count = calc_dday(post.getDeadline());
        if (dday_count <= 2 && dday_count >= 0 ){
            if (dday_count==0){
                dday.setVisibility(View.VISIBLE);
                dday.setText("D-Day");
            } else {
                dday.setVisibility(View.VISIBLE);
                dday.setText("D-"+(dday_count+1));
            }
        }
        participants.setText(""+post.getParticipants()+"/"+post.getGoal_participants());
        participants_percent.setText((100*post.getParticipants()/post.getGoal_participants())+"%");

        title.setText(post.getTitle());
        target.setText(post.getTarget());
        content.setText(post.getContent());
        if(!post.isWith_prize()){
            prizeLayout.setVisibility(View.GONE);
        }else{
            prize.setText(post.getPrize()+" "+ post.getNum_prize()+ "명");
        }
        surveyURL = post.getUrl();

    }

    public void ReportDialog(){
        if (post.getReports().contains(UserPersonalInfo.userID)){
            Toast.makeText(PostDetailActivity.this, "이미 신고하셨습니다", Toast.LENGTH_SHORT).show();
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailActivity.this);
            dialog = builder.setMessage("신고는 반대의견을 나타내는 기능이 아닙니다.\n" +
                    "신고 사유에 맞지 않는 신고의 경우,\n" +
                    "해당 신고는 처리되지 않습니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(PostDetailActivity.this);
                            builder2.setTitle("신고 사유");
                            builder2.setItems(R.array.reportreason, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        updateReports();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(PostDetailActivity.this, "신고 사유는 "+getResources().getStringArray(R.array.reportreason)[which], Toast.LENGTH_SHORT).show();
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
    }

    private void ShareDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.share_dialog, null);
        builder.setView(view);

        final ListView listview = (ListView)view.findViewById(R.id.listview_alterdialog_list);
        final AlertDialog dialog = builder.create();

        SimpleAdapter simpleAdapter = new SimpleAdapter(PostDetailActivity.this, dialogItemList,
                R.layout.share_listitem,
                new String[]{TAG_IMAGE, TAG_TEXT},
                new int[]{R.id.share_item_image, R.id.share_item_name});

        listview.setAdapter(simpleAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent Sharing_intent = new Intent(Intent.ACTION_SEND);
                        Sharing_intent.setType("text/plain");

                        String Test_Message = post.getUrl();

                        Sharing_intent.putExtra(Intent.EXTRA_TEXT, Test_Message);

                        Intent Sharing = Intent.createChooser(Sharing_intent, "공유하기");
                        startActivity(Sharing);
                        dialog.dismiss();
                        break;
                    case 1:
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("label", post.getUrl());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(PostDetailActivity.this, "클립보드에 복사되었습니다", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        break;
                }
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = 800;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        Window window = dialog.getWindow();
        window.setAttributes(lp);
    }

    private void RemoveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailActivity.this);
        dialog = builder.setMessage("설문을 삭제하더라도 설문 작성 가능 횟수는\n" +
                "늘어나지 않습니다.\n" +
                "설문을 삭제하겠습니까?")
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        today = new Date();
                        long diff = (today.getTime() - post.getDate().getTime()) / (60*1000);
                        int mindiff = (int)diff;
                        Log.d("todat", mindiff+ "  "+ diff + "  " + today + post.getDate());
                        if (mindiff<10){
                            try {
                                deletePost();
                            } catch (Exception e) {
                                setResult(0);
                                Toast.makeText(PostDetailActivity.this, "삭제 오류", Toast.LENGTH_SHORT).show();
                                finish();
                                e.printStackTrace();
                            }
//                            setResult(4);
//                            Toast.makeText(PostDetailActivity.this, "설문이 삭제되었습니다", Toast.LENGTH_SHORT).show();
//                            finish();

                        } else {
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(PostDetailActivity.this);
                            builder2.setMessage("등록 후 10분이 경과하여 삭제할 수 없습니다").setNegativeButton("확인", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            AlertDialog dialog2 = builder2.create();
                            dialog2.setOnShowListener(new DialogInterface.OnShowListener() {
                                @SuppressLint("ResourceAsColor")
                                @Override
                                public void onShow(DialogInterface arg0) {
                                    dialog2.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.teal_200);
                                }
                            });
                            dialog2.show();
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

    public void SurveyEndDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailActivity.this);
        dialog = builder.setMessage("설문을 마감하면 더 이상 설문 참여를 받을 수 없으며, 참여 보상이 있는 경우 설문 참여자들에게 추첨을 통해 자동으로 지급됩니다.")
                .setPositiveButton("설문 마감", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        partilayout.setVisibility(View.VISIBLE);
                        authorlayout.setVisibility(View.GONE);
                        surveyButton.setClickable(false);
                        surveyButton.setText("마감되었습니다");

                        deadline.setText(new SimpleDateFormat("MM.dd").format(post.getDate()) + " - " + new SimpleDateFormat("MM.dd").format(today));
                        try {
                            updateDeadlinePost(today);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        donepost();
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

    public void SurveyExDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailActivity.this);
        dialog = builder.setMessage("설문을 1회(24시간) 연장하면 1크레딧이\n" +
                "차감됩니다.\n" +
                "설문을 연장하겠습니까?")
                .setPositiveButton("설문 연장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        extendPost();
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

    private void updateDeadlinePost(Date deadline) throws Exception{
        String requestURL = getString(R.string.server)+"/api/posts/updatepost/" + post.getID();
        Log.d("fix", UserPersonalInfo.name);
        try{
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS");
            params.put("deadline", fm.format(deadline));
            Log.d("fix", UserPersonalInfo.name + params.toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, requestURL, params, response -> {
                        Log.d("fix is", ""+response);
                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
            Log.d("fix", UserPersonalInfo.name);
        } catch (Exception e){
            Log.d("exception", "failed posting");
            e.printStackTrace();
        }
    }


    public void donepost(){
        String requestURL = getString(R.string.server)+"/api/posts/done/"+post.getID();
        try{
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, requestURL, params, response -> {
                        Log.d("response is", ""+response);
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


    private void deletePost() throws Exception{
        String requestURL =getString(R.string.server)+ "/api/posts/deletepost/" + post.getID();
        try{
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.DELETE, requestURL, null, response -> {
                        Log.d("delete", ""+response);
                            Intent intent = new Intent(PostDetailActivity.this, BoardFragment1.class);
                            intent.putExtra("position", position);
                            setResult(4, intent);
                            Toast.makeText(PostDetailActivity.this, "설문이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                            finish();
                        }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
            Log.d("fix", UserPersonalInfo.name);
        } catch (Exception e){
            Log.d("exception", "failed posting");
            e.printStackTrace();
        }
    }


    private void extendPost() {
        String token = UserPersonalInfo.token;
        try{
            String requestURL = getString(R.string.server)+"/api/posts/extend/"+post.getID();
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest
                    (Request.Method.PUT, requestURL, null, response -> {
                        try {
                            JSONObject res = new JSONObject(response.toString());
                            Log.d("response is", ""+response);
                            boolean success = res.getBoolean("type");
                            if (success){
                                extendView();
                            } else {
                                Toast.makeText(PostDetailActivity.this, "설문기간 연장에 실패하였습니다다", Toast.LENGTH_SHORT).show();
                            }
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
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed getting response");
            e.printStackTrace();
        }
    }

    public void extendView(){
        Date dl = post.getDeadline();

        Log.d("todat",dl.toString());
        long exdl = dl.getTime() + 24*60*60*1000;

        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS");
        String dlstr = fm.format(exdl);
        try {
            dl = fm.parse(dlstr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        deadline.setText(new SimpleDateFormat("MM.dd").format(post.getDate()) + " - " + new SimpleDateFormat("MM.dd").format(dl));
        int dday_count = calc_dday(dl);
        if (dday_count <= 2 && dday_count >= 0 ){
            if (dday_count==0){
                dday.setVisibility(View.VISIBLE);
                dday.setText("D-Day");
            } else {
                dday.setVisibility(View.VISIBLE);
                dday.setText("D-"+(dday_count+1));
            }
        }
        Toast.makeText(PostDetailActivity.this, "설문이 연장되었습니다", Toast.LENGTH_SHORT).show();
    }
}
