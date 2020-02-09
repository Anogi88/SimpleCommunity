package com.example.myapplication.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ArticleAdapter;
import com.example.myapplication.cloud.ArticleDetail;
import com.example.myapplication.cloud.PublishActivity;
import com.example.myapplication.cloud.SearchCloud;
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
import java.util.Collections;
import java.util.List;

import static com.example.myapplication.ForgetActivity.TAG;


/**
 * 社区页面
 */
public class MessageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private Article article;

    private List<Article> articleList=new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView1;

    private ArticleAdapter articleAdapter;

    private ProgressDialog pdExecuting;


    private  String Msg;
    public  int code;

    public static MessageFragment newInstance() {
        MessageFragment fragment = new MessageFragment();
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 你现在引用的布局文件是R.layout.fragment_message
        View view =  inflater.inflate(R.layout.fragment_message, container, false);
        recyclerView1 = view.findViewById(R.id.recyclerView1);
        swipeRefreshLayout=view.findViewById(R.id.spl);

        Toolbar toolbar = view.findViewById(R.id.message_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        pdExecuting = new ProgressDialog(getActivity());
        pdExecuting.setMessage("加載中 ...");
        pdExecuting.setCancelable(true);
        pdExecuting.show();
        //获取代码；开启线程；
        // 请求服务器
        initArticle();

        // 布局管理器必须有，否则不显示布局
        // No layout manager attached; skipping layout
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        // 最常见的错误，简称 NPE
        // 一般情况都是 recyclerView1 使用到的对象只进行了声明，没有初始化
        //  java.lang.NullPointerException:
        //  Attempt to invoke virtual method 'void androidx.recyclerview.widget.RecyclerView.setLayoutManager
        //  (androidx.recyclerview.widget.RecyclerView$LayoutManager)' on a null object reference
        recyclerView1.setLayoutManager(layoutManager);

        // 下拉刷新控件
        // 因为该类 implements SwipeRefreshLayout.OnRefreshListener
        // 所以只需要在onCreate里注册一下监听器，具体的响应事件可以写到onCreate方法之外
        swipeRefreshLayout.setOnRefreshListener(this);


        return view;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);//加上这句话，menu才会显示出来
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(getActivity(),  SearchCloud.class));
                break;
            case R.id.action_add:
                startActivity(new Intent(getActivity(),  PublishActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    private  void  initArticle(){
        new Thread(new RequestThread()).start();//请求服务器

    }
    private class RequestThread implements Runnable {
        @Override
        public void run() {
            //因为选择POST方法，所以new HttpPost对象，构造方法传入处理请求php文件的url
            HttpPost httpRequest = new HttpPost("http://192.168.43.187/ceshi/first.php");
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
                    pdExecuting.dismiss();
                    articleAdapter = new ArticleAdapter(getActivity(), articleList);
                    Log.d(TAG,String.valueOf(articleAdapter));
                    recyclerView1.setAdapter(articleAdapter);
                    articleAdapter.setOnItemClickListener(new ArticleAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position,String id) {
                            Intent intent = new Intent(getActivity(), ArticleDetail.class);
                            /*传ID在Main2Activity接受*/
                            intent.putExtra("id",id);
                            startActivity(intent);
                        }
                    });
                    break;
                case  0:
                    Toast.makeText(getActivity(),(String)msg.obj, Toast.LENGTH_SHORT).show();


                default:
                    break;
            }
        }
    };



    @Override
    public void onRefresh() {
        // 加载数据（先清空原来的数据）
        articleList.clear();
        // loadBackendData(url);
        initArticle();
        // 打乱顺序（为了确认确实是刷新了）
        Collections.shuffle(articleList);
        // 通知适配器数据已经改变
        articleAdapter.notifyDataSetChanged();
        // 下拉刷新完成
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

}
