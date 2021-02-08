package com.example.surbay;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.surbay.classfile.Post;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.surbay.BoardFragment1.listView;
import static com.example.surbay.BoardFragment1.listViewAdapter;

public class WriteActivity extends AppCompatActivity {
    static final int NEWPOST = 1;
    static final int FIX_DONE = 3;
    private InputMethodManager imm;
    private EditText writeTitle;
    private EditText writeTarget;
    private Button writeDeadline;
    private EditText writeGoalParticipants;
    private CheckBox withPrize;
    private EditText writePrize;
    private EditText writeUrl;
    private EditText writeEstTime;
    private EditText writeContent;
    private AlertDialog dialog;
    Button urlCheck;

    private EditText prize_count;
    private RelativeLayout prize_layout;
    private TextView prize_plus;
    private TextView prize_list;


    Integer goalParticipants;
    String url;
    Integer participants;
    String author;
    Integer author_lvl;
    Integer est_time;
    Date date;
    Date deadline;
    String prize;
    Integer count;

    ArrayList<Uri> image_uris;

    int purpose;
    private static Post post;

    private DatePickerDialog.OnDateSetListener callbackMethod;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        Intent intent = getIntent();
        purpose = intent.getIntExtra("purpose",1);

        writeTitle = findViewById(R.id.write_title);
        writeTarget = findViewById(R.id.write_target);
        writeDeadline = findViewById(R.id.write_deadline);
        writeGoalParticipants = findViewById(R.id.write_goal_participants);
        withPrize = findViewById(R.id.with_prize);
        writePrize = findViewById(R.id.write_prize);
        writeUrl = findViewById(R.id.write_url);
        writeEstTime = findViewById(R.id.write_est_time);
        writeContent = findViewById(R.id.write_content);
        urlCheck = findViewById(R.id.write_url_check);

        prize_count = findViewById(R.id.prize_count);
        prize_layout = findViewById(R.id.prize_image_layout);
        prize_plus = findViewById(R.id.prize_image_plus);
        prize_list = findViewById(R.id.gift_list);

        this.InitializeListener();
        if(writeTitle.getText().length()!=0){writeTitle.setTextColor(Color.parseColor("#000000"));}
        if(writeTarget.getText().length()!=0){writeTarget.setTextColor(Color.parseColor("#000000"));}
        if(writeDeadline.getText().length()!=0){writeDeadline.setTextColor(Color.parseColor("#000000"));}
        if(writePrize.getText().length()!=0){writePrize.setTextColor(Color.parseColor("#000000"));}
        if(writeUrl.getText().length()!=0){writeUrl.setTextColor(Color.parseColor("#000000"));}
        if(writeEstTime.getText().length()!=0){writeEstTime.setTextColor(Color.parseColor("#000000"));}
        if(writeContent.getText().length()!=0){writeContent.setTextColor(Color.parseColor("#000000"));}

