package com.example.myapplication.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 文章的基本信息
 */

public class Article implements Serializable {
    private String title;
    private String content;                  // 内容信息
    private String createTime;               // 时间
    private String channelId;                // id
    private String nickName;                 // 名字


    private String comment_num;              //评论数
    private String headImageUrl;             // 头像
    private ArrayList<MyMedia> mediaList;   // 九宫格数据

    public Article(String channelId,String title,String createTime,String nickName){
        this.channelId=channelId;
        this.title=title;
        this.createTime=createTime;
        this.nickName=nickName;
    }


    public Article(ArrayList<MyMedia> mediaList, String content, String createTime, String channelId, String nickName, String headImageUrl) {
        this.mediaList = mediaList;
        this.content = content;
        this.createTime = createTime;
        this.channelId = channelId;
        this.nickName = nickName;
        this.headImageUrl = headImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadImageUrl() {
        return headImageUrl;
    }

    public void setHeadImageUrl(String headImageUrl) {
        this.headImageUrl = headImageUrl;
    }

    public ArrayList<MyMedia> getMediaList() {
        return mediaList;
    }

    public void setMediaList(ArrayList<MyMedia> mediaList) {
        this.mediaList = mediaList;
    }

    public String getComment_num() {
        return comment_num;
    }

    public void setComment_num(String comment_num) {
        this.comment_num = comment_num;
    }


}
