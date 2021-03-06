package com.example.parti.wrappers;

import androidx.annotation.NonNull;

public class EmailMessage {

    public static final String CLASS_ID = "email_message";

    // [start of the field constants]
    public static final String SUBJECT_FIELD = "subject";
    public static final String TEXT_FIELD = "text";
    public static final String HTML_FIELD = "html";
    // [end of the field constants]

    private String subject;
    private String text;
    private String html;

    public EmailMessage() {}

    public EmailMessage(
            @NonNull String subject,
            @NonNull String text) {
        this(subject, text, "");
    }

    public EmailMessage(
            @NonNull String subject,
            @NonNull String text,
            @NonNull String html) {
        this.subject = subject;
        this.text = text;
        this.html = html;
    }

    public String getSubject() {return subject;}
    public String getText() {return text;}
    public String getHtml() {return html;}

    public void setSubject(String subject) {this.subject = subject;}
    public void setText(String text) {this.text = text;}
    public void setHtml(String html) {this.html = html;}
}
