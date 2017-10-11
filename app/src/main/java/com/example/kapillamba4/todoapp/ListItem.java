package com.example.kapillamba4.todoapp;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by kapil on 27/9/17.
 */

public class ListItem {
    private String title;
    private String content;
    private String date;
    private String time;
    private long created;
    private long accessed;
    private long id;

    public ListItem(String title, String content, long id, String date, String time, long epoch1, long epoch2) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.time = time;
        this.id = id;
        this.created = epoch1;
        this.accessed = epoch2;
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

    public long getCreated() {
        return created;
    }

    public long getAccessed() {
        return accessed;
    }

    public void setAccessed(long accessed) {
        this.accessed = accessed;
    }
}
