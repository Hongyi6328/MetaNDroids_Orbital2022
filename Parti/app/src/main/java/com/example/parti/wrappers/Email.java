package com.example.parti.wrappers;

import androidx.annotation.NonNull;

public class Email {

    public static final String CLASS_ID = "email";
    public static final String DEFAULT_VERIFICATION_CODE_SUBJECT = "[Parti.] Your verification code list for the project";
    public static final String DEFAULT_TRANSFER_CONFIRMATION_SUBJECT = "[Parti.] PP transfer confirmation";

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
