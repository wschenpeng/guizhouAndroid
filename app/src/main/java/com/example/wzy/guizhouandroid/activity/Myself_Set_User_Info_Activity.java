package com.example.wzy.guizhouandroid.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wzy.guizhouandroid.R;
import com.example.wzy.guizhouandroid.model.Person;
import com.example.wzy.guizhouandroid.model.PersonService;
import com.example.wzy.guizhouandroid.server.PreferencesUsers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Myself_Set_User_Info_Activity extends Activity {

    private PreferencesUsers preuser;
    private EditText user;
    private EditText psw;
    private Button user_save;
    private PersonService service;
    private String m_user;
    private String m_psw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myself_set_user_info_activity);

        InitView();
    }

    @SuppressLint("WrongViewCast")
    private void InitView() {

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        user = (EditText)findViewById(R.id.user);
        psw = (EditText)findViewById(R.id.psw);
        user_save = (Button)findViewById(R.id.user_save);
        service = new PersonService(this);

        user_save.setOnClickListener(v -> {
            m_user = user.getText().toString();
            m_psw = psw.getText().toString();
            if((m_user.length()!=0)&&(m_psw.length()!=0)){
                preuser.save(m_user,m_psw);
                Toast.makeText(getApplicationContext(),"保存成功", Toast.LENGTH_LONG).show();
                //finish();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                Person person = new Person("修改用户配置",str);
                service.save(person);
            }else {
                Toast.makeText(getApplicationContext(),"编辑框不能为空", Toast.LENGTH_LONG).show();
            }
        });

        GetPara();

    }

    private void GetPara() {
        //获取参数
        preuser = new PreferencesUsers(getApplicationContext());
        Map<String, String> params = preuser.getPreferences();

        String User = params.get("user");
        String pswd = params.get("pswd");
        user.setText(User);
        psw.setText(pswd);
    }
}
