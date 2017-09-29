package com.example.kapillamba4.todoapp;

/**
 * Created by kapil on 27/9/17.
 */

public class ListItem {
    private String title;
    private String content;
    private long id;

    public ListItem(String title, String content, long id) {
        this.title = title;
        this.content = content;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
