package com.jacobmdavidson.computersms;

/**
 * Created by jacobdavidson on 9/29/15. SMS Messages to be sent
 */
public class SMSMessage {
    private String number;
    private String body;

    public String getNumber() {
        return number;
    }

    public String getBody() {
        return body;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
