package com.example.myapplication.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.CircleImg;
import com.example.myapplication.LoginActivity;
import com.example.myapplication.R;
import com.example.myapplication.user.ComplainMain;
import com.example.myapplication.user.RepairMain;
import com.example.myapplication.user.MyHome;
import com.example.myapplication.user.Setting;
import com.example.myapplication.user.UserMessage;
import com.example.myapplication.utils.SharedPreUtil;
import com.gyf.immersionbar.ImmersionBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.io.File;

/**
 * 我的页面
 */
public class MineFragment extends Fragment implements View.OnClickListener {
    private CircleImg picture;
    private Toolbar mToolbar;
    private TextView name;

    private SharedPreferences preferences;
    private String use_phone;
    

    public static MineFragment newInstance() {
        MineFragment fragment = new MineFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view =  inflater.inflate(R.layout.fragment_mine, container, false);
        mToolbar =  view.findViewById(R.id.tcbar);
        ImmersionBar.setTitleBar(this, mToolbar);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        use_phone = preferences.getString("phone","");
        
        name=view.findViewById(R.id.tv_name);
        name.setText(use_phone.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2"));

        view.findViewById(R.id.ll_userInfo).setOnClickListener(this);
        view.findViewById(R.id.ll_house).setOnClickListener(this);
        view.findViewById(R.id.my_complain).setOnClickListener(this);
        view.findViewById(R.id.my_repair).setOnClickListener(this);
        view.findViewById(R.id.ll_set).setOnClickListener(this);
        view.findViewById(R.id.bt_logout).setOnClickListener(this);
        picture=view.findViewById(R.id.iv_picture);
        picture.setOnClickListener(this);

        Bitmap bt = getBitmap("temphead.jpg");
        if (bt != null) {
            @SuppressWarnings("deprecation")
            Drawable drawable = new BitmapDrawable(bt);
            picture.setImageDrawable(drawable);
        }
       return view;
    }
    
    private Bitmap getBitmap(String pathString) {
        Bitmap bitmap = null;
        try {
            File file = new File(pathString);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(pathString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_userInfo:
            case R.id.iv_picture:
                startActivity(new Intent(getActivity(),UserMessage.class));
                break;
            case R.id.ll_house:
                startActivity(new Intent(getActivity(), MyHome.class));
                break;
            case R.id.my_complain:
                startActivity(new Intent(getActivity(), ComplainMain.class));
                break;
            case R.id.my_repair:
                startActivity(new Intent(getActivity(), RepairMain.class));
                break;
            case R.id.ll_set:
                startActivity(new Intent(getActivity(), Setting.class));
                break;
            case R.id.bt_logout:
                logout();
                SharedPreUtil.setParam(getContext(), SharedPreUtil.IS_LOGIN, false);
                SharedPreUtil.removeParam(getContext(), SharedPreUtil.LOGIN_DATA);

                //重新跳转到登录页面
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
        }
    }

    private void logout() {
        new AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage("是否确定退出")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        getActivity().finish();
                    }
                }).setNegativeButton("取消",null)
                .create().show();
    }
}
