package at.ac.univie.entertain.elterntrainingapp.model;

import java.util.Date;

public class Message {

    private String title;
    private String content;
    private User author;
    private User receiver;
    private Date sentDate;

    public Message(String title, String content, User author, User receiver) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.receiver = receiver;
        this.sentDate = new Date(System.currentTimeMillis());
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

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }
}
