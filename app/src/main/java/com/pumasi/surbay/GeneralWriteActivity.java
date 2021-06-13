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
import android.widget.ImageButton;
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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.pumasi.surbay.adapter.GiftImageAdapter;
import com.pumasi.surbay.adapter.GiftImageAdapter2;
import com.pumasi.surbay.adapter.PollWriteAdapter;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.General;
import com.pumasi.surbay.classfile.GifSizeFilter;
import com.pumasi.surbay.classfile.Poll;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.classfile.VolleyMultipartRequest;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import org.json.JSONArray;
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

public class    GeneralWriteActivity extends AppCompatActivity {
    static final int NEWPOST = 1;
    static final int FIX_DONE = 3;
    static final int CHECK = 2;
    private InputMethodManager imm;

    RecyclerView pollRecyclerView;

    TextView writeBack;
    TextView writeDone;

    private EditText writeTitle;
    private EditText writeContent;
    private AlertDialog dialog;

    ImageButton addPollBtn;

    CheckBox multiResponse;
    CheckBox setDeadline;
    TextView deadlineTextview;

    String author;
    Integer author_lvl;
    Date date;
    Date deadline;
    String datestr;
    String timestr;

    public static ArrayList<Uri> image_uris = new ArrayList<>();

    GiftImageAdapter2 giftImageAdapter2;
    ArrayList<Bitmap> image_bitmaps = new ArrayList<>();

    CustomDialog customDialog;
    TimePickerDialog timedialog;
    private TimePickerDialog.OnTimeSetListener tcallbackMethod;
    private DatePickerDialog.OnDateSetListener callbackMethod;
    private List<Image> images;
    private RelativeLayout loading;
    private boolean postDone = false;
    private postHandler handler = new postHandler();

    private static int image_add_pos;

    static PollWriteAdapter pollWriteAdapter;

