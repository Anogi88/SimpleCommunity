package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.view.CountDownTextViewHelper;

public class PaySuccess extends Activity {
    private ImageView im_back;
    private TextView tv_title;
    private TextView tv_paysuccess_time;//开始是3秒
    private Button b;
    private Boolean abc=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_success);

        im_back= findViewById(R.id.im_back);
        im_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        tv_title=  findViewById(R.id.tv_title);
        tv_title.setText("支付成功");
        tv_paysuccess_time=findViewById(R.id.paysuccess_time);

        CountDownTextViewHelper helper_pay=new CountDownTextViewHelper(tv_paysuccess_time, "0", 3, 1);
        helper_pay.setOnFinishListener(new CountDownTextViewHelper.OnFinishListener() {

            @Override
            public void finish() {
                // TODO Auto-generated method stub
                if (abc==false) {
                    PaySuccess.this.finish();
                }
            }
        });
        helper_pay.start();

    }
}
