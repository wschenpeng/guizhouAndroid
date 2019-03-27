package com.example.wzy.guizhouandroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.wzy.guizhouandroid.R;

public class Main_WebView_Activity extends Activity {

    private String url ;

    private WebView webview;
    private String file_url;
    private int screenDensity;

    /*
     *网页回退
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void goBack() {
        // 若无上级页面，则退出Activity
        if (webview == null ||!webview.canGoBack()){
            finish();
        }else{
            webview.goBack();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_web_view_activity);
        InitView();

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        webview = (WebView) findViewById(R.id.webview);

        final WebSettings webSettings = webview.getSettings();

        /**代表屏幕缩放60%
         * 比如你要加载的网页中有图片的宽度是 500px ，如果你的手机分辨率（屏幕宽度） 是1000的话，那么整个图片只占一半的屏幕。 其他的字体都是按照标准展示
         如果这个时候如果你设置 webview.setInitialScale(200) ，代表放大一倍，真个网页都会放大一倍，这个时候图片正好展示整个屏幕宽度，另外网页的字体也会放大一倍
         如果这个时候如果你设置 webview.setInitialScale(50) ，代表缩小一倍，这个时候图片正好展示只占屏幕的四分之一，另外网页的字体也会同时变小
         */
        webview.setInitialScale(60);


        webSettings.setDomStorageEnabled(true);// 主要是这句
        webSettings.setJavaScriptEnabled(true);// 启用js
        webSettings.setBlockNetworkImage(false);// 解决图片不显示

        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
//        webSettings.getSettings().setBuiltInZoomControls();//设置是否支持缩放
//        webSettings.addJavascriptInterface(obj,str);//向html页面注入java对象
//        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
//        webSettings.setLoadWithOverviewMode(true);// 页面支持缩放：
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setBuiltInZoomControls(true);
//        webUrl.requestFocusFromTouch(); //如果webView中需要用户手动输入用户名、密码或其他，则webview必须设置支持获取手势焦点。
//        webSettings.setJavaScriptEnabled(true);  //支持js
//        webSettings.setUseWideViewPort(false);  //将图片调整到适合webview的大小
//        webSettings.setSupportZoom(true);  //支持缩放    webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
//        webSettings.supportMultipleWindows();  //多窗口
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //关闭webview中缓存
//        webSettings.setAllowFileAccess(true);  //设置可以访问文件
//        webSettings.setNeedInitialFocus(true); //当webview调用requestFocus时为webview设置节点
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
//        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
//        webSettings.setLoadsImagesAutomatically(true);  //支持自动加载图片

        // 让JavaScript可以自动打开windows
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 设置缓存
        webSettings.setAppCacheEnabled(true);
        // 设置缓存模式,一共有四种模式
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 设置缓存路径
        webSettings.setAppCachePath("");
        // 支持缩放(适配到当前屏幕)
        webSettings.setSupportZoom(true);
        // 将图片调整到合适的大小
        webSettings.setUseWideViewPort(true);
        // 支持内容重新布局,一共有四种方式
        // 默认的是NARROW_COLUMNS,SINGLE_COLUMN
        //LayoutAlgorithm.NARROW_COLUMNS ： 适应内容大小
        //LayoutAlgorithm.SINGLE_COLUMN:适应屏幕，内容将自动缩放

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // 设置默认字体大小
        webSettings.setDefaultFontSize(6);
        // 支持缩放
        webSettings.setSupportZoom(false);
        //设置支持两指缩放手势
        webSettings.setBuiltInZoomControls(false);

        /**
         * 限制在webview中打开网页，不用默认浏览器
         * */
        webview.setWebChromeClient(new WebChromeClient());

        // 该方法解决的问题是打开浏览器不调用系统浏览器，直接用webview打开
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                return true;
            }
        });
        webview.loadUrl(url);

    }

    private void InitView() {

        Intent i = getIntent();
        url = i.getStringExtra("Intent_url");
        file_url = i.getStringExtra("Intent_file_url");
    }
}

