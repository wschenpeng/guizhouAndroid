package com.example.wzy.guizhouandroid.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wzy.guizhouandroid.R;
import com.example.wzy.guizhouandroid.jna.HCNetSDKJNAInstance;
import com.example.wzy.guizhouandroid.model.PlaySurfaceView;
import com.example.wzy.guizhouandroid.server.FinalConstant;
import com.example.wzy.guizhouandroid.server.HttpReqService;
import com.example.wzy.guizhouandroid.server.SubmitService;
import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.PTZCommand;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author:     wuzhuoyu
 * @date:       2018-11-19
 * @activity：   监控播放页面
 * @version:    1.0.0
 * @project:    生猪养殖物联网系统
 * @description: 集成海康威视硬盘录像机视频，提供功能：云台控制，抓图，录像
 */

/**
 * @author:     wuzhuoyu
 * @date:       2018-11-26
 * @activity：   监控播放页面
 * @version:    1.0.0
 * @project:    生猪养殖物联网系统
 * @description: 做调整，将登录界面删除，所以硬盘录像机相关参数都从服务器获取，用户使用不用输入也可以使用
 * 并修改云台控制显示的方式，以及通道号列表的显示位置！
 */

/**
 * @author:     wuzhuoyu
 * @date:       2018-11-29
 * @activity：   监控播放页面
 * @version:    1.0.0
 * @project:    生猪养殖物联网系统
 * @description:  云台控制界面的编写，点击上下左右时候通过切换底部图片，实现界面渐变色变化
 */

/**
 * @author:     wuzhuoyu
 * @date:       2018-12-15
 * @activity：   监控播放页面
 * @version:    1.0.0
 * @project:    生猪养殖物联网系统
 * @description:  更新视频源名称，符合用户需求
 */



public class Main_Iot_Video_Activity extends Activity implements SurfaceHolder.Callback {

    private TextView tv_content;
    /*设备信息*/
    private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;
    /*登录参数*/
    private String strIP;
    private int intPort;
    private String strName;
    private String strPsw;
    /*登陆返回参数*/
    private int m_iLogID =-1;
    /*surface view 控件*/
    private SurfaceView m_osurfaceView = null;
    /*NET_DVR_RealPlay_V40 返回值*/
    private int m_iPlayID = -1;
    /*按时间回放录像文件 NET_DVR_PlayBackByTime 返回值*/
    private int m_iPlaybackID = -1;
    private PlaySurfaceView[] playView = new PlaySurfaceView[5];
    /*设备开始通道*/
    private int m_iStartChan;
    private int m_vChan = 33;
    /*设备通道个数*/
    private int m_iChanNum;

    /*加载云台控制界面*/
    private View include_ptz;
    /*云台控制按钮*/
    private ImageView iv_video_ptz;
    /*云台控制按钮 */
    private TextView btn_to_up;
    private TextView btn_to_down;
    private TextView btn_to_left;
    private TextView btn_to_right;
    private TextView btn_to_zoomin;
    private TextView btn_to_zoomout;
    /*云台控制图片显示*/
    private ImageView ptz_close;
    private ImageView ptz;
    private ImageView zoom_out;
    private ImageView zoom_in;
    /*抓图按钮*/
    private ImageView iv_video_photo;
    /*抓图保存地址*/
    String path = "/mnt/sdcard/Work/Photo/";
    /*移动侦测按钮*/
    private ImageView iv_video_motion_detection;

    /*监控录像按钮*/
    private ImageView iv_video_record;
    /*录像保存路径*/
    String path_video = "/mnt/sdcard/Work/";
    /*录像提示文本*/
    private TextView tv_video_record;
    /*服务器命令参数*/
    private String cmd;
    private HashMap<String, Object> reqparams;
    /*线程初始*/
    private boolean nThread = false;

    private AlertDialog dialog;
    private ImageView icon_play;
    private WebView webview;
    private String url ="http://120.79.76.116:7777/index.php?m=content&c=index&a=lists&catid=48";
    private View include_fengji;
    private ImageView iv_fengji;
    private ImageView fengji_close;
    private int num;


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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_iot_video_activity);

        InitSubmit();
        InitWebView();
