package com.example.myapplication.model;

public class Notice {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private  String id;
    private String title;
    private String content;
    private String  datetime;
    public Notice(String id,String title,String content,String time) {
        this.id=id;
        this.title = title;
        this.content = content;
        this.datetime = time;
    }
    public  Notice(String id,String title){
        this.id=id;
        this.title=title;
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

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }


}
