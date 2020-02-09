package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.text.TextUtils;

import com.example.myapplication.fragment.AppFragment;
import com.example.myapplication.fragment.MessageFragment;
import com.example.myapplication.fragment.MineFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gyf.immersionbar.ImmersionBar;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView mBottomNavigationBar;
    private static final String KEY_FRAGMENT_TAG = "fragment_tag";
    private static final String FRAGMENT_TAG_APP = "fragment_app";
    private static final String FRAGMENT_TAG_MESSAGE = "fragment_message";
    private static final String FRAGMENT_TAG_MINE = "fragment_mine";
    private String[] mFragmentTags = new String[]{FRAGMENT_TAG_APP,FRAGMENT_TAG_MESSAGE,  FRAGMENT_TAG_MINE};
    private String mFragmentCurrentTag = FRAGMENT_TAG_APP;
    private Fragment mAppFragment;
    private Fragment mMessageFragment;
    private Fragment mMineFragment;
    //用来初始化Activity实例对象
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            //如果savedInstanceState不为null,说明进程异常死亡
            restoreFragments();
            mFragmentCurrentTag = savedInstanceState.getString(KEY_FRAGMENT_TAG);
        }
        setContentView(R.layout.activity_main);
       initView();
    }

    //初始化view
    private void initView() {
        mBottomNavigationBar = findViewById(R.id.bottom_navigation_bar);
        //去掉默认点击背景色
        mBottomNavigationBar.setItemIconTintList(null);
        // 添加监听
        mBottomNavigationBar.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_app:
                    ontBottomSelect(FRAGMENT_TAG_APP);
                    break;
                case R.id.menu_message:
                    ontBottomSelect(FRAGMENT_TAG_MESSAGE);
                    break;
                case R.id.menu_me:
                    ontBottomSelect(FRAGMENT_TAG_MINE);
                    break;
                default:
                    break;
            }
            return true;
        });
        //默认选中第一个
        if (TextUtils.equals(FRAGMENT_TAG_APP, mFragmentCurrentTag)) {
            mBottomNavigationBar.setSelectedItemId(mBottomNavigationBar.getMenu().getItem(0).getItemId());
        }else if (TextUtils.equals(FRAGMENT_TAG_MESSAGE, mFragmentCurrentTag)) {
            mBottomNavigationBar.setSelectedItemId(mBottomNavigationBar.getMenu().getItem(1).getItemId());
        }else if (TextUtils.equals(FRAGMENT_TAG_MINE, mFragmentCurrentTag)) {
            mBottomNavigationBar.setSelectedItemId(mBottomNavigationBar.getMenu().getItem(2).getItemId());
        }
    }
    //下面tab点击
    private void ontBottomSelect(String tag) {
        switch (tag) {
            case FRAGMENT_TAG_APP:
                if (mAppFragment == null) {
                    mAppFragment = AppFragment.newInstance();
                }
                switchFragment(mAppFragment, FRAGMENT_TAG_APP);
                break;
            case FRAGMENT_TAG_MESSAGE:
                if (mMessageFragment == null) {
                    mMessageFragment = MessageFragment.newInstance();
                }
                switchFragment(mMessageFragment, FRAGMENT_TAG_MESSAGE);
                break;

            case FRAGMENT_TAG_MINE:
                if (mMineFragment == null) {
                    mMineFragment = MineFragment.newInstance();
                }
                switchFragment(mMineFragment, FRAGMENT_TAG_MINE);
                break;
            default:
                break;
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_FRAGMENT_TAG, mFragmentCurrentTag);
        super.onSaveInstanceState(outState);
    }
    //创建和显示Fragment
    private void switchFragment(Fragment fragment, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        hideFragments(manager, transaction);
        mFragmentCurrentTag = tag;
        if (!fragment.isAdded()) {
            transaction.add(R.id.container, fragment, tag);
        }
        transaction.show(fragment).commit();
    }
    //隐藏所有Fragment
    private void hideFragments(FragmentManager fragmentManager, FragmentTransaction transaction) {
        for (String mFragmentTag : mFragmentTags) {
            Fragment fragment = fragmentManager.findFragmentByTag(mFragmentTag);
            if (fragment != null && fragment.isVisible()) {
                transaction.hide(fragment);
            }
        }
    }

    /**
     * 异常退出 恢复Fragment
     */
    private void restoreFragments() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        for (String mFragmentTag : mFragmentTags) {
            Fragment fragment = manager.findFragmentByTag(mFragmentTag);
            if (fragment instanceof AppFragment) {
                mAppFragment = fragment;
            }else if (fragment instanceof MessageFragment) {
                mMessageFragment = fragment;
            }  else if (fragment instanceof MineFragment) {
                mMineFragment = fragment;
            }
            if (fragment!=null) {
                transaction.hide(fragment);
            }
        }
        transaction.commit();
    }

}
