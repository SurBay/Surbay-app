package com.example.surbay;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();

        EditText nameEditText = findViewById(R.id.name);
        EditText emailEditText = findViewById(R.id.email);
        EditText usernameEditText = findViewById(R.id.username);
        EditText passwordEditText = findViewById(R.id.password);

        Button signupButton = findViewById(R.id.sign_up_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if(name.length()==0){Toast.makeText(getApplicationContext(), "이름을 입력해주세요", Toast.LENGTH_SHORT).show();}
                else if(email.length()==0){Toast.makeText(getApplicationContext(), "이메일을 입력해주세요", Toast.LENGTH_SHORT).show();}
                else if(username.length()==0){Toast.makeText(getApplicationContext(), "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();}
                else if(password.length()==0){Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();}
                else{
                    try {
                        makeSignupRequest(name, email, username, password);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void makeSignupRequest(String name, String email, String username, String password) throws Exception{
        try{
            String requestURL = "https://surbay-server.herokuapp.com/signup";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("name", name);
            params.put("email", email);
            params.put("userID", username);
            params.put("userPassword", password);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, requestURL, params, response -> {
                        Log.d("response is", ""+response);
                        try {
                            JSONObject resultObj = new JSONObject(response.toString());
                            Boolean success = resultObj.getBoolean("type");
                            AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                            AlertDialog dialog;
                            if(success) {
                                dialog = builder.setMessage("회원가입이 완료되었습니다")
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .create();
                                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @SuppressLint("ResourceAsColor")
                                    @Override
                                    public void onShow(DialogInterface arg0) {
                                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.black);
                                    }
                                });
                                dialog.show();
                            }else {
                                dialog = builder.setMessage("중복된 아이디입니다.")
                                        .setNegativeButton("다시시도", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
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
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
