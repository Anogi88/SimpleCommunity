package com.example.myapplication.cloud;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.CommentExpandAdapter;
import com.example.myapplication.model.CommentDetailBean;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

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

public class ArticleDetail extends AppCompatActivity implements View.OnClickListener{

    private SharedPreferences preferences;
    private String phone;
    private String id;
    private String name1,time1,content1,title1;

    private TextView bt_comment,usrename,datetime,title,content,jubao,count;
    private RecyclerView recyclerView;
    private CommentExpandAdapter adapter;
    private List<CommentDetailBean> commentList=new ArrayList<>();
    private BottomSheetDialog dialog;

    private  EditText commentText;

    public  int code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_detail);

        jubao=findViewById(R.id.jubao);



        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        phone = preferences.getString("phone", "");

        //接受MessageFragment传过来的id
        Intent intent=getIntent();
        id=intent.getStringExtra("id");

        initView();
        initArticle();
        initComment();

        LinearLayoutManager layoutManager=new LinearLayoutManager(ArticleDetail.this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        ImageView back=findViewById(R.id.art_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        jubao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArticleDetail.this,TipActivity.class);
                /*传ID在Main2Activity接受*/
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });
    }
    private void initView() {
        usrename=findViewById(R.id.detail_page_userName);
        datetime=findViewById(R.id.detail_page_time);
        title=findViewById(R.id.detail_page_title);
        content=findViewById(R.id.detail_page_story);
        recyclerView =  findViewById(R.id.detail_page_lv_comment);
        bt_comment =  findViewById(R.id.detail_page_do_comment);
        bt_comment.setOnClickListener(this);
        commentList = generateTestData();
        count=findViewById(R.id.reply_null);
    }
    /**
     * by moos on 2018/04/20
     * func:生成测试数据
     * @return 评论数据
     */
    private List<CommentDetailBean> generateTestData(){
        return commentList;
    }

    private  void  initArticle(){
        new Thread(new RequestThread()).start();//请求服务器

    }
   private void initComment() {
        new Thread(new CommentThread()).start();//请求服务器
    }

    private class RequestThread implements Runnable {
        @Override
        public void run() {
            //因为选择POST方法，所以new HttpPost对象，构造方法传入处理请求php文件的url
            HttpPost httpRequest = new HttpPost("http://192.168.43.187/ceshi/second.php");
            //POST方法的参数列表
            ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("id", id));

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
                        JSONObject jsonObject=new JSONObject(result);
                        JSONArray jsonArray=jsonObject.getJSONArray("main");
                        for (int i=0;i<jsonArray.length();i++) {
                            JSONObject jsonObject1=jsonArray.getJSONObject(i);
                            name1 = jsonObject1.getString("phone");
                            content1 = jsonObject1.getString("content");
                            time1 = jsonObject1.getString("time");
                            title1 = jsonObject1.getString("title");
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
                    usrename.setText(name1);
                    datetime.setText(time1);
                    title.setText(title1);
                    content.setText(content1);
                    break;
                case  0:
                    Toast.makeText(ArticleDetail.this,(String)msg.obj, Toast.LENGTH_SHORT).show();

                default:
                    break;
            }
        }
    };
    private class CommentThread implements Runnable {
        @Override
        public void run() {
            //因为选择POST方法，所以new HttpPost对象，构造方法传入处理请求php文件的url
            HttpPost httpRequest = new HttpPost("http://192.168.43.187/ceshi/second.php");
            //POST方法的参数列表
            ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("id", id));

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
                        code=jsonObject.getInt("code");
                        if(code==6){
                        JSONArray jsonArray = jsonObject.getJSONArray("comment");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            String name=obj.getString("user").replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
                            String comment=obj.getString("comment");
                            String time=obj.getString("tim");
                            CommentDetailBean commentDetailBean=new CommentDetailBean(name,comment,time);
                            commentDetailBean.setNickName(obj.getString("user").replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2"));
                            commentDetailBean.setContent(obj.getString("comment"));
                            commentDetailBean.setCreateDate(obj.getString("tim"));
                            CommentDetailBean list=new CommentDetailBean(name,comment,time);
                            commentList.add(list);
                            }
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    handler1.sendMessage(msg);
                }else if(responseCode== 400){//说明没有获取到代码执行的返回值
                    Message msg=new Message();
                    msg.what=0;
                    msg.obj="Bad Request 访问请求出现语法错误！";
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
                    if (code==6){
                        // RecyclerView适配器
                        adapter=new CommentExpandAdapter(ArticleDetail.this,commentList);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setVisibility(View.VISIBLE);
                    }if(code==0) {
                        //无评论
                        count.setVisibility(View.VISIBLE);
                    }
                    break;
                case  0:
                    Toast.makeText(ArticleDetail.this,(String)msg.obj, Toast.LENGTH_SHORT).show();

                default:
                    break;
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.detail_page_do_comment){

            showCommentDialog();
        }
    }

    /**
     * by moos on 2018/04/20
     * func:弹出评论框
     */
    private void showCommentDialog(){
        dialog = new BottomSheetDialog(this);
        View commentView = LayoutInflater.from(this).inflate(R.layout.comment_dialog_layout,null);
        commentText =  commentView.findViewById(R.id.dialog_comment_et);
        final Button bt_comment =  commentView.findViewById(R.id.dialog_comment_bt);
        dialog.setContentView(commentView);
        /**
         * 解决bsd显示不全的情况
         */
        View parent = (View) commentView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        commentView.measure(0,0);
        behavior.setPeekHeight(commentView.getMeasuredHeight());

        bt_comment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String commentContent = commentText.getText().toString().trim();
                if(!TextUtils.isEmpty(commentContent)){

                    //commentOnWork(commentContent);
                    dialog.dismiss();
                    count.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);

                    //new Thread(new CommentThread()).start();//请求服务器
                    CommentDetailBean detailBean = new CommentDetailBean(phone.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2"), commentContent,"刚刚");
                    adapter.addTheCommentData(detailBean);
                    new Thread(new ReplyThread()).start();//请求服务器

                }else {
                    Toast.makeText(ArticleDetail.this,"评论内容不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence) && charSequence.length()>2){
                    bt_comment.setBackgroundColor(Color.parseColor("#FFB568"));
                }else {
                    bt_comment.setBackgroundColor(Color.parseColor("#D8D8D8"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog.show();
    }
    private class ReplyThread implements Runnable {
        @Override
        public void run() {
            //因为选择POST方法，所以new HttpPost对象，构造方法传入处理请求php文件的url
            HttpPost httpRequest = new HttpPost("http://192.168.43.187/ceshi/comment.php");
            //POST方法的参数列表
            ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

            String comment=commentText.getText().toString();

            params.add(new BasicNameValuePair("user_phone",phone));
            params.add(new BasicNameValuePair("id", id));
            params.add(new BasicNameValuePair("comment",comment));

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
                    handler2.sendMessage(msg);
                }else if(responseCode== 400){//说明没有获取到代码执行的返回值
                    Message msg=new Message();
                    msg.what=0;
                    msg.obj="Bad Request 访问请求出现语法错误！";
                    handler2.sendMessage(msg);
                }else if(responseCode== 500){//说明没有获取到代码执行的返回值
                    Message msg=new Message();
                    msg.obj="远程服务器运行发生错误！";
                    msg.what=-1;
                    handler2.sendMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Handler handler2 = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
                case 1://获取到what为1的消息，更新界面显示，不但包括提示信息，还要取消loading状态。
                    if(code==6){
                        Toast.makeText(ArticleDetail.this,"评论成功",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case  0:
                    Toast.makeText(ArticleDetail.this,(String)msg.obj, Toast.LENGTH_SHORT).show();

                default:
                    break;
            }
        }
    };



}
