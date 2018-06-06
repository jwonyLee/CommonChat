package com.example.jiwon.commonchat;


public class ImgMessageDTO {

    private String imgmessage;
    private String user;

    public ImgMessageDTO() {}

    public ImgMessageDTO(String imgmessage, String user) {
        this.imgmessage = imgmessage;
        this.user = user;
    }

    public String getImgMessage() {
        return imgmessage;
    }

    public void setMessage(String imgmessage) {
        this.imgmessage = imgmessage;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}