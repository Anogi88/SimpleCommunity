package com.example.myapplication.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.util.List;

/**
 * 公告页面
 */

public class NoticeActivity extends AppCompatActivity {
    private List<Notice> noticeList=new ArrayList<>();

    private Notice notice;
    private TextView mtvCoTwo;
    private TextView mtvTeTwo;

    public  int code;
    private NoticeAdapter adapter;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        mtvCoTwo=findViewById(R.id.notice_content);

        initNotice();
        recyclerView=findViewById(R.id.notice_recyle);
        LinearLayoutManager layoutManager=new LinearLayoutManager(NoticeActivity.this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        ImageView back=findViewById(R.id.back);//返回按钮
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private  void  initNotice(){
        new Thread(new RequestThread()).start();//请求服务器

    }
    private class RequestThread implements Runnable {
        @Override
        public void run() {
            //因为选择POST方法，所以new HttpPost对象，构造方法传入处理请求php文件的url
            HttpPost httpRequest = new HttpPost("http://192.168.43.187/ceshi/notice.php");
            //POST方法的参数列表
            ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

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
                        JSONArray jsonArray=new JSONArray(result);
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject=jsonArray.getJSONObject(i);
                            String id=jsonObject.optString("id");
                            String title=jsonObject.optString("title");
                            String content=jsonObject.optString("content");
                            String datetime=jsonObject.optString("time");
                            Notice notice=new Notice(id,title,content,datetime);
                            notice.setId(jsonObject.optString("id"));
                            notice.setTitle(jsonObject.optString("title"));
                            notice.setContent(jsonObject.optString("content"));
                            notice.setDatetime(jsonObject.optString("time"));
                            Notice notice1=new Notice(id,title,content,datetime);
                            noticeList.add(notice1);
                        }
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
                    adapter=new NoticeAdapter(noticeList);
                    recyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener(new NoticeAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position,String id) {
                            Intent intent = new Intent(NoticeActivity.this, Notice_detail.class);
                            /*传ID在Main2Activity接受*/
                            intent.putExtra("id",id);
                            startActivity(intent);
                        }
                    });
                    break;
                case  0:
                    Toast.makeText(NoticeActivity.this,(String)msg.obj, Toast.LENGTH_SHORT).show();


                default:
                    break;
            }
        }
    };



}
