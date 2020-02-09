package com.example.myapplication.cloud;

import androidx.appcompat.app.AppCompatActivity;

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

import com.example.myapplication.R;
import com.example.myapplication.adapter.NoticeAdapter;
import com.example.myapplication.model.Notice;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 举报页面
 */

public class TipActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private String phone;
    private String id;

    private EditText text;
    private Button jb;

    public  int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip);

        //接受MessageFragment传过来的id
        Intent intent=getIntent();
        id=intent.getStringExtra("id");

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        phone = preferences.getString("phone", "");

        text=findViewById(R.id.jb_text);
        jb=findViewById(R.id.bt_jb);
        jb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new RequestThread()).start();//请求服务器
            }
        });

        ImageView back=findViewById(R.id.tip_back);//返回按钮
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
            HttpPost httpRequest = new HttpPost("http://192.168.43.187/ceshi/report.php");
            //POST方法的参数列表
            ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("user_phone", phone));
            params.add(new BasicNameValuePair("id", id));
            params.add(new BasicNameValuePair("reason", text.getText().toString()));

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
                    msg.what=0;
                    msg.obj="Bad Request 访问请求出现语法错误！";
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
                   if(code==6){
                       Toast.makeText(TipActivity.this,"投诉成功", Toast.LENGTH_LONG).show();
                   }
                case  0:
                    Toast.makeText(TipActivity.this,(String)msg.obj, Toast.LENGTH_SHORT).show();


                default:
                    break;
            }
        }
    };
}
