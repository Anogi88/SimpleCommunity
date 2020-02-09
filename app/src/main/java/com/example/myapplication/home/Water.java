package com.example.myapplication.home;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.PaySuccess;
import com.example.myapplication.R;
import com.example.myapplication.user.SetPayPassword;
import com.example.myapplication.view.PayDailog;

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
 * 水费缴费页面
 */

public class Water extends AppCompatActivity {
    private String phone;
    private SharedPreferences preferences;
    private EditText editText;

    private String password1;
    private String money;
    private Integer temp;

    public  int code;
    public int i;

    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);
        editText=findViewById(R.id.water_money);

        i=1;//代表水费
        //获取登录用户名
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        phone = preferences.getString("phone","");

        Button pay=findViewById(R.id.water_pay);//支付按钮
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                money=editText.getText().toString();
                temp=Integer.parseInt(money);
                new Thread(new RequestThread()).start();
            }
        });
        ImageView back=findViewById(R.id.back);//返回按钮
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private class RequestThread implements Runnable {
        @Override
        public void run() {
            //因为选择POST方法，所以new HttpPost对象，构造方法传入处理请求php文件的url
            HttpPost httpRequest = new HttpPost("http://192.168.43.187/ceshi/set_pay_pwd.php");
            //POST方法的参数列表
            ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            //添加名为codeMsg的参数，值为代码输入框的内容。
            params.add(new BasicNameValuePair("user_phone", phone));
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
                        //启动支付对话框
                        createPayDialog();
                    }if(code==0) {
                    //没有支付密码，跳转到设置支付密码页面
                    alert = null;
                    builder = new AlertDialog.Builder(Water.this);
                    alert = builder.setIcon(R.mipmap.tishi)
                            .setTitle("系统提示：")
                            .setMessage("您还没有设置支付密码，是否去设置支付密码？")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(Water.this, SetPayPassword.class));
                                    finish();
                                }
                            }).create();             //创建AlertDialog对象
                    alert.show();                    //显示对话框

                }
                default:
                    break;
            }
        }
    };
    void createPayDialog() {
        final PayDailog payDialog = new PayDailog(this);
        payDialog.setMoney(temp);
        payDialog.setPasswordCallback(new PayDailog.PasswordCallback() {
            @Override
            public void callback(String password) {
                password1=password;
                new Thread(new RequestThreadfirst()).start();
            }
        });
        payDialog.clearPasswordText();
        payDialog.show();
    }
    private class RequestThreadfirst implements Runnable {
        @Override
        public void run() {
            //因为选择POST方法，所以new HttpPost对象，构造方法传入处理请求php文件的url
            HttpPost httpRequest = new HttpPost("http://192.168.43.187/ceshi/charge_process.php");
            //POST方法的参数列表
            ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            //添加名为codeMsg的参数，值为代码输入框的内容。
            params.add(new BasicNameValuePair("temp",String.valueOf(i)));
            params.add(new BasicNameValuePair("user_phone", phone));
            params.add(new BasicNameValuePair("m_password",password1));
            params.add(new BasicNameValuePair("money",money));
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
                    handler1.sendMessage(msg);
                }else if(responseCode== 400){//说明没有获取到代码执行的返回值
                    Message msg=new Message();
                    msg.obj="Bad Request 访问请求出现语法错误！";
                    msg.what=0;
                    handler1.sendMessage(msg);
                }else if(responseCode== 500){//说明没有获取到代码执行的返回值
                    Message msg=new Message();
                    msg.obj="远程服务器运行发生错误！";
                    msg.what=-1;
                    handler1.sendMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Handler handler1 = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
                case 1://获取到what为1的消息，更新界面显示，不但包括提示信息，还要取消loading状态。
                    if (code== 6) {
                        //跳转到支付成功页面
                        startActivity(new Intent(Water.this, PaySuccess.class));
                        finish();
                    }if(code==0) {
                    //密码错误
                    alert = null;
                    builder = new AlertDialog.Builder(Water.this);
                    alert = builder.setIcon(R.mipmap.tishi)
                            .setMessage("密码错误，请重新输入")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create();             //创建AlertDialog对象
                    alert.show();                    //显示对话框

                }
                default:
                    break;
            }
        }
    };
}
