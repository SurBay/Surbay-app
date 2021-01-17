package com.example.surbay;

import android.content.Context;
import android.content.Intent;
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
import androidx.appcompat.app.AppCompatActivity;

public class SurveyWebActivity extends AppCompatActivity {
    private WebView mWebView;
    static final int DONE = 1;
    static final int NOT_DONE = 0;
    private int doneSurvey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey_webview);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        doneSurvey = NOT_DONE;
        mWebView = (WebView) findViewById(R.id.webview);//xml 자바코드 연결
        mWebView.getSettings().setJavaScriptEnabled(true);//자바스크립트 허용
        String url = intent.getStringExtra("url");
        if(!(url.startsWith("https:")||url.startsWith("http:"))){
            url = "https://" + url;
        }
        Log.d("url is ", url);
        mWebView.loadUrl(url);//웹뷰 실행


        WebViewClient mWebViewClient = new WebViewClient(){
            @Override
            public void doUpdateVisitedHistory (WebView view, String url, boolean isReload){
              if(url.contains("/formResponse") && !isReload){
                  Log.d("survey", "survey done");
                  doneSurvey = DONE;
              }
              else if(url.contains("/formResponse") && isReload){
                  Log.d("no survey", "survey done again");
              }
              else{
                  Log.d("haha", "hahahah");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.write_bar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        setResult(doneSurvey);
        finish();
        return true;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//뒤로가기 버튼 이벤트
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {//웹뷰에서 뒤로가기 버튼을 누르면 뒤로가짐
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class WebViewClientClass extends WebViewClient {//페이지 이동
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
