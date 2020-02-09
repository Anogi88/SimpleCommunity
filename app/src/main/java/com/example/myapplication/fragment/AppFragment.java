package com.example.myapplication.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.adapter.NoticeAdapter;
import com.example.myapplication.home.ComplainActivity;
import com.example.myapplication.home.NoticeActivity;
import com.example.myapplication.home.Notice_detail;
import com.example.myapplication.home.PaymentActivity;
import com.example.myapplication.GlideImage;
import com.example.myapplication.R;
import com.example.myapplication.home.RepairActivity;
import com.example.myapplication.home.SearchActivity;
import com.example.myapplication.model.Notice;
import com.example.myapplication.view.UpView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

/**
 * 首页
 */
public class AppFragment  extends Fragment implements View.OnClickListener {
    private String TAG="打印";
    private Context context;
    private Banner banner;
    //公告
    private UpView vf;
    private List<Notice> noticeData = new ArrayList<>();
    private NoticeAdapter data;


    //轮播图集合
    Integer[] images = {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4, R.drawable.image5};
    public static AppFragment newInstance() {
        AppFragment fragment = new AppFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view =  inflater.inflate(R.layout.fragment_app, container, false);
       context=getActivity();

        initView(view);
        new Thread(new RequestThread()).start();//请求服务器

       return view;
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
                            String id=jsonObject.getString("id");
                            String title=jsonObject.getString("title");
                            Notice notice=new Notice(id,title);
                            notice.setId(jsonObject.getString("id"));
                            notice.setTitle(jsonObject.getString("title"));
                            Notice notice1=new Notice(id,title);
                            noticeData.add(notice1);
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
                    data=new NoticeAdapter(noticeData);
                    setData();
                case  0:
                    Toast.makeText(getActivity(),(String)msg.obj, Toast.LENGTH_SHORT).show();


                default:
                    break;
            }
        }
    };


    private void setData() {
        List<View> views = new ArrayList<>();
        for (int i = 0; i < data.size(); i= i+2) { //一次遍历两条数据
            final int position = i;
            View v = View.inflate(getActivity(),R.layout.item_notice,null);
            TextView tv1 = v.findViewById(R.id.tv1);
            TextView tv2 = v.findViewById(R.id.tv2);
            //设置监听

            v.findViewById(R.id.rl).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), Notice_detail.class);
                    //传ID在Main2Activity接受//
                    Notice notice3=noticeData.get(position);
                    intent.putExtra("id",notice3.getId());
                    startActivity(intent);
                    //Toast.makeText(getActivity(), getId() + "你点击了" + noticeData.get(position).toString(), Toast.LENGTH_SHORT).show();
                }
            });

              //设置监听

            v.findViewById(R.id.rl2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), Notice_detail.class);
                    //传ID在Main2Activity接受//
                    Notice notice2=noticeData.get(position+1);
                    intent.putExtra("id",notice2.getId());
                    startActivity(intent);
                   //Toast.makeText(getActivity(), position + "你点击了" + noticeData.get(position + 1), Toast.LENGTH_SHORT).show();
                }
            });
            Notice notice1=noticeData.get(i);
            tv1.setText(notice1.getTitle());
            if (noticeData.size() > i+1){
                Notice notice = noticeData.get(i+1);
                tv2.setText( notice.getTitle());
            }else {
                tv2.setVisibility(View.GONE);
            }
            views.add(v);
        }
        vf.setViews(views);
    }

    private void initView(View view) {
        banner = view.findViewById(R.id.banner);
       view.findViewById(R.id.ll_search).setOnClickListener(this);
       view.findViewById(R.id.ll_pay).setOnClickListener(this);
       view.findViewById(R.id.ll_complain).setOnClickListener(this);
       view.findViewById(R.id.ll_repair).setOnClickListener(this);
       view.findViewById(R.id.ll_notice).setOnClickListener(this);
        vf = view.findViewById(R.id.vf);
        initBanner();
    }

    private void initBanner() {

        //初始化banner
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        banner.setImageLoader(new GlideImage());
        //设置图片集合

        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(1500);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < images.length; i++) {
            list.add(images[i]);
        }
        banner.setImages(list);

        banner.start();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_search:
                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
            case R.id.ll_pay:
                startActivity(new Intent(getActivity(), PaymentActivity.class));
                break;
            case R.id.ll_complain:
                startActivity(new Intent(getActivity(), ComplainActivity.class));
                break;
            case R.id.ll_repair:
                startActivity(new Intent(getActivity(), RepairActivity.class));
                break;
            case R.id.ll_notice:
                startActivity(new Intent(getActivity(), NoticeActivity.class));
                break;
        }
    }


}