        withPrize.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()){
                    writePrize.setHint("기프트콘 상품");
                    writePrize.setEnabled(true);
                    prize_count.setVisibility(View.VISIBLE);
                    prize_layout.setVisibility(View.VISIBLE);
                } else {
                    writePrize.setHint("체크하면 내용을 입력할 수 있습니다");
                    writePrize.setEnabled(false);
                    prize_count.setVisibility(View.GONE);
                    prize_layout.setVisibility(View.GONE);
                }
            }
        });

        prize_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prize_count.getText().toString().length() == 0 || prize_count.getText().toString().equals("0")){
                    Toast.makeText(WriteActivity.this, "추첨 인원을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    count = Integer.valueOf(prize_count.getText().toString());
                    goToAlbum();
                }
            }
        });

        if (purpose == 2){
            post = intent.getParcelableExtra("post");
            writeTitle.setText(post.getTitle());
            writeTarget.setText(post.getTarget());

            Date date = post.getDeadline();
            SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");

            writeDeadline.setText(fm.format(date));
            writeGoalParticipants.setText(post.getGoal_participants().toString());
            if (post.getPrize() != null){
                withPrize.setChecked(true);
                writePrize.setText(post.getPrize());
            }
            writeUrl.setText(post.getUrl());
            writeEstTime.setText(post.getEst_time().toString());
            writeContent.setText(post.getContent());
            writeDeadline.setClickable(false);
            withPrize.setClickable(false);
            writePrize.setClickable(false);
            writeUrl.setClickable(false);
            prize_count.setText(post.getNum_prize().toString());
        }

        urlCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = writeUrl.getText().toString();
                Intent newIntent = new Intent(getApplicationContext(), SurveyWebActivity.class);
                newIntent.putExtra("url", url);
                startActivity(newIntent);
            }
        });

        writeUrl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    url = writeUrl.getText().toString();
                    if (url.length() > 0){
                        urlCheck.setVisibility(View.VISIBLE);
                    }
                } else {
                    urlCheck.setVisibility(View.GONE);
                }
            }
        });
    }

    public void InitializeListener()
    {
        callbackMethod = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                Log.d("date set", "ok");
                int realMonth = monthOfYear +1;
                writeDeadline.setText(year + "년" + realMonth + "월" + dayOfMonth + "일");
            }
        };
    }


    public void OnClickHandler(View view)
    {
        Date today = new Date();
        String year = new SimpleDateFormat("yyyy").format(today);
        String month = new SimpleDateFormat("MM").format(today);
        String day = new SimpleDateFormat("dd").format(today);
        DatePickerDialog dialog = new DatePickerDialog(this, callbackMethod, Integer.valueOf(year), Integer.valueOf(month)-1, Integer.valueOf(day));

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        cal.add(Calendar.DATE, 7);

        dialog.getDatePicker().setMinDate(today.getTime());
        dialog.getDatePicker().setMaxDate(cal.getTime().getTime());

        dialog.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.write_bar, menu);
        return true;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        imm.hideSoftInputFromWindow(findViewById(R.id.write_title).getWindowToken(), 0);
        imm.hideSoftInputFromWindow(findViewById(R.id.write_content).getWindowToken(), 0);
        return super.onTouchEvent(event);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                setResult(0);
                String message;
                if(purpose==1){message = "게시글 작성을 취소하시겠습니까";} else {message = "게시글 수정을 취소하시겠습니까";}
                AlertDialog.Builder builder = new AlertDialog.Builder(WriteActivity.this);
                    dialog = builder.setMessage(message)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
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
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.black);
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.black);
                        }
                    });
                    dialog.show();
                return true;
            case R.id.ok_bar:
                String title = writeTitle.getText().toString(); ///게시글 작성 당시 글쓴이의 레벨이 반영?
                String content = writeContent.getText().toString();
                String target = writeTarget.getText().toString();
                boolean with_prize = withPrize.isChecked();
                est_time = Integer.valueOf((writeEstTime.getText().toString().getBytes().length > 0) ? writeEstTime.getText().toString() : "1");

                if(writeGoalParticipants.getText().toString().length() > 0) {
                    goalParticipants = Integer.valueOf(writeGoalParticipants.getText().toString());
                    if (goalParticipants == 0){
                        goalParticipants = 100;
                    }
                } else{goalParticipants = 100;}

                if (purpose == 1){
                    url = writeUrl.getText().toString();
                    participants = 0;
                    author = UserPersonalInfo.name;
                    author_lvl = UserPersonalInfo.level;

                    SimpleDateFormat fm = new SimpleDateFormat("yyyy년MM월dd일");
                    date = new Date();

                    deadline = null;

                    try {
                        Log.d("deadline is", writeDeadline.getText().toString());
                        deadline = fm.parse(writeDeadline.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (with_prize){
                        count = Integer.valueOf(prize_count.getText().toString());
                        prize = writePrize.getText().toString();
                    } else {
                        count = 0;
                        prize = null;///상품 없을 때는 null 처리해야될듯
                    }
                } else {
                    url = post.getUrl();
                    participants = post.getParticipants();
                    author = post.getAuthor();
                    author_lvl = post.getAuthor_lvl();
                    date = post.getDate();
                    deadline = post.getDeadline();
                    prize = post.getPrize();
                    count = post.getNum_prize();
                }
                SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.date_format));


                if (title.getBytes().length <= 0 || content.getBytes().length <= 0 || target.getBytes().length <= 0 || est_time <= 0){
                    AlertDialog.Builder bu = new AlertDialog.Builder(WriteActivity.this);
                    dialog = bu.setMessage("모든 값들을 입력해주세요")
                            .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onShow(DialogInterface arg0) {
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.black);
                        }
                    });
                    dialog.show();
                } else {
                    if (deadline.before(date)){
                        AlertDialog.Builder bu = new AlertDialog.Builder(WriteActivity.this);
                        dialog = bu.setMessage("날짜를 제대로 입력해주세요")
                                .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .create();
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @SuppressLint("ResourceAsColor")
                            @Override
                            public void onShow(DialogInterface arg0) {
                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.black);
                            }
                        });
                        dialog.show();
                    } else {
                        if (purpose == 1){
                            Post addedpost = new Post(null ,title, author, author_lvl, content, participants, goalParticipants, url, date, deadline, with_prize, prize, est_time, target,count);
                            MainActivity.postArrayList.add(0, addedpost);
                            listViewAdapter.notifyDataSetChanged();
                            listView.setAdapter(listViewAdapter);
                            Intent intent = new Intent(WriteActivity.this, BoardFragment1.class);
                            Log.d("date formatted", formatter.format(deadline));
                            try {
                                postPost(title, author, author_lvl, content, participants, goalParticipants, url, date, deadline, with_prize, prize, est_time, target, count);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            setResult(NEWPOST, intent);
                            finish();
                            return true;
                        } else if (purpose == 2){
                            post.setTitle(title);
                            post.setTarget(target);
                            post.setGoal_participants(goalParticipants);
                            post.setEst_time(est_time);
                            post.setContent(content);

                            Intent intent = new Intent(WriteActivity.this, PostDetailActivity.class);
                            intent.putExtra("post", post);
                            Log.d("date formatted", formatter.format(deadline));
                            try {
                                updatePost(title, author, content, goalParticipants, est_time, target);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            startActivity(intent);
                            setResult(FIX_DONE);
                            Log.d("fix", UserPersonalInfo.name);
                            finish();
                            return true;
                        }
                    }
                }
            default:
                setResult(0);
                return true;
        }
    }



    public void postPost(String title, String author, Integer author_lvl, String content, Integer participants, Integer goal_participants, String url, Date date, Date deadline, Boolean with_prize, String prize, Integer est_time, String target, Integer count) throws Exception{
        try{
            Log.d("starting request", "post posts");
            String requestURL = "https://surbay-server.herokuapp.com/api/posts";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("title", title);
            params.put("author", author);
            params.put("author_lvl", author_lvl);
            params.put("content", content);
            params.put("participants", participants);
            if(goal_participants!=0) {
                params.put("goal_participants", goal_participants);
            }
            params.put("url", url);
            SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS");
            params.put("date", fm.format(date));
            params.put("deadline", fm.format(deadline));
            params.put("with_prize", with_prize);
            if(with_prize) {
                params.put("prize", prize);
                params.put("num_prize", count);
            } else {
                params.put("prize", "");
                params.put("num_prize", 0);
            }
            params.put("est_time", est_time);
            params.put("target", target);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, requestURL, params, response -> {
                        Log.d("response is", ""+response);
                        try {
                            JSONObject resultObj = new JSONObject(response.toString());
                            String id = resultObj.getString("id");
                            Post item = new Post(id, title, author, author_lvl, content, participants, goal_participants, url, date, deadline, with_prize, prize, est_time, target, count);
                            MainActivity.postArrayList.add(item);

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

    private void updatePost(String title, String author, String content, Integer goal_participants, Integer est_time, String target) throws Exception{
        String requestURL = "https://surbay-server.herokuapp.com/api/posts/" + post.getID();
        Log.d("fix", UserPersonalInfo.name);
        try{
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("title", title);
            params.put("author", author);
            params.put("content", content);
            if(goal_participants!=0) {
                params.put("goal_participants", goal_participants);
            }
            params.put("est_time", est_time);
            params.put("target", target);
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

    public void goToAlbum(){
        Config config = new Config();
        config.setSelectionMin(count);
        config.setSelectionLimit(count);
        ImagePickerActivity.setConfig(config);
        Intent intent  = new Intent(this, ImagePickerActivity.class);
        startActivityForResult(intent,13);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 13 && resultCode == RESULT_OK){
            image_uris = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
            Log.d("prizeplus", image_uris.toString());

            String uritext = "";
            for(Uri uri : image_uris) {
                uritext += uri + "\n";
            }
            prize_list.setText(uritext);
        }
    }
}
