package com.example.wzy.guizhouandroid.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.wzy.guizhouandroid.R;
import com.example.wzy.guizhouandroid.server.FinalConstant;
import com.example.wzy.guizhouandroid.server.HttpReqService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RelativeLayout RL_iot_feel;
    private RelativeLayout RL_iot_video;
    private RelativeLayout RL_iot_fodder;
    private WebView webview;
//    private String url = "http://120.79.76.116:7777/APP/lunbo/index.html";
    private String url = "file:///android_asset/lunbo/lunbo/index.html";

    private String mVrsion_Name_Server;

    private String Version_Code_Server;
    private int mVersion_local;
    private String Version_Code_Local;
    private String cmd;
    private boolean nThread = false;
    private HashMap<String, Object> reqparams;
    private View nav_share;
    private String Date_Size;
    private String mDate_Size;
    private RelativeLayout RL_iot_two;
    private RelativeLayout RL_iot_vaccine;
    private RelativeLayout RL_iot_sow;
    private RelativeLayout RL_iot_grow;

    /**网页页面*/
    /**网页回退*/
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
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //获取本地版本信息
        UpdateAppUtil mm = new UpdateAppUtil();
        mVersion_local = mm.getAPPLocalVersion(this);
        Version_Code_Local = String.valueOf(mVersion_local);

        cmd = FinalConstant.UPDATE_REQUEST_SERVER;
        nThread = true;
        reqparams = new HashMap<String, Object>();
        reqparams.put("cmd",cmd);
        new Thread(query_show).start(); // 向服务器提交参数


        InitView();


    }

    private void InitView() {
        //智能感知
        RL_iot_feel = (RelativeLayout)findViewById(R.id.RL_iot_feel);
        RL_iot_feel.setOnClickListener(mOnViewClickListener);
        //智能监控
        RL_iot_video = (RelativeLayout)findViewById(R.id.RL_iot_video);
        RL_iot_video.setOnClickListener(mOnViewClickListener);
        //饲料投放
        RL_iot_fodder = (RelativeLayout)findViewById(R.id.RL_iot_fodder);
        RL_iot_fodder.setOnClickListener(mOnViewClickListener);
		//疫苗登记
        RL_iot_vaccine = (RelativeLayout)findViewById(R.id.RL_iot_vaccine);
        RL_iot_vaccine.setOnClickListener(mOnViewClickListener);
        //母猪记录
        RL_iot_sow = (RelativeLayout)findViewById(R.id.RL_iot_sow);
        RL_iot_sow.setOnClickListener(mOnViewClickListener);
        //生长记录
        RL_iot_grow = (RelativeLayout)findViewById(R.id.RL_iot_grow);
        RL_iot_grow.setOnClickListener(mOnViewClickListener);
        /**网页*/
        webview = (WebView) findViewById(R.id.wb);
        final WebSettings webSettings = webview.getSettings();
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
//        webSettings.setSupportZoom(false);
        // 将图片调整到合适的大小
//        webSettings.setUseWideViewPort(false);
        // 支持内容重新布局,一共有四种方式
        // 默认的是NARROW_COLUMNS
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // 设置默认字体大小
        webSettings.setDefaultFontSize(12);
        // 支持缩放
        webSettings.setSupportZoom(false);
        //设置支持两指缩放手势
        webSettings.setBuiltInZoomControls(false);
        //自适应屏幕
//        webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        webview.getSettings().setLoadWithOverviewMode(true);

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

    protected View.OnClickListener mOnViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.RL_iot_feel:
                    Intent i = new Intent();
                    i.setClass(MainActivity.this, Main_Iot_Data_Activity.class);
                    startActivity(i);
                    break;
                case R.id.RL_iot_video:
                    Intent i2 = new Intent();
                    i2.setClass(MainActivity.this, Main_Iot_Video_Activity.class);
                    startActivity(i2);
                    break;
                case R.id.RL_iot_fodder:
                    Intent i3 = new Intent();
                    i3.setClass(MainActivity.this, Main_WebView_Title_Activity.class);
                    i3.putExtra("Intent_url","http://120.79.76.116:7777/index.php?m=content&c=index&a=lists&catid=65");
                    startActivity(i3);
                    break;
				case R.id.RL_iot_vaccine:
					Intent i4 = new Intent();
					i4.setClass(MainActivity.this,Main_WebView_Title_Activity.class);
					i4.putExtra("Intent_url","http://120.79.76.116:7777/index.php?m=content&c=index&a=lists&catid=63");
					startActivity(i4);
					break;
                case R.id.RL_iot_sow:
                    Intent i5 = new Intent();
                    i5.setClass(MainActivity.this,Sow_Activity.class);
                    startActivity(i5);
                    break;
                case R.id.RL_iot_grow:
                    Intent i6 = new Intent();
                    i6.setClass(MainActivity.this,Main_Grow_Activity.class);
                    startActivity(i6);
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /**膨胀的菜单;如果动作栏存在，它会将项添加到动作栏中。*/
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /**处理操作栏项目点击这里。只要在AndroidManifest.xml中指定父活动，操作栏就会自动处理Home/Up按钮上的单击。*/
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        /**处理导航视图项单击这里*/
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent i = new Intent(MainActivity.this, Myself_Set_User_Info_Activity.class);
            startActivity(i);

        }
        /**学习中心*/
        else if (id == R.id.nav_gallery) {
            Intent i = new Intent();
            i.setClass(MainActivity.this,Main_WebView_Activity.class);
            i.putExtra("Intent_url","http://agri.ckcest.cn/index.html");
            startActivity(i);

        }
        /**技术支持*/
        else if (id == R.id.nav_slideshow) {
            Intent i = new Intent();
            i.setClass(MainActivity.this,Main_WebView_Activity.class);
            i.putExtra("Intent_url","http://120.79.76.116:7777/APP/newjishu/index.html");
            startActivity(i);

        }
