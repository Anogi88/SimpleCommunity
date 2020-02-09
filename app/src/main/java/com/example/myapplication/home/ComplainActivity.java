package com.example.myapplication.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.lzy.imagepicker.bean.ImageItem;

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
 * 投诉页面
 */

public class ComplainActivity extends AppCompatActivity {

    private EditText ts_address;
    private EditText ts_phone;
    private EditText ts_message;
    private EditText ts_title;
    private Button tousu;

    private SharedPreferences preferences;
    private String use_phone;

    private Context mContext;

    public  int code;

    private ArrayList<ImageItem> pathList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        use_phone = preferences.getString("phone","");


        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.layout_titlebar);

        mContext = ComplainActivity.this;

        ts_address=findViewById(R.id.ts_address);
        ts_phone=findViewById(R.id.ts_phone);
        ts_message=findViewById(R.id.ts_message);
        ts_title=findViewById(R.id.ts_title);
        tousu=findViewById(R.id.btn_touusu);


        tousu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ts_address==null){
                    Toast.makeText(ComplainActivity.this,"地址不能为空", Toast.LENGTH_SHORT).show();
                }if(ts_phone==null){
                    Toast.makeText(ComplainActivity.this,"电话后面不能为空", Toast.LENGTH_SHORT).show();
                }if(ts_message==null){
                    Toast.makeText(ComplainActivity.this,"投诉内容不能为空", Toast.LENGTH_SHORT).show();
                }if(ts_title==null){
                    Toast.makeText(ComplainActivity.this,"标题不能为空", Toast.LENGTH_SHORT).show();
                }else {
                    new Thread(new RequestThread()).start();

                }

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
            HttpPost httpRequest = new HttpPost("http://192.168.43.187/ceshi/complain_process.php");
            //POST方法的参数列表
            ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            //添加名为codeMsg的参数，值为代码输入框的内容。

            String address=ts_address.getText().toString();
            String  complain_phone=ts_phone.getText().toString();
            String title=ts_title.getText().toString();
            String  message=ts_message.getText().toString();

            params.add(new BasicNameValuePair("user_phone",use_phone));
            params.add(new BasicNameValuePair("address", address));
            params.add(new BasicNameValuePair("phone", complain_phone));
            params.add(new BasicNameValuePair("content", title));
            params.add(new BasicNameValuePair("title", message));

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
                        Toast.makeText(ComplainActivity.this, "发布成功",Toast.LENGTH_SHORT).show();
                        ts_address.setText(null);
                        ts_phone.setText(null);
                        ts_message.setText(null);
                        ts_title.setText(null);
                    }if(code==0) {
                    Toast.makeText(ComplainActivity.this, "失败",Toast.LENGTH_SHORT).show();
                }
                default:
                    break;
            }
        }
    };


}
