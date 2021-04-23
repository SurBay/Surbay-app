package com.pumasi.surbay;

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
import android.text.InputType;
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
import com.esafirm.imagepicker.model.Image;
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
import java.io.FileNotFoundException;
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
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class WriteActivity extends AppCompatActivity {
    static final int NEWPOST = 1;
    static final int FIX_DONE = 3;
    static final int CHECK = 2;
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

    private EditText writeAuthorInfo;
    private CheckBox writeAnnonymous;
    private TextView wrtieAnnonymousTextView;


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
    private List<Image> images;
    private RelativeLayout loading;
    private boolean postDone = false;
    private postHandler handler = new postHandler();
    private boolean updateDone = false;
    private updateHandler updateHandler = new updateHandler();

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_write);
        getSupportActionBar().hide();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 200
                    );
                }
            }
        }

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

        writeAuthorInfo = findViewById(R.id.write_author_info);
        writeAnnonymous = findViewById(R.id.write_annonymous);
        wrtieAnnonymousTextView = findViewById(R.id.write_annonymous_textview);

        writeBack = findViewById(R.id.writeBack);
        writeSave = findViewById(R.id.writesave);
        writeDone = findViewById(R.id.writeDone);

        loading = findViewById(R.id.loadingPanel);
        loading.setVisibility(View.GONE);


        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.simple_spinner_item, spinner_esttime);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        writeEstTime.setAdapter(adapter);
        writeEstTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView text = view.findViewById(android.R.id.text1);
                if(position==0) text.setTextColor(getColor(R.color.nav_gray));
                else text.setTextColor(getColor(R.color.text_black));
                est_time = writeEstTime.getSelectedItemPosition() - 1;
                return;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.InitializeListener();
        if(writeTitle.getText().length()!=0){writeTitle.setTextColor(Color.parseColor("#000000"));}
        if(writeTarget.getText().length()!=0){writeTarget.setTextColor(Color.parseColor("#000000"));}
        if(writeDeadline.getText().length()!=0){writeDeadline.setTextColor(Color.parseColor("#000000"));}
        if(writePrize.getText().length()!=0){writePrize.setTextColor(Color.parseColor("#000000"));}
        if(writeUrl.getText().length()!=0){writeUrl.setTextColor(Color.parseColor("#000000"));}
        if(writeContent.getText().length()!=0){writeContent.setTextColor(Color.parseColor("#000000"));}

        withPrize.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()){
                    writePrize.setHint("기프티콘 상품");
                    writePrize.setEnabled(true);
                    prize_count.setVisibility(View.VISIBLE);
                    prize_layout.setVisibility(View.VISIBLE);
                    withPrize.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#3AD1BF")));
                } else {
                    writePrize.setHint("체크하면 내용을 입력할 수 있습니다");
                    writePrize.setEnabled(false);
                    prize_count.setVisibility(View.GONE);
                    prize_layout.setVisibility(View.GONE);
                    withPrize.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#C4C4C4")));
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
            Log.d("postiswithprize",""+post.getPrize_urls()+post.isWith_prize());
            if (post.isWith_prize()){
                withPrize.setChecked(true);
                withPrize.setClickable(false);
                prize_layout.setVisibility(View.VISIBLE);
                writePrize.setText(post.getPrize());
                writePrize.setEnabled(true);
                prize_count.setText(post.getNum_prize().toString());
                prize_count.setVisibility(View.VISIBLE);

                getPrizeImages();
            }else{
                withPrize.setChecked(false);
                withPrize.setClickable(true);
            }
            writeUrl.setText(post.getUrl());
            writeContent.setText(post.getContent());
            editUnable(writeDeadline);
            editUnable(writeUrl);
//            writePrize.setClickable(false);
            writeEstTime.setSelection(post.getEst_time()+1);
        }

        urlCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = writeUrl.getText().toString();
                if (url.contains("docs.google.com") || url.contains("forms.gle")){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    Intent newIntent = new Intent(getApplicationContext(), SurveyWebActivity.class);
                    newIntent.putExtra("url", url);
                    newIntent.putExtra("requestCode", CHECK);
                    startActivity(newIntent);
                    startActivityForResult(newIntent, CHECK);
                } else {

                    CustomDialog customDialog = new CustomDialog(WriteActivity.this, null);
                    customDialog.show();
                    customDialog.setMessage("제공하지 않는 url입니다");
                    customDialog.setNegativeButton("확인");
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
        writeDeadline.setCursorVisible(false);
        writeDeadline.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int inType = writeDeadline.getInputType(); // backup the input type
                writeDeadline.setInputType(InputType.TYPE_NULL); // disable soft input
                writeDeadline.onTouchEvent(event); // call native handler
                writeDeadline.setInputType(inType); // restore input type
                return true; // consume touch even
            }
        });
        writeDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = new Date();
                String year = new SimpleDateFormat("yyyy").format(date);
                String month = new SimpleDateFormat("MM").format(date);
                String day = new SimpleDateFormat("dd").format(date);
                String hour = new SimpleDateFormat("kk").format(date);
                DatePickerDialog dialog = new DatePickerDialog(WriteActivity.this, callbackMethod, Integer.valueOf(year), Integer.valueOf(month)-1, Integer.valueOf(day));
                timedialog = new TimePickerDialog(WriteActivity.this, tcallbackMethod, Integer.valueOf(hour), 00, false);
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
        writeDeadline.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    date = new Date();
                    String year = new SimpleDateFormat("yyyy").format(date);
                    String month = new SimpleDateFormat("MM").format(date);
                    String day = new SimpleDateFormat("dd").format(date);
                    String hour = new SimpleDateFormat("kk").format(date);
                    DatePickerDialog dialog = new DatePickerDialog(WriteActivity.this, callbackMethod, Integer.valueOf(year), Integer.valueOf(month)-1, Integer.valueOf(day));
                    timedialog = new TimePickerDialog(WriteActivity.this, tcallbackMethod, Integer.valueOf(hour), 00, false);
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

        writeAnnonymous.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    annonymous = false;
                    withPrize.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#3AD1BF")));
                }
                else{
                    annonymous = true;
                    withPrize.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#C4C4C4")));
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
        /*imm.hideSoftInputFromWindow(findViewById(R.id.write_title).getWindowToken(), 0);
        imm.hideSoftInputFromWindow(findViewById(R.id.write_content).getWindowToken(), 0);*/
        url = writeUrl.getText().toString();
        if (url.length() > 0){
            urlCheck.setVisibility(View.VISIBLE);
        } else {
            urlCheck.setVisibility(View.INVISIBLE);
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
        String requestURL = getString(R.string.server)+"/api/posts/";
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, requestURL,
                response -> {
                    try {
                        Log.d("response is", ""+new String(response.data));
                        JSONObject resultObj = new JSONObject(new String(response.data));
                        int result = resultObj.getInt("result");
                        if(result==1) {
                            String id = resultObj.getString("id");
                            SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss");
                            fm.setTimeZone(TimeZone.getTimeZone("UTC"));
                            String utc_date = fm.format(date);
                            fm.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                            Date realdate = fm.parse(utc_date);
                            fm.setTimeZone(TimeZone.getTimeZone("UTC"));
                            String utc_deadline = fm.format(deadline);
                            fm.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                            Date realdeadline = fm.parse(utc_deadline);
                            Integer pinned = 0;
                            Post item = new Post(id, title, author, author_lvl, content, participants, goal_participants, url, realdate, realdeadline, with_prize, prize, est_time, target, count, new ArrayList<Reply>(), false, 0, new ArrayList<String>(), new ArrayList<String>(), false, author_userid, pinned, annonymous, author_info);

                            MainActivity.postArrayList.add(item);
                            Log.d("response id", id);
                            Intent intent = new Intent(WriteActivity.this, BoardPost.class);
                            intent.putExtra("post", item);
                            setResult(NEWPOST, intent);
                            finish();
                        }
                        else{
                            if(resultObj.getString("message").startsWith("not enough points")){
                                String message = "크레딧이 부족합니다";
                                CustomDialog customDialogFailPost = new CustomDialog(WriteActivity.this);
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
                        }
                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                    postDone = true;
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("GotError",""+error);
                    }
                }) {


            @Override
            protected Map<String, DataPart> getByteData() { //이미지 추가하는곳
                Map<String, DataPart> params = new HashMap<>();
                for(int i=0; i<images.size(); i++){
                    long imagename = System.currentTimeMillis();
                    try {
                        params.put("image"+i, new DataPart(imagename + ".png", getBytes(WriteActivity.this, images.get(i))));
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
        Log.d("fix", UserPersonalInfo.name);
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.PUT, requestURL,
                response -> {
                    try {Log.d("fix is", ""+response);

                    } catch (Exception e) {
                        Log.d("exception", "volley error");
                        e.printStackTrace();
                    }
                    updateDone = true;
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("GotError",""+error);
                    }
                }) {


            @Override
            protected Map<String, DataPart> getByteData() { //이미지 추가하는곳
                Map<String, DataPart> params = new HashMap<>();
                for(int i=0; i<images.size(); i++){
                    long imagename = System.currentTimeMillis();
                    try {
                        params.put("image"+i, new DataPart(imagename + ".png", getBytes(WriteActivity.this, images.get(i))));
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
//        Config config = new Config();
//        config.setSelectionMin(count);
//        config.setSelectionLimit(count);
//        ImagePickerActivity.setConfig(config);
//        Intent intent  = new Intent(this, ImagePickerActivity.class);
//        startActivityForResult(intent,13);
        Matisse.from(WriteActivity.this)
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
//        ImagePicker.create(this)
//                .returnMode(ReturnMode.NONE)
//                .folderMode(true) // folder mode (false by default)
////                .toolbarFolderTitle("폴더") // folder selection title
////                .toolbarImageTitle("") // image selection title
//                .toolbarArrowColor(Color.WHITE) // Toolbar 'up' arrow color
//                .multi() // multi mode (default mode)
//                .limit(Math.max(count-image_bitmaps.size(),1)) // max images can be selected (99 by default)
//                .showCamera(false) // show camera or not (true by default)
//                .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
//                .origin((ArrayList<Image>) images) // original selected images, used in multi mode
////                .exclude((ArrayList<Image>) images) // exclude anything that in image.getPath()
//                .theme(R.style.ImagePickerTheme) // must inherit ef_BaseTheme. please refer to sample
//                .enableLog(false) // disabling log
//                .language("ko")
//                .start(); // start image picker activity with request code
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
//            // Get a list of picked images
//            images = ImagePicker.getImages(data);
//        }
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
                giftImageAdapter2 = new GiftImageAdapter2(WriteActivity.this, arrayList);
                gift_image_list.setAdapter(giftImageAdapter2);
                gift_image_list.setLayoutManager(new LinearLayoutManager(WriteActivity.this, LinearLayoutManager.HORIZONTAL, false));
                gift_image_list.setVisibility(View.VISIBLE);
                gift_image_list.setVisibility(View.VISIBLE);
                prize_list.setVisibility(View.GONE);
                image_uris = (ArrayList<Uri>) Matisse.obtainResult(data);
            }
            else {
                image_uris = (ArrayList<Uri>) Matisse.obtainResult(data);
                //            image_uris = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

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
    }

    @Override
    public void onBackPressed() {
        Back_survey();
    }

    public void Back_survey(){
        setResult(0);
        String message;
        if(purpose==1){message = "'보관함'에서 작성중인 글을 임시저장 할 수 있습니다. 임시저장 없이 작성중인 글을 취소하겠습니까";} else {message = "게시글 수정을 취소하시겠습니까";}

        CustomDialog customDialog = new CustomDialog(WriteActivity.this, new View.OnClickListener() {
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
        String title = writeTitle.getText().toString(); ///게시글 작성 당시 글쓴이의 레벨이 반영?
        String content = writeContent.getText().toString();
        String target = writeTarget.getText().toString();
        boolean with_prize = withPrize.isChecked();
        est_time = writeEstTime.getSelectedItemPosition() - 1;
        Log.d("est_timeis",""+est_time);
        author_info = writeAuthorInfo.getText().toString();

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
                if(prize_count.getText().toString().length()!=0)
                    count = Integer.valueOf(prize_count.getText().toString());
                else count = 0;
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
//            prize = post.getPrize();
            if(with_prize) {
                if(prize_count.getText().toString().length()!=0)
                    count = Integer.valueOf(prize_count.getText().toString());
                else count = 0;
                prize = writePrize.getText().toString();
            }else {
                count = 0;
                prize = null;///상품 없을 때는 null 처리해야될듯
            }

//            count = post.getNum_prize();
        }

        SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.date_format));


        if (title.getBytes().length <= 0 || content.getBytes().length <= 0 || target.getBytes().length <= 0 || est_time < 0 || deadline==null){

            CustomDialog customDialog = new CustomDialog(WriteActivity.this, null);
            customDialog.show();
            customDialog.setMessage("입력되지 않은 정보가 있습니다");
            customDialog.setNegativeButton("확인");
        } else {
            if (deadline.before(date)){

                CustomDialog customDialog = new CustomDialog(WriteActivity.this, null);
                customDialog.show();
                customDialog.setMessage("날짜를 제대로 입력해주세요");
                customDialog.setNegativeButton("확인");
            } else{
                if(url.length()<=0){

                    CustomDialog customDialog = new CustomDialog(WriteActivity.this, null);
                    customDialog.show();
                    customDialog.setMessage("입력되지 않은 정보가 있습니다");
                    customDialog.setNegativeButton("확인");
                    AlertDialog.Builder bu = new AlertDialog.Builder(WriteActivity.this);
                }else if(with_prize==true && prize.length()<=0){
                    CustomDialog customDialog = new CustomDialog(WriteActivity.this, null);
                    customDialog.show();
                    customDialog.setMessage("입력되지 않은 정보가 있습니다");
                    customDialog.setNegativeButton("확인");
                }
                else if(with_prize==true && count==0){
                    CustomDialog customDialog = new CustomDialog(WriteActivity.this, null);
                    customDialog.show();
                    customDialog.setMessage("추첨 인원을 입력해주세요");
                    customDialog.setNegativeButton("확인");
                }
                else if(!(url.contains("docs.google.com") || url.contains("forms.gle"))){
                    CustomDialog customDialog = new CustomDialog(WriteActivity.this, null);
                    customDialog.show();
                    customDialog.setMessage("제공하지 않는 url입니다");
                    customDialog.setNegativeButton("확인");
                }else if(with_prize==true && (count!=(image_uris.size()+image_bitmaps.size()))){
                    Log.d("countimageis", ""+count+"  "+image_bitmaps.size()+"  "+image_uris.size());
                    CustomDialog customDialog = new CustomDialog(WriteActivity.this, null);
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


                        Log.d("date formatted", formatter.format(deadline));
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
            Log.d("writedeadline", deadline.toString());
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        long diff = deadline.getTime() - date.getTime();
        Log.d("writedeadline", String.valueOf(diff));
        if (Integer.valueOf((int) diff) > 120*60*60*1000){

            CustomDialog customDialog = new CustomDialog(WriteActivity.this, null);
            customDialog.show();
            customDialog.setMessage("설문 기간이 120시간을 초과했습니다");
            customDialog.setNegativeButton("확인");
            writeDeadline.clearFocus();
        } else {
            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd a KK시");

            writeDeadline.setText(transFormat.format(deadline));
            writeDeadline.setTextSize(2, 15);
            writeDeadline.clearFocus();
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
        AlertDialog.Builder builder2 = new AlertDialog.Builder(WriteActivity.this);
                        builder2.setItems(R.array.savemenu, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        customDialog = new CustomDialog(WriteActivity.this, new View.OnClickListener() {
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
                                        if (writeTitle.getText().toString().length()==0 && writeTarget.getText().toString().length()==0 && writeDeadline.getText().toString().length()==0 && writePrize.getText().toString().length()==0 && writeUrl.getText().toString().length()==0 && writeContent.getText().toString().length()==0){
                                            message = "임시저장된 글을 불러오겠습니까?";
                                        } else {
                                            message = "임시저장된 글을 불러올 경우\n" +
                                                    "작성중인 글은 사라지게 됩니다.\n" +
                                                    "불러오겠습니까?";
                                        }
                                        customDialog = new CustomDialog(WriteActivity.this, new View.OnClickListener() {
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

        String title = writeTitle.getText().toString();
        String content = writeContent.getText().toString();
        String target = writeTarget.getText().toString();
        boolean with_prize = withPrize.isChecked();
        est_time = writeEstTime.getSelectedItemPosition()-1;
        if (writeGoalParticipants.getText().toString().length() > 0 ){
            goalParticipants = Integer.valueOf(writeGoalParticipants.getText().toString());
        } else {
            goalParticipants = 0;
        }
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
    /**
     * get bytes array from Uri.
     *
     * @param context current context.
     * @param uri uri fo the file to read.
     * @return a bytes array.
     * @throws IOException
     */
    public static byte[] getBytes(Context context, Uri uri) throws IOException {
//        InputStream iStream = context.getContentResolver().openInputStream(Uri.fromFile(new File(uri.getPath())));
//        InputStream iStream = new FileInputStream(new File(uri.getPath()));
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

    public byte[] getFileDataFromUri(Uri uri){
        InputStream iStream = null;
        try {
            iStream = getContentResolver().openInputStream(uri);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] inputData = new byte[0];
        try {
            inputData = getBytes(iStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputData;
    }
    private void getPrizeImages() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Log.d("got messagemessage", "here");
                giftImageAdapter2 = new GiftImageAdapter2(WriteActivity.this, image_bitmaps);
                gift_image_list.setAdapter(giftImageAdapter2);
                gift_image_list.setLayoutManager(new LinearLayoutManager(WriteActivity.this, LinearLayoutManager.HORIZONTAL, false));
                gift_image_list.setVisibility(View.VISIBLE);
                gift_image_list.setVisibility(View.VISIBLE);
                prize_list.setVisibility(View.GONE);

            }
        };
        if(post.getPrize_urls()!=null){
            for (int i = 0; i < post.getPrize_urls().size(); i++) {
                int finalI = i;
                new Thread() {
                    Message msg;

                    public void run() {
                        try {

                            Log.d("start", "bitmap no." + finalI);
                            String uri = post.getPrize_urls().get(finalI);
                            URL url = new
                                    URL(uri);
                            URLConnection conn = url.openConnection();
                            conn.connect();
                            BufferedInputStream bis = new
                                    BufferedInputStream(conn.getInputStream());

                            Bitmap bm = BitmapFactory.decodeStream(bis);
                            Log.d("got bitmap", "bitmap no." + finalI + "    " + bm);
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
            Intent intent = new Intent(WriteActivity.this, PostDetailActivity.class);
            intent.putExtra("post", post);
            startActivity(intent);
            setResult(FIX_DONE, intent);
            Log.d("fix", UserPersonalInfo.name);
            finish();
            updateDone = false;
        }
    }
}
