/*
데이터 전송 객체(Data Transfer Object)
 */
package com.example.jiwon.commonchat;

public class UserDTO {
    private String email;
    private String tel;

    public UserDTO() {
    }

    public UserDTO(String email, String tel) {
        this.email = email;
        this.tel = tel;
    }

    public String getemail() {
        return email;
    }

    public void setemail(String email) {
        this.email = email;
    }

    public String gettel() {
        return tel;
    }

    public void settel(String tel) {
        this.tel = tel;
    }
}
