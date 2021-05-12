package com.pumasi.surbay;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.pumasi.surbay.classfile.CustomDialog;

public class SurveyWebActivity extends AppCompatActivity {
    private static final String WEBVIEW_INTERFACE_NAME = "survey";
    private AlertDialog dialog;
    private WebView mWebView;
    static final int DONE = 1;
    static final int NOT_DONE = 0;
    int result = 0;
    int requestCode;
    private CustomDialog customDialog;
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
                result = DONE;
                customDialog = new CustomDialog(SurveyWebActivity.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setResult(DONE);
                        finish();

                        customDialog.dismiss();
                    }
                });
                customDialog.show();
                customDialog.setMessage("응답 완료");
                customDialog.setPositiveButton("확인");
                customDialog.hideNegativeButton(true);
                customDialog.setCanceledOnTouchOutside(false);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey_webview);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기

        Intent intent = getIntent();

        requestCode= intent.getIntExtra("requestCode", -1);
        mWebView = (WebView) findViewById(R.id.webview);//xml 자바코드 연결
        mWebView.getSettings().setJavaScriptEnabled(true);//자바스크립트 허용
        String url = intent.getStringExtra("url");
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

    private class WebViewClientClass extends WebViewClient {//페이지 이동
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
