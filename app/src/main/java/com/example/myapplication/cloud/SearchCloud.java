package com.example.myapplication.cloud;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ArticleAdapter;
import com.example.myapplication.model.Article;
import com.example.searchview.ICallBack;
import com.example.searchview.SearchView;
import com.example.searchview.bCallBack;

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

public class SearchCloud extends AppCompatActivity {
    //1. 初始化搜索框变量
    private SearchView searchView;
    private String searchtext;

    private List<Article> articleList=new ArrayList<>();
    private RecyclerView recyclerView1;

    public ArticleAdapter articleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_cloud);
        searchView =  findViewById(R.id.search_view);

        recyclerView1 = findViewById(R.id.recyclerView2);


        LinearLayoutManager layoutManager=new LinearLayoutManager(SearchCloud.this,LinearLayoutManager.VERTICAL,false);
        recyclerView1.setLayoutManager(layoutManager);

        // 4. 设置点击搜索按键后的操作（通过回调接口）
        // 参数 = 搜索框输入的内容
        searchView.setOnClickSearch(new ICallBack() {
            @Override
            public void SearchAciton(String string) {
               searchtext=string;
                new Thread(new RequestThread()).start();//请求服务器
            }
        });

        // 5. 设置点击返回按键后的操作（通过回调接口）
        searchView.setOnClickBack(new bCallBack() {
            @Override
            public void BackAciton() {
                finish();
            }
        });

    }
    private class RequestThread implements Runnable {
        @Override
        public void run() {
            //因为选择POST方法，所以new HttpPost对象，构造方法传入处理请求php文件的url
            HttpPost httpRequest = new HttpPost("http://192.168.43.187/ceshi/comsearch.php");
            //POST方法的参数列表
            ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

            params.add(new BasicNameValuePair("searchtext", searchtext));

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
                            String id=jsonObject.getString("id");
                            String title=jsonObject.getString("title");
                            String username=jsonObject.getString("username").replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
                            String createtime=jsonObject.getString("time");
                            Article article=new Article(id,title,username,createtime);
                            article.setTitle(jsonObject.getString("title"));
                            article.setNickName(jsonObject.getString("username").replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2"));
                            article.setCreateTime(jsonObject.getString("time"));
                            Article article1=new Article(id,title,username,createtime);
                            articleList.add(article1);
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
                    // RecyclerView适配器
                    articleAdapter = new ArticleAdapter(SearchCloud.this, articleList);
                    recyclerView1.setAdapter(articleAdapter);
                    articleAdapter.setOnItemClickListener(new ArticleAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position, String id) {
                            Intent intent = new Intent(SearchCloud.this, ArticleDetail.class);
                            /*传ID在Main2Activity接受*/
                            intent.putExtra("id",id);
                            startActivity(intent);
                        }
                    });
                    break;
                case  0:
                    Toast.makeText(SearchCloud.this,(String)msg.obj, Toast.LENGTH_SHORT).show();


                default:
                    break;
            }
        }
    };
}
