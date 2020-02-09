package com.example.myapplication.fragment;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.example.myapplication.ForgetActivity;
import com.example.myapplication.R;
import com.example.myapplication.SetPasswordActivity;
import com.example.myapplication.view.ScrollVerifyView;

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
 * 拖动拼接图片
 */
public class VerifyDialogFragment extends DialogFragment {
    private String phone;

    public  int code;
    private ProgressDialog pdExecuting;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Bundle bundle =this.getArguments();//得到从Activity传来的数据
        if(bundle!=null){
            phone = bundle.getString("phone");
        }
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.verify_dialog_fragment, null);
        ScrollVerifyView verifyView = view.findViewById(R.id.verify);
        verifyView.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.image2));
        verifyView.setOnVerifyListener(new ScrollVerifyView.OnVerifyListener() {

            @Override
            public void success() {
                // TODO Auto-generated method stub
                getDialog().dismiss();
                //获取代码；开启线程；
                startActivity(new Intent(getContext(),SetPasswordActivity.class));
                new Thread(new RequestThread()).start();
            }

            @Override
            public void fail() {
                // TODO Auto-generated method stub
                Toast.makeText(getContext(), "验证失败，请重试", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
    private class RequestThread implements Runnable {
        @Override
        public void run() {
            //因为选择POST方法，所以new HttpPost对象，构造方法传入处理请求php文件的url
            HttpPost httpRequest = new HttpPost("http://192.168.3.16/ceshi/login_process.php");
            //POST方法的参数列表
            ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            //添加名为codeMsg的参数，值为代码输入框的内容。
            params.add(new BasicNameValuePair("user_phone", phone));
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
                        startActivity(new Intent(getActivity(), SetPasswordActivity.class));
                    }if(code==-1) {
                    Toast.makeText(getActivity(), "数据库连接失败",Toast.LENGTH_SHORT).show();
                    pdExecuting.dismiss();
                }if(code==0) {
                    Toast.makeText(getActivity(), "用户名不存在",Toast.LENGTH_SHORT).show();
                    pdExecuting.dismiss();
                }
                default:
                    pdExecuting.dismiss();
                    break;
            }
        }
    };
}
