package com.pumasi.surbay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.pumasi.surbay.classfile.UserPersonalInfo;

import org.json.JSONException;

public class SurveyWebActivity extends AppCompatActivity {
    private AlertDialog dialog;
    private WebView mWebView;
    static final int DONE = 1;
    static final int NOT_DONE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey_webview);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기

        Intent intent = getIntent();
        mWebView = (WebView) findViewById(R.id.webview);//xml 자바코드 연결
        mWebView.getSettings().setJavaScriptEnabled(true);//자바스크립트 허용
        String url = intent.getStringExtra("url");
        if(!(url.startsWith("https:")||url.startsWith("http:"))){
            url = "https://" + url;
        }
        Log.d("url is ", url);
        mWebView.loadUrl(url);//웹뷰 실행


        WebViewClient mWebViewClient = new WebViewClient(){
//            @Override
//            public void doUpdateVisitedHistory (WebView view, String url, boolean isReload){
//              if(url.contains("/formResponse") && !isReload){
//                  Log.d("survey", "survey done");
//                  doneSurvey = DONE;
//              }
//              else if(url.contains("/formResponse") && isReload){
//                  Log.d("no survey", "survey done again");
//              }
//              else{
//                  Log.d("haha", "hahahah");
//              }
//            }

            @Override
            public void onPageFinished(WebView view, String url){
                if(url.contains("/formResponse")){
                    Log.d("survey", "survey done");

                    AlertDialog.Builder builder = new AlertDialog.Builder(SurveyWebActivity.this);
                    dialog = builder.setMessage("응답 완료")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setResult(DONE);
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
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                Log.d("url is ", url);
                if (url.startsWith("http:") || url.startsWith("https:")) {
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                AlertDialog.Builder builder = new AlertDialog.Builder(SurveyWebActivity.this);
                dialog = builder.setMessage("응답을 취소하겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setResult(NOT_DONE);
                                finish();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
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
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {//뒤로가기 버튼 이벤트
        AlertDialog.Builder builder = new AlertDialog.Builder(SurveyWebActivity.this);
        dialog = builder.setMessage("응답을 취소하겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(NOT_DONE);
                        finish();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
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

    private class WebViewClientClass extends WebViewClient {//페이지 이동
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
