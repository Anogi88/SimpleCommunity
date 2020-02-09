package com.example.myapplication.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.Article;

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
 * 水费详情页
 */

public class WaterDetail extends AppCompatActivity {

    private TextView water_bill;//余额
    private TextView water_above;//总费用
    private TextView water_price;//水的单价
    private TextView water_count;//用水吨数
    private Button select;

    private SharedPreferences preferences;
    private String phone;

    private Spinner spinner;
    private boolean one_selected = false;

    private String temp1;
    private String temp2;
    private String temp3;
    private String temp4;
    private String month;

    public  int code;
    public int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_detail);

        water_above=findViewById(R.id.water_above);
        water_bill=findViewById(R.id.water_bill);
        water_count=findViewById(R.id.water_count);
        water_price=findViewById(R.id.water_price);
        select=findViewById(R.id.water_select);

        i=1;//代表水费
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        phone = preferences.getString("phone","");

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new RequestThread()).start();//请求服务器
                LinearLayout linearLayout=findViewById(R.id.detail);
                linearLayout.setVisibility(v.VISIBLE);
            }
        });
        spinner=findViewById(R.id.spin_two);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (parent.getId()) {
                    case R.id.spin_two:
                        if (one_selected) {
                            month = parent.getItemAtPosition(position).toString();
                        } else one_selected = true;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
            HttpPost httpRequest = new HttpPost("http://192.168.43.187/ceshi/bill.php");
            //POST方法的参数列表
            ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            //添加名为codeMsg的参数，值为代码输入框的内容。

            params.add(new BasicNameValuePair("user_phone",phone));
            params.add(new BasicNameValuePair("month",month));
            params.add(new BasicNameValuePair("type",String.valueOf(i)));

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
                        JSONObject water=new JSONObject(result);
                        //解析并显示
                        code= water.optInt("code");//接收的值
                        temp1=water.getString("wateruse");//使用数量
                        temp2=water.getString("watermoney");//使用金额
                        temp3=water.getString("watercharge");//单价
                        temp4=water.getString("waterbank");//余额

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
                    if(code==1){
                        water_bill.setText(temp4);
                        water_above.setText(temp2);
                        water_count.setText(temp1);
                        water_price.setText(temp3);
                        //Toast.makeText(WaterDetail.this,"查询成功", Toast.LENGTH_SHORT).show();
                    }
                case  0:
                    //Toast.makeText(WaterDetail.this,"查询失败", Toast.LENGTH_SHORT).show();

                default:
                    break;
            }
        }
    };
}