//        else if (id == R.id.nav_manage) {
//
//        }
        /**版本更新*/
        else if (id == R.id.nav_share) {
            m();
        }
        /**清除缓存*/
        else if (id == R.id.nav_send) {
            DataCleanManager mm = new DataCleanManager();
            try {
                Date_Size = mm.getTotalCacheSize(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //添加"Yes"按钮
            //添加取消
            AlertDialog a = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("是否清除缓存")
                    .setMessage("将会清除掉所有网络下载缓存！大小为："+Date_Size)

                    .setPositiveButton("确定", (dialogInterface, i) -> {
                        try {
                            clear_data();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    })
                    .setNegativeButton("取消", (dialogInterface, i) -> {

                    })
                    .create();
            a.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void clear_data() throws Exception {
        DataCleanManager mm = new DataCleanManager();
        //调用清除缓存方法
        mm.clearAllCache(this);
        mDate_Size =  mm.getTotalCacheSize(this);
        Toast.makeText(getApplicationContext(),"清除完成！"+mDate_Size, Toast.LENGTH_LONG).show();
    }


    private String serverIP = "120.79.76.116:7777";
    private Runnable query_show = new Runnable() {
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
                            if (str_cmd.equals(FinalConstant.UPDATE_REBACK_SERVER)) {
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

    private void show(JSONArray arr) {
        try{
            if (arr.get(1).equals(false)) {
            }if(!arr.get(1).equals(false)){
                //获取json数组对象有效数据
                JSONArray arr_data = (JSONArray) arr.get(1);
                Log.d("json数组", "json数组" + arr_data);
                JSONObject temp = (JSONObject) arr_data.get(0);

                mVrsion_Name_Server = temp.getString("new_VersionName");
                Version_Code_Server = temp.getString("VersionCode");

                if(mVrsion_Name_Server.length()!=0&&Version_Code_Server.length()!=0){
                    nThread=false;
                }

                String[] str = {mVrsion_Name_Server,Version_Code_Server};

                L(str);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void L(final String[] str) {
        mVrsion_Name_Server = str[0];
        Version_Code_Server = str[1];

    }

    private void m() {
        if(Version_Code_Local.equals(Version_Code_Server)){
            Toast.makeText(getApplication(),"当前版本为最新版本！！",Toast.LENGTH_LONG).show();
        }else {
            Intent i = new Intent(MainActivity.this, Myself_Set_Update_Activity.class);

            i.putExtra("version", mVrsion_Name_Server);
            i.putExtra("Intent_url","http://120.79.76.116:7777/phpcms/templates/APP/update/gengxin.html");
            nThread=false;
            startActivity(i);
        }
    }

    public class UpdateAppUtil {
        /**
         * 获取当前apk的版本号 currentVersionCode
         *
         * @param ctx
         * @return
         */
        public int getAPPLocalVersion(Context ctx) {
            int currentVersionCode = 0;
            PackageManager manager = ctx.getPackageManager();
            try {
                PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
                String appVersionName = info.versionName; // 版本名
                currentVersionCode = info.versionCode; // 版本号
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return currentVersionCode;
        }
    }

    public class DataCleanManager {

        /**
         * 获取缓存大小
         *
         * @param context
         * @return
         * @throws Exception
         */
        public String getTotalCacheSize(Context context) throws Exception {
            long cacheSize = getFolderSize(context.getCacheDir());
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                cacheSize += getFolderSize(context.getExternalCacheDir());
            }
            return getFormatSize(cacheSize);
        }

        /**
         * 清除缓存
         *
         * @param context
         */
        public void clearAllCache(Context context) {
            deleteDir(context.getCacheDir());
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                deleteDir(context.getExternalCacheDir());
            }
        }

        private boolean deleteDir(File dir) {
            if (dir != null && dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
            return dir.delete();
        }

        // 获取文件大小
        //Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
        //Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
        public long getFolderSize(File file) throws Exception {
            long size = 0;
            try {
                File[] fileList = file.listFiles();
                for (int i = 0; i < fileList.length; i++) {
                    // 如果下面还有文件
                    if (fileList[i].isDirectory()) {
                        size = size + getFolderSize(fileList[i]);
                    } else {
                        size = size + fileList[i].length();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return size;
        }

        /**
         * 格式化单位
         *
         * @param size
         * @return
         */
        public String getFormatSize(double size) {
            double kiloByte = size / 1024;
            if (kiloByte < 1) {
//            return size + "Byte";
                return "0K";
            }

            double megaByte = kiloByte / 1024;
            if (megaByte < 1) {
                BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
                return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                        .toPlainString() + "K";
            }

            double gigaByte = megaByte / 1024;
            if (gigaByte < 1) {
                BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
                return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                        .toPlainString() + "M";
            }

            double teraBytes = gigaByte / 1024;
            if (teraBytes < 1) {
                BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
                return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                        .toPlainString() + "GB";
            }
            BigDecimal result4 = new BigDecimal(teraBytes);
            return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                    + "TB";
        }
    }
}
