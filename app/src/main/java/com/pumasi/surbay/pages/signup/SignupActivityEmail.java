package com.pumasi.surbay.pages.signup;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.DomainSearchDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SignupActivityEmail extends AppCompatActivity {

    TextView university_edittext;
    ImageButton university_search;
    EditText userid_edittext;
    TextView domain_textview;
    Button signup_next;
    RelativeLayout signup_layout;
    TextView check_id;

    public static JSONObject domains;
    public static String search_result_university = null;


    Boolean dup_check = false;
    private RelativeLayout loading;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_email_layout);
        getSupportActionBar().hide();
        getDomains();


        university_edittext = findViewById(R.id.signup_university);
        university_search = findViewById(R.id.sign_up_university_search);
        userid_edittext = findViewById(R.id.signup_userid);
        domain_textview = findViewById(R.id.signup_domain);
        signup_next = findViewById(R.id.signup_email_next);
        loading = findViewById(R.id.loadingPanel);
        loading.setVisibility(View.GONE);
        signup_layout = findViewById(R.id.rl_sign_up_university_search);
        check_id = findViewById(R.id.signup_check_id);

        signup_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchDialog();
            }
        });
        university_edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchDialog();
            }
        });
        university_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchDialog();
            }
        });

        userid_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(domain_textview.getText().toString().length()!=0) {
                    check_id.setVisibility(View.VISIBLE);
                    check_id.setText("중복 확인 중입니다.");
                    check_id.setTextColor(getResources().getColor(R.color.red));
                    try {
                        idCheck();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        signup_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userid = userid_edittext.getText().toString() + "@" + domain_textview.getText().toString();
                if (userid.length() == 0 || dup_check==false) {
                    CustomDialog customDialog = new CustomDialog(SignupActivityEmail.this, null);
                    customDialog.show();
                    customDialog.setMessage("아이디를 확인해주세요");
                    customDialog.setNegativeButton("확인");
                    return;
                }
                Intent intent = new Intent(SignupActivityEmail.this, SignupActivityPassword.class);

                intent.putExtra("userid", userid);
                startActivity(intent);

            }
        });




    }

    private void SearchDialog()
    {
        DomainSearchDialog domainSearchDialog = new DomainSearchDialog(SignupActivityEmail.this);
        domainSearchDialog.setCanceledOnTouchOutside(true);
        domainSearchDialog.setCancelable(true);
        domainSearchDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        domainSearchDialog.show();
        getFragmentManager().executePendingTransactions();
        domainSearchDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(search_result_university!=null){
                    university_edittext.setText(search_result_university);
                    try {
                        JSONArray university_domains = domains.getJSONArray(search_result_university);
                        ArrayList<String> domains_list= new ArrayList<>();
                        for(int i=0;i<university_domains.length();i++){
                            domains_list.add(university_domains.getString(i));
                        }
                        domain_textview.setText(domains_list.get(0));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getDomains(){
        try{
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/domains";
            RequestQueue requestQueue = Volley.newRequestQueue(SignupActivityEmail.this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, requestURL, null, response -> {
                        try {
                            JSONObject res = new JSONObject(response.toString());
                            domains  = res.getJSONObject("domainMap");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed getting response");
            e.printStackTrace();
        }
    }


    private void idCheck(){
        if (userid_edittext.getText().toString().length() == 0) {
            check_id.setVisibility(View.GONE);
        } else {
            try {
                String userid = userid_edittext.getText().toString() + "@" + domain_textview.getText().toString();
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                String requestURL = getString(R.string.server)+"/userids/duplicate?userID="+ userid;
                JSONObject params = new JSONObject();
                params.put("userID", userid);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, requestURL, params, response -> {
                            Log.d("response is", ""+response);
                            try {
                                JSONObject idObj = new JSONObject(response.toString());
                                Boolean success = idObj.getBoolean("type");
                                if (success){
                                    check_id.setText("사용 가능한 아이디입니다.");
                                    dup_check = true;
                                    check_id.setTextColor(getResources().getColor(R.color.blue));
                                } else {
                                    check_id.setText("이미 가입된 이메일입니다.");
                                    dup_check = false;
                                    check_id.setTextColor(getResources().getColor(R.color.red));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, error -> {
                            Log.d("exception", "volley error");
                            error.printStackTrace();
                        });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(jsonObjectRequest);
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(getCurrentFocus()!=null)imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
}