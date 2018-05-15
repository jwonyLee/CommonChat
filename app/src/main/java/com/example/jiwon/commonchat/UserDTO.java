/*
데이터 전송 객체(Data Transfer Object)
 */
package com.example.jiwon.commonchat;

public class UserDTO {
    private String name;
    private String tel;
    private String email;

    public UserDTO() {
    }

    public UserDTO(String name, String email, String tel) {
        this.name = name;
        this.email = email;
        this.tel = tel;
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
