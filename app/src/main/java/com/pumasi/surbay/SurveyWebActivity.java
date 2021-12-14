package com.pumasi.surbay;

import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Telephony;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.tools.FirebaseLogging;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SurveyWebActivity extends AppCompatActivity {
    private static final String WEBVIEW_INTERFACE_NAME = "survey";
    private WebView mWebView;
    static final int DONE = 1;
    int result = 0;
    int requestCode;
    private CustomDialog customDialog;
    private String post_id;
    private boolean isSuccessful = false;
    private ParticipateHandler handler;
    private class WebViewInterface
    {
        Context mContext;

        WebViewInterface(Context context)
        {
            mContext = context;
        }
        @JavascriptInterface
        public void googleFormSubmitted()
        {
            // Do what you need
            if(requestCode!=2) {
                Log.d("survey", "survey done");
                CustomDialog customDialog2 = new CustomDialog(SurveyWebActivity.this);
                result = DONE;
                customDialog2.customSimpleDialog("응답 완료", "확인", "", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog2.customDialog.dismiss();
                        ParticipateThread thread = new ParticipateThread();
                        thread.start();
                    }
                });
//                customDialog = new CustomDialog(SurveyWebActivity.this, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
////                        new ParticipateThread().start();
//                        customDialog.dismiss();
//                    }
//                });
//                customDialog.show();
//                customDialog.setMessage("응답 완료");
//                customDialog.setPositiveButton("확인");
//                customDialog.hideNegativeButton(true);
//                customDialog.setCanceledOnTouchOutside(false);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey_webview);


        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기

        customDialog = new CustomDialog(SurveyWebActivity.this);
        Intent intent = getIntent();

        requestCode= intent.getIntExtra("requestCode", -1);
        mWebView = (WebView) findViewById(R.id.webview);//xml 자바코드 연결
        mWebView.getSettings().setJavaScriptEnabled(true);//자바스크립트 허용
        String url = intent.getStringExtra("url");
        post_id = intent.getStringExtra("post_id");
        if(!(url.startsWith("https:")||url.startsWith("http:"))){
            url = "https://" + url;
        }
        Log.d("url is ", url);

        mWebView.loadUrl(url);//웹뷰 실행

        mWebView.getSettings().setSupportMultipleWindows(true);
        WebViewClient mWebViewClient = new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url)
            {
                Log.d("hereonpagefinished", "url is "+url);
                String js =
                        "( " +
                                "function() { " +
                                "if(document.getElementsByClassName('freebirdFormviewerViewResponseConfirmContentContainer').length > 0) {" +
                                WEBVIEW_INTERFACE_NAME + ".googleFormSubmitted();" +
                                "}" +
                                "}) " +
                                "()";

                if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT)
                    mWebView.evaluateJavascript(js, null);
                else
                    mWebView.loadUrl("javascript:" + js);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                Log.d("url is ", url);
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    Log.d("here", "url is " + url);
                    webView.loadUrl(url);
                    return false;
                } else {
                    if (url.startsWith("intent://")) {
                        try {
                            Context context = webView.getContext();
                            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                            if (intent != null) {
                                PackageManager packageManager = context.getPackageManager();
                                ResolveInfo info = packageManager.resolveActivity(intent,
                                        PackageManager.MATCH_DEFAULT_ONLY);
                                // This IF statement can be omitted if you are not strict about
                                // opening the Google form url in WebView & can be opened in an
                                // External Browser
                                if ((intent != null) && ((intent.getScheme().equals("https"))
                                        || (intent.getScheme().equals("http")))) {
                                    String fallbackUrl = intent.getStringExtra(
                                            "browser_fallback_url");
                                    webView.loadUrl(fallbackUrl);
                                    return true;
                                }
                                if (info != null) {
                                    context.startActivity(intent);
                                } else {
                                    // Call external broswer
                                    String fallbackUrl = intent.getStringExtra(
                                            "browser_fallback_url");
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                            Uri.parse(fallbackUrl));
                                    context.startActivity(browserIntent);
                                }
                                return true;
                            } else {
                                return false;
                            }
                        } catch (Exception e) {
                            return false;
                        }
                    }
                    else {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        webView.getContext().startActivity(intent);
                        return true;
                    }
                }
            }
        };
        mWebView.setWebViewClient(mWebViewClient);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            mWebView.addJavascriptInterface(new WebViewInterface(SurveyWebActivity.this), WEBVIEW_INTERFACE_NAME);

            mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, android.os.Message resultMsg)
            {
                WebView.HitTestResult result = view.getHitTestResult();
                String data = result.getExtra();
                Context context = view.getContext();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
                context.startActivity(browserIntent);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: //toolbar의 back키 눌렀을 때 동작
                if (requestCode==-1) {

                    customDialog = new CustomDialog(SurveyWebActivity.this, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setResult(result);
                            finish();

                            customDialog.dismiss();
                        }
                    });
                    customDialog.show();
                    customDialog.setMessage("응답을 취소하겠습니까?");
                    customDialog.setPositiveButton("확인");
                    customDialog.setNegativeButton("취소");
                }else{
                    finish();
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {//뒤로가기 버튼 이벤트
        if(requestCode==-1) {
            customDialog = new CustomDialog(SurveyWebActivity.this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(result);
                    finish();

                    customDialog.dismiss();
                }
            });
            customDialog.show();
            customDialog.setMessage("응답을 취소하겠습니까?");
            customDialog.setPositiveButton("확인");
            customDialog.setNegativeButton("취소");
        }else{
            finish();
        }
    }
    public class ParticipateThread extends Thread {
        @Override
        public void run() {
            int counter = 0;
            updateUserParticipation(post_id);
            while (!isSuccessful && counter != 20) {
                try {
                    Thread.sleep(100);
                    counter ++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                if (isSuccessful) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new FirebaseLogging(getApplicationContext()).LogScreen("research_done", "설문조사_완료");
                            setResult(DONE);
                            finish();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("hello2", "run: ??");
                            customDialog.customSimpleDialog("설문조사 제출을 실패하셨습니다.", "재시도", "취소", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new ParticipateThread().start();
                                }
                            });
                        }
                    });
                }
            } catch (Exception e) {
            }
        }
    }
    public class ParticipateHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
//            if (msg.what == 0) {
//                new CustomDialog(SurveyWebActivity.this).customSimpleDialog("오류가 발생하였습니다\n 재시도 하시겠습니까?", "재시도", "취소", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        new ParticipateThread().start();
//                    }
//                });
//            } else if (msg.what == 1) {
//                setResult(DONE);
//                finish();
//            }
        }
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
                        try {
                            if (response.getString("message").equals("participations updated")) isSuccessful = true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed posting");
            e.printStackTrace();
        }
    }

}
