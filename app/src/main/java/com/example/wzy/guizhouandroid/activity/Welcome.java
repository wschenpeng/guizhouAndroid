package com.example.wzy.guizhouandroid.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wzy.guizhouandroid.R;
import com.example.wzy.guizhouandroid.model.Person;
import com.example.wzy.guizhouandroid.model.PersonService;
import com.example.wzy.guizhouandroid.server.FinalConstant;
import com.example.wzy.guizhouandroid.server.NetReceiver;
import com.example.wzy.guizhouandroid.server.PreferencesUsers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Welcome extends Activity implements View.OnClickListener {

    private View welcome = null;
    private SeekBar seekBar = null;

    private View main = null;
    private View passwordError = null;

    private TextView exit = null;
    private EditText username = null;
    private EditText password = null;
    private CheckBox cbPW ;
    private SharedPreferences sp;
    private TextView restChances = null;

    private Button login = null;
    private Button btnTryAgainYes = null;
    private Button btnTryAgainNo = null;

    private String name = "";
    private String pd = "";

    private int cnt = 0;

    private static final int totalms = 4000;
    private int current = 0;
    public static boolean authFlag = false;
    /**
     * <p>读取用户配置</p>
     */
    private PreferencesUsers preUser;
    private String user = "";
    private String pswd = "";
    private String flag="";
    NetReceiver mReceiver = new NetReceiver();
    IntentFilter mFilter = new IntentFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.welcome_activity);
        //广播监听网络状态，动态注册
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, mFilter);

        //如果网络没有连接，打开系统设置页面
        if(!isConn(getApplicationContext())){
            setNetworkMethod(Welcome.this);
        }
        initViews();

        seekThread.start();
        current = 800;
    }
    /*
     * 判断网络连接是否已开
     *true 已打开  false 未打开
     * */
    public static boolean isConn(Context context){
        boolean bisConnFlag=false;
        ConnectivityManager conManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = conManager.getActiveNetworkInfo();
        if(network!=null){
            bisConnFlag=conManager.getActiveNetworkInfo().isAvailable();
        }
        return bisConnFlag;
    };
    /*
     * 打开设置网络界面
     * */
    public static void setNetworkMethod(final Context context){
        //提示对话框
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("提示")
                .setIcon(R.drawable.notice)
                .setMessage("网络连接没有打开,请检查网络连接！")
                .setPositiveButton("确定", null).show();
    };
    private void initViews() {
        //android添加第三方字体
//        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/SIMLI.TTF");	//fonts默认路径为assets文件夹下
//        ((TextView) this.findViewById(R.id.icon)).setTypeface(typeface);
//        Typeface typeface2 = Typeface.createFromAsset(getAssets(), "fonts/ziti2.ttf");
//        ((TextView) this.findViewById(R.id.subTitle)).setTypeface(typeface2);
//        Typeface typeface3 = Typeface.createFromAsset(getAssets(), "fonts/SIMLI.TTF");	//fonts默认路径为assets文件夹下
//        ((TextView) this.findViewById(R.id.app_icon)).setTypeface(typeface3);

        welcome = (View) this.findViewById(R.id.welcome);
        seekBar = (SeekBar) this.findViewById(R.id.my_seekbar);
        sp = this.getSharedPreferences("userdata", Context.MODE_PRIVATE);
        main = (View) this.findViewById(R.id.main);

        exit = (TextView) this.findViewById(R.id.exit);
        exit.setOnClickListener(this);

        restChances = (TextView) this.findViewById(R.id.restChances);

        username = (EditText) this.findViewById(R.id.username);
        username.setFocusableInTouchMode(true);
        password = (EditText) this.findViewById(R.id.password);
        password.setFocusableInTouchMode(true);
        cbPW =(CheckBox) findViewById(R.id.cbPW);
        login = (Button) this.findViewById(R.id.login);
        login.setOnClickListener(this);
        if(sp.getBoolean("IsCheck", true)){
            //设置默认是记录密码状态
            cbPW.setChecked(true);
            username.setText(sp.getString("username", ""));
            password.setText(sp.getString("password", ""));
        }

        passwordError = (View) this.findViewById(R.id.pdwrong);
        passwordError.getBackground().setAlpha(89);

        btnTryAgainYes = (Button) this.findViewById(R.id.btnTryAgainYes);
        btnTryAgainYes.setOnClickListener(this);
        btnTryAgainNo = (Button) this.findViewById(R.id.btnTryAgainNo);
        btnTryAgainNo.setOnClickListener(this);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exit:
                //this.finish();
                dialog_Exit(Welcome.this);
                break;
            case R.id.login:
                name = username.getText().toString();
                pd = password.getText().toString();

                if(!name.equals("") && !pd.equals("")) {
                    //获取参数
                    preUser = new PreferencesUsers(getApplicationContext());
                    Map<String, String> params = preUser.getPreferences();
                    user = params.get("user");
                    pswd = params.get("pswd");
                    Log.d("用户名", user);
                    Log.d("密码", pswd);
                    if(user.equals("")){
                        user = FinalConstant.USERNAME;
                        pswd = FinalConstant.PASSWORD;
                        preUser.save(user, pswd);
                    }
                    if(name.equals(user) && pd.equals(pswd))
                    {
                        authFlag = AUTH();
                        startActivity(new Intent(this, MainActivity.class));
                        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
                        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                        String str = formatter.format(curDate);
                        PersonService service = new PersonService(this);
                        Person person = new Person("用户名为"+user+"登录",str);
                        service.save(person);

                        //登录成功和记住密码框为选中状态才保存用户信息
                        if(cbPW.isChecked())
                        {
                            //记住用户名、密码、
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("username", name);
                            editor.putString("password",pd);
                            editor.commit();
                        }
                        this.finish();
                    } else {
                        cnt += 1;
                        if (cnt != 4) {
                            passwordError.setVisibility(View.VISIBLE);
                        } else {
                            this.finish();
                        }
                    }
                } else {
                    Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnTryAgainNo:
                this.finish();
                break;
            case R.id.btnTryAgainYes:
                passwordError.setVisibility(View.GONE);
                if (cnt == 1) {
                    restChances.setText("您还有3次尝试的机会，请谨慎！");
                }
                if (cnt == 2) {
                    restChances.setText("您还有2次尝试的机会，请谨慎！");
                }
                if (cnt == 3) {
                    restChances.setText("您还有1次尝试的机会，请谨慎！");
                }
                break;
        }

    }
    public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
        if (cbPW.isChecked()) {
            System.out.println("记住密码已选中");
            sp.edit().putBoolean("IsCheck", true).commit();
        }else {
            System.out.println("记住密码没有选中");
            sp.edit().putBoolean("IsCheck", false).commit();
        }
    }
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    seekBar.setProgress(current*100/totalms);
                    break;
                case 2:
                    welcome.setVisibility(View.GONE);
                    main.setVisibility(View.VISIBLE);
                    break;
            }
        }

    };

    public boolean AUTH (){
        if(name.equals(user))
        {
            authFlag = true;
        }
        else
        {
            authFlag = false;
        }

        return authFlag;
    }

    private Thread seekThread = new Thread(new Runnable(){

        @Override
        public void run() {
            while (current < totalms) {
                current += 50;
                mHandler.sendEmptyMessage(1);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (current == totalms) {
                mHandler.sendEmptyMessage(2);
            }
        }

    });
    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    };
    public static void dialog_Exit(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("确定要退出吗?");
        builder.setTitle("提示");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("确认",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        android.os.Process.killProcess(android.os.Process
                                .myPid());
                    }
                });

        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    };
}