//        Intent i = getIntent();
//        m_iLogID = i.getIntExtra("iLogID", m_iLogID);
//        m_iStartChan = i.getIntExtra("m_iStartChan", m_iStartChan);
//        m_iChanNum = i.getIntExtra("m_iChanNum", m_iChanNum);


        //新建一个File，传入文件夹目录
        File file = new File(path);
        //判断文件夹是否存在，如果不存在就创建，否则不创建
        if (!file.exists()) {
            //通过file的mkdirs()方法创建目录中包含却不存在的文件夹
            file.mkdirs();
        }


        if (!initeSdk()) {
            this.finish();
            return;
        }

        if (!initeActivity()) {
            this.finish();
            return;
        }


    }

    @SuppressLint("JavascriptInterface")
    private void InitWebView() {
        webview = (WebView) findViewById(R.id.webview);



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
        webSettings.setUseWideViewPort(true);  //将图片调整到适合webview的大小
//        webSettings.setSupportZoom(true);  //支持缩放    webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
//        webSettings.supportMultipleWindows();  //多窗口
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //关闭webview中缓存
//        webSettings.setAllowFileAccess(true);  //设置可以访问文件
//        webSettings.setNeedInitialFocus(true); //当webview调用requestFocus时为webview设置节点
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
//        webSettings.setLoadsImagesAutomatically(true);  //支持自动加载图片

        // 让JavaScript可以自动打开windows
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 设置缓存
        webSettings.setAppCacheEnabled(false);
        // 设置缓存模式,一共有四种模式
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 设置缓存路径
        webSettings.setAppCachePath("");
        // 支持缩放(适配到当前屏幕)
        // 将图片调整到合适的大小
        webSettings.setUseWideViewPort(true);
        // 支持内容重新布局,一共有四种方式
        // 默认的是NARROW_COLUMNS
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // 设置默认字体大小
        webSettings.setDefaultFontSize(12);
        // 支持缩放
        webSettings.setSupportZoom(false);
        //设置支持两指缩放手势
        webSettings.setBuiltInZoomControls(false);

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
        webview.addJavascriptInterface(Main_Iot_Video_Activity.this,"android");
    }

    private void InitSubmit() {
        cmd = FinalConstant.FIND_NVR_SET_REQUEST_SERVER;
        reqparams = new HashMap<String, Object>();
        reqparams.put("cmd", cmd);
        nThread = true;
        new Thread(query).start();
    }

    private String serverIP = "120.79.76.116:7777";
    private Runnable query = new Runnable() {
        @Override
        public void run() {
            SubmitService serviceip;
            serviceip = new SubmitService(getApplicationContext());
            Map<String, String> params = serviceip.getPreferences();
            String url = params.get("serviceip");
            String path = "";
            if (url.equals("")) {
                path = "http://" + serverIP + "/AppService.php";
            } else {
                path = "http://" + serverIP + "/AppService.php";
            }
            try {
                String reqdata = HttpReqService.postRequest(path, reqparams, "GB2312");
                Log.d("debugTest", "reqdata -- " + reqdata);
                if (reqdata != null) {
                    // 子线程用sedMessage()方法传弟)Message对象
                    Message msg = mhandler.obtainMessage(FinalConstant.QUERY_BACK_DATA);
                    Bundle bundle = new Bundle();// 创建一个句柄
                    bundle.putString(FinalConstant.BACK_INFO, reqdata);// 将reqdata填充入句柄
                    msg.setData(bundle);// 设置一个任意数据值的Bundle对象。
                    mhandler.sendMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @SuppressLint("HandlerLeak")
        private Handler mhandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                if (msg.what == FinalConstant.QUERY_BACK_DATA) {
                    String jsonData = msg.getData().getString(FinalConstant.BACK_INFO);
                    try {
                        if (jsonData.equals("1")) {
                            Toast.makeText(getApplicationContext(), "服务器连接失败！", Toast.LENGTH_LONG).show();
                        } else {
                            JSONArray arr = new JSONArray(jsonData);
                            //获取Json数组的第一位
                            JSONObject tmp_cmd = (JSONObject) arr.get(0);
                            String str_cmd = tmp_cmd.getString("cmd");
                            Log.d("debugTest", "arr_data -- " + arr);
                            int len = 0;
                            len = arr.length();
                            Log.d("debugTest", "len -- " + len);
                            JSONObject result_cmd = (JSONObject) arr.get(0);
                            if (len > 1) {
                                if (str_cmd.equals(FinalConstant.FIND_NVR_SET_REBACK_SERVER)) {
                                    show(arr);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        };
    };

    private void show(JSONArray arr) {
        try{
            if (arr.get(1).equals(false)){
                Toast.makeText(getApplicationContext(),"获取NVR设备参数失败！",Toast.LENGTH_LONG).show();
            }if(!arr.get(1).equals(false)){
                //获取json数组对象有效数据
                JSONArray arr_data = (JSONArray) arr.get(1);
                Log.d("json数组", "json数组" + arr_data);
                JSONObject temp = (JSONObject) arr_data.get(0);
                strIP = temp.getString("NVR_IP");
                intPort = Integer.parseInt(temp.getString("NVR_Port"));
                strName = temp.getString("NVR_Name");
                strPsw = temp.getString("NVR_Psw");


                InitLogin();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    private void InitLogin() {

        try {
            if (m_iLogID < 0) {
                    /*登录设备：
                    *-1 表示失败，
                    * 其他值表示返回的用户 ID 值。
                    * 该用户 ID 具有唯一性，后续对设备的操作都需要通 过此 ID 实现。
                    * 接口返回失败请调用 NET_DVR_GetLastError 获取错误码，通过错误码判断出错原因 */
                Log.d("helloworld","-------"+strIP+"-----"+intPort+"-----"+strName+"------"+strPsw);
                m_iLogID = LoginDevice();
                if (m_iLogID < 0) {
                    Toast.makeText(getApplication(), "设备登录失败！", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Log.d("hello", "设备返回值m_iLogID：" + m_iLogID);
                }
                    /*获取异常回调的实例并设置*/
                ExceptionCallBack oexceptionCbf = getExceptiongCbf();
                if (oexceptionCbf == null) {
                    Log.d("hello", "异常回调对象失败!");
                    return;
                }
                    /*注册接收异常、重连消息回调函数 */
                if (!HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(oexceptionCbf)) {
                    Log.d("hello", "NET_DVR_SetExceptionCallBack 失败!");
                    return;
                }

                Toast.makeText(getApplication(), "设备登录成功！", Toast.LENGTH_SHORT).show();
                Log.d("hello", "设备登录成功！");
            } else {
                    /*判断是否有登出的情况
                    * 返回值：TRUE 表示成功， FALSE 表示失败。
                    * 接口返回失败请调用 NET_DVR_GetLastError 获取错误码，通过错误码判断出错原因*/
                if (!HCNetSDK.getInstance().NET_DVR_Logout_V30(m_iLogID)) {
                    Log.d("hello", "登出设备失败！");
                    return;
                }

                m_iLogID = -1;
            }

        } catch (Exception err) {
            Log.d("hello", "错误：error: " + err.toString());
        }

    }

    private int LoginDevice() {
        int iLogID = -1;
        iLogID = Login_IP_Device();
        return iLogID;
    }

    private int Login_IP_Device() {

         /*实列化设备信息*/
        m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
        if(null == m_oNetDvrDeviceInfoV30 ) {
            Log.d("hello","设备信息为空！");
            return -1;
        }
        /*获取文本框的值*/
//        strIP = device_ip.getText().toString();
//        intPort = Integer.parseInt(device_port.getText().toString());
//        strName = device_name.getText().toString();
//        strPsw = device_psw.getText().toString();

        /*NET_DVR_Login_V30登录设备接口：
        * 返回值：-1 表示失败，其他值表示返回的用户 ID值
        * 接口返回失败调用 NET_DVR_GetLastError 获取错误码，通过错误码判断出错原因。
        * strIP：String 、 设备IP地址或静态域名
        * intPort：int 、 设备端口号，特指TCP端口号
        * strName：String 、 登录用户名
        * strPsw： String 、 登录密码
        * m_oNetDvrDeviceInfoV30 、 设备信息
        */

        int iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(strIP, intPort, strName, strPsw, m_oNetDvrDeviceInfoV30);
        if(iLogID < 0){
            Log.d("hello","NVR设备登录失败！错误信息："+HCNetSDK.getInstance().NET_DVR_GetLastError());
            return -1;
        }
        /* NET_DVR_DEVICEINFO_V30参数
         * byChanNum 设备模拟通道个数
         * byStartChan 模拟通道起始通道号
         * byIPChanNum 设备最大数字通道个数，低 8 位 */
        if (m_oNetDvrDeviceInfoV30.byChanNum > 0) {
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartChan;
            /*设备通道个数*/
            m_iChanNum = m_oNetDvrDeviceInfoV30.byChanNum;
        } else if (m_oNetDvrDeviceInfoV30.byIPChanNum > 0) {
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartDChan;
            m_iChanNum = m_oNetDvrDeviceInfoV30.byIPChanNum + m_oNetDvrDeviceInfoV30.byHighDChanNum * 256;
        }

        if (m_iChanNum > 1) {
            ChangeSingleSurFace(false);
        } else {
            ChangeSingleSurFace(true);
        }

        return iLogID;
    }


    /*获取异常回调的实例并设置:
    * TRUE 表示成功，FALSE 表示失败。
    * 接口返回失败请调用 NET_DVR_GetLastError 获取错误码，通过错误码判断出错原因。
    * iType:异常或重连登录类型
    * iUserID：登录的ID
    * iHandle：出现异常的相应类型的句柄 */
    private ExceptionCallBack getExceptiongCbf() {
        ExceptionCallBack oExceptionCbf = new ExceptionCallBack() {
            public void fExceptionCallBack(int iType, int iUserID, int iHandle) {
                System.out.println("recv exception, type:" + iType);
            }
        };
        return oExceptionCbf;
    }
    // @Override
    public void surfaceCreated(SurfaceHolder holder) {
        m_osurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        Log.d("hello", "创建表面!");

        //当单通道预览时有效
        if (-1 == m_iPlayID && -1 == m_iPlaybackID) {
            return;
        }
        playView[0].m_hHolder = holder;
        Surface surface = holder.getSurface();
        if (true == surface.isValid()) {
            if (m_iPlayID != -1) {
                if (-1 == HCNetSDK.getInstance().NET_DVR_RealPlaySurfaceChanged(m_iPlayID, 0, holder)) {
                    Log.d("hello", "Player setVideoWindow failed!失败！");
                }
            } else {
                if (-1 == HCNetSDK.getInstance().NET_DVR_PlayBackSurfaceChanged(m_iPlaybackID, 0, holder)) {
                    Log.d("hello", "Player setVideoWindow failed!失败！");
                }
            }

        }
    }

    // @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("hello", "surface changed");
    }

    // @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("hello", "Player setVideoWindow release!");
        if (-1 == m_iPlayID && -1 == m_iPlaybackID) {
            Log.d("hello", "----" + m_iPlayID);
            return;
        }
        if (true == holder.getSurface().isValid()) {
            if (m_iPlayID != -1) {
                if (-1 == HCNetSDK.getInstance().NET_DVR_RealPlaySurfaceChanged(m_iPlayID, 0, null)) {
                    Log.d("hello", "Player setVideoWindow failed!");
                }
            } else {
                if (-1 == HCNetSDK.getInstance().NET_DVR_PlayBackSurfaceChanged(m_iPlaybackID, 0, null)) {
                    Log.d("hello", "Player setVideoWindow failed!");
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("m_iPlayID", m_iPlayID);
        super.onSaveInstanceState(outState);
        Log.d("hello", "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        m_iPlayID = savedInstanceState.getInt("m_iPlayID");
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("hello", "onRestoreInstanceState");
    }


    private void ChangeSingleSurFace(boolean bSingle) {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);

        for (int i = 0; i < 5; i++) {

            if (playView[i] == null) {
                playView[i] = new PlaySurfaceView(this);
                playView[i].setParam(metric.widthPixels);


                /*LayoutParams( , )跟的是长和宽两个属性*/
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);


                //设置边距
//                params.gravity = Gravity.LEFT;
//                params.setMargins(0,getResources().getDimensionPixelOffset(R.dimen.dp30),0,0);
//                params.bottomMargin =playView[i].getM_iHeight() - (i/2) * playView[i].getM_iHeight();
//                params.leftMargin = (i % 2) * playView[i].getM_iWidth();


                addContentView(playView[i], params);
                playView[i].setVisibility(View.INVISIBLE);

            }
        }

        if (bSingle) {
            for (int i = 0; i < 5; ++i) {
                playView[i].setVisibility(View.INVISIBLE);
            }
            playView[0].setParam(metric.widthPixels * 2);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);

            playView[0].setLayoutParams(params);
            playView[0].setVisibility(View.VISIBLE);
        } else {
            for (int i = 0; i < 5; ++i) {
                playView[i].setVisibility(View.VISIBLE);
            }
            playView[0].setParam(metric.widthPixels);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            /*设置边距
            * 设置FrameLayout需要设置Gravity，setMargins才会有效
            * 但是默认设置是:Gravity.TOP| Gravity.LEFT。*/
//            params.gravity = Gravity.LEFT;
            params.setMargins(0, getResources().getDimensionPixelOffset(R.dimen.dp84), 0, 0);
            playView[0].setLayoutParams(params);
        }
    }

    private boolean initeActivity() {
        InitView();
        m_osurfaceView.getHolder().addCallback(this);
        return true;
    }

    private boolean initeSdk() {
        //init net sdk
        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            Log.d("hello", "HCNetSDK init is failed!");
            return false;
        }
        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "/mnt/sdcard/sdklog/", true);
        return true;
    }


    private void InitView() {

        /*返回监听*/
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*视频预览*/
        m_osurfaceView = (SurfaceView) findViewById(R.id.Sur_Player);

        tv_content = (TextView) findViewById(R.id.tv_content);
        icon_play = (ImageView)findViewById(R.id.icon_play);
        icon_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preview_Listener(m_vChan);
            }
        });

        /*视屏选择*/
        findViewById(R.id.video_choose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Main_Iot_Video_Activity.this);
                builder.setTitle("选择视频");
                String str_wd = "育肥棚1号房(球)#育肥棚1号房(枪)#育肥棚室外#仓库#育肥棚2号房(枪)#育肥棚2号房(球)#实验室#门卫室#2号棚保育室3" +
                        "#3号棚配怀室#2号棚产房1#2号棚产房3#2号棚保育室2#2号棚保育室1#2号棚产房2#生活区";
//                指定下拉列表的显示数据
                final String[] cities = str_wd.split("#");
                builder.setItems(cities, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        tv_content.setText(cities[which]);
                        if (m_iPlayID >= 0) {
                            stopSinglePreview();//先停止预览前画面

                            m_vChan = m_iStartChan + which;
//                            startSinglePreview(m_vChan);
                            Pass(which);
                            Preview_Listener(m_vChan);

                        } else {
                            m_vChan = 33 + which;    //进入页面后，首先选择摄像头号
                            Preview_Listener(m_vChan);

                        }
                    }
                });
                dialog = builder.create();
                dialog.show();
                //设置大小
                WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
                layoutParams.width = getResources().getDimensionPixelOffset(R.dimen.dp200);
                layoutParams.height = getResources().getDimensionPixelOffset(R.dimen.dp450);
                layoutParams.gravity = Gravity.RIGHT|Gravity.TOP;

//                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setAttributes(layoutParams);
            }

            private void Pass(int which) {
                /*育肥棚1号房球机*/
                if(which==0||which==1){

                    num = 1;
                    GetDate(num);
                }
                /*育肥棚1号房枪机*/
//                if(which==1){
//                    webview.loadUrl("javascript:get_data('" + 1 + "')");
//                }
                /*育肥棚2号房枪机*/
                if(which==4||which==5){
                    num = 2;
                    GetDate(num);
//                    webview.loadUrl("javascript:get_data('" + 2 + "')");
                }
                /*育肥棚2号房球机*/
//                if(which==5){
//                    webview.loadUrl("javascript:get_data('" + 2 + "')");
//                }
                /*2号棚保育室3*/
                if(which==8){
                    num = 8;
                    GetDate(num);
//                    webview.loadUrl("javascript:get_data('" + 8 + "')");
                }
                /*3号棚配怀室*/
                if(which==9){
                    num = 9;
                    GetDate(num);
//                    webview.loadUrl("javascript:get_data('" + 9 + "')");
                }
                /*2号棚产房1*/
                if(which==10){
                    num = 7;
                    GetDate(num);
//                    webview.loadUrl("javascript:get_data('" + 7 + "')");
                }
                /*2号棚产房3*/
                if(which==11){
                    num = 3;
                    GetDate(num);
//                    webview.loadUrl("javascript:get_data('" + 3 + "')");
                }
                 /*2号棚保育室2*/
                if(which==12){
                    num = 6;
                    GetDate(num);
//                    webview.loadUrl("javascript:get_data('" + 6 + "')");
                }
                 /*2号棚保育室1*/
                if(which==13){
                    num = 4;
                    GetDate(num);
//                    webview.loadUrl("javascript:get_data('" + 4 + "')");
                }
                /*2号棚产房2*/
                if(which==14){
                    num = 5;
                    GetDate(num);
//                    webview.loadUrl("javascript:get_data('" + 5 + "')");
                }
            }

        });


        // 声明抓图按钮
        iv_video_photo = (ImageView) findViewById(R.id.iv_video_photo);
        iv_video_photo.setOnClickListener(Capture_Listener);
        // 声明监控录像按钮
        iv_video_record = (ImageView) findViewById(R.id.iv_video_record);
        tv_video_record = (TextView) findViewById(R.id.tv_video_record);
        iv_video_record.setOnClickListener(Record_Listener);

          /*云台控制*/
        include_ptz = this.findViewById(R.id.include_ptz);

        ptz = (ImageView)findViewById(R.id.ptz);
        /*声明云台控制按钮*/
        iv_video_ptz = (ImageView) findViewById(R.id.iv_video_ptz);
        iv_video_ptz.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                include_ptz.setVisibility(View.VISIBLE);
            }
        });

        /*关闭云台控制*/
        ptz_close = (ImageView)findViewById(R.id.ptz_close);
        ptz_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                include_ptz.setVisibility(View.GONE);
            }
        });

        include_fengji = this.findViewById(R.id.include_fengji);
        iv_fengji = (ImageView) findViewById(R.id.iv_fengji);
        iv_fengji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                include_fengji.setVisibility(View.VISIBLE);
            }
        });

        fengji_close = (ImageView)findViewById(R.id.fengji_close);
        fengji_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                include_fengji.setVisibility(View.GONE);
            }
        });

        /*云台向上*/
        btn_to_up = (TextView) findViewById(R.id.btn_to_up);
        btn_to_up.setOnTouchListener(PTZ_Listener);
        /*云台向下*/
        btn_to_down = (TextView) findViewById(R.id.btn_to_down);
        btn_to_down.setOnTouchListener(PTZ_Listener);
        /*云台向左*/
        btn_to_left = (TextView) findViewById(R.id.btn_to_left);
        btn_to_left.setOnTouchListener(PTZ_Listener);
        /*云台向右*/
        btn_to_right = (TextView) findViewById(R.id.btn_to_right);
        btn_to_right.setOnTouchListener(PTZ_Listener);
        /*云台放大*/
        btn_to_zoomin = (TextView) findViewById(R.id.btn_to_zoomin);
        zoom_out = (ImageView)findViewById(R.id.zoom_out);
        btn_to_zoomin.setOnTouchListener(PTZ_Listener);
        /*云台缩小*/
        btn_to_zoomout = (TextView) findViewById(R.id.btn_to_zoomout);
        zoom_in = (ImageView)findViewById(R.id.zoom_in);
        btn_to_zoomout.setOnTouchListener(PTZ_Listener);


    }

    private void GetDate(int num) {
        webview.loadUrl("javascript:get_data(" + num + ")");
    }


    private boolean m_bMultiPlay = false;

    // 预览监听
    private void Preview_Listener(int m_vChan) {
        try {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(Main_Iot_Video_Activity.this
                            .getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            if (m_iLogID < 0) {
                Log.d("hello", "请先登录设备");
                return;
            }

            if (m_iPlaybackID >= 0) {
                Log.d("hello", "请先停止回程");
                return;
            }

                /*通道号多于1
                * 数字模拟通道号从33开始*/
            if (m_iChanNum > 33) {
                Log.d("hello", "通道号多于1" + m_iChanNum);
                if (!m_bMultiPlay) {
                        /*多画面同时展示，未做该效果*/
                    startMultiPreview();
                    m_bMultiPlay = true;
                } else {
                    stopMultiPreview();
                    m_bMultiPlay = false;
                }
            } else // preview a channel--预览一个通道
            {
                icon_play.setVisibility(View.GONE);
                /**
                 * 125.71.233.23
                 * 8000
                 * admin
                 * cdsynkj1234
                 * */
                if (m_iPlayID < 0) {
                    Log.d("hello", "预览一个通道" + m_iChanNum);

                    startSinglePreview(m_vChan);
                } else {
                    stopSinglePreview();
                }
            }
        } catch (Exception ex) {
            Log.d("hello", ex.toString());
        }
    }

    ;

    /*多画面预览*/
    private void startMultiPreview() {
         /*一个个展示*/
        for (int i = 0; i < 5; i++) {
            playView[i].startPreview(m_iLogID, m_iStartChan + i);
            Log.d("hello", "----------startMultiPreview-----m_iStartChan:" + m_iStartChan);
        }
    }

    /*多画面停止*/
    private void stopMultiPreview() {

        int i = 0;
        for (i = 0; i < 5; i++) {
            playView[i].stopPreview();
        }
        m_iPlayID = -1;
    }

    /*开始单通道预览*/
    private int startSinglePreview(int m_vChan) {
        if (m_iPlaybackID >= 0) {
            Log.d("hello", "请先停止回放");
            return m_iPlaybackID;
        }
        Log.d("hello", "m_iStartChan:" + m_iStartChan);

        /*设备信息*/
        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = m_vChan;
        previewInfo.dwStreamType = 0; // main stream
        previewInfo.bBlocked = 1;
        previewInfo.hHwnd = playView[0].m_hHolder;


        /*NET_DVR_RealPlay_V40：
        * 返回值：-1 表示失败，其他值作为 NET_DVR_StopRealPlay 等函数的句柄参数。
        * 参数：
        * m_iLogID：NET_DVR_Login_V30 的返回值
        * previewInfo：预览参数，包括码流类型、取流协议、通道号等
        * 接口返回失败请调用 NET_DVR_GetLastError 获取错误码*/
        m_iPlayID = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(m_iLogID, previewInfo, null);
        Log.d("hello", "---单窗口预览 --- m_iPlayID：" + m_iPlayID);
        if (m_iPlayID < 0) {
            Log.d("hello", "NET_DVR_RealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return m_iPlayID;
        }
        return m_iPlayID;
    }

    /*停止预览*/
    private void stopSinglePreview() {
        if (m_iPlayID < 0) {
            Log.d("hello", "m_iPlayID < 0");
            return;
        }

        /*停止预览视频
        * 返回值：TRUE 表示成功，FALSE 表示失败
        * 接口返回失败请调用 NET_DVR_GetLastError 获取错误码。
        * m_iPlayID：预览句柄，NET_DVR_RealPlay_V40 的返回值 */

        if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(m_iPlayID)) {
            Log.d("hello", "停止预览失败!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }
        Log.d("hello", "停止预览成功！");
        m_iPlayID = -1;
    }

    /*时间*/
    private String date;
    /*抓图*/
    private ImageView.OnClickListener Capture_Listener = new ImageView.OnClickListener() {
        public void onClick(View v) {
            try {
                if (m_iPlayID < 0 && m_iPlaybackID < 0) {
                    Log.d("hello", "请先预览视频!");
                    Toast.makeText(getApplicationContext(), "请先预览视频！", Toast.LENGTH_LONG).show();
                    return;
                }
                if (m_iPlayID >= 0) {
                    /* NET_DVR_SetCapturePictureMode
                    * 设置抓图模式，JPEG 模式 = 1、BMP 模式 = 0
                    * 返回值为true就成功，
                    * 失败调用 NET_DVR_GetLastError */
//            		HCNetSDKJNAInstance.getInstance().NET_DVR_SetCapturePictureMode(0x1);

                    /*实例化日期时间*/
                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
                    date = sDateFormat.format(new java.util.Date());

                    /*NET_DVR_CapturePictureBlock
                    * 预览时抓图并保存成图片文件
                    * m_iPlayID：预览句柄，NET_DVR_RealPlay_V40的返回值
                    * sPicFileName ：保存图片的文件路径，包含文件名
                    *  dwTimeOut ：超时时间，目前无效 */
                    if (HCNetSDKJNAInstance.getInstance().NET_DVR_CapturePictureBlock(m_iPlayID, path + date + ".jpg", 0)) {
                        Log.d("hello", "jpg抓图成功！");
                        Toast.makeText(getApplication(), "抓图成功！", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("hello", "jpg抓图失败！---- Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
                    }
                } else {
                    /*bmp格式抓图，未使用*/
                    if (HCNetSDKJNAInstance.getInstance().NET_DVR_PlayBackCaptureFile(m_iPlaybackID, "/sdcard/capfile.bmp")) {
                        Log.d("hello", "NET_DVR_PlayBackCaptureFile succ");
                    } else {
                        Log.d("hello", "NET_DVR_PlayBackCaptureFile fail " + HCNetSDK.getInstance().NET_DVR_GetLastError());
                    }
                }

                /*更新图库，部分手机可以实现该功能*/
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    /*通知扫描指定文件*/
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(new File(path));
                    mediaScanIntent.setData(contentUri);
                    sendBroadcast(mediaScanIntent);
                } else {
                    //更新应用存储图片的路径内所有图片
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + path)));
                }
//                /*通知扫描指定文件*/
//                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(path))));
            } catch (Exception err) {
                Log.d("hello", "error: " + err.toString());
            }


        }
    };

    /*录像*/
    private boolean m_bSaveRealData = false;
    // record listener
    private ImageView.OnClickListener Record_Listener = new ImageView.OnClickListener() {
        public void onClick(View v) {
            /*提示预览*/
            if (m_iPlayID < 0 && m_iPlaybackID < 0) {
                Log.d("hello", "请先预览视频!");
                Toast.makeText(getApplicationContext(), "请先预览视频！", Toast.LENGTH_LONG).show();
                return;
            }
            /*是否点击*/
            if (!m_bSaveRealData) {
                /*实例化日期时间
                * 用于保存视频名字*/
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
                date = sDateFormat.format(new java.util.Date());

                /*NET_DVR_SaveRealData_V30
                * m_iPlayID：预览句柄，NET_DVR_RealPlay_V40的返回值
                * dwTransType：存储的码流封装格式
                * sFileName ：保存视频的路径和文件名，此处用的时间命名
                * 返回值：TRUE 表示成功，FALSE 表示失败。接口返回失败请调用 NET_DVR_GetLastError 获取错误码*/
                if (!HCNetSDKJNAInstance.getInstance().NET_DVR_SaveRealData_V30(m_iPlayID, 0x2, path_video + date + ".mp4")) {
                    Toast.makeText(getApplication(), "开始录像失败！", Toast.LENGTH_LONG).show();
                    Log.d("hello", "录像失败！error: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
                    return;
                } else {
                    Toast.makeText(getApplication(), "开始录像！", Toast.LENGTH_LONG).show();
                    tv_video_record.setText("录像中！！");
                    tv_video_record.setTextColor(getResources().getColor(R.color.red));
                }
                m_bSaveRealData = true;
            } else {
                if (!HCNetSDK.getInstance().NET_DVR_StopSaveRealData(m_iPlayID)) {
                    Toast.makeText(getApplication(), "停止录像失败！", Toast.LENGTH_LONG).show();
                    Log.d("hello", "停止录像失败！error: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
                } else {
                    Toast.makeText(getApplication(), "停止录像！", Toast.LENGTH_LONG).show();
                    tv_video_record.setText("开始录像");
                    tv_video_record.setTextColor(getResources().getColor(R.color.black));

                }
                m_bSaveRealData = false;
            }
        }
    };

    /*云台控制
    * NET_DVR_PTZControl_Other:（可以不用启动预览就）
    * m_iLogID：NET_DVR_Login_V30 的返回值
    * m_vChan：当前通道号
    * PTZCommand.TILT_UP：云台控制命令
    * dwStop：云台停止动作或开始动作：0- 开始；1- 停止
    * 返回值：TRUE 表示成功，FALSE 表示失败。接口返回失败请调用 NET_DVR_GetLastError 获取错误码
    * */
    private TextView.OnTouchListener PTZ_Listener = new TextView.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()) {
                case R.id.btn_to_up:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            ptz.setImageResource(R.drawable.ptz_up_press_on);
                            HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_vChan, PTZCommand.TILT_UP, 0);
                            break;
                        case MotionEvent.ACTION_UP:
                            ptz.setImageResource(R.drawable.ptz_base_map);
                            HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_vChan, PTZCommand.TILT_UP, 1);
                            break;
                    }
                    break;
                case R.id.btn_to_down:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            ptz.setImageResource(R.drawable.ptz_down_press_on);
                            HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_vChan, PTZCommand.TILT_DOWN, 0);
                            break;
                        case MotionEvent.ACTION_UP:
                            ptz.setImageResource(R.drawable.ptz_base_map);
                            HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_vChan, PTZCommand.TILT_DOWN, 1);
                            break;
                    }
                    break;
                case R.id.btn_to_left:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            ptz.setImageResource(R.drawable.ptz_left_press_on);
                            HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_vChan, PTZCommand.PAN_LEFT, 0);
                            break;
                        case MotionEvent.ACTION_UP:
                            ptz.setImageResource(R.drawable.ptz_base_map);
                            HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_vChan, PTZCommand.PAN_LEFT, 1);
                            break;
                    }
                    break;
                case R.id.btn_to_right:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            ptz.setImageResource(R.drawable.ptz_right_press_on);
                            HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_vChan, PTZCommand.PAN_RIGHT, 0);
                            break;
                        case MotionEvent.ACTION_UP:
                            ptz.setImageResource(R.drawable.ptz_base_map);
                            HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_vChan, PTZCommand.PAN_RIGHT, 1);
                            break;
                    }
                    break;
                case R.id.btn_to_zoomin:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            zoom_in.setImageResource(R.drawable.ptz_zoomin_press_on);
                            HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_vChan, PTZCommand.ZOOM_IN, 0);
                            break;
                        case MotionEvent.ACTION_UP:
                            zoom_in.setImageResource(R.drawable.ptz_zoomin_press_off);
                            HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_vChan, PTZCommand.ZOOM_IN, 1);
                            break;
                    }
                    break;
                case R.id.btn_to_zoomout:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            zoom_out.setImageResource(R.drawable.ptz_zoomout_press_on);
                            HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_vChan, PTZCommand.ZOOM_OUT, 0);
                            break;
                        case MotionEvent.ACTION_UP:
                            zoom_out.setImageResource(R.drawable.ptz_zoomout_press_off);
                            HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_vChan, PTZCommand.ZOOM_OUT, 1);
                            break;
                    }
                    break;
            }
            return true;
        }
    };

}

