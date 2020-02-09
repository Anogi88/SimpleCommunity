package com.example.myapplication.model;

import java.io.Serializable;

/**
 * 投诉和报修的记录
 */
public class Record implements Serializable {
    private String title;
    private String content;                  // 内容信息
    private String createTime;               // 时间
    private String nickName;                 // 名字

    private String reply;                    //回复

    //投诉
    public Record(String nickName,String createTime,String content,String reply){
        this.nickName=nickName;
        this.createTime=createTime;
        this.content=content;
        this.reply=reply;
    }
    //报修
    public Record(String nickName,String createTime,String title,String content,String reply){
        this.nickName=nickName;
        this.createTime=createTime;
        this.title=title;
        this.content=content;
        this.reply=reply;
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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

}
