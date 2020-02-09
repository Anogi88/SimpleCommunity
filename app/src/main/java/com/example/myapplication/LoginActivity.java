package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.utils.SharedPreUtil;
import com.example.myapplication.utils.useInfoUtils;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    // 声明UI对象
    private Button bt_login;
    private EditText et_account ;
    private EditText et_password ;
    private TextView tv_to_register ;
    private TextView tv_forget_password ;

    private CheckBox remember;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private String account;
    private String password;

    // Log打印的通用Tag
    private final String TAG = "LoginActivity";

    private ProgressDialog pdExecuting;

    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;

    public  int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_account = findViewById(R.id.et_account); // 输入账号
        et_password = findViewById(R.id.et_password); // 输入密码
        tv_to_register = findViewById(R.id.tv_to_register); // 注册
        tv_forget_password = findViewById(R.id.tv_forget_password); // 忘记密码
        remember=findViewById(R.id.ck_remember);//记住密码
        preferences = PreferenceManager.getDefaultSharedPreferences(this);


        boolean isRemember = preferences.getBoolean("remember",false);
        //如果是记住密码，就从preferences中通过key取出用户名和密码
        if(isRemember){
            String phone = preferences.getString("phone","");
            String password = preferences.getString("password","");
            boolean saveInfo= useInfoUtils.saveUserInfo(phone,password);
            et_account.setText(phone);
            et_password.setText(password);
            remember.setChecked(true);
        }

        /*
            设置当输入框焦点失去时提示错误信息
            第一个参数指明输入框对象
            第二个参数指明输入数据类型
            第三个参数指明输入不合法时提示信息
         */
        setOnFocusChangeErrMsg(et_account, "phone", "手机号格式不正确");
        setOnFocusChangeErrMsg(et_password, "password", "密码位数在6-20位之间");



        bt_login = findViewById(R.id.bt_login); //登录按钮
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //tv.setText(strResult);
                //线程运行中更新UI界面，更新loading图标。
                //TODO: 这里填写正在执行中的图标显示代码：
                pdExecuting = new ProgressDialog(LoginActivity.this);
                pdExecuting.setMessage("正在执行中 ...");
                pdExecuting.setCancelable(true);
                pdExecuting.show();
                //获取代码；开启线程；
                new Thread(new RequestThread()).start();
            }
        });

        //返回按钮
        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        //注册按钮
        TextView btnRegister=findViewById(R.id.tv_to_register);
        //注册按钮的监听事件
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it_login_to_register = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(it_login_to_register);
            }
        });

        //找回密码按钮
        TextView btnForget=findViewById(R.id.tv_forget_password);
        btnForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it_login_to_register = new Intent(LoginActivity.this, ForgetActivity.class);
                startActivity(it_login_to_register);
            }
        });

    }
    private class RequestThread implements Runnable {
        @Override
        public void run() {
            //因为选择POST方法，所以new HttpPost对象，构造方法传入处理请求php文件的url
            HttpPost httpRequest = new HttpPost("http://192.168.43.187/ceshi/login_process.php");
            //POST方法的参数列表
            ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            //添加名为codeMsg的参数，值为代码输入框的内容。
            account=et_account.getText().toString();
            password=et_password.getText().toString();
            editor = preferences.edit();
            //登录时如果点击了记住密码，就把用户名和密码保存在preferences中
            if(remember.isChecked()){
                editor.putBoolean("remember",true);
                editor.putString("phone",account);
                editor.putString("password",password);
            }else {
                editor.clear();
            }
            editor.apply();

            params.add(new BasicNameValuePair("user_phone", account));
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
                        pdExecuting.dismiss();
                        SharedPreUtil.setParam(LoginActivity.this, SharedPreUtil.IS_LOGIN, true);
                        SharedPreUtil.setParam(LoginActivity.this, SharedPreUtil.LOGIN_DATA, account);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }if(code==-1) {
                        Toast.makeText(LoginActivity.this, "数据库连接失败",Toast.LENGTH_SHORT).show();
                        pdExecuting.dismiss();
                    }if(code==0) {
                        Toast.makeText(LoginActivity.this, "用户不存在",Toast.LENGTH_SHORT).show();
                        pdExecuting.dismiss();
                    }if(code==1) {
                    Toast.makeText(LoginActivity.this, "密码错误",Toast.LENGTH_SHORT).show();
                    pdExecuting.dismiss();
                }if(code==8){
                    builder = new AlertDialog.Builder(LoginActivity.this);
                    alert = builder.setIcon(R.mipmap.tishi)
                            .setTitle("系统提示：")
                            .setMessage("您的审核还未通过！请联系管理员审核！审核通过后方可进行登录！")
                            .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create();             //创建AlertDialog对象
                    alert.show();

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
