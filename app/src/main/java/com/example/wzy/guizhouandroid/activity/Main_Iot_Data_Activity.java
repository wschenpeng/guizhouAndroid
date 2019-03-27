package com.example.wzy.guizhouandroid.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wzy.guizhouandroid.R;
import com.example.wzy.guizhouandroid.model.EnviromentSurfaceView;
import com.example.wzy.guizhouandroid.model.PlaySurfaceView;
import com.example.wzy.guizhouandroid.server.FinalConstant;
import com.example.wzy.guizhouandroid.server.HttpReqService;
import com.example.wzy.guizhouandroid.server.SubmitService;
import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main_Iot_Data_Activity extends Activity implements SurfaceHolder.Callback {

    private ViewPager viewPager;
    private ArrayList<View> pageview;
    private int dev_id;
    private String cmd;
    private boolean nThread = false;
    private HashMap<String, Object> reqparams;
    /**
     * dev_id==1
     */
    private TextView air_temp_1;
    private TextView air_humidity_1;
    private TextView illuminance_1;
    private TextView NH3_1_1;
    private TextView NH3_2_1;
    private TextView NH3_3_1;
    private TextView NH3_4_1;
    /**
     * dev_id==2
     */
    private TextView air_temp;
    private TextView air_humidity;
    private TextView illuminance;
    private TextView NH3_1;
    private TextView NH3_2;
    private TextView NH3_3;
    private TextView NH3_4;
    /**
     * dev_id==3
     */
    private TextView air_temp_3;
    private TextView air_humidity_3;
    private TextView illuminance_3;
    private TextView NH3_1_3;
    private TextView NH3_2_3;
    /**
     * dev_id==4
     */
    private TextView air_temp_4;
    private TextView air_humidity_4;
    private TextView illuminance_4;
    private TextView NH3_1_4;
    private TextView NH3_2_4;
    /**
     * dev_id==5
     */
    private TextView air_temp_5;
    private TextView air_humidity_5;
    private TextView illuminance_5;
    private TextView NH3_1_5;
    private TextView NH3_2_5;
    /**
     * dev_id==6
     */
    private TextView air_temp_6;
    private TextView air_humidity_6;
    private TextView illuminance_6;
    private TextView NH3_1_6;
    private TextView NH3_2_6;
    /**
     * dev_id==7
     */
    private TextView air_temp_7;
    private TextView air_humidity_7;
    private TextView illuminance_7;
    private TextView NH3_1_7;
    private TextView NH3_2_7;
    /**
     * dev_id==8
     */
    private TextView air_temp_8;
    private TextView air_humidity_8;
    private TextView illuminance_8;
    private TextView NH3_1_8;
    private TextView NH3_2_8;
    /**
     * dev_id==9
     */
    private TextView air_temp_9;
    private TextView air_humidity_9;
    private TextView illuminance_9;
    private TextView NH3_1_9;
    private TextView NH3_2_9;
    private TextView NH3_3_9;
    private TextView NH3_4_9;
    private TextView NH3_5_9;


    private TextView data_time_1;
    private TextView data_time_2;
    private TextView data_time_3;
    private TextView data_time_4;
    private TextView data_time_5;
    private TextView data_time_6;
    private TextView data_time_7;
    private TextView data_time_8;
    private TextView data_time_9;



    private TextView data_location_1;
    private TextView data_location_2;
    private TextView data_location_3;
    private TextView data_location_4;
    private TextView data_location_5;
    private TextView data_location_6;
    private TextView data_location_7;
    private TextView data_location_8;
    private TextView data_location_9;

    /**
     * 摄像头
     * */
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
    private EnviromentSurfaceView[] playView = new EnviromentSurfaceView[5];
    /*设备开始通道*/
    private int m_iStartChan= 33;
    private int m_vChan ;
    /*设备通道个数*/
    private int m_iChanNum;
    /*抓图保存地址*/
    String path = "/mnt/sdcard/Work/Photo/";
    private SurfaceView m_osurfaceView_1;
    private SurfaceView m_osurfaceView_2;
    private SurfaceView m_osurfaceView_3;
    private SurfaceView m_osurfaceView_4;
    private SurfaceView m_osurfaceView_5;
    private SurfaceView m_osurfaceView_6;
    private SurfaceView m_osurfaceView_7;
    private SurfaceView m_osurfaceView_8;
    private SurfaceView m_osurfaceView_9;
    private ImageView icon_play;
    private boolean m_bMultiPlay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_iot_data_activity);

        /**获取UI*/
        InitView();
        /**获取传感器参数线程*/
        dev_id = 1;
        Submit();
        /**获取NVR参数信息*/
        InitSubmit();
        /**获取控制H5*/
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

    private void InitWebView() {

    }

    private void InitSubmit() {
        cmd = FinalConstant.FIND_NVR_SET_REQUEST_SERVER;
        reqparams = new HashMap<String, Object>();
        reqparams.put("cmd", cmd);
        nThread = true;
        new Thread(query).start();
    }

