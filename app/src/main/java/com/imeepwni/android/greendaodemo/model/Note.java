package com.imeepwni.android.greendaodemo.model;

import androidx.annotation.NonNull;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity(indexes = {@Index(value = "text, date DESC", unique = true)})
public class Note {

    @Id
    private Long id;

    @NonNull
    private String text = "";
    private String comment;
    private Date date;

    @Generated(hash = 223120171)
    public Note(Long id, @NonNull String text, String comment, Date date) {
        this.id = id;
        this.text = text;
        this.comment = comment;
        this.date = date;
    }
    @Generated(hash = 1272611929)
    public Note() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getText() {
        return this.text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getComment() {
        return this.comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public Date getDate() {
        return this.date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    @NonNull
    @Override
    public String toString() {
        return "Note [id:" + id + ", text:" + text + ", comment:" + comment + ", date:" + date + "]";
    }
}