    public static ArrayList<String> write_polls = new ArrayList<>();
    private boolean getGeneralsDone = false;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_write);
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


        writeTitle = findViewById(R.id.write_title);
        writeContent = findViewById(R.id.write_content);
        writeBack = findViewById(R.id.writeBack);
        writeDone = findViewById(R.id.writeDone);

        loading = findViewById(R.id.loadingPanel);
        loading.setVisibility(View.GONE);

        pollRecyclerView = findViewById(R.id.write_polls);

        addPollBtn = findViewById(R.id.write_add_poll);
        multiResponse = findViewById(R.id.write_multi_response_checkbox);
        setDeadline = findViewById(R.id.write_deadline_checkbox);
        deadlineTextview = findViewById(R.id.write_deadline);

        this.InitializeListener();
        setDeadline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    date = new Date();
                    String year = new SimpleDateFormat("yyyy").format(date);
                    String month = new SimpleDateFormat("MM").format(date);
                    String day = new SimpleDateFormat("dd").format(date);
                    String hour = new SimpleDateFormat("kk").format(date);
                    DatePickerDialog dialog = new DatePickerDialog(GeneralWriteActivity.this, callbackMethod, Integer.valueOf(year), Integer.valueOf(month)-1, Integer.valueOf(day));
                    timedialog = new TimePickerDialog(GeneralWriteActivity.this, tcallbackMethod, Integer.valueOf(hour), 00, false);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date());
                    cal.add(Calendar.DATE, 14);

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
                            if(datestr==null) setDeadline.setChecked(false);
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
                }else{
                    deadlineTextview.setText("");
                }
            }
        });

        write_polls = new ArrayList<>();
        image_uris = new ArrayList<>();
        for(int i=0;i<2;i++){
            write_polls.add("");
            image_uris.add(null);
        }

        pollWriteAdapter = new PollWriteAdapter(GeneralWriteActivity.this);
        pollRecyclerView.setLayoutManager(new LinearLayoutManager(GeneralWriteActivity.this, RecyclerView.VERTICAL, false));
        pollRecyclerView.setAdapter(pollWriteAdapter);

        addPollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_polls.add("");
                image_uris.add(null);
                pollWriteAdapter.notifyItemInserted(write_polls.size()-1);
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
        return super.onTouchEvent(event);
    }


    public void postGeneral(String title, String author, Integer author_lvl, String content,
                         Date date, Date deadline, String author_userid, Boolean multi_response,
                         Boolean with_image, JSONArray polls, JSONArray bitArray) {
        String requestURL = getString(R.string.server)+"/api/generals/parseandroid";
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, requestURL,
                response -> {
                    try {
                        Log.d("response is", ""+new String(response.data));
                        JSONObject resultObj = new JSONObject(new String(response.data));
                        int result = resultObj.getInt("result");
                        if(result==1) {
                            postDone = true;
                        }
                    } catch (Exception e) {
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
                for(int i=0; i<image_uris.size(); i++){
                    if(image_uris.get(i)!=null) {
                        long imagename = System.currentTimeMillis();
                        try {
                            params.put("image" + i, new DataPart(imagename + ".png", getBytes(GeneralWriteActivity.this, image_uris.get(i))));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
                SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss.SSS");
                fm.setTimeZone(TimeZone.getTimeZone("UTC"));
                params.put("date", fm.format(date));
                params.put("deadline", fm.format(deadline));
                params.put("author_userid", author_userid);
                params.put("multi_response", String.valueOf(multi_response));
                params.put("with_image", String.valueOf(with_image));
                params.put("polls", polls.toString());
                params.put("bitarray", bitArray.toString());

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

    public static void goToAlbum(Context ctx, int position){
        image_add_pos = position;
        Matisse.from((Activity) ctx)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG, MimeType.WEBP))
                .theme(R.style.Matisse_White)
                .countable(false)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .maxSelectable(1)
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

    public static void erasePoll(int position){
        image_uris.remove(position);
        write_polls.remove(position);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 13 && resultCode == RESULT_OK){

            ArrayList<Uri> selected_images = (ArrayList<Uri>) Matisse.obtainResult(data);
            image_uris.set(image_add_pos, selected_images.get(0));
            pollWriteAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onBackPressed() {
        Back_survey();
    }

    public void Back_survey(){
        setResult(0);
        String message;
        message = "작성중인 글을 취소하겠습니까";

        CustomDialog customDialog = new CustomDialog(GeneralWriteActivity.this, new View.OnClickListener() {
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
        String title = writeTitle.getText().toString();
        String content = writeContent.getText().toString();

        author = UserPersonalInfo.name;
        author_lvl = UserPersonalInfo.level;
        String author_userid = UserPersonalInfo.userID;
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd a KK시");

        Boolean multi_response = multiResponse.isChecked();
        date = new Date();

        deadline = null;
        if(setDeadline.isChecked()) {
            if (deadlineTextview.getText().length() != 0) {
                try {
                    Log.d("deadline is", deadlineTextview.getText().toString());
                    deadline = fm.parse(deadlineTextview.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.WEEK_OF_MONTH, 2);
                deadline = c.getTime();
            }
        }else {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.WEEK_OF_MONTH, 2);
            deadline = c.getTime();
        }
        SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.date_format));

        JSONArray bitArray = new JSONArray();
        JSONArray new_polls = new JSONArray();
        int is_image = 0;
        int empty = 0;
        for(int i=0;i<image_uris.size();i++){
            JSONObject poll = new JSONObject();
            try {
                poll.put("content", write_polls.get(i));
                if(write_polls.get(i).length()==0){
                    empty = 1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new_polls.put(poll);
            if(image_uris.get(i)!=null){
                is_image = 1;
                bitArray.put(1);
            }else{
                bitArray.put(0);
            }
        }

        Boolean with_image;
        if(is_image==1){
            with_image = true;
        }else{
            with_image=false;
        }

        if (title.getBytes().length <= 0 || content.getBytes().length <= 0){
            CustomDialog customDialog = new CustomDialog(GeneralWriteActivity.this, null);
            customDialog.show();
            customDialog.setMessage("입력되지 않은 정보가 있습니다");
            customDialog.setNegativeButton("확인");
        }
        else if (setDeadline.isChecked() && deadline.before(date)){
            Log.d("de222adlineis", ""+setDeadline.isChecked());
            CustomDialog customDialog = new CustomDialog(GeneralWriteActivity.this, null);
            customDialog.show();
            customDialog.setMessage("날짜를 제대로 입력해주세요");
            customDialog.setNegativeButton("확인");
        }
        else if(empty==1){
            CustomDialog customDialog = new CustomDialog(GeneralWriteActivity.this, null);
            customDialog.show();
            customDialog.setMessage("입력되지 않은 항목이 있습니다");
            customDialog.setNegativeButton("확인");
        }
        else {
            loading.setVisibility(View.VISIBLE);
            new Thread(new Runnable() {
                @Override
                public void run() {

//                        postPost(title, author, author_lvl, content, participants, goalParticipants, url, date, deadline, with_prize, prize, est_time, target, count, new ArrayList<Reply>(), false, image_uris, UserPersonalInfo.userID);
                    postGeneral(title, author, author_lvl, content, date, deadline, author_userid, multi_response, with_image, new_polls, bitArray);
                    while(!(postDone)) {
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {}
                    }
                    getGenerals();
                    while(!(getGeneralsDone)) {
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {}
                    }
                    Message message = handler.obtainMessage();
                    handler.sendMessage(message);
                }
            }).start();
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
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd a KK시");
        deadlineTextview.setText(transFormat.format(deadline));
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
    private void getGenerals(){
        try{
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/generals";
            RequestQueue requestQueue = Volley.newRequestQueue(GeneralWriteActivity.this);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                    (Request.Method.GET, requestURL, null, response -> {
                        try {
                            ArrayList<General> generalArrayList = new ArrayList<>();
                            JSONArray resultArr = new JSONArray(response.toString());
                            Log.d("gettinggeneralresponseis", ""+response);
                            for (int i = 0; i < resultArr.length(); i++) {
                                JSONObject general = resultArr.getJSONObject(i);
                                String id = general.getString("_id");
                                String title = general.getString("title");
                                String author = general.getString("author");
                                Integer author_lvl = general.getInt("author_lvl");
                                String content = general.getString("content");
                                SimpleDateFormat fm = new SimpleDateFormat(getString(R.string.date_format));
                                Date date = null;
                                try {
                                    date = fm.parse(general.getString("date"));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Date deadline = null;
                                try {
                                    deadline = fm.parse(general.getString("deadline"));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                ArrayList<Reply> comments = new ArrayList<>();
                                try{
                                    JSONArray ja = (JSONArray)general.get("comments");
                                    if (ja.length() != 0){
                                        for (int j = 0; j<ja.length(); j++){
                                            JSONObject reply = ja.getJSONObject(j);
                                            String reid = reply.getString("_id");
                                            String writer = reply.getString("writer");
                                            String contetn = reply.getString("content");
                                            Date datereply = null;
                                            try {
                                                datereply = fm.parse(reply.getString("date"));
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            Boolean replyhide = reply.getBoolean("hide");
                                            JSONArray ua = (JSONArray)reply.get("reports");


                                            ArrayList<String> replyreports = new ArrayList<String>();
                                            for (int u = 0; u<ua.length(); u++){
                                                replyreports.add(ua.getString(u));
                                            }
                                            String writer_name = null;
                                            try {
                                                writer_name = reply.getString("writer_name");
                                            }catch (Exception e){
                                                writer_name = null;
                                            }
                                            Reply re = new Reply(reid, writer, contetn, datereply,replyreports,replyhide);
                                            re.setWriter_name(writer_name);
                                            if ((!replyhide )&& (!replyreports.contains(UserPersonalInfo.userID))){
                                                comments.add(re);
                                            }
                                        }
                                    }

                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                                Boolean done = general.getBoolean("done");
                                String author_userid = general.getString("author_userid");
                                JSONArray ka = (JSONArray)general.get("reports");
                                ArrayList<String> reports = new ArrayList<String>();
                                for (int j = 0; j<ka.length(); j++){
                                    reports.add(ka.getString(j));
                                }
                                Boolean multi_response = general.getBoolean("multi_response");
                                Integer participants = general.getInt("participants");
                                JSONArray ia = (JSONArray)general.get("participants_userids");
                                ArrayList<String> participants_userids = new ArrayList<String>();
                                for (int j = 0; j<ia.length(); j++){
                                    participants_userids.add(ia.getString(j));
                                }
                                Boolean with_image = general.getBoolean("with_image");
                                ArrayList<Poll> polls = new ArrayList<>();
                                try{
                                    JSONArray ja = (JSONArray)general.get("polls");
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

                                JSONArray la = (JSONArray)general.get("liked_users");
                                ArrayList<String> liked_users = new ArrayList<String>();
                                for (int j = 0; j<la.length(); j++){
                                    liked_users.add(la.getString(j));
                                }

                                Integer likes = general.getInt("likes");
                                Boolean hide = general.getBoolean("hide");

                                General newGeneral = new General(id, title, author, author_lvl, content,
                                        date, deadline, comments, done, author_userid, reports, multi_response,
                                        participants, participants_userids, with_image, polls, liked_users, likes, hide);
                                if((!newGeneral.getReports().contains(UserPersonalInfo.userID)) && (hide!=true))
                                    generalArrayList.add(newGeneral);
                            }
                            Log.d("gotgenerals123", "sizeis"+MainActivity.generalArrayList.size());
                            MainActivity.generalArrayList = generalArrayList;
                            Log.d("gotgenerals", "sizeis"+MainActivity.generalArrayList.size());
                            getGeneralsDone = true;
                        } catch (JSONException e) {
                            Log.d("exception", "JSON error");
                            e.printStackTrace();
                        }
                        getGeneralsDone = true;
                    }, error -> {
                        getGeneralsDone = true;
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e){
            Log.d("exception", "failed getting response");
            e.printStackTrace();
        }
    }
    private class postHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            loading.setVisibility(View.GONE);
            postDone = false;
            getGeneralsDone = false;
            Intent intent = new Intent(GeneralWriteActivity.this, BoardGeneral.class);
            setResult(NEWPOST, intent);
            finish();
        }
    }
}
