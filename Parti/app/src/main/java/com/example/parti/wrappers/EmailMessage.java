package com.example.parti.wrappers;

public class EmailMessage {
    // [start of the field constants]
    public static final String SUBJECT_FIELD = "subject";
    public static final String TEXT_FIELD = "text";
    public static final String HTML_FIELD = "html";
    // [end of the field constants]

    private String subject;
    private String text;
    private String html;

    public EmailMessage() {}

    public EmailMessage(String subject, String text) {
        this(subject, text, "");
    }

    public EmailMessage(String subject, String text, String html) {
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
