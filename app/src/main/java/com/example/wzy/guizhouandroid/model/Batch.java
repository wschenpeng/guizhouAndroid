//package com.example.wzy.guizhouandroid.model;
//
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.example.wzy.guizhouandroid.server.FinalConstant;
//import com.example.wzy.guizhouandroid.server.HttpReqService;
//import com.example.wzy.guizhouandroid.server.SubmitService;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author wuzhuoyu
// * @date 2018/12/19.
// * email：wuzhuoyu77@163.com
// * description：
// */
//
//public class Batch {
//
//    private static String cmd;
//    private static HashMap<String, Object> reqparams;
//    private static boolean nThread = false;
//    private static String serverIP = "120.79.76.116:7777";
//
//    private Context context (){
//
//    }
//    public static void Batch_List() {
//        InitShow();
//    }
//
//    private static void InitShow() {
//        cmd = FinalConstant.FIND_BATCH_REQUEST_SERVER;
//        reqparams = new HashMap<String, Object>();
//        reqparams.put("cmd", cmd);
//        nThread = true;
//        new Thread(query).start();
//    }
//
//
//    private static Runnable query = new Runnable() {
//        @Override
//        public void run() {
//            SubmitService serviceip;
//            serviceip = new SubmitService(getApplicationContext());
//            Map<String, String> params = serviceip.getPreferences();
//            String url = params.get("serviceip");
//            String path = "";
//            if (url.equals("")) {
//                path = "http://" + serverIP + "/AppService.php";
//            } else {
//                path = "http://" + serverIP + "/AppService.php";
//            }
//            try {
//                String reqdata = HttpReqService.postRequest(path, reqparams, "GB2312");
//                Log.d("debugTest", "reqdata -- " + reqdata);
//                if (reqdata != null) {
//                    // 子线程用sedMessage()方法传递Message对象
//                    Message msg = mhandler.obtainMessage(FinalConstant.QUERY_BACK_DATA);
//                    Bundle bundle = new Bundle();// 创建一个句柄
//                    bundle.putString(FinalConstant.BACK_INFO, reqdata);// 将reqdata填充入句柄
//                    msg.setData(bundle);// 设置一个任意数据值的Bundle对象。
//                    mhandler.sendMessage(msg);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        @SuppressLint("HandlerLeak")
//        private Handler mhandler = new Handler() {
//
//            @Override
//            public void handleMessage(Message msg) {
//                if (msg.what == FinalConstant.QUERY_BACK_DATA) {
//                    String jsonData = msg.getData().getString(FinalConstant.BACK_INFO);
//                    try {
//                        if (jsonData.equals("1")) {
//                            Toast.makeText(getApplicationContext(), "服务器连接失败！", Toast.LENGTH_LONG).show();
//                        } else {
//                            JSONArray arr = new JSONArray(jsonData);
//                            //获取Json数组的第一位
//                            JSONObject tmp_cmd = (JSONObject) arr.get(0);
//                            String str_cmd = tmp_cmd.getString("cmd");
//                            Log.d("debugTest", "arr_data -- " + arr);
//                            int len = 0;
//                            len = arr.length();
//                            Log.d("debugTest", "len -- " + len);
//                            JSONObject result_cmd = (JSONObject) arr.get(0);
//                            if (len > 1) {
//                                if (str_cmd.equals(FinalConstant.FIND_BATCH_REBACK_SERVER)) {
//                                    show(arr);
//                                }
//                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//        };
//    };
//
//    private void show(JSONArray arr) {
//        try {
//            JSONArray arr_data = (JSONArray) arr.get(1);
//            Log.d("hello", "json数组" + arr_data);
//
//            String[] number = new String[arr_data.length()];
//            if (arr_data.length() > 1) {
//
//                if (!arr.get(1).equals(false)) {
//                    HashMap<Integer, HashMap> hs = new HashMap<>();
//                    for (int i = 1; i <= arr_data.length(); i++) {
//                        JSONObject temp = (JSONObject) arr_data.get(i - 1);
//                        HashMap<String, Object> hm = new HashMap<>();
//                        hm.put("pici", temp.getString("pici"));
//                        hs.put(i - 1, hm);
//                    }
//                    Log.d("hello", hs.toString());
//
//                    for (int i = 0; i < arr_data.length(); i++) {
//                        HashMap<String, String> hx = hs.get(i);
//                        number[i] = hx.get("pici");
//                        Log.d("hello", number[i]);
//                        Log.d("hello", "数组：" + Arrays.toString(number));
//                    }
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//}
