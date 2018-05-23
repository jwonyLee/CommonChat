package com.example.jiwon.commonchat;

public class FriendDTO {
    private int profileImageView;
    private String nameTextView;
    private String stateTextView;

    public FriendDTO(int profileImageView, String nameTextView) {
        this.profileImageView = profileImageView;
        this.nameTextView = nameTextView;
        this.stateTextView = "";
    }

    public int getProfileImageView() {
        return profileImageView;
    }

    public void setProfileImageView(int profileImageView) {
        this.profileImageView = profileImageView;
    }

    public String getNameTextView() {
        return nameTextView;
    }

    public void setNameTextView(String nameTextView) {
        this.nameTextView = nameTextView;
    }

    public String getStateTextView() {
        return stateTextView;
    }

    public void setStateTextView(String stateTextView) {
        this.stateTextView = stateTextView;
    }
}
