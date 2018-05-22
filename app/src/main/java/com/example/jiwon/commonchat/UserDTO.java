/*
데이터 전송 객체(Data Transfer Object)
 */
package com.example.jiwon.commonchat;

public class UserDTO {
    private String name;
    private String tel;
    private String email;
    private String state;

    public UserDTO() {
    }

    public UserDTO(String email, String state, String name, String tel) {
        this.name = name;
        this.email = email;
        this.tel = tel;
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
