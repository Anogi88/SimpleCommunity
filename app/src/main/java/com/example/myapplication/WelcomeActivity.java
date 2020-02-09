package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.myapplication.utils.SharedPreUtil;

public class WelcomeActivity extends AppCompatActivity {
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                //判断用户是否登录
                boolean userIsLogin = (boolean) SharedPreUtil.getParam(WelcomeActivity.this,
                        SharedPreUtil.IS_LOGIN, false);
                if (userIsLogin) {
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

                finish();
            } else if (msg.what == 0) {
                thread.interrupt();
            }

        }

    };
    final Message message = new Message();
    final Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(3000);
                message.what = 1;
                handler.sendMessage(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });

    private CountDownTimer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setFullScreen();
        // 设置全屏一定要在setContentView()之前,否则有可能不起作用
        setContentView(R.layout.activity_welcome);

        TextView countDownText = findViewById(R.id.tv_welcome);

        initCountDown(countDownText);
    }

    // 全屏显示
    private void setFullScreen() {
        // 如果该类是 extends Activity ，下面这句代码起作用
        // 去除ActionBar(因使用的是NoActionBar的主题，故此句有无皆可)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 如果该类是 extends AppCompatActivity， 下面这句代码起作用
        if (getSupportActionBar() != null){ getSupportActionBar().hide(); }
        // 去除状态栏，如 电量、Wifi信号等
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    // 倒计时逻辑
    private void initCountDown(final TextView countDownText) {
        // 判断当前Activity是否isFinishing()，
        // 避免在finish，所有对象都为null的状态下执行CountDown造成内存泄漏
        if (!isFinishing()) {
            timer = new CountDownTimer(1000 * 6, 1000) {
                @SuppressLint("SetTextI18n")
                @Override
                public void onTick(long millisUntilFinished) {
                    // TODO: 耗时操作，如异步登录
                    // ......
                    int time = (int) millisUntilFinished;
                    countDownText.setText(time / 1000 + " 跳过");
                    countDownText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkToJump();
                        }
                    });
                }

                @Override
                public void onFinish() {
                    checkToJump();
                }
            }.start();
        }
    }

    // 首次进入引导页判断
    private void checkToJump() {
        //  TODO：首次安装判断
        // 如果是首次安装打开，则跳至引导页；否则跳至主界面
        // 这里先不放引导页，直接跳到主界面
        message.what = 0;
        handler.sendMessage(message);

        //判断用户是否登录
        boolean userIsLogin = (boolean) SharedPreUtil.getParam(WelcomeActivity.this, SharedPreUtil.IS_LOGIN, false);
        if (userIsLogin) {
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        // 回收内存
        destoryTimer();
        finish();
    }

    public void destoryTimer() {
        // 避免内存泄漏
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
