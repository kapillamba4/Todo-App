package com.example.kapillamba4.todoapp;

/**
 * Created by kapil on 27/9/17.
 */

public class ListItem {
    private String title;
    private String content;
    private String date;
    private String time;
    private long id;

    public ListItem(String title, String content, long id, String date, String time) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.time = time;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
