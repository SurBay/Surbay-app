package com.example.surbay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.surbay.adapter.GiftImageAdapter;
import com.example.surbay.classfile.Post;
import com.example.surbay.classfile.Reply;
import com.example.surbay.classfile.UserPersonalInfo;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WriteActivity extends AppCompatActivity {
    static final int NEWPOST = 1;
    static final int FIX_DONE = 3;
    private InputMethodManager imm;

    TextView writeBack;
    TextView writeSave;
    TextView writeDone;
    RecyclerView gift_image_list;
    GiftImageAdapter giftImageAdapter;

    private EditText writeTitle;
    private EditText writeTarget;
    private EditText writeDeadline;
    private EditText writeGoalParticipants;
    private CheckBox withPrize;
    private EditText writePrize;
    private EditText writeUrl;
    private Spinner writeEstTime;
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
    String datestr;
    String timestr;
    String prize;
    Integer count;

    ArrayList<Uri> image_uris;

    int purpose;
    private static Post post;

    TimePickerDialog timedialog;
    private TimePickerDialog.OnTimeSetListener tcallbackMethod;
    private DatePickerDialog.OnDateSetListener callbackMethod;
    final String[] spinner_esttime = {"선택해주세요", "1분 미만", "1~2분", "2~3분", "3~5분", "5~7분", "7~10분", "10분 초과"};
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        getSupportActionBar().hide();
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
        gift_image_list = findViewById(R.id.gith_image_list);

        writeBack = findViewById(R.id.writeBack);
        writeSave = findViewById(R.id.writesave);
        writeDone = findViewById(R.id.writeDone);


        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinner_esttime);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        writeEstTime.setAdapter(adapter);

        this.InitializeListener();
        if(writeTitle.getText().length()!=0){writeTitle.setTextColor(Color.parseColor("#000000"));}
        if(writeTarget.getText().length()!=0){writeTarget.setTextColor(Color.parseColor("#000000"));}
        if(writeDeadline.getText().length()!=0){writeDeadline.setTextColor(Color.parseColor("#000000"));}
        if(writePrize.getText().length()!=0){writePrize.setTextColor(Color.parseColor("#000000"));}
        if(writeUrl.getText().length()!=0){writeUrl.setTextColor(Color.parseColor("#000000"));}
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
            writeSave.setVisibility(View.INVISIBLE);
            post = intent.getParcelableExtra("post");
            writeTitle.setText(post.getTitle());
            writeTarget.setText(post.getTarget());

            Date date = post.getDeadline();
            SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd a KK시");

            writeDeadline.setText(fm.format(date));
            writeGoalParticipants.setText(post.getGoal_participants().toString());
            if (post.getPrize() != null){
                withPrize.setChecked(true);
                writePrize.setText(post.getPrize());
            }
            writeUrl.setText(post.getUrl());
            writeEstTime.setSelection(post.getEst_time());
            writeContent.setText(post.getContent());
            editUnable(writeDeadline);
            editUnable(writeUrl);
            withPrize.setClickable(false);
            writePrize.setClickable(false);
            prize_count.setText(post.getNum_prize().toString());
            writeEstTime.setSelection(post.getEst_time());
        }

        urlCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = writeUrl.getText().toString();
                if (url.contains("docs.google.com") || url.contains("forms.gle")){
                    Intent newIntent = new Intent(getApplicationContext(), SurveyWebActivity.class);
                    newIntent.putExtra("url", url);
                    startActivity(newIntent);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(WriteActivity.this);
                    dialog = builder.setMessage("제공하지 않는 url입니다.")
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
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.teal_200);
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.gray);
                        }
                    });
                    dialog.show();
                }
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

        writeDeadline.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    date = new Date();
                    String year = new SimpleDateFormat("yyyy").format(date);
                    String month = new SimpleDateFormat("MM").format(date);
                    String day = new SimpleDateFormat("dd").format(date);
                    String hour = new SimpleDateFormat("hh").format(date);
                    DatePickerDialog dialog = new DatePickerDialog(WriteActivity.this, callbackMethod, Integer.valueOf(year), Integer.valueOf(month)-1, Integer.valueOf(day));
                    timedialog = new TimePickerDialog(WriteActivity.this, tcallbackMethod, Integer.valueOf(hour), 00, false);

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date());
                    cal.add(Calendar.DATE, 7);

                    dialog.getDatePicker().setMinDate(date.getTime());
                    dialog.getDatePicker().setMaxDate(cal.getTime().getTime());
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            timedialog.show();
                        }
                    });
                    timedialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            setDeadline();
                        }
                    });
                    dialog.show();
                }
            }
        });

        writeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Back_survey();
            }
        });

        writeDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Done_survey();
            }
        });

        writeSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDialog();
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
                datestr = String.format("%04d-%02d-%02d", year, realMonth, dayOfMonth);
            }
        };
        tcallbackMethod = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timestr = String.format("%02d:%02d", hourOfDay, minute);
            }
        };
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
                Back_survey();
                return true;
            case R.id.ok_bar:
                Done_survey();
            default:
                setResult(0);
                return true;
        }
    }



    public void postPost(String title, String author, Integer author_lvl, String content, Integer participants, Integer goal_participants, String url, Date date, Date deadline, Boolean with_prize, String prize, Integer est_time, String target, Integer count, ArrayList<Reply> comments, boolean done) throws Exception{
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
            params.put("done", false);
            params.put("comments", new ArrayList<Reply>());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, requestURL, params, response -> {
                        Log.d("response is", ""+response);
                        try {
                            JSONObject resultObj = new JSONObject(response.toString());
                            String id = resultObj.getString("id");
                            Post item = new Post(id, title, author, author_lvl, content, participants, goal_participants, url, date, deadline, with_prize, prize, est_time, target, count, new ArrayList<Reply>(), false);
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
        String requestURL = "https://surbay-server.herokuapp.com/api/posts/updatepost/" + post.getID();
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

            giftImageAdapter = new GiftImageAdapter(WriteActivity.this, image_uris);
            gift_image_list.setAdapter(giftImageAdapter);
            gift_image_list.setLayoutManager(new LinearLayoutManager(WriteActivity.this, LinearLayoutManager.HORIZONTAL, false));
            gift_image_list.setVisibility(View.VISIBLE);

            giftImageAdapter.setOnItemClickListener(new GiftImageAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Intent intent = new Intent(WriteActivity.this, TipImageDetail.class);
                    intent.putExtra("uri", image_uris.get(position));
                    intent.putExtra("position", position);
                    startActivityForResult(intent, 20);
                }
            });
            gift_image_list.setVisibility(View.VISIBLE);
            prize_list.setVisibility(View.GONE);
        }
    }
    public void Back_survey(){
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
    }

    public void Done_survey(){
        String title = writeTitle.getText().toString(); ///게시글 작성 당시 글쓴이의 레벨이 반영?
        String content = writeContent.getText().toString();
        String target = writeTarget.getText().toString();
        boolean with_prize = withPrize.isChecked();
        est_time = writeEstTime.getSelectedItemPosition();

        if(writeGoalParticipants.getText().toString().length() > 0) {
            goalParticipants = Integer.valueOf(writeGoalParticipants.getText().toString());
            if (goalParticipants == 0){
                goalParticipants = 100;
            }
        } else{goalParticipants = 100;}

        if (purpose == 1){
            url = writeUrl.getText().toString();
            participants = 0;
            author = UserPersonalInfo.userID;
            author_lvl = UserPersonalInfo.level;

            SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd a KK시");
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
                    Post addedpost = new Post(null ,title, author, author_lvl, content, participants, goalParticipants, url, date, deadline, with_prize, prize, est_time, target,count, new ArrayList<Reply>(), false);
                    MainActivity.postArrayList.add(addedpost);
                    Intent intent = new Intent(WriteActivity.this, BoardFragment1.class);
                    Log.d("date formatted", formatter.format(deadline));
                    try {
                        postPost(title, author, author_lvl, content, participants, goalParticipants, url, date, deadline, with_prize, prize, est_time, target, count, new ArrayList<Reply>(), false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    setResult(NEWPOST, intent);
                    finish();
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
                }
            }
        }
    }

    public void setDeadline(){
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS");
        try {
            deadline = fm.parse(datestr+"T"+ timestr+":00.000Z");
            Log.d("writedeadline", deadline.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diff = deadline.getTime() - date.getTime();
        Log.d("writedeadline", String.valueOf(diff));
        if (Integer.valueOf((int) diff) > 72*60*60*1000){
            AlertDialog.Builder bu = new AlertDialog.Builder(WriteActivity.this);
            dialog = bu.setMessage("설문 기간이 72시간을 초과했습니다")
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
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.teal_200);
                }
            });
            dialog.show();
            writeDeadline.clearFocus();
        } else {
            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd a KK시");

            writeDeadline.setText(transFormat.format(deadline));
            writeDeadline.clearFocus();
        }
    }

    @SuppressLint("ResourceAsColor")
    public void editUnable(EditText e){
        e.setClickable(false);
        e.setFocusable(false);
        e.setEnabled(false);
        e.setFocusableInTouchMode(false);
        e.setTextColor(R.color.gray2);
    }

    public void saveDialog(){
        AlertDialog.Builder builder2 = new AlertDialog.Builder(WriteActivity.this);
                        builder2.setItems(R.array.savemenu, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        AlertDialog.Builder builder_save = new AlertDialog.Builder(WriteActivity.this);
                                        AlertDialog dialog_save = builder_save.setMessage("임시저장은 최대 1개까지 가능하여 임시저장할 경우 기존에 임시저장한 글이 사라집니다.\n" +
                                                "임시저장하겠습니까?")
                                                .setPositiveButton("임시저장", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        tempSave();
                                                    }
                                                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }
                                                })
                                                .create();
                                        dialog_save.setOnShowListener(new DialogInterface.OnShowListener() {
                                            @SuppressLint("ResourceAsColor")
                                            @Override
                                            public void onShow(DialogInterface arg0) {
                                                dialog_save.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.teal_200);
                                                dialog_save.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.gray);
                                            }
                                        });
                                        dialog_save.show();
                                        break;
                                    case 1:
                                        AlertDialog.Builder builder_load = new AlertDialog.Builder(WriteActivity.this);
                                        String message;
                                        if (writeTitle.getText().toString().length()==0 && writeTarget.getText().toString().length()==0 && writeDeadline.getText().toString().length()==0 && writePrize.getText().toString().length()==0 && writeUrl.getText().toString().length()==0 && writeContent.getText().toString().length()==0){
                                            message = "임시저장된 글을 불러오겠습니까?";
                                        } else {
                                            message = "임시저장된 글을 불러올 경우\n" +
                                                    "작성중인 글은 사라지게 됩니다.\n" +
                                                    "불러오겠습니까?";
                                        }
                                        AlertDialog dialog_load = builder_load.setMessage(message)
                                                .setPositiveButton("불러오기", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        loadSave();
                                                    }
                                                }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }
                                                })
                                                .create();
                                        dialog_load.setOnShowListener(new DialogInterface.OnShowListener() {
                                            @SuppressLint("ResourceAsColor")
                                            @Override
                                            public void onShow(DialogInterface arg0) {
                                                dialog_load.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.teal_200);
                                                dialog_load.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.gray);
                                            }
                                        });
                                        dialog_load.show();
                                        break;
                                    default:
                                        return;
                                }
                            }
                        });
                        builder2.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        Dialog dialog2 = builder2.create();
                        dialog2.show();

    }

    public void tempSave(){
        SharedPreferences tempWrite = getSharedPreferences("tempWrite", Activity.MODE_PRIVATE);
        SharedPreferences.Editor tempWriteedit = tempWrite.edit();

        String title = writeTitle.getText().toString();
        String content = writeContent.getText().toString();
        String target = writeTarget.getText().toString();
        boolean with_prize = withPrize.isChecked();
        est_time = writeEstTime.getSelectedItemPosition();
        goalParticipants = Integer.valueOf(writeGoalParticipants.getText().toString());
        url = writeUrl.getText().toString();

        if (with_prize){
            count = Integer.valueOf(prize_count.getText().toString());
            prize = writePrize.getText().toString();
        } else {
            count = 0;
            prize = null;///상품 없을 때는 null 처리해야될듯
        }

        tempWriteedit.putString("title", title);
        tempWriteedit.putString("content", content);
        tempWriteedit.putString("target", target);
        tempWriteedit.putInt("est_time", est_time);
        tempWriteedit.putInt("goalparticipations",goalParticipants);
        tempWriteedit.putString("uri", url);
        tempWriteedit.putString("deadline", writeDeadline.getText().toString());
        tempWriteedit.putInt("count", count);
        tempWriteedit.putString("prize",prize);
        tempWriteedit.putBoolean("with_prize", with_prize);
        tempWriteedit.commit();
    }

    public void loadSave(){
        SharedPreferences tempWrite = getSharedPreferences("tempWrite", Activity.MODE_PRIVATE);

        writeTitle.setText(tempWrite.getString("title",""));
        writeTarget.setText(tempWrite.getString("target",""));

        writeDeadline.setText(tempWrite.getString("deadline","수정불가, YYYYMMDD"));
        if (tempWrite.getInt("goalparticipations",0) != 0){
            writeGoalParticipants.setText(String.valueOf(tempWrite.getInt("goalparticipations",0)));
        }
        if (tempWrite.getBoolean("with_prize",false)){
            withPrize.setChecked(true);
            writePrize.setText(tempWrite.getString("prize",""));
            prize_count.setText(String.valueOf(tempWrite.getInt("count",0)));
            writePrize.setEnabled(true);
            prize_count.setVisibility(View.VISIBLE);
            prize_layout.setVisibility(View.VISIBLE);
        } else {
            withPrize.setChecked(false);
        }
        writeUrl.setText(tempWrite.getString("uri",""));
        writeEstTime.setSelection(tempWrite.getInt("est_time",0));
        writeContent.setText(tempWrite.getString("content",""));
    }
}
