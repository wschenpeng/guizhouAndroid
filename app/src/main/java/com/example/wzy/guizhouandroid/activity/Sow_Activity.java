package com.example.wzy.guizhouandroid.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.wzy.guizhouandroid.R;

public class Sow_Activity extends Activity {

    private View fAM_stable;
    private View add;
    private View demand;
    private View amend;
    private View history;
    private WebView webView;
    private String url="http://120.79.76.116:7777/APP/qq/index.html";


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

    private void goBack(){
        // 若无上级页面，则退出Activity
        if (webView == null ||!webView.canGoBack()){
            finish();
        }else{
            webView.goBack();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sow_activity);


        setWebview();
        findViewById(R.id.back).setOnClickListener(v -> finish());

        fAM_stable = findViewById(R.id.float_stable);

        /**添加*/
        add = findViewById(R.id.add);
        add.setOnClickListener(new MuneOnClickListener());
        /**查询*/
        demand = findViewById(R.id.demand);
        demand.setOnClickListener(new MuneOnClickListener());
//        /**修改*/
//        amend = findViewById(R.id.amend);
//        amend.setOnClickListener(new MuneOnClickListener());
//        /**历史纪录*/
//        history = findViewById(R.id.history);
//        history.setOnClickListener(new MuneOnClickListener());
        /**WebView*/
    }


    @SuppressLint("JavascriptInterface")
    private void setWebview() {
        webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();


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
        webSettings.setAppCacheEnabled(false);
        // 设置缓存模式,一共有四种模式
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 设置缓存路径
//        webSettings.setAppCachePath("");
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
        webView.setWebChromeClient(new WebChromeClient());

        // 该方法解决的问题是打开浏览器不调用系统浏览器，直接用webview打开
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                return true;
            }
        });
        webView.loadUrl("http://120.79.76.116:7777/APP/qq/showPig.html");
//        /*设置支持两指缩放手势*/
//        webSettings.setBuiltInZoomControls(false);
//        /*缩放*/
//        webSettings.setSupportZoom(false);
//        //与js交互必须设置
//        webSettings.setJavaScriptEnabled(true);
//        //调取本地的html
////        webView.loadUrl("file:///android_asset/html/camera.html");
//        //调取web的html
//        webView.loadUrl("http://120.79.76.116:7080/APP/qq/index.html");
////        webView.loadUrl("http://120.79.76.116:7080/index.php?m=content&c=index&a=lists&catid=133");
        webView.addJavascriptInterface(Sow_Activity.this,"android");

    }

    @JavascriptInterface
    public void success(){
        Toast.makeText(getApplicationContext(),"该母猪记录已完成！",Toast.LENGTH_LONG).show();
    }


    @JavascriptInterface
    public void error(){
        Toast.makeText(getApplicationContext(),"该母猪记录失败！",Toast.LENGTH_LONG).show();
    }

    /**
     * 点击事件
     * */
    private class MuneOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.add:
                    Intent i = new Intent(Sow_Activity.this,Add_Activity.class);
                    i.putExtra("Intent_url","http://120.79.76.116:7777/APP/qq/myinput/index1.html");
                    startActivity(i);
                    break;
                case R.id.demand:
                    Intent i2 = new Intent(Sow_Activity.this,Add_Activity.class);
                    i2.putExtra("Intent_url","http://120.79.76.116:7777/APP/qq/historyPage.html");
                    startActivity(i2);
                    break;
//                case R.id.amend:
//                    Intent i3 = new Intent(Sow_Activity.this,Emand_Activity.class);
//                    startActivity(i3);
//                    break;
//                case  R.id.history:
//                    Intent i4 = new Intent(Sow_Activity.this,History_Activity.class);
//                    startActivity(i4);
//                    break;
            }
        }
    }

}
