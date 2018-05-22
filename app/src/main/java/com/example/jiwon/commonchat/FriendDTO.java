package com.example.jiwon.commonchat;

public class FriendDTO {
    private int icon;
    private String name;
    private String stateMessage;

    public int getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getStateMessage() {
        return stateMessage;
    }

    public FriendDTO(int icon) {
        this.icon = icon;
    }

    public FriendDTO(int icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    public FriendDTO(int icon, String name, String stateMessage) {
        this.icon = icon;
        this.name = name;
        this.stateMessage = stateMessage;
    }

    @Override
    public String toString() {
        return "FriendListViewitem{" +
                "icon=" + icon +
                ", name='" + name + '\'' +
                ", stateMessage='" + stateMessage + '\'' +
                '}';
    }
}
