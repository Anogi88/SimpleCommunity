package com.example.myapplication.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.myapplication.R;
import com.example.myapplication.fragment.MineFragment;

/**
 * 设置页面
 */

public class Setting extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        findViewById(R.id.pay_password).setOnClickListener(this);
        findViewById(R.id.change).setOnClickListener(this);

        ImageView back=findViewById(R.id.back);//返回按钮
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_password:
                startActivity(new Intent(Setting.this, SetPayPassword.class));
                break;
            case R.id.change:
                startActivity(new Intent(Setting.this,ChangePassword.class));
                break;
        }
    }
}
