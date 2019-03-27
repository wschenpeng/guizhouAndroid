package com.example.wzy.guizhouandroid.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.wzy.guizhouandroid.R;
import com.example.wzy.guizhouandroid.server.DownLoadService;
import com.tbruyelle.rxpermissions.RxPermissions;




public class Myself_Set_Update_Activity extends Activity {
    //  进度条
    private ProgressBar mProgressBar;
    //  对话框
    private Dialog mDownloadDialog;
    //  判断是否停止
    private boolean mIsCancel = false;
    //  进度
    private int mProgress;
    //  文件保存路径
    private String mSavePath;
    //  版本名称
    private String mVersion_name ;
    //  请求链接
    private String url_update = "http://120.79.76.116:7777/APP/Update_Apk/1.1.0.190307_Release_GzPig_IoT.apk";
    private String mVrsion_Name_Server;


    private String url ;

    private WebView webview;
    private String file_url;
    private Context context;

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
        setContentView(R.layout.myself_set_update_activity);

        Intent i = getIntent();
        url = i.getStringExtra("Intent_url");
        mVrsion_Name_Server = i.getStringExtra("version");

        file_url = i.getStringExtra("Intent_file_url");

        InitView();


    }


    private void InitView() {
        /*更新按钮监听*/
        findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mIsCancel = false;
                showDownloadDialog();

            }
            /*提示更新弹窗*/
            private void showDownloadDialog() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Myself_Set_Update_Activity.this);
//                builder.setTitle("是否进行更新");
                builder.setMessage("版本更新啦！");
                View view = LayoutInflater.from(Myself_Set_Update_Activity.this).inflate(R.layout.dialog_progress, null);
                mProgressBar = (ProgressBar) view.findViewById(R.id.id_progress);
                builder.setView(view);

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 隐藏当前对话框
                        dialog.dismiss();
                        // 设置下载状态为取消
                        mIsCancel = true;
                    }
                });
                builder.setPositiveButton("升级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 下载文件
                        Intent service = new Intent(Myself_Set_Update_Activity.this, DownLoadService.class);
                        service.putExtra("downloadurl", url_update);
                        RxPermissions.getInstance(Myself_Set_Update_Activity.this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(granted ->{
                            if (granted) {
                                Toast.makeText(Myself_Set_Update_Activity.this,"正在下载中",Toast.LENGTH_SHORT).show();
                                startService(service);
                            } else {
                                Toast.makeText(Myself_Set_Update_Activity.this,"SD卡下载权限被拒绝",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                mDownloadDialog = builder.create();
                mDownloadDialog.show();


            }
        });

        //返回监听
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        webview = (WebView) findViewById(R.id.wb);



        final WebSettings webSettings = webview.getSettings();
        Log.d("hello","测试2:");

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
        // 默认的是NARROW_COLUMNS
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // 设置默认字体大小
        webSettings.setDefaultFontSize(12);
        // 支持缩放
        webSettings.setSupportZoom(true);
        //设置支持两指缩放手势
        webSettings.setBuiltInZoomControls(true);

        webview.setWebChromeClient(new WebChromeClient());//限制在webview中打开网页，不用默认浏览器

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
}
