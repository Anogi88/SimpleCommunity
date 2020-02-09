package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 重置密码界面
 */

public class SetPasswordActivity extends AppCompatActivity {
    private EditText et_new_psw, et_new_psw_again;
    private Button btn_save;
    private String  newPsw, newPswAgain;

    private String phone;
    private SharedPreferences preferences;

    public  int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        //获取登录用户名
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        phone = preferences.getString("phone","");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();

        ImageView back=findViewById(R.id.back);//返回按钮
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(SetPasswordActivity.this, ForgetActivity.class);
                startActivity(intent1);
                SetPasswordActivity.this.finish();
            }
        });
    }
    /**
     * 获取界面控件并处理相关控件的点击事件
     */
    private void init() {
        et_new_psw =  findViewById(R.id.et_new_psw);
        et_new_psw_again =  findViewById(R.id.et_new_psw_again);
        btn_save = findViewById(R.id.btn_save);
        //保存按钮的点击事件
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEditString();
                if (TextUtils.isEmpty(newPsw)) {
                    Toast.makeText(SetPasswordActivity.this, "请输入新密码", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(newPswAgain)) {
                    Toast.makeText(SetPasswordActivity.this, "请再次输入新密码", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!newPsw.equals(newPswAgain)) {
                    Toast.makeText(SetPasswordActivity.this, "两次输入的新密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    //获取代码；开启线程；
                    new Thread(new RequestThread()).start();
                }
            }
        });
    }
    /**
     * 获取控件上的字符串
     */
    private void getEditString() {
        newPsw = et_new_psw.getText().toString().trim();
        newPswAgain = et_new_psw_again.getText().toString().trim();
    }
    private class RequestThread implements Runnable {
        @Override
        public void run() {
            //因为选择POST方法，所以new HttpPost对象，构造方法传入处理请求php文件的url
            HttpPost httpRequest = new HttpPost("http://192.168.43.187/ceshi/login_process.php");
            //POST方法的参数列表
            ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

            params.add(new BasicNameValuePair("user_phone", phone));
            params.add(new BasicNameValuePair("user_password", newPsw));
            //这里判断是否要保存为特定文件到服务器目录中。
            try {
                //设置请求实体，设定了参数列表
                httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //执行请求,等待服务器返回结果
                HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
                //log出http返回报文头
                Log.e("status", httpResponse.getStatusLine().toString());
                //判断返回码是否为200，200表示请求成功
                int responseCode=httpResponse.getStatusLine().getStatusCode();
                if (responseCode== 200) {
                    //获取返回字符串
                    //old code:   strResult = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
                    //以消息形式返回给Handler类，从而更新UI界面。
                    Message msg=new Message();
                    msg.what=1;
                    String result= EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        code= jsonObject.optInt("code");//接收的值
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    handler.sendMessage(msg);
                }else if(responseCode== 400){//说明没有获取到代码执行的返回值
                    Message msg=new Message();
                    msg.obj="Bad Request 访问请求出现语法错误！";
                    msg.what=0;
                    handler.sendMessage(msg);
                }else if(responseCode== 500){//说明没有获取到代码执行的返回值
                    Message msg=new Message();
                    msg.obj="远程服务器运行发生错误！";
                    msg.what=-1;
                    handler.sendMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
                case 1://获取到what为1的消息，更新界面显示，不但包括提示信息，还要取消loading状态。
                    if (code== 6) {
                        startActivity(new Intent(SetPasswordActivity.this, MainActivity.class));
                    }if(code==-1) {
                    Toast.makeText(SetPasswordActivity.this, "数据库连接失败",Toast.LENGTH_SHORT).show();
                }if(code==0) {
                    Toast.makeText(SetPasswordActivity.this, "用户不存在",Toast.LENGTH_SHORT).show();
                }if(code==1) {
                    Toast.makeText(SetPasswordActivity.this, "密码错误",Toast.LENGTH_SHORT).show();
                }
                default:

                    break;
            }
        }
    };
}
