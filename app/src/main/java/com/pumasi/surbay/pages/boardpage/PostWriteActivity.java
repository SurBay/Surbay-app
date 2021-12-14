package com.pumasi.surbay.pages.boardpage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.pages.MainActivity;
import com.pumasi.surbay.R;
import com.pumasi.surbay.SurveyWebActivity;
import com.pumasi.surbay.adapter.GiftImageAdapter;
import com.pumasi.surbay.adapter.GiftImageAdapter2;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.GifSizeFilter;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.classfile.VolleyMultipartRequest;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class PostWriteActivity extends AppCompatActivity {
    static final int FIX_DONE = 3;
    static final int CHECK = 2;

    private EditText et_research_write_title;
    private EditText et_research_write_target;
    private EditText et_research_write_author_info;
    private CheckBox cb_research_write_anonymous;
    private EditText et_research_write_deadline;
    private EditText et_research_write_goal;

    private EditText et_research_write_url;
    private Spinner sp_research_write_est_time;
    private EditText et_research_write_content;

    TextView writeBack;
    TextView writeSave;
    TextView writeDone;
    RecyclerView gift_image_list;
    GiftImageAdapter giftImageAdapter;

    private LinearLayout ll_research_write_rewards_container;
    private LinearLayout ll_research_write_reward_credit;
    private LinearLayout ll_research_write_reward_gift;
    private CheckBox cb_research_write_reward;
    private TextView tv_post_write_reward;
    private LinearLayout ll_research_write_rewards_indicator;
    private LinearLayout ll_research_write_reward_credit_indicator;
    private LinearLayout ll_research_write_reward_gift_indicator;
    private TextView tv_research_write_reward_credit_indicator;
    private TextView tv_research_write_reward_gift_indicator;
    private CheckBox cb_research_write_credit;
    private CheckBox cb_research_write_gift;
    private TextView tv_post_write_static_credit;
    private TextView tv_research_write_static_gift;

    private EditText et_research_write_reward_credit_each;
    private EditText et_research_write_reward_credit_person;
    private TextView tv_research_write_reward_credit_total;

    private EditText et_research_write_gift_name;
    private Button btn_research_write_check_url;

    private EditText et_research_write_gift_count;
    private RelativeLayout prize_layout;
    private TextView tv_research_write_image_album;


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
    String author_info;
    Boolean annonymous = true;

    private Integer credit_each = 0;
    private Integer credit_person = 0;
    private Integer prize_num = 0;

    ArrayList<Uri> image_uris = new ArrayList<>();

    GiftImageAdapter2 giftImageAdapter2;
    ArrayList<Bitmap> image_bitmaps = new ArrayList<>();

    int purpose;
    private static Post post;

    CustomDialog customDialog;
    TimePickerDialog timedialog;
    private TimePickerDialog.OnTimeSetListener tcallbackMethod;
    private DatePickerDialog.OnDateSetListener callbackMethod;
    final String[] spinner_esttime = {"선택해주세요", "1분 미만", "1~2분", "2~3분", "3~5분", "5~7분", "7~10분", "10분 초과"};
    private RelativeLayout loading;
    private boolean postDone = false;
    private final postHandler handler = new postHandler();
    private boolean updateDone = false;
    private final updateHandler updateHandler = new updateHandler();

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_write);
        getSupportActionBar().hide();

        customDialog = new CustomDialog(PostWriteActivity.this);
        Intent intent = getIntent();
        purpose = intent.getIntExtra("purpose",1);

        permissionHandle();
        viewHandle();
        setListeners();
        viewSizeHandle();

        cb_research_write_reward.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()){
                    et_research_write_gift_name.setHint("기프티콘 상품");
                    et_research_write_gift_name.setEnabled(true);
                    et_research_write_gift_count.setVisibility(View.VISIBLE);
                    prize_layout.setVisibility(View.VISIBLE);
                    cb_research_write_reward.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#3AD1BF")));
                    ll_research_write_rewards_container.setVisibility(View.VISIBLE);
                    ll_research_write_rewards_indicator.setVisibility(View.VISIBLE);
                    tv_post_write_reward.setVisibility(View.GONE);

                } else {
                    et_research_write_gift_name.setHint("체크하면 내용을 입력할 수 있습니다");
                    et_research_write_gift_name.setEnabled(false);
                    et_research_write_gift_count.setVisibility(View.GONE);
                    prize_layout.setVisibility(View.GONE);
                    cb_research_write_reward.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#C4C4C4")));
                    ll_research_write_rewards_container.setVisibility(View.GONE);
                    ll_research_write_rewards_indicator.setVisibility(View.GONE);
                    tv_post_write_reward.setVisibility(View.VISIBLE);
                }
            }
        });
        cb_research_write_credit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ll_research_write_reward_credit_indicator.setVisibility(View.VISIBLE);
                    cb_research_write_credit.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#3AD1BF")));
                    ll_research_write_reward_credit.setVisibility(View.VISIBLE);
                    tv_post_write_static_credit.setVisibility(View.GONE);
                } else {
                    ll_research_write_reward_credit_indicator.setVisibility(View.GONE);
                    cb_research_write_credit.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#C4C4C4")));
                    ll_research_write_reward_credit.setVisibility(View.GONE);
                    tv_post_write_static_credit.setVisibility(View.VISIBLE);
                }
            }
        });
        cb_research_write_gift.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ll_research_write_reward_gift_indicator.setVisibility(View.VISIBLE);
                    cb_research_write_gift.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#3AD1BF")));
                    ll_research_write_reward_gift.setVisibility(View.VISIBLE);
                    tv_research_write_static_gift.setVisibility(View.GONE);
                } else {
                    ll_research_write_reward_gift_indicator.setVisibility(View.GONE);
                    cb_research_write_gift.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#C4C4C4")));
                    ll_research_write_reward_gift.setVisibility(View.GONE);
                    tv_research_write_static_gift.setVisibility(View.VISIBLE);
                }
            }
        });



        this.InitializeListener();

        tv_research_write_image_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_research_write_gift_count.getText().toString().length() == 0 || et_research_write_gift_count.getText().toString().equals("0")){
                    Toast.makeText(PostWriteActivity.this, "추첨 인원을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    count = Integer.valueOf(et_research_write_gift_count.getText().toString());
                    goToAlbum();
                }
            }
        });

        if (purpose == 2){
            writeSave.setVisibility(View.INVISIBLE);
            post = intent.getParcelableExtra("post");
            et_research_write_title.setText(post.getTitle());
            et_research_write_target.setText(post.getTarget());

            Date date = post.getDeadline();
            SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd a KK시");

            et_research_write_deadline.setText(fm.format(date));
            et_research_write_goal.setText(post.getGoal_participants().toString());
            if (post.isWith_prize()){
                cb_research_write_reward.setChecked(true);
                cb_research_write_reward.setClickable(false);
                prize_layout.setVisibility(View.VISIBLE);
                et_research_write_gift_name.setText(post.getPrize());
                et_research_write_gift_name.setEnabled(true);
                et_research_write_gift_count.setText(post.getNum_prize().toString());
                et_research_write_gift_count.setVisibility(View.VISIBLE);

                getPrizeImages();
            }else{
                cb_research_write_reward.setChecked(false);
                cb_research_write_reward.setClickable(true);
            }
            et_research_write_url.setText(post.getUrl());
            et_research_write_content.setText(post.getContent());
            editUnable(et_research_write_deadline);
            editUnable(et_research_write_url);
            sp_research_write_est_time.setSelection(post.getEst_time()+1);
        }

        et_research_write_deadline.setCursorVisible(false);
        et_research_write_deadline.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int inType = et_research_write_deadline.getInputType(); // backup the input type
                et_research_write_deadline.setInputType(InputType.TYPE_NULL); // disable soft input
                et_research_write_deadline.onTouchEvent(event); // call native handler
                et_research_write_deadline.setInputType(inType); // restore input type
                return true; // consume touch even
            }
        });
        et_research_write_deadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = new Date();
                String year = new SimpleDateFormat("yyyy").format(date);
                String month = new SimpleDateFormat("MM").format(date);
                String day = new SimpleDateFormat("dd").format(date);
                String hour = new SimpleDateFormat("kk").format(date);
                DatePickerDialog dialog = new DatePickerDialog(PostWriteActivity.this, callbackMethod, Integer.valueOf(year), Integer.valueOf(month)-1, Integer.valueOf(day));
                timedialog = new TimePickerDialog(PostWriteActivity.this, tcallbackMethod, Integer.valueOf(hour), 00, false);
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.DATE, 5);

                dialog.getDatePicker().setMinDate(date.getTime());
                dialog.getDatePicker().setMaxDate(cal.getTime().getTime());

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        dialog.getButton(Dialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
                    }
                });
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if(datestr!=null) timedialog.show();
                    }
                });
                timedialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        timedialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE);
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
        });
        et_research_write_deadline.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    date = new Date();
                    String year = new SimpleDateFormat("yyyy").format(date);
                    String month = new SimpleDateFormat("MM").format(date);
                    String day = new SimpleDateFormat("dd").format(date);
                    String hour = new SimpleDateFormat("kk").format(date);
                    DatePickerDialog dialog = new DatePickerDialog(PostWriteActivity.this, callbackMethod, Integer.valueOf(year), Integer.valueOf(month)-1, Integer.valueOf(day));
                    timedialog = new TimePickerDialog(PostWriteActivity.this, tcallbackMethod, Integer.valueOf(hour), 00, false);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date());
                    cal.add(Calendar.DATE, 3);

                    dialog.getDatePicker().setMinDate(date.getTime());
                    dialog.getDatePicker().setMaxDate(cal.getTime().getTime());

                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            dialog.getButton(Dialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
                        }
                    });
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if(datestr!=null) timedialog.show();
                        }
                    });
                    timedialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            timedialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE);
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

        cb_research_write_anonymous.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    annonymous = false;
                    cb_research_write_anonymous.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#3AD1BF")));
                }
                else{
                    annonymous = true;
                    cb_research_write_anonymous.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#C4C4C4")));
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
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(getCurrentFocus()!=null)imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        url = et_research_write_url.getText().toString();
        if (url.length() > 0){
            btn_research_write_check_url.setVisibility(View.VISIBLE);
        } else {
            btn_research_write_check_url.setVisibility(View.INVISIBLE);
        }
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

    public void postPost(String title, String author, Integer author_lvl, String content,
                         Integer participants, Integer goal_participants, String url, Date date,
                         Date deadline, Boolean with_prize, String prize, Integer est_time,
                         String target, Integer count, ArrayList<Reply> comments, boolean done,
                         ArrayList<Uri> images, String author_userid, Boolean annonymous, String author_info) {
        String requestURL = getString(R.string.server)+"/api/posts/postposts";
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, requestURL,
                response -> {
                    try {
                        JSONObject resultObj = new JSONObject(new String(response.data));
                        Log.d("message_from_post", "postPost: " + resultObj.getString("message"));
                        int result = resultObj.getInt("result");
                        if(result==1) {
                            Log.d("why_existed?", "postPost: done well");
                            finish();
                        }
                        else{
                            if(resultObj.getString("message").startsWith("not enough points")){
                                String message = "크레딧이 부족합니다";
                                CustomDialog customDialogFailPost = new CustomDialog(PostWriteActivity.this);
                                customDialogFailPost.setmPositiveListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        customDialogFailPost.dismiss();
                                    }
                                });
                                customDialogFailPost.show();
                                customDialogFailPost.setMessage(message);
                                customDialogFailPost.setPositiveButton("확인");
                            }
                            Toast.makeText(this, "업로드에 실패하셨습니다\n 재시도 해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    postDone = true;
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(PostWriteActivity.this, "업로드에 실패하셨습니다\n 재시도 해주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).start();

                    }
                }) {


            @Override
            protected Map<String, DataPart> getByteData() { //이미지 추가하는곳
                Map<String, DataPart> params = new HashMap<>();
                for(int i=0; i<images.size(); i++){
                    long imagename = System.currentTimeMillis();
                    try {
                        params.put("image"+i, new DataPart(imagename + ".png", getBytes(PostWriteActivity.this, images.get(i))));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return params;
            }

            @Override
            protected Map<String, String> getParams() { //이미지 외 param은 여기서 추가해주세요. 단 전부 string이어야 해서 .toString()을 붙여주세요
                Map<String, String> params = new HashMap<>();

                params.put("title", title);
                params.put("author", author);
                params.put("author_lvl", String.valueOf(author_lvl));
                params.put("content", content);
                params.put("participants", String.valueOf(participants));
                if(goal_participants!=0) {
                    params.put("goal_participants", String.valueOf(goal_participants));
                }
                params.put("url", url);
                SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss.SSS");
                fm.setTimeZone(TimeZone.getTimeZone("UTC"));
                params.put("date", fm.format(date));
                params.put("deadline", fm.format(deadline));
                params.put("with_prize", String.valueOf(with_prize));
                if(with_prize) {
                    params.put("prize", prize);
                    params.put("num_prize", String.valueOf(count));
                } else {
                    params.put("prize", "");
                    params.put("num_prize", String.valueOf(0));
                }
                params.put("est_time", String.valueOf(est_time));
                params.put("target", target);
                params.put("done", String.valueOf(false));
                params.put("comments", String.valueOf(new ArrayList<Reply>()));
                params.put("author_userid", UserPersonalInfo.userID);
                params.put("annonymous", String.valueOf(annonymous));
                params.put("author_info", author_info);
                params.put("num_credit", String.valueOf(credit_person));
                params.put("credit", String.valueOf(credit_each));


                return params;
            }

        };

        //adding the request to volley
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }
    public void updatePost(String title, String author, String content, Integer goal_participants,
                           Integer est_time, String target, ArrayList<Uri> images, Integer num_prize,
                           String prize, Boolean with_prize) {
        String requestURL = getString(R.string.server)+"/api/posts/updatepost/" + post.getID();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.PUT, requestURL,
                response -> {
                    try {Log.d("fix is", ""+response);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    updateDone = true;
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {


            @Override
            protected Map<String, DataPart> getByteData() { //이미지 추가하는곳
                Map<String, DataPart> params = new HashMap<>();
                for(int i=0; i<images.size(); i++){
                    long imagename = System.currentTimeMillis();
                    try {
                        params.put("image"+i, new DataPart(imagename + ".png", getBytes(PostWriteActivity.this, images.get(i))));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return params;
            }

            @Override
            protected Map<String, String> getParams() { //이미지 외 param은 여기서 추가해주세요. 단 전부 string이어야 해서 .toString()을 붙여주세요
                Map<String, String> params = new HashMap<>();

                params.put("title", title);
                params.put("author", author);
                params.put("author_info", author_info);
                params.put("content", content);
                if(goal_participants!=0) {
                    params.put("goal_participants", String.valueOf(goal_participants));
                }
                params.put("est_time", String.valueOf(est_time));
                params.put("target", target);
                if(with_prize) {
                    params.put("with_prize", String.valueOf(true));
                    params.put("num_prize", String.valueOf(num_prize));
                    params.put("prize", prize);
                }

                return params;
            }

        };

        //adding the request to volley
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    public void goToAlbum(){
        Matisse.from(PostWriteActivity.this)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG, MimeType.WEBP))
                .theme(R.style.Matisse_White)
                .countable(false)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .maxSelectable(Math.max(count-image_bitmaps.size(),1))
                .originalEnable(true)
                .maxOriginalSize(10)
                .imageEngine(new GlideEngine())
                .setOnSelectedListener((uriList, pathList) -> {
                    Log.e("onSelected", "onSelected: pathList=" + pathList);
                })
                .showSingleMediaType(true)
                .autoHideToolbarOnSingleTap(true)
                .setOnCheckedListener(isChecked -> {
                    Log.e("isChecked", "onCheck: isChecked=" + isChecked);
                })
                .forResult(13);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 13 && resultCode == RESULT_OK){
            if(purpose==2){
                ArrayList<Bitmap> arrayList = (ArrayList<Bitmap>) image_bitmaps.clone();
                ArrayList<Uri> result_uris = (ArrayList<Uri>) Matisse.obtainResult(data);
                for(int i=0;i<result_uris.size();i++){
                    try {
                        arrayList.add(MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), result_uris.get(i)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                giftImageAdapter2 = new GiftImageAdapter2(PostWriteActivity.this, arrayList);
                gift_image_list.setAdapter(giftImageAdapter2);
                gift_image_list.setLayoutManager(new LinearLayoutManager(PostWriteActivity.this, LinearLayoutManager.HORIZONTAL, false));
                gift_image_list.setVisibility(View.VISIBLE);
                gift_image_list.setVisibility(View.VISIBLE);
                image_uris = (ArrayList<Uri>) Matisse.obtainResult(data);
            }
            else {
                image_uris = (ArrayList<Uri>) Matisse.obtainResult(data);
                giftImageAdapter = new GiftImageAdapter(PostWriteActivity.this, image_uris);
                gift_image_list.setAdapter(giftImageAdapter);
                gift_image_list.setLayoutManager(new LinearLayoutManager(PostWriteActivity.this, LinearLayoutManager.HORIZONTAL, false));
                gift_image_list.setVisibility(View.VISIBLE);

                giftImageAdapter.setOnItemClickListener(new GiftImageAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Intent intent = new Intent(PostWriteActivity.this, TipImageDetail.class);
                        intent.putExtra("uri", image_uris.get(position));
                        intent.putExtra("position", position);
                        startActivityForResult(intent, 20);
                    }
                });
                gift_image_list.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Back_survey();
    }

    public void Back_survey(){
        setResult(0);
        String message;
        if(purpose==1){message = "'보관함'에서 작성중인 글을 임시저장 할 수 있습니다. 작성중인 글을 취소하겠습니까";} else {message = "게시글 수정을 취소하시겠습니까";}

        CustomDialog customDialog = new CustomDialog(PostWriteActivity.this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        customDialog.show();
        customDialog.setMessage(message);
        customDialog.setPositiveButton("작성취소");
        customDialog.setNegativeButton("아니오");
    }

    public void Done_survey(){
        String title = et_research_write_title.getText().toString(); ///게시글 작성 당시 글쓴이의 레벨이 반영?
        String content = et_research_write_content.getText().toString();
        String target = et_research_write_target.getText().toString();
        boolean with_prize = cb_research_write_gift.isChecked();
        est_time = sp_research_write_est_time.getSelectedItemPosition() - 1;
        author_info = et_research_write_author_info.getText().toString();

        if(et_research_write_goal.getText().toString().length() > 0) {
            goalParticipants = Integer.valueOf(et_research_write_goal.getText().toString());
            if (goalParticipants == 0){
                goalParticipants = 30;
            }
        } else{goalParticipants = 30;}

        if (purpose == 1){
            url = et_research_write_url.getText().toString();
            participants = 0;
            author = UserPersonalInfo.name;
            author_lvl = UserPersonalInfo.level;

            SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd a KK시");
            date = new Date();

            deadline = null;

            try {
                deadline = fm.parse(et_research_write_deadline.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (with_prize){
                if(et_research_write_gift_count.getText().toString().length()!=0)
                    count = Integer.valueOf(et_research_write_gift_count.getText().toString());
                else count = 0;
                prize = et_research_write_gift_name.getText().toString();
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
            if(with_prize) {
                if(et_research_write_gift_count.getText().toString().length()!=0)
                    count = Integer.valueOf(et_research_write_gift_count.getText().toString());
                else count = 0;
                prize = et_research_write_gift_name.getText().toString();
            }else {
                count = 0;
                prize = null;///상품 없을 때는 null 처리해야될듯
            }
        }


        if (title.getBytes().length <= 0 || content.getBytes().length <= 0 || target.getBytes().length <= 0 || est_time < 0 || deadline==null){

            CustomDialog customDialog = new CustomDialog(PostWriteActivity.this, null);
            customDialog.show();
            customDialog.setMessage("입력되지 않은 정보가 있습니다");
            customDialog.setNegativeButton("확인");
        } else {
            if (deadline.before(date)){

                CustomDialog customDialog = new CustomDialog(PostWriteActivity.this, null);
                customDialog.show();
                customDialog.setMessage("날짜를 제대로 입력해주세요");
                customDialog.setNegativeButton("확인");
            } else{
                if(url.length()<=0){

                    CustomDialog customDialog = new CustomDialog(PostWriteActivity.this, null);
                    customDialog.show();
                    customDialog.setMessage("입력되지 않은 정보가 있습니다");
                    customDialog.setNegativeButton("확인");
                    AlertDialog.Builder bu = new AlertDialog.Builder(PostWriteActivity.this);
                }else if(with_prize==true && prize.length()<=0){
                    CustomDialog customDialog = new CustomDialog(PostWriteActivity.this, null);
                    customDialog.show();
                    customDialog.setMessage("입력되지 않은 정보가 있습니다");
                    customDialog.setNegativeButton("확인");
                }
                else if(with_prize==true && count==0){
                    CustomDialog customDialog = new CustomDialog(PostWriteActivity.this, null);
                    customDialog.show();
                    customDialog.setMessage("추첨 인원을 입력해주세요");
                    customDialog.setNegativeButton("확인");
                }
                else if(!(url.contains("docs.google.com") || url.contains("forms.gle"))){
                    CustomDialog customDialog = new CustomDialog(PostWriteActivity.this, null);
                    customDialog.show();
                    customDialog.setMessage("제공하지 않는 url입니다");
                    customDialog.setNegativeButton("확인");
                }else if(with_prize==true && (count!=(image_uris.size()+image_bitmaps.size()))){
                    CustomDialog customDialog = new CustomDialog(PostWriteActivity.this, null);
                    customDialog.show();
                    customDialog.setMessage("기프티콘 이미지 개수와 추첨 인원 수가 다릅니다");
                    customDialog.setNegativeButton("확인");
                }
                else {
                    if (purpose == 1) {
                        loading.setVisibility(View.VISIBLE);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                postPost(title, author, author_lvl, content, participants, goalParticipants, url, date, deadline, with_prize, prize, est_time, target, count, new ArrayList<Reply>(), false, image_uris, UserPersonalInfo.userID, annonymous, author_info);

                                while(!(postDone)) {
                                    try {
                                        Thread.sleep(100);
                                    } catch (Exception e) {}
                                }

                                Message message = handler.obtainMessage();
                                handler.sendMessage(message);
                            }
                        }).start();

                    } else if (purpose == 2) {
                        post.setTitle(title);
                        post.setTarget(target);
                        post.setGoal_participants(goalParticipants);
                        post.setEst_time(est_time);
                        post.setContent(content);
                        post.setWith_prize(with_prize);
                        post.setPrize(prize);
                        post.setNum_prize(count);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                updatePost(title, author, content, goalParticipants, est_time, target, image_uris, count, prize, with_prize);
                                while(!(updateDone)) {
                                    try {
                                        Thread.sleep(100);
                                    } catch (Exception e) {}
                                }
                                Message message = updateHandler.obtainMessage();
                                updateHandler.sendMessage(message);
                            }
                        }).start();

                    }
                }
            }
        }
    }
    public void setDeadline(){
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss.SSS");
        try {
            deadline = fm.parse(datestr+"T"+ timestr+":00.000Z");
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        long diff = deadline.getTime() - date.getTime();
        if (Integer.valueOf((int) diff) > 72*60*60*1000){

            CustomDialog customDialog = new CustomDialog(PostWriteActivity.this, null);
            customDialog.show();
            customDialog.setMessage("리서치 기간이 72시간을 초과했습니다");
            customDialog.setNegativeButton("확인");
            et_research_write_deadline.clearFocus();
        } else {
            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd a KK시");

            et_research_write_deadline.setText(transFormat.format(deadline));
            et_research_write_deadline.setTextSize(2, 15);
            et_research_write_deadline.clearFocus();
        }
    }

    @SuppressLint("ResourceAsColor")
    public void editUnable(EditText e){
        e.setClickable(false);
        e.setFocusable(false);
        e.setEnabled(false);
        e.setFocusableInTouchMode(false);
        e.setTextColor(R.color.text_gray);
    }
    public void saveDialog(){
        AlertDialog.Builder builder2 = new AlertDialog.Builder(PostWriteActivity.this);
                        builder2.setItems(R.array.savemenu, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        customDialog = new CustomDialog(PostWriteActivity.this, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                tempSave();

                                                customDialog.dismiss();
                                            }
                                        });
                                        customDialog.show();
                                        customDialog.setMessage("임시저장은 최대 1개까지 가능하여 임시저장할 경우 기존에 임시저장한 글이 사라집니다. " +"임시저장하겠습니까?");
                                        customDialog.setPositiveButton("임시저장");
                                        customDialog.setNegativeButton("취소");
                                        break;
                                    case 1:
                                        String message;
                                        if (et_research_write_title.getText().toString().length()==0 && et_research_write_target.getText().toString().length()==0 && et_research_write_deadline.getText().toString().length()==0 && et_research_write_gift_name.getText().toString().length()==0 && et_research_write_url.getText().toString().length()==0 && et_research_write_content.getText().toString().length()==0){
                                            message = "임시저장된 글을 불러오겠습니까?";
                                        } else {
                                            message = "임시저장된 글을 불러올 경우\n" +
                                                    "작성중인 글은 사라지게 됩니다.\n" +
                                                    "불러오겠습니까?";
                                        }
                                        customDialog = new CustomDialog(PostWriteActivity.this, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                loadSave();

                                                customDialog.dismiss();
                                            }
                                        });
                                        customDialog.show();
                                        customDialog.setMessage(message);
                                        customDialog.setPositiveButton("불러오기");
                                        customDialog.setNegativeButton("아니오");
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

        String title = et_research_write_title.getText().toString();
        String content = et_research_write_content.getText().toString();
        String target = et_research_write_target.getText().toString();
        boolean with_prize = cb_research_write_gift.isChecked();
        est_time = sp_research_write_est_time.getSelectedItemPosition()-1;
        if (et_research_write_goal.getText().toString().length() > 0 ){
            goalParticipants = Integer.valueOf(et_research_write_goal.getText().toString());
        } else {
            goalParticipants = 0;
        }
        url = et_research_write_url.getText().toString();

        if (with_prize){
            count = Integer.valueOf(et_research_write_gift_count.getText().toString());
            prize = et_research_write_gift_name.getText().toString();
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
        tempWriteedit.putString("deadline", et_research_write_deadline.getText().toString());
        tempWriteedit.putInt("count", count);
        tempWriteedit.putString("prize",prize);
        tempWriteedit.putBoolean("with_prize", with_prize);
        tempWriteedit.commit();
    }

    public void loadSave(){
        SharedPreferences tempWrite = getSharedPreferences("tempWrite", Activity.MODE_PRIVATE);

        et_research_write_title.setText(tempWrite.getString("title",""));
        et_research_write_target.setText(tempWrite.getString("target",""));

        et_research_write_deadline.setText(tempWrite.getString("deadline","수정불가, YYYYMMDD"));
        if (tempWrite.getInt("goalparticipations",0) != 0){
            et_research_write_goal.setText(String.valueOf(tempWrite.getInt("goalparticipations",0)));
        }
        if (tempWrite.getBoolean("with_prize",false)){
            cb_research_write_reward.setChecked(true);
            et_research_write_gift_name.setText(tempWrite.getString("prize",""));
            et_research_write_gift_count.setText(String.valueOf(tempWrite.getInt("count",0)));
            et_research_write_gift_name.setEnabled(true);
            et_research_write_gift_count.setVisibility(View.VISIBLE);
            prize_layout.setVisibility(View.VISIBLE);
        } else {
            cb_research_write_reward.setChecked(false);
        }
        et_research_write_url.setText(tempWrite.getString("uri",""));
        sp_research_write_est_time.setSelection(tempWrite.getInt("est_time",0));
        et_research_write_content.setText(tempWrite.getString("content",""));
    }
    /**
     * get bytes array from Uri.
     *
     * @param context current context.
     * @param uri uri fo the file to read.
     * @return a bytes array.
     * @throws IOException
     */
    public static byte[] getBytes(Context context, Uri uri) throws IOException {
        InputStream iStream = context.getContentResolver().openInputStream(uri);
        try {
            return getBytes(iStream);
        } finally {
            // close the stream
            try {
                iStream.close();
            } catch (IOException ignored) { /* do nothing */ }
        }
    }



    /**
     * get bytes from input stream.
     *
     * @param inputStream inputStream.
     * @return byte array read from the inputStream.
     * @throws IOException
     */
    public static byte[] getBytes(InputStream inputStream) throws IOException {

        byte[] bytesResult = null;
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        try {
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            bytesResult = byteBuffer.toByteArray();
        } finally {
            // close the stream
            try{ byteBuffer.close(); } catch (IOException ignored){ /* do nothing */ }
        }
        return bytesResult;
    }

    private void getPrizeImages() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                giftImageAdapter2 = new GiftImageAdapter2(PostWriteActivity.this, image_bitmaps);
                gift_image_list.setAdapter(giftImageAdapter2);
                gift_image_list.setLayoutManager(new LinearLayoutManager(PostWriteActivity.this, LinearLayoutManager.HORIZONTAL, false));
                gift_image_list.setVisibility(View.VISIBLE);
                gift_image_list.setVisibility(View.VISIBLE);

            }
        };
        if(post.getPrize_urls()!=null){
            for (int i = 0; i < post.getPrize_urls().size(); i++) {
                int finalI = i;
                new Thread() {
                    Message msg;

                    public void run() {
                        try {
                            String uri = post.getPrize_urls().get(finalI);
                            URL url = new
                                    URL(uri);
                            URLConnection conn = url.openConnection();
                            conn.connect();
                            BufferedInputStream bis = new
                                    BufferedInputStream(conn.getInputStream());

                            Bitmap bm = BitmapFactory.decodeStream(bis);
                            image_bitmaps.add(bm);
                            bis.close();
                            msg = handler.obtainMessage();
                            handler.sendMessage(msg);
                        } catch (IOException e) {
                        }
                    }
                }.start();
            }
        }
    }

    private class postHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            loading.setVisibility(View.GONE);
            postDone = false;
        }
    }
    private class updateHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            loading.setVisibility(View.GONE);
            Intent intent = new Intent(PostWriteActivity.this, PostDetailActivity.class);
            intent.putExtra("post", post);
            startActivity(intent);
            setResult(FIX_DONE, intent);
            finish();
            updateDone = false;
        }
    }
    private void permissionHandle() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 200
                    );
                }
            }
        }
    }
    private void viewHandle() {

        writeBack = findViewById(R.id.writeBack);
        writeSave = findViewById(R.id.writesave);
        writeDone = findViewById(R.id.writeDone);

        et_research_write_title = findViewById(R.id.et_research_write_title);
        et_research_write_target = findViewById(R.id.et_research_write_target);
        et_research_write_author_info = findViewById(R.id.et_research_write_author_info);
        cb_research_write_anonymous = findViewById(R.id.cb_research_write_anonymous);
        et_research_write_deadline = findViewById(R.id.et_research_write_deadline);
        et_research_write_goal = findViewById(R.id.et_research_write_goal);


        cb_research_write_reward = findViewById(R.id.cb_research_write_reward);
        ll_research_write_rewards_indicator = findViewById(R.id.ll_research_write_rewards_indicator);
            ll_research_write_reward_credit_indicator = findViewById(R.id.ll_research_write_reward_credit_indicator);
                tv_research_write_reward_credit_indicator = findViewById(R.id.tv_research_write_reward_credit_indicator);
            ll_research_write_reward_gift_indicator = findViewById(R.id.ll_research_write_reward_gift_indicator);
                tv_research_write_reward_gift_indicator = findViewById(R.id.tv_research_write_reward_gift_indicator);


        ll_research_write_rewards_container = findViewById(R.id.ll_research_write_rewards_container);
            cb_research_write_credit = findViewById(R.id.cb_research_write_credit);
                tv_post_write_static_credit = findViewById(R.id.tv_post_write_static_credit);
                ll_research_write_reward_credit = findViewById(R.id.ll_research_write_reward_credit);
                    et_research_write_reward_credit_each = findViewById(R.id.et_research_write_reward_credit_each);
                    et_research_write_reward_credit_person = findViewById(R.id.et_research_write_reward_credit_person);
                    tv_research_write_reward_credit_total = findViewById(R.id.tv_research_write_reward_credit_total);
            cb_research_write_gift = findViewById(R.id.cb_research_write_gift);
                tv_research_write_static_gift = findViewById(R.id.tv_research_write_static_gift);
                ll_research_write_reward_gift = findViewById(R.id.ll_research_write_reward_gift);
                    et_research_write_gift_name = findViewById(R.id.et_research_write_gift_name);
                    et_research_write_gift_count = findViewById(R.id.et_research_write_gift_count);


        et_research_write_url = findViewById(R.id.et_research_write_url);
            btn_research_write_check_url = findViewById(R.id.btn_research_write_check_url);
        sp_research_write_est_time = findViewById(R.id.sp_research_write_est_time);
        et_research_write_content = findViewById(R.id.et_research_write_content);

        tv_post_write_reward = findViewById(R.id.tv_post_write_reward);
        prize_layout = findViewById(R.id.prize_image_layout);
        tv_research_write_image_album = findViewById(R.id.tv_research_write_image_album);
        gift_image_list = findViewById(R.id.gith_image_list);

        loading = findViewById(R.id.loadingPanel);
    }
    private void setListeners() {
        setUrlListener();
        setRewardListener();
        setTimeListener();
    }
    private void setUrlListener() {
        btn_research_write_check_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = et_research_write_url.getText().toString();
                if (url.contains("docs.google.com") || url.contains("forms.gle")){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    Intent newIntent = new Intent(getApplicationContext(), SurveyWebActivity.class);
                    newIntent.putExtra("url", url);
                    newIntent.putExtra("requestCode", CHECK);
                    startActivityForResult(newIntent, CHECK);
                } else {
                    customDialog.customRejectDialog("제공하지 않는 url 입니다.", "확인");
                }
            }
        });
        et_research_write_url.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    btn_research_write_check_url.setVisibility(View.GONE);
                } else {
                    btn_research_write_check_url.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    private void setRewardListener() {
        et_research_write_gift_count.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    prize_num = Integer.parseInt(s.toString());
                } catch (Exception e) {
                    prize_num = 0;
                }
                updateIndicators();
            }
        });
        et_research_write_reward_credit_each.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    credit_each = Integer.parseInt(s.toString());
                } catch (Exception e) {
                    credit_each = 0;
                }
                updateIndicators();
            }
        });
        et_research_write_reward_credit_person.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    credit_person = Integer.parseInt(s.toString());
                } catch (Exception e) {
                    credit_person = 0;
                }
                updateIndicators();
            }
        });
    }
    private void setTimeListener() {
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.simple_spinner_item, spinner_esttime);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_research_write_est_time.setAdapter(adapter);
        sp_research_write_est_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView text = view.findViewById(android.R.id.text1);
                if(position==0) text.setTextColor(getColor(R.color.nav_gray));
                else text.setTextColor(getColor(R.color.text_black));
                est_time = sp_research_write_est_time.getSelectedItemPosition() - 1;
                return;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void viewSizeHandle() {
        et_research_write_title.setTextSize((float) (MainActivity.screen_width / 23.4285714286));
        writeBack.setTextSize((float) (MainActivity.screen_width / 23.4285714286));
        writeSave.setTextSize((float) (MainActivity.screen_width / 23.4285714286));
        writeDone.setTextSize((float) (MainActivity.screen_width / 23.4285714286));
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) writeDone.getLayoutParams();
        params.rightMargin = (int) (MainActivity.screen_width_px / 15.7692307692);
        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) writeSave.getLayoutParams();
        params2.rightMargin = (int) (MainActivity.screen_width_px / 21.8666666667);
        RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) writeBack.getLayoutParams();
        params3.leftMargin = (int) (MainActivity.screen_width_px / 15.7692307692);
    }
    @SuppressLint("SetTextI18n")
    private void updateIndicators() {
        tv_research_write_reward_credit_indicator.setText("X " + credit_each * credit_person);
        tv_research_write_reward_credit_total.setText(String.valueOf(credit_each * credit_person));
        tv_research_write_reward_gift_indicator.setText("X " + prize_num);
    }
}
