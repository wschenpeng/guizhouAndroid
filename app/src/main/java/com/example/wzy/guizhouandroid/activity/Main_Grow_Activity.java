package com.example.wzy.guizhouandroid.activity;

/**
 * @author:     wuzhuoyu
 * @date:
 * @activity：
 * @version:
 * @project:    生猪养殖系统
 * @description:
 *
 ****************************************************************************************************
 * 相册、相机权限参考：https://www.jianshu.com/p/41b093d213fb
 *
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wzy.guizhouandroid.R;
import com.example.wzy.guizhouandroid.model.getPhotoFromPhotoAlbum;
import com.example.wzy.guizhouandroid.server.FinalConstant;
import com.example.wzy.guizhouandroid.server.HttpReqService;
import com.example.wzy.guizhouandroid.server.SubmitService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gr.escsoft.michaelprimez.searchablespinner.SearchableSpinner;
import pub.devrel.easypermissions.EasyPermissions;

public class Main_Grow_Activity extends Activity implements View.OnClickListener, EasyPermissions.PermissionCallbacks{

    private SearchableSpinner batch_number;
//    private String[] item_spinner_id = new String[]{"综合气象监测站", "水源监测站", "水质监测站①", "水质监测站②", "水质监测站③", "水质监测站④", "便携式重金属监测仪"};
    private String[] item_spinner_id;
    private SearchableSpinner grow_stage;
    /**获取批次号*/
    private String serverIP = "120.79.76.116:7777";
    private String cmd;
    private HashMap<String, Object> reqparams;
    private boolean nThread = false;
    private ImageView camera;

    protected static final int REQUEST_CODE_IMAGE = 1;//请求图库
    protected static final int REQUEST_CODE_CAMERA = 2;//请求相机
    //系统相册的路径
    String path=Environment.getExternalStorageDirectory()+File.separator+Environment.DIRECTORY_DCIM+File.separator;
    private Uri photoUri;
    private Bitmap mge;
    private String str;
    private String event_type;
    private int digree =0;
    private View include_image;
    private ImageView image_show;
    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private Uri uri;
    private File cameraSavePath;
    private ImageView close;
    private TextView Save;
    private String photoPath;
    private String mbatch_number;
    private String mgrow_stage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_grow_activity);

        InitView();
        InitShow();
    }

    private void InitShow() {
        cmd = FinalConstant.FIND_BATCH_REQUEST_SERVER;
        reqparams = new HashMap<String, Object>();
        reqparams.put("cmd", cmd);
        nThread = true;
        new Thread(query).start();
    }

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
                    // 子线程用sedMessage()方法传递Message对象
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
                                if (str_cmd.equals(FinalConstant.FIND_BATCH_REBACK_SERVER)) {
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
                JSONArray arr_data = (JSONArray) arr.get(1);
                Log.d("hello", "json数组" + arr_data);

                String [] number = new String[arr_data.length()];
                if(arr_data.length()>1){

                    if(!arr.get(1).equals(false)){
                        HashMap<Integer, HashMap> hs = new HashMap<>();
                        for(int i = 1; i<=arr_data.length(); i++){
                            JSONObject temp = (JSONObject) arr_data.get(i - 1);
                            HashMap<String, Object> hm = new HashMap<>();
                            hm.put("pici",temp.getString("pici"));
                            hs.put(i-1,hm);
                        }
                        Log.d("hello",hs.toString());

                        for (int i = 0; i<arr_data.length();i++){
                            HashMap<String,String> hx = hs.get(i);
                            number[i] = hx.get("pici");
                            Log.d("hello",number[i]);
                            Log.d("hello", "数组：" + Arrays.toString(number));
                        }
                    }
                }
            Adapter(number);

        }catch (JSONException e){
            e.printStackTrace();
        }

    }



    private void InitView() {
        findViewById(R.id.back).setOnClickListener(v -> finish());

        batch_number =(SearchableSpinner) findViewById(R.id.batch_number);
        grow_stage = (SearchableSpinner) findViewById(R.id.grow_stage);
        camera = (ImageView)findViewById(R.id.camera);
        Save = (TextView)findViewById(R.id.Save);
        Save.setOnClickListener(new HsSubmitOnClickListener());

        cameraSavePath = new File(Environment.getExternalStorageDirectory().getPath() + "/" + System.currentTimeMillis() + ".jpg");
    }

    private void Adapter(String[] number) {
        ArrayAdapter<String> adapter_id = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,number);
        batch_number.setAdapter(adapter_id);


        String [] stage = new String[]{"保育期","育肥期"};
        ArrayAdapter<String> adapter_id2 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,stage);
        grow_stage.setAdapter(adapter_id2);

         /*图片展示*/
        include_image = this.findViewById(R.id.include_image);
        image_show = (ImageView)findViewById(R.id.image_show);
        camera.setOnClickListener(v -> {
            /**动态获取权限*/
            include_image.setVisibility(View.VISIBLE);
            camera.setVisibility(View.GONE);
            getPermission();
            Picture_Choose();
        });

        close = (ImageView)findViewById(R.id.close);
        close.setOnClickListener(v -> {
            camera.setVisibility(View.VISIBLE);
            include_image.setVisibility(View.GONE);
        });
    }


    /**动态获取权限*/
    private void getPermission() {
        if (EasyPermissions.hasPermissions(this, permissions)) {
            //已经打开权限
//            Toast.makeText(this, "已经申请相关权限", Toast.LENGTH_SHORT).show();
        } else {
            //没有打开相关权限、申请权限
            EasyPermissions.requestPermissions(this, "需要获取您的相册、照相使用权限", 1, permissions);
        }
    }

    /**弹窗选择*/
    private void Picture_Choose() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Main_Grow_Activity.this);
        String [] item=new String[] {"从手机相册选取","拍照"};
        builder.setItems(item, (dialog, which) -> {
            if (which == 0){
                /**相册*/
                goPhotoAlbum();
            }else {
                /**相机*/
                goCamera();
            }
        });
        builder.show();
    }


    /**打开相册*/
    private void goPhotoAlbum() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }
    /**打开相机*/
    private void goCamera() {
        String imagename =getphotoname()+".jpg";
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(Main_Grow_Activity.this, "com.example.wzy.guizhouandroid.fileprovider", cameraSavePath);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(cameraSavePath);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        Main_Grow_Activity.this.startActivityForResult(intent, 1);
    }

    /**
     * 重写onRequestPermissionsResult，用于接受请求结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //框架要求必须这么写
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    //成功打开权限
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

        Toast.makeText(this, "相关权限获取成功", Toast.LENGTH_SHORT).show();
    }
    //用户未同意权限
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "请同意相关权限，否则功能无法使用", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        String photoPath;
        if (requestCode == 1 && resultCode == RESULT_OK) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                photoPath = String.valueOf(cameraSavePath);
            } else {
                photoPath = uri.getEncodedPath();
            }
            Log.d("拍照返回图片路径:", photoPath);
            Glide.with(Main_Grow_Activity.this).load(photoPath).into(image_show);
            mge= BitmapFactory.decodeFile(photoPath);
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            photoPath = getPhotoFromPhotoAlbum.getRealPathFromUri(this, data.getData());
            Glide.with(Main_Grow_Activity.this).load(photoPath).into(image_show);
            mge= BitmapFactory.decodeFile(photoPath);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onClick(View v) {}

    //照片命名规则
    private String getphotoname() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return "IMG_" + dateFormat.format(date);
    }

    private class HsSubmitOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
//            Log.d("hello1","batch_number:"+batch_number.toString()+"grow_stage:"+grow_stage.toString());
            if(mge!=null){
                //图片转换格式
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                mge.compress(Bitmap.CompressFormat.JPEG, 10, baos);//100代表图片不压缩，10代表压缩至原有大小的1/10
                byte []imagedata=baos.toByteArray();
                str= Base64.encodeToString(imagedata, Base64.DEFAULT);
                Log.d("hello1","str:-----------"+str.toString());
                mbatch_number = batch_number.getSelectedItem().toString();
                mgrow_stage = grow_stage.getSelectedItem().toString();
                if(batch_number!=null&grow_stage!=null){
                    try{
                        cmd = FinalConstant.FIND_GROW_SUBMIT_SERVER;
                        reqparams = new HashMap<String,Object>();
                        reqparams.put("cmd",cmd);
                        reqparams.put("batch_number", URLEncoder.encode(mbatch_number,"UTF-8"));
                        reqparams.put("grow_stage", URLEncoder.encode(mgrow_stage,"UTF-8"));
                        reqparams.put("image",URLEncoder.encode(str, "UTF-8"));   //  图片
                    }catch (UnsupportedEncodingException e){
                        e.printStackTrace();
                    }
                    new Thread(query_submit).start();
                }else{
                    Toast.makeText(getApplicationContext(),"请选择批次号或生长阶段！！",Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(getApplicationContext(),"请将信息补充完整！！",Toast.LENGTH_LONG).show();
            }
        }
    }

    private Runnable query_submit = new Runnable() {
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

    };

    @SuppressLint("HandlerLeak")
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == FinalConstant.QUERY_BACK_DATA) {
                String jsonData = msg.getData().getString(FinalConstant.BACK_INFO);
                try {
                    if (jsonData.equals("1")) {
                        Toast.makeText(Main_Grow_Activity.this, "服务器没有开启或异常", Toast.LENGTH_LONG).show();
                    } else {
                        JSONArray arr = new JSONArray(jsonData); // 收到JSON数组对象解析
                        JSONObject tmp_cmd = (JSONObject) arr.get(0); // 获取json数组对象返回命令
                        String str_cmd = tmp_cmd.getString("cmd");
                        Log.d("DebugTest", "arr_data -- " + arr);
                        int len = 0;
                        len = arr.length();
                        Log.d("debugTest", "len -- " + len);
                        if (len > 0) {

                            if (str_cmd.equals(FinalConstant.FIND_GROW_SUBMIT_SERVER_REBACK)) {

                                JSONObject result_cmd = (JSONObject) arr.get(1);
                                if (result_cmd.getString("RESULT").equals("SUCCESS")) {
                                    Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_LONG).show();

                                    image_show.setImageDrawable(null);
                                    include_image.setVisibility(View.GONE);
                                    camera.setVisibility(View.VISIBLE);
                                }else{
                                    Toast.makeText(getApplicationContext(), "保存失败", Toast.LENGTH_LONG).show();
//                                    image_show.setImageDrawable(null);
//                                    include_image.setVisibility(View.GONE);
//                                    camera.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }

    };
}
