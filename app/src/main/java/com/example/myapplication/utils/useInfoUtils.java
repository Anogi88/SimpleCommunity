package com.example.myapplication.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class useInfoUtils {
    //保存用户信息登录数据
    public static boolean saveUserInfo(String phone,String password){
        try{
            String result=phone+"##"+password;
            //写入数据
            //创建file类，指定我们数据存储的位置
            File file=new File("/data/data/com.example.myapplication/userInfo.txt");
            //创建一个文件的输出流
            FileOutputStream fos=new FileOutputStream(file);
            //写入数据
            fos.write(result.getBytes());
            //关闭数据流
            fos.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    //读取保存的用户登录信息
    public static Map<String,String> readUserInfo(){
        try{
            Map<String,String> useInfoMap=new HashMap<String, String>();
            File file=new File("/data/data/com.example.myapplication/userInfo.txt");
            FileInputStream fis=new FileInputStream(file);
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(fis));
            String content=bufferedReader.readLine();//读取数据
            //切割数据，并把用户信息存入Map
            String[] userInfo=content.split("##");
            useInfoMap.put("phone",userInfo[0]);
            useInfoMap.put("password",userInfo[1]);
            //关闭流
            fis.close();
            return  useInfoMap;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
