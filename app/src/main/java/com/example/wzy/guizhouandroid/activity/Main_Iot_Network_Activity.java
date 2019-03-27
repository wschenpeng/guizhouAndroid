package com.example.wzy.guizhouandroid.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wzy.guizhouandroid.R;
import com.example.wzy.guizhouandroid.model.Person;
import com.example.wzy.guizhouandroid.model.PersonService;
import com.example.wzy.guizhouandroid.server.PreferencesService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Main_Iot_Network_Activity extends Activity {

    private Button btn_parasave;
    private PreferencesService preservice;
    /**
     * <p>服务器地址</p>
     */
    private EditText m_serviceIP;

    /**
     * <p>视频IP</p>
     */
    private EditText m_vedioIP;
    /**
     * <p>视频端口</p>
     */
    private EditText m_vedioPort;
    /**
     * <p>视频登陆用户名</p>
     */
    private EditText m_vedioUser;
    /**
     * <p>视频登陆密码</p>
     */
    private EditText m_vedioPsw;
    private TextView back;
    private PersonService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_iot_network_activity);
        btn_parasave = (Button) findViewById(R.id.paraSave);
        service = new PersonService(this);
        btn_parasave.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String serviceIP = m_serviceIP.getText().toString();   //获取编辑框内容

                String vedioIP = m_vedioIP.getText().toString();
                String vedioPort = m_vedioPort.getText().toString();
                String vedioUser = m_vedioUser.getText().toString();
                String vedioPsw = m_vedioPsw.getText().toString();
                if((serviceIP.length()!=0)&&(vedioIP.length()!=0)
                        &&(vedioPort.length()!=0)&&(vedioUser.length()!=0)&&(vedioPsw.length()!=0)){
                    preservice.save(serviceIP, vedioIP, vedioPort, vedioUser, vedioPsw);
                    Toast.makeText(getApplicationContext(),"保存成功", Toast.LENGTH_LONG).show();
                    //startActivity(new Intent(ParaActivity.this, VideoActivity.class));
                    SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String str = formatter.format(curDate);
                    Person person = new Person("修改网络配置",str);
                    service.save(person);
                }else{
                    Toast.makeText(getApplicationContext(),"编辑框不能为空", Toast.LENGTH_LONG).show();
                }
            };
        });
        m_serviceIP = (EditText) findViewById(R.id.serviceIP);

        m_vedioIP = (EditText) findViewById(R.id.vedioIP);
        m_vedioPort = (EditText) findViewById(R.id.vedioPort);
        m_vedioUser = (EditText) findViewById(R.id.vedioUser);
        m_vedioPsw = (EditText) findViewById(R.id.vedioPsw);
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        GetPara();
    }
    private void GetPara() {
        //获取参数
        preservice = new PreferencesService(getApplicationContext());
        Map<String, String> params = preservice.getPreferences();

        String serviceIP = params.get("serviceIP");
        Log.i("serverip", serviceIP+"------2");
        String vedioIP = params.get("vedioIP");
        String vedioPort = params.get("vedioPort");
        String vedioUser = params.get("vedioUser");
        String vedioPsw = params.get("vedioPsw");
        m_serviceIP.setText(serviceIP);

        m_vedioIP.setText(vedioIP);
        m_vedioPort.setText(vedioPort);
        m_vedioUser.setText(vedioUser);
        m_vedioPsw.setText(vedioPsw);
    };

}
