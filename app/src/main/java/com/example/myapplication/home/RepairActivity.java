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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myapplication.R;

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
 * 报修页面
 */
public class RepairActivity extends AppCompatActivity {
    private EditText bxaddress;
    private EditText bxphone;
    private EditText bxmessage;
    private Button submit;

    private Spinner spin_one;
    private boolean one_selected = false;

    private SharedPreferences preferences;
    private String phone;

    private String temp;
    private String temp1;
    private String sInfo;
    private String area;

    public  int code;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.layout_titlebar);

        mContext = RepairActivity.this;

        bxaddress = findViewById(R.id.bx_address);
        bxphone = findViewById(R.id.bx_phone);
        bxmessage=findViewById(R.id.bx_message);
        submit = findViewById(R.id.bx_submit);
        spin_one=findViewById(R.id.spin_one);
        spin_one.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                               @Override
                                               public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                   switch (parent.getId()) {
                                                       case R.id.spin_one:
                                                           if (one_selected) {
                                                               area = parent.getItemAtPosition(position).toString();
                                                           } else one_selected = true;
                                                           break;
                                                   }
                                               }

                                               @Override
                                               public void onNothingSelected(AdapterView<?> parent) {

                                               }
                                           });

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        phone = preferences.getString("phone", "");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // uploadImage(selImageList);
                //获取代码；开启线程；
                new Thread(new RequestThread()).start();
            }
        });

        ImageView back = findViewById(R.id.back);//返回按钮
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

           String address=bxaddress.getText().toString();
           String  repair_phone=bxphone.getText().toString();
           String  message=bxmessage.getText().toString();

           params.add(new BasicNameValuePair("user_phone",phone));
           params.add(new BasicNameValuePair("address", address));
           params.add(new BasicNameValuePair("phone", repair_phone));
           params.add(new BasicNameValuePair("content", message));
           params.add(new BasicNameValuePair("title", area));

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
                        Toast.makeText(RepairActivity.this, "发布成功",Toast.LENGTH_SHORT).show();
                    }if(code==0) {
                    Toast.makeText(RepairActivity.this, "失败",Toast.LENGTH_SHORT).show();
                }
                default:
                    break;
            }
        }
    };
}