//    private String serverIP = "120.79.76.116:7777";
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
                Log.d("hello","硬盘录像机参数：----"+"IP地址"+strIP+"端口号："+intPort+"用户名："+strName+"密码："+strPsw);


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
                Log.d("hello","-------"+strIP+"-----"+intPort+"-----"+strName+"------"+strPsw);
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
                Preview_Listener(33);
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
        Log.d("hello", "创建表面!1");

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

     @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("hello", "surface changed");
    }

     @Override
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
                playView[i] = new EnviromentSurfaceView(this);
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
            params.setMargins(0, getResources().getDimensionPixelOffset(R.dimen.dp80), 0, 0);
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

        findViewById(R.id.back).setOnClickListener(v -> finish());

        viewPager = (ViewPager) findViewById(R.id.viewPager);


        //查找布局文件用LayoutInflater.inflate
        LayoutInflater inflater = getLayoutInflater();
        View view1 = inflater.inflate(R.layout.first, null);
        View view2 = inflater.inflate(R.layout.first, null);
        View view3 = inflater.inflate(R.layout.second, null);
        View view4 = inflater.inflate(R.layout.second, null);
        View view5 = inflater.inflate(R.layout.second, null);
        View view6 = inflater.inflate(R.layout.second, null);
        View view7 = inflater.inflate(R.layout.second, null);
        View view8 = inflater.inflate(R.layout.second, null);
        View view9 = inflater.inflate(R.layout.three, null);


        /**1号站*/
        air_temp_1 = (TextView) view1.findViewById(R.id.air_temp);
        air_humidity_1 = (TextView) view1.findViewById(R.id.air_humidity);
        illuminance_1 = (TextView) view1.findViewById(R.id.illuminance);
        NH3_1_1 = (TextView) view1.findViewById(R.id.NH3_1);
        NH3_2_1 = (TextView) view1.findViewById(R.id.NH3_2);
        NH3_3_1 = (TextView) view1.findViewById(R.id.NH3_3);
        NH3_4_1 = (TextView) view1.findViewById(R.id.NH3_4);
        data_time_1 = (TextView) view1.findViewById(R.id.data_time);
        data_location_1 = (TextView) view1.findViewById(R.id.data_location);
        /*视频预览*/
        m_osurfaceView = (SurfaceView)view1.findViewById(R.id.Sur_Player);
        icon_play = (ImageView)view1.findViewById(R.id.icon_play);
        /**web*/


        /**2号站*/
        air_temp = (TextView) view2.findViewById(R.id.air_temp);
        air_humidity = (TextView) view2.findViewById(R.id.air_humidity);
        illuminance = (TextView) view2.findViewById(R.id.illuminance);
        NH3_1 = (TextView) view2.findViewById(R.id.NH3_1);
        NH3_2 = (TextView) view2.findViewById(R.id.NH3_2);
        NH3_3 = (TextView) view2.findViewById(R.id.NH3_3);
        NH3_4 = (TextView) view2.findViewById(R.id.NH3_4);
        data_time_2 = (TextView) view2.findViewById(R.id.data_time);
        data_location_2 = (TextView) view2.findViewById(R.id.data_location);
        /*视频预览*/
        m_osurfaceView = (SurfaceView) view2.findViewById(R.id.Sur_Player);
        icon_play = (ImageView)view2.findViewById(R.id.icon_play);

        /**3号站*/
        air_temp_3 = (TextView) view3.findViewById(R.id.air_temp);
        air_humidity_3 = (TextView) view3.findViewById(R.id.air_humidity);
        illuminance_3 = (TextView) view3.findViewById(R.id.illuminance);
        NH3_1_3 = (TextView) view3.findViewById(R.id.NH3_1);
        NH3_2_3 = (TextView) view3.findViewById(R.id.NH3_2);
        data_time_3 = (TextView) view3.findViewById(R.id.data_time);
        data_location_3 = (TextView) view3.findViewById(R.id.data_location);
        /*视频预览*/
        m_osurfaceView = (SurfaceView) view3.findViewById(R.id.Sur_Player);
        icon_play = (ImageView)view3.findViewById(R.id.icon_play);
        /**4号站*/
        air_temp_4 = (TextView) view4.findViewById(R.id.air_temp);
        air_humidity_4 = (TextView) view4.findViewById(R.id.air_humidity);
        illuminance_4 = (TextView) view4.findViewById(R.id.illuminance);
        NH3_1_4 = (TextView) view4.findViewById(R.id.NH3_1);
        NH3_2_4 = (TextView) view4.findViewById(R.id.NH3_2);
        data_time_4 = (TextView) view4.findViewById(R.id.data_time);
        data_location_4 = (TextView) view4.findViewById(R.id.data_location);
        /*视频预览*/
        m_osurfaceView = (SurfaceView) view4.findViewById(R.id.Sur_Player);
        icon_play = (ImageView)view4.findViewById(R.id.icon_play);
        /**5号站*/
        air_temp_5 = (TextView) view5.findViewById(R.id.air_temp);
        air_humidity_5 = (TextView) view5.findViewById(R.id.air_humidity);
        illuminance_5 = (TextView) view5.findViewById(R.id.illuminance);
        NH3_1_5 = (TextView) view5.findViewById(R.id.NH3_1);
        NH3_2_5 = (TextView) view5.findViewById(R.id.NH3_2);
        data_time_5 = (TextView) view5.findViewById(R.id.data_time);
        data_location_5 = (TextView) view5.findViewById(R.id.data_location);
        /*视频预览*/
        m_osurfaceView = (SurfaceView) view5.findViewById(R.id.Sur_Player);
        icon_play = (ImageView)view5.findViewById(R.id.icon_play);
        /**6号站*/
        air_temp_6 = (TextView) view6.findViewById(R.id.air_temp);
        air_humidity_6 = (TextView) view6.findViewById(R.id.air_humidity);
        illuminance_6 = (TextView) view6.findViewById(R.id.illuminance);
        NH3_1_6 = (TextView) view6.findViewById(R.id.NH3_1);
        NH3_2_6 = (TextView) view6.findViewById(R.id.NH3_2);
        data_time_6 = (TextView) view6.findViewById(R.id.data_time);
        data_location_6 = (TextView) view6.findViewById(R.id.data_location);
        /*视频预览*/
        m_osurfaceView = (SurfaceView) view6.findViewById(R.id.Sur_Player);
        icon_play = (ImageView)view6.findViewById(R.id.icon_play);

        /**7号站*/
        air_temp_7 = (TextView) view7.findViewById(R.id.air_temp);
        air_humidity_7 = (TextView) view7.findViewById(R.id.air_humidity);
        illuminance_7 = (TextView) view7.findViewById(R.id.illuminance);
        NH3_1_7 = (TextView) view7.findViewById(R.id.NH3_1);
        NH3_2_7 = (TextView) view7.findViewById(R.id.NH3_2);
        data_time_7 = (TextView) view7.findViewById(R.id.data_time);
        data_location_7 = (TextView) view7.findViewById(R.id.data_location);
        /*视频预览*/
        m_osurfaceView = (SurfaceView) view7.findViewById(R.id.Sur_Player);
        icon_play = (ImageView)view7.findViewById(R.id.icon_play);
        /**8号站*/
        air_temp_8 = (TextView) view8.findViewById(R.id.air_temp);
        air_humidity_8 = (TextView) view8.findViewById(R.id.air_humidity);
        illuminance_8 = (TextView) view8.findViewById(R.id.illuminance);
        NH3_1_8 = (TextView) view8.findViewById(R.id.NH3_1);
        NH3_2_8 = (TextView) view8.findViewById(R.id.NH3_2);
        data_time_8 = (TextView) view8.findViewById(R.id.data_time);
        data_location_8 = (TextView) view8.findViewById(R.id.data_location);
        /*视频预览*/
        m_osurfaceView = (SurfaceView) view7.findViewById(R.id.Sur_Player);
        icon_play = (ImageView)view8.findViewById(R.id.icon_play);
        /**9号站*/
        air_temp_9 = (TextView) view9.findViewById(R.id.air_temp);
        air_humidity_9 = (TextView) view9.findViewById(R.id.air_humidity);
        illuminance_9 = (TextView) view9.findViewById(R.id.illuminance);
        NH3_1_9 = (TextView) view9.findViewById(R.id.NH3_1);
        NH3_2_9 = (TextView) view9.findViewById(R.id.NH3_2);
        NH3_3_9 = (TextView) view9.findViewById(R.id.NH3_3);
        NH3_4_9 = (TextView) view9.findViewById(R.id.NH3_4);
        NH3_5_9 = (TextView) view9.findViewById(R.id.NH3_5);
        data_time_9 = (TextView) view9.findViewById(R.id.data_time);
        data_location_9 = (TextView) view9.findViewById(R.id.data_location);
        /*视频预览*/
        m_osurfaceView = (SurfaceView) view9.findViewById(R.id.Sur_Player);
        icon_play = (ImageView)view9.findViewById(R.id.icon_play);


        //将view装入数组
        pageview = new ArrayList<View>();
        pageview.add(view1);
        pageview.add(view2);
        pageview.add(view3);
        pageview.add(view4);
        pageview.add(view5);
        pageview.add(view6);
        pageview.add(view7);
        pageview.add(view8);
        pageview.add(view9);


        //数据适配器
        PagerAdapter mPagerAdapter = new PagerAdapter() {


            @Override
            //获取当前窗体界面数
            public int getCount() {
                // TODO Auto-generated method stub
                return pageview.size();
            }

            @Override
            //断是否由对象生成界面
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }

            //是从ViewGroup中移出当前View
            public void destroyItem(View arg0, int arg1, Object arg2) {
                ((ViewPager) arg0).removeView(pageview.get(arg1));
            }

            //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
            public Object instantiateItem(View arg0, int arg1) {
                ((ViewPager) arg0).addView(pageview.get(arg1));
                return pageview.get(arg1);
            }


        };

        icon_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preview_Listener(33);
            }
        });
        //绑定适配器
        viewPager.setAdapter(mPagerAdapter);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    dev_id = 1;
                    stopSinglePreview();//先停止预览前画面
                    m_vChan = m_iStartChan;
                    Preview_Listener(m_vChan);
                    Submit();
                }
                if (position == 1) {
                    dev_id = 2;
                    stopSinglePreview();//先停止预览前画面
                    m_vChan = m_iStartChan + 5;
                    Preview_Listener(m_vChan);
                    Submit();
                }
                if (position == 2) {
                    dev_id = 3;
                    stopSinglePreview();//先停止预览前画面
                    m_vChan = m_iStartChan + 11;
                    Preview_Listener(m_vChan);
                    Submit();
                }
                if (position == 3) {
                    dev_id = 4;
                    stopSinglePreview();//先停止预览前画面
                    m_vChan = m_iStartChan + 13;
                    Preview_Listener(m_vChan);
                    Submit();
                }
                if (position == 4) {
                    dev_id = 5;
                    stopSinglePreview();//先停止预览前画面
                    m_vChan = m_iStartChan + 14;
                    Preview_Listener(m_vChan);
                    Submit();
                }
                if (position == 5) {
                    dev_id = 6;
                    stopSinglePreview();//先停止预览前画面
                    m_vChan = m_iStartChan + 12;
                    Preview_Listener(m_vChan);
                    Submit();
                }
                if (position == 6) {
                    dev_id = 7;
                    stopSinglePreview();//先停止预览前画面
                    m_vChan = m_iStartChan + 10;
                    Preview_Listener(m_vChan);
                    Submit();
                }
                if (position == 7) {
                    dev_id = 8;
                    stopSinglePreview();//先停止预览前画面
                    m_vChan = m_iStartChan + 8;
                    Preview_Listener(m_vChan);
                    Submit();
                }
                if (position == 8) {
                    dev_id = 9;
                    stopSinglePreview();//先停止预览前画面
                    m_vChan = m_iStartChan + 9;
                    Preview_Listener(m_vChan);
                    Submit();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    // 预览监听
    private void Preview_Listener(int m_vChan) {
        try {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(Main_Iot_Data_Activity.this
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
//                icon_play.setVisibility(View.GONE);
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
        Log.d("hello", "m_iStartChan:" + m_vChan);

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

    private void Submit() {
        cmd = FinalConstant.FIND_DATA_REQUEST_SERVER;
        nThread = true; // query_int线程开启

        reqparams = new HashMap<String, Object>(); // 组织参数
        reqparams.put("cmd", cmd);
        reqparams.put("dev_id", dev_id);
        new Thread(query_int).start(); // 从服务器获取水源站传感器参数
    }

    private String serverIP = "120.79.76.116:7777";
    private Runnable query_int = new Runnable() {
        @Override
        public void run() {
            while (nThread) {
                try {
                    String path = "http://" + serverIP + "/AppService.php";

                    String reqdata = HttpReqService.postRequest(path, reqparams, "GB2312");
                    Log.d("debugTest", "reqdata -- " + reqdata);
                    if (reqdata != null) {
                        // 子线程用sedMessage()方法传弟)Message对象
                        Message msg = mhandler_get.obtainMessage(FinalConstant.GT_QUERY_BACK_DATA);
                        Bundle bundle = new Bundle();// 创建一个句柄
                        bundle.putString(FinalConstant.GT_BACK_INFO, reqdata);// 将reqdata填充入句柄
                        msg.setData(bundle);// 设置一个任意数据值的Bundle对象。
                        mhandler_get.sendMessage(msg);
                    }
                    Thread.sleep(1000);// 线程暂停10秒，单位毫秒 启动线程后，线程每10s发送一次消息
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mhandler_get = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == FinalConstant.GT_QUERY_BACK_DATA) {
                String jsonData = msg.getData().getString(FinalConstant.GT_BACK_INFO);
                try {
                    if (jsonData.equals("1")) {
                        Toast.makeText(getApplicationContext(), "服务器没有开启或异常", Toast.LENGTH_LONG).show();
                        nThread = false;
                    } else {
                        JSONArray arr = new JSONArray(jsonData); // 收到JSON数组对象解析
                        Log.d("arr", "arr -- " + arr);

                        JSONObject tmp_cmd = (JSONObject) arr.get(0); // 获取json数组对象返回命令
                        String str_cmd = tmp_cmd.getString("cmd");
                        int len = 0;
                        len = arr.length();
                        if (len > 1) {
                            if (str_cmd.equals(FinalConstant.FIND_DATA_REBACK_SERVER)) {
                                if (dev_id == 1 | dev_id == 2) {
                                    show_data_1(arr);
                                }
                                if (dev_id == 3 | dev_id == 4 | dev_id == 5 | dev_id == 6 | dev_id == 7 | dev_id == 8) {
                                    show_data_2(arr);
                                }
                                if (dev_id == 9) {
                                    show_data_3(arr);
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private void show_data_1(JSONArray arr) {

            try {
                if (!arr.get(1).equals(false)) {
//                    Log.d("hello", "hhh");
                    // 获取json数组对象有效数据
                    JSONArray arr_data = (JSONArray) arr.get(1);
                    Log.d("json数组", "json数组" + arr_data);
                    JSONObject temp = (JSONObject) arr_data.get(0);
                    if (dev_id == 1) {
                        air_temp_1.setText(temp.getString("air_temp"));
                        air_humidity_1.setText(temp.getString("air_humidity"));
                        illuminance_1.setText(temp.getString("illuminance"));
                        NH3_1_1.setText(temp.getString("NH3_1"));
                        NH3_2_1.setText(temp.getString("NH3_2"));
                        NH3_3_1.setText(temp.getString("NH3_3"));
                        NH3_4_1.setText(temp.getString("NH3_4"));
                        data_time_1.setText(temp.getString("data_time"));
                        data_location_1.setText("监测点：育肥棚1号房");
                    }
                    if (dev_id == 2) {
                        air_temp.setText(temp.getString("air_temp"));
                        air_humidity.setText(temp.getString("air_humidity"));
                        illuminance.setText(temp.getString("illuminance"));
                        NH3_1.setText(temp.getString("NH3_1"));
                        NH3_2.setText(temp.getString("NH3_2"));
                        NH3_3.setText(temp.getString("NH3_3"));
                        NH3_4.setText(temp.getString("NH3_4"));
                        data_time_2.setText(temp.getString("data_time"));
                        data_location_2.setText("监测点：育肥棚2号房");
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void show_data_2(JSONArray arr) {
            try {
                if (!arr.get(1).equals(false)) {
//                    Log.d("hello", "hhh");
                    // 获取json数组对象有效数据
                    JSONArray arr_data = (JSONArray) arr.get(1);
                    Log.d("json数组", "json数组" + arr_data);
                    JSONObject temp = (JSONObject) arr_data.get(0);
                    if (dev_id == 3) {
                        air_temp_3.setText(temp.getString("air_temp"));
                        air_humidity_3.setText(temp.getString("air_humidity"));
                        illuminance_3.setText(temp.getString("illuminance"));
                        NH3_1_3.setText(temp.getString("NH3_1"));
                        NH3_2_3.setText(temp.getString("NH3_2"));
                        data_time_3.setText(temp.getString("data_time"));
                        data_location_3.setText("监测点：2号棚产房3");
                    }
                    if (dev_id == 4) {
                        air_temp_4.setText(temp.getString("air_temp"));
                        air_humidity_4.setText(temp.getString("air_humidity"));
                        illuminance_4.setText(temp.getString("illuminance"));
                        NH3_1_4.setText(temp.getString("NH3_1"));
                        NH3_2_4.setText(temp.getString("NH3_2"));
                        data_time_4.setText(temp.getString("data_time"));
                        data_location_4.setText("监测点：2号棚保育室1");
                    }
                    if (dev_id == 5) {
                        air_temp_5.setText(temp.getString("air_temp"));
                        air_humidity_5.setText(temp.getString("air_humidity"));
                        illuminance_5.setText(temp.getString("illuminance"));
                        NH3_1_5.setText(temp.getString("NH3_1"));
                        NH3_2_5.setText(temp.getString("NH3_2"));
                        data_time_5.setText(temp.getString("data_time"));
                        data_location_5.setText("监测点：2号棚产房2");
                    }
                    if (dev_id == 6) {
                        air_temp_6.setText(temp.getString("air_temp"));
                        air_humidity_6.setText(temp.getString("air_humidity"));
                        illuminance_6.setText(temp.getString("illuminance"));
                        NH3_1_6.setText(temp.getString("NH3_1"));
                        NH3_2_6.setText(temp.getString("NH3_2"));
                        data_time_6.setText(temp.getString("data_time"));
                        data_location_6.setText("监测点：2号棚保育室2");
                    }
                    if (dev_id == 7) {
                        air_temp_7.setText(temp.getString("air_temp"));
                        air_humidity_7.setText(temp.getString("air_humidity"));
                        illuminance_7.setText(temp.getString("illuminance"));
                        NH3_1_7.setText(temp.getString("NH3_1"));
                        NH3_2_7.setText(temp.getString("NH3_2"));
                        data_time_7.setText(temp.getString("data_time"));
                        data_location_7.setText("监测点：2号棚产房1");
                    }
                    if (dev_id == 8) {
                        air_temp_8.setText(temp.getString("air_temp"));
                        air_humidity_8.setText(temp.getString("air_humidity"));
                        illuminance_8.setText(temp.getString("illuminance"));
                        NH3_1_8.setText(temp.getString("NH3_1"));
                        NH3_2_8.setText(temp.getString("NH3_2"));
                        data_time_8.setText(temp.getString("data_time"));
                        data_location_8.setText("监测点：2号棚保育室3");
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void show_data_3(JSONArray arr) {
            try {
                if (!arr.get(1).equals(false)) {
//                    Log.d("hello", "hhh");
                    // 获取json数组对象有效数据
                    JSONArray arr_data = (JSONArray) arr.get(1);
                    Log.d("json数组", "json数组" + arr_data);
                    JSONObject temp = (JSONObject) arr_data.get(0);
                    if (dev_id == 9) {
                        air_temp_9.setText(temp.getString("air_temp"));
                        air_humidity_9.setText(temp.getString("air_humidity"));
                        illuminance_9.setText(temp.getString("illuminance"));
                        NH3_1_9.setText(temp.getString("NH3_1"));
                        NH3_2_9.setText(temp.getString("NH3_2"));
                        NH3_3_9.setText(temp.getString("NH3_3"));
                        NH3_4_9.setText(temp.getString("NH3_4"));
                        NH3_5_9.setText(temp.getString("NH3_4"));
                        data_time_9.setText(temp.getString("data_time"));
                        data_location_9.setText("监测点：3号棚配怀室");
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    };


}


