package com.example.surbay;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WriteActivity extends AppCompatActivity {
    static final int NEWPOST = 1;
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

    private DatePickerDialog.OnDateSetListener callbackMethod;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        writeTitle = findViewById(R.id.write_title);
        writeTarget = findViewById(R.id.write_target);
        writeDeadline = findViewById(R.id.write_deadline);
        writeGoalParticipants = findViewById(R.id.write_goal_participants);
        withPrize = findViewById(R.id.with_prize);
        writePrize = findViewById(R.id.write_prize);
        writeUrl = findViewById(R.id.write_url);
        writeEstTime = findViewById(R.id.write_est_time);
        writeContent = findViewById(R.id.write_content);

        this.InitializeListener();
        if(writeTitle.getText().length()!=0){writeTitle.setTextColor(Color.parseColor("#000000"));}
        if(writeTarget.getText().length()!=0){writeTarget.setTextColor(Color.parseColor("#000000"));}
        if(writeDeadline.getText().length()!=0){writeDeadline.setTextColor(Color.parseColor("#000000"));}
        if(writePrize.getText().length()!=0){writePrize.setTextColor(Color.parseColor("#000000"));}
        if(writeUrl.getText().length()!=0){writeUrl.setTextColor(Color.parseColor("#000000"));}
        if(writeEstTime.getText().length()!=0){writeEstTime.setTextColor(Color.parseColor("#000000"));}
        if(writeContent.getText().length()!=0){writeContent.setTextColor(Color.parseColor("#000000"));}
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
                AlertDialog.Builder builder = new AlertDialog.Builder(WriteActivity.this);
                    dialog = builder.setMessage("게시글 작성을 취소하시겠습니까")
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
                String title = writeTitle.getText().toString();
                String author = UserPersonalInfo.name;
                Integer author_lvl = UserPersonalInfo.level;
                String content = writeContent.getText().toString();
                Integer participants = 0;
                Integer goalParticipants;

                if(writeGoalParticipants.getText()!=null) {
                    goalParticipants = Integer.valueOf(writeGoalParticipants.getText().toString());
                } else{goalParticipants = null;}
                String url = writeUrl.getText().toString();

                SimpleDateFormat fm = new SimpleDateFormat("yyyy년MM월dd일");
                Date date = new Date();

                Date deadline = null;

                try {
                    Log.d("deadline is", writeDeadline.getText().toString());
                    deadline = fm.parse(writeDeadline.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Boolean with_prize = withPrize.isChecked();
                String prize = writePrize.getText().toString();
                Integer est_time = Integer.valueOf(writeEstTime.getText().toString());
                String target = writeTarget.getText().toString();

                SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.date_format));

                Intent intent = new Intent(WriteActivity.this, BoardFragment1.class);
                Log.d("date formatted", formatter.format(deadline));
                intent.putExtra("title", title);
                intent.putExtra("author", author);
                intent.putExtra("author_lvl", author_lvl);
                intent.putExtra("participants", participants);
                intent.putExtra("goal_participants", goalParticipants);
                intent.putExtra("url", url);
                intent.putExtra("date", formatter.format(date));
                intent.putExtra("deadline", formatter.format(deadline));
                intent.putExtra("content", content);
                intent.putExtra("with_prize", with_prize);
                if(with_prize) {
                    intent.putExtra("prize", prize);
                }

                intent.putExtra("est_time", est_time);
                intent.putExtra("target", target);
                setResult(NEWPOST, intent);
                try {
                    postPost(title, author, author_lvl, content, participants, goalParticipants, url, date, deadline, with_prize, prize, est_time, target);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
                return true;
            default:
                setResult(0);
                return true;
        }
    }



    public void postPost(String title, String author, Integer author_lvl, String content, Integer participants, Integer goal_participants, String url, Date date, Date deadline, Boolean with_prize, String prize, Integer est_time, String target) throws Exception{
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
            }
            params.put("est_time", est_time);
            params.put("target", target);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, requestURL, params, response -> {
                        Log.d("response is", ""+response);
                        try {
                            JSONObject resultObj = new JSONObject(response.toString());
                            String id = resultObj.getString("id");
                            Post item = new Post(id, title, author, author_lvl, content, participants, goal_participants, url, date, deadline, with_prize, prize, est_time, target);
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

}
