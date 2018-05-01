/*
데이터 전송 객체(Data Transfer Object)
 */
package com.example.jiwon.commonchat;

public class ChatDTO {
    private String userName;
    private String message;

    public ChatDTO() {}

    public ChatDTO(String userName, String message) {
        this.userName = userName;
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
