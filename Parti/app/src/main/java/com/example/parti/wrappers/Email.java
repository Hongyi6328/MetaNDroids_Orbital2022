package com.example.parti.wrappers;

import androidx.annotation.NonNull;

public class Email {
    // [start of the field constants]
    public static final String TO_FIELD = "to";
    public static final String MESSAGE_FIELD = "message";
    // [end of the field constants]

    private String to;
    private EmailMessage message;

    public Email() {}

    public Email(
            @NonNull String to,
            @NonNull String subject,
            @NonNull String text) {
        this(to, subject, text, "");
    }

    public Email(
            @NonNull String to,
            @NonNull String subject,
            @NonNull String text,
            @NonNull String html) {
        this(to, new EmailMessage(subject, text, html));
    }

    public Email(
            @NonNull String to,
            @NonNull EmailMessage message) {
        this.to = to;
        this.message = message;
    }

    public String getTo() {return to;}
    public EmailMessage getMessage() {return message;}

    public void setTo(String to) {this.to = to;}
    public void setMessage(EmailMessage message) {this.message = message;}
}
