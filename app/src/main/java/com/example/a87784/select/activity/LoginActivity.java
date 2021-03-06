package com.example.a87784.select.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import com.example.a87784.select.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class LoginActivity extends AppCompatActivity implements OnClickListener {

    private static final String TAG = "LoginActivity";



    private static final String INFORMATION_PORTAL_URL = "http://my.tjut.edu.cn/index.portal";      //信息门户网站
    private static final String INFORMATION_PORTAL_POST_URL = "http://my.tjut.edu.cn/userPasswordValidate.portal";      //信息门户请求网站
    private static final String INFORMATION_PORTAL_CAPTCHA_URL = "http://my.tjut.edu.cn/captchaGenerate.portal";      //信息门户验证码

    private static final int  LOAD_CAPTCHA_OK = 0;      //下载验证码成功
    private static final int  LOGIN_SUCCESS = 1;      //登录成功
    private static final int  LOGIN_FAIL = 2;      //登录失败
    private static final int GET_INFO_SUCCESS = 3;      //抓取信息成功

    private EditText studentIdEdit;
    private EditText passwordEdit;
    private EditText captchaEdit;
    private TextView loading;

    private String studentId;
    private String password;
    private String captcha;
    private String name;
    private String identity;
    private String major;

    private Button signInBtn;
    private ImageView captchaImgView;

    private OkHttpClient client;
    private String firstCookie;
    private String lastCookie;
    private String cookie;

    private String responseTitle;
    private Document doc;




    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LOAD_CAPTCHA_OK:
                    byte[] imgBts = (byte[]) msg.obj;
                    Bitmap captchaBitmap = BitmapFactory.decodeByteArray(imgBts,0,imgBts.length);
                    captchaImgView.setImageBitmap(captchaBitmap);
                    break;
                case LOGIN_SUCCESS:
                    Log.d(TAG, "handleMessage: -----------------------------------2");
                    Log.d(TAG, "handleMessage: ----------------------first    " + firstCookie);
                    Log.d(TAG, "handleMessage: ----------------------last    " + lastCookie);
                    Log.d(TAG, "handleMessage: ----------------------cookie    " + cookie);

                    getInfo();
                    break;
                case LOGIN_FAIL:
                    Toast.makeText(LoginActivity.this,"账号或密码不正确，请重新输入",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "run: -----------------------------------6");
                    break;
                case GET_INFO_SUCCESS:
                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "handleMessage: ------------------------7");
                    saveInfo();
                    enterMainActivity();
                    break;


            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        client = new OkHttpClient();

        initView();
        initListener();
        initData();
    }



    public void login(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody requestBody = new FormBody.Builder()
                        .add("Login.Token1",studentId)
                        .add("Login.Token2",password)
                        .add("captchaField",captcha)
                        .add("goto","http://my.tjut.edu.cn/loginSuccess.portal")
                        .add("gotoOnFail","http://my.tjut.edu.cn/loginFailure.portal")
                        .build();
                Request request = new Request.Builder()
                        .url(INFORMATION_PORTAL_POST_URL)
                        .addHeader("Cookie",firstCookie)
                        .post(requestBody)
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Message message = handler.obtainMessage();
                Log.d(TAG, "run: ------------------------1");
                if(response != null && response.isSuccessful() && !(response.headers("Set-Cookie").isEmpty())) {
                    lastCookie = response.headers("Set-Cookie").get(0).substring(0,response.headers("Set-Cookie").get(0).indexOf(";"));
                    cookie = firstCookie + ";" + lastCookie ;
                    message.what = LOGIN_SUCCESS;
                }else {
                    message.what = LOGIN_FAIL;
                }
                handler.sendMessage(message);
            }
        }).start();
    }



    public void getCaptchaImg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder().url(INFORMATION_PORTAL_CAPTCHA_URL).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: ---------------------------");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        firstCookie = response.headers("Set-Cookie").get(0).substring(0,response.headers("Set-Cookie").get(0).indexOf(";"));
                        //获取验证码图片
                        byte[] bytes = response.body().bytes();
                        Message message = handler.obtainMessage();
                        message.what = LOAD_CAPTCHA_OK;
                        message.obj = bytes;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();

    }


    public boolean checkInfo(String studentId,String password,String captcha){
        if(studentId.isEmpty()||password.isEmpty()||captcha.isEmpty()){
            Toast.makeText(this,"学号或密码或验证码不能为空！",Toast.LENGTH_SHORT).show();
            return false;
        }else if(studentId.length() != 8){
            Toast.makeText(this,"学号长度必须为6！",Toast.LENGTH_SHORT).show();
            return false;
        }else if(password.length() <6 ){
            Toast.makeText(this,"密码长度必须大于6！",Toast.LENGTH_SHORT).show();
            return false;
        }else if(captcha.length() !=4 ){
            Toast.makeText(this,"验证码长度必须为4！",Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }


    public void getInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: -----------------------------------3");
                try {
                    doc = Jsoup.connect(INFORMATION_PORTAL_URL).cookie("Cookie",cookie).post();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                responseTitle = doc.select("title").first().text();
                Log.d(TAG, "run: -------------------------" + responseTitle);
                Message message = handler.obtainMessage();
                Log.d(TAG, "run: -----------------------------------4");
                if("欢迎访问信息门户".equals(responseTitle)){
                    name = doc.select("div.composer ul li").get(0).text().split("，")[0];
                    identity = doc.select("div.composer ul li").select("span").text();
                    major = doc.select("div.composer ul li").get(4).text().split("：")[1];
                    message.what = GET_INFO_SUCCESS;
                }else {
                    message.what = LOGIN_FAIL;
                }
                handler.sendMessage(message);
            }
        }).start();
    }


    public void initView(){
        studentIdEdit = (EditText) findViewById(R.id.studentId);
        passwordEdit = (EditText) findViewById(R.id.password);
        captchaEdit = (EditText)findViewById(R.id.captcha);
        captchaImgView = (ImageView)findViewById(R.id.captchaImg);
        signInBtn = (Button) findViewById(R.id.sign_in_button);
        loading = (TextView)findViewById(R.id.loading);

    }

    public void initListener(){
        captchaImgView.setOnClickListener(this);
        signInBtn.setOnClickListener(this);
    }

    public void initData(){

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.captchaImg:
                getCaptchaImg();
                break;
            case R.id.sign_in_button:
                studentId = studentIdEdit.getText().toString();
                password = passwordEdit.getText().toString();
                captcha = captchaEdit.getText().toString();
                loading.setVisibility(View.VISIBLE);
                if(checkInfo(studentId,password,captcha)){
                    login();
                }
                break;
        }
    }


    /**
     * 保存信息到本地
     */
    public void saveInfo(){
        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putString("cookie",cookie);
        editor.putString("studentId",studentId);
        editor.putString("password",password);
        editor.putString("name",name);
        editor.putString("identity",identity);
        editor.putString("major",major);
        editor.apply();
    }



    public void enterMainActivity(){
        Intent i = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(i);
        this.finish();
    }
}

