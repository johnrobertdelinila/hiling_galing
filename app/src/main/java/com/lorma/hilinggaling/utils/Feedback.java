package com.lorma.hilinggaling.utils;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Feedback {
    private String feedback;
    private @ServerTimestamp Date timestamp;

    public Feedback() {}

    public Feedback(String feedback, Date timestamp) {
        this.feedback = feedback;
        this.timestamp = timestamp;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
