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
import org.jsoup.nodes.Document;


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
import org.jsoup.Jsoup;

import java.util.HashMap;
import java.util.Map;

// "requestCode" parameter info
// 0. Just show the web view (DEFAULT)
// 1. Google Form --> if survey's done, update survey participants status.
// 2. Naver Form --> if survey's done, update survey participants status.
// you can add more...

public class SurveyWebActivity extends AppCompatActivity {
    private static final String WEBVIEW_INTERFACE_NAME = "survey";
    private WebView mWebView;
    public static final int DEFAULT = 0;
    public static final int GOOGLE_FORM = 1;
    public static final int NAVER_FORM = 2;
    static final int DONE = 1;
    int result = 0;
    int requestCode;
    private CustomDialog customDialog;
    private String post_id;
    private boolean isSuccessful = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey_webview);


        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기

        customDialog = new CustomDialog(SurveyWebActivity.this);
        Intent intent = getIntent();

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setSupportMultipleWindows(true);


        String url = intent.getStringExtra("url");
        post_id = intent.getStringExtra("post_id");
        boolean auto = intent.getBooleanExtra("auto", false);

        if (auto) {
            if (url.contains("docs.google.com") || url.contains("forms.gle")) {
                requestCode = SurveyWebActivity.GOOGLE_FORM;
            } else if (url.contains("form.office") || url.contains("http://naver.me")) {
                requestCode = SurveyWebActivity.NAVER_FORM;
            } else {
                requestCode = SurveyWebActivity.DEFAULT;
            }
        } else {
            requestCode = intent.getIntExtra("requestCode", DEFAULT);
        }



        if(!(url.startsWith("https:")||url.startsWith("http:"))){
            url = "https://" + url;
        }

        mWebView.loadUrl(url);//웹뷰 실행

        WebViewClient mWebViewClient = new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url)
            {
                switch (requestCode) {
                    case DEFAULT:
                        return;
                    case GOOGLE_FORM:
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
                    case NAVER_FORM:
                        mWebView.loadUrl("javascript:window." + WEBVIEW_INTERFACE_NAME + ".naverFormSubmitted" +
                                "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                        return;
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                switch (requestCode) {
                    case DEFAULT:
                        if (url.startsWith("intent://")) {
                            try {
                                Context context = webView.getContext();
                                Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                                if (intent != null) {
                                    PackageManager packageManager = context.getPackageManager();
                                    ResolveInfo info = packageManager.resolveActivity(intent,
                                            PackageManager.MATCH_DEFAULT_ONLY);
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
                        return false;
                    case GOOGLE_FORM:
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
                    case NAVER_FORM:

                        return false;
                }
            return false;
            }
        };
        mWebView.setWebViewClient(mWebViewClient);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            mWebView.addJavascriptInterface(new WebViewInterface(), WEBVIEW_INTERFACE_NAME);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (requestCode == DEFAULT) {
                    finish();
                }else{
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
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if (requestCode == DEFAULT) {
            customDialog.customSimpleDialog("확인을 중단하시겠습니까?", "확인 중단", "아니오", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            customDialog.customSimpleDialog("응답을 취소하시겠습니까?", "예", "아니오", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(result);
                    finish();
                }
            });
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

    public void updateUserParticipation(String id){
        String token = UserPersonalInfo.token;
        Log.d("token", token);
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
                            Log.d("tag", response.getString("message"));
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
    private class WebViewInterface
    {
        @JavascriptInterface
        public void googleFormSubmitted() {
            if(requestCode == GOOGLE_FORM) {
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
            }
        }
        @JavascriptInterface
        public void naverFormSubmitted(String html) {
            if (requestCode == NAVER_FORM) {
                Document doc = Jsoup.parse(html);
                String indicator = doc.select("div.finishMessage").last().text();
                Log.d("indicator", "naverFormSubmitted: " + indicator);
                Log.d("indicator", String.valueOf(indicator.length()));
                if (indicator.length() > 0) {
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
                }
            }
        }
    }

}
