package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
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
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 注册页面
 */

public class RegisterActivity extends AppCompatActivity {
    // 声明UI对象
    private EditText re_phone ;
    private EditText re_username ;
    private EditText re_age ;
    private RadioButton reMale,reFamale;
    private EditText re_password1 ;

    private DataBase dataBase;
    private SQLiteDatabase db;

    private ProgressDialog pdExecuting;

    public  int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        dataBase = new DataBase(this,"student.db",null,1);
        db = dataBase.getWritableDatabase();

        // 初始化UI对象
        initUI();
        /*
            设置当输入框焦点失去时提示错误信息
            第一个参数指明输入框对象
            第二个参数指明输入数据类型
            第三个参数指明输入不合法时提示信息
         */

        setOnFocusChangeErrMsg(re_phone, "phone", "手机号格式不正确");
        setOnFocusChangeErrMsg(re_password1, "password", "密码必须不少于6位");

        Button btnSubmit = findViewById(R.id.bt_register);//注册按钮
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    // TODO Auto-generated method stub
                    //tv.setText(strResult);
                    //线程运行中更新UI界面，更新loading图标。
                    //TODO: 这里填写正在执行中的图标显示代码：
                    pdExecuting = new ProgressDialog(RegisterActivity.this);
                    pdExecuting.setMessage("正在执行中 ...");
                    pdExecuting.setCancelable(true);
                    pdExecuting.show();
                    //获取代码；开启线程；
                    new Thread(new RequestThread()).start();


            }
        });

        ImageView back=findViewById(R.id.back);//返回按钮
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent1);
                finish();
            }
        });
    }
    private class RequestThread implements Runnable {
        @Override
        public void run() {
            //因为选择POST方法，所以new HttpPost对象，构造方法传入处理请求php文件的url
            HttpPost httpRequest = new HttpPost("http://192.168.43.187/ceshi/register_process.php");
            //POST方法的参数列表
            ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            //添加名为codeMsg的参数，值为代码输入框的内容。
            String phone=re_phone.getText().toString();
            String username=re_username.getText().toString();
            String age=re_age.getText().toString();
            String sex;
            if(reMale.isChecked()){
                sex = reMale.getText().toString();
            }else{
                sex = reFamale.getText().toString();
            }
            String password=re_password1.getText().toString();
            //db.execSQL("insert into student(phone,username,age,sex,password) values(?,?,?,?,?)",
             //       new String[]{phone,username,age,sex,password});
            params.add(new BasicNameValuePair("user_phone", phone ));
            params.add(new BasicNameValuePair("user_name", username ));
            params.add(new BasicNameValuePair("user_age", age));
            params.add(new BasicNameValuePair("user_sex",sex));
            params.add(new BasicNameValuePair("user_password", password));
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
                        code= jsonObject.optInt("code");
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
                        //保存文件名
                        SharedPreferences qw=getSharedPreferences("s1", Context.MODE_PRIVATE);
                        SharedPreferences.Editor e1=qw.edit();
                        String phone=re_phone.getText().toString();
                        String username=re_username.getText().toString();
                        String age=re_age.getText().toString();
                        String password=re_password1.getText().toString();
                        e1.putString("phone",phone);
                        e1.putString("username",username);
                        e1.putString("age",age);
                        e1.putString("password",password);
                        pdExecuting.dismiss();
                        Toast.makeText(RegisterActivity.this, "注册成功",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    }if(code==0){
                        Toast.makeText(RegisterActivity.this, "用户已存在",Toast.LENGTH_LONG).show();
                        pdExecuting.dismiss();
                    }
                default:
                    pdExecuting.dismiss();
                    break;
            }
        }
    };
    // 全屏显示
    private void fullScreenConfig() {
        // 去除ActionBar(因使用的是NoActionBar的主题，故此句有无皆可)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 去除状态栏，如 电量、Wifi信号等
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    // 初始化UI对象
    private void initUI() {
        re_phone = findViewById(R.id.re_phone); // 输入账号
        re_username= findViewById(R.id.re_username); // 输入户主名
        re_age = findViewById(R.id.re_age); // 输入年龄
        reMale=findViewById(R.id.rbMale);
        reFamale=findViewById(R.id.rbFemale);
        re_password1 = findViewById(R.id.re_password1); // 输入密码
    }
    /*
   当输入账号FocusChange时，校验账号是否是中国大陆手机号
   当输入密码FocusChange时，校验密码是否不少于6位
    */
    private void setOnFocusChangeErrMsg(final EditText editText, final String inputType, final String errMsg) {
        editText.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        String inputStr = editText.getText().toString();
                        if (!hasFocus) {
                            if (inputType == "phone") {
                                if (isTelphoneValid(inputStr)) {
                                    editText.setError(null);
                                } else {
                                    editText.setError(errMsg);
                                }
                            }
                            if (inputType == "password") {
                                if (isPasswordValid(inputStr)) {
                                    editText.setError(null);
                                } else {
                                    editText.setError(errMsg);
                                }
                            }
                        }
                    }
                }
        );
    }
    // 校验账号不能为空且必须是中国大陆手机号（宽松模式匹配）
    private boolean isTelphoneValid(String account) {
        if (account == null) {
            return false;
        }
        // 首位为1, 第二位为3-9, 剩下九位为 0-9, 共11位数字
        String pattern = "^[1]([3-9])[0-9]{9}$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(account);
        return m.matches();
    }

    // 校验密码不少于6位
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
