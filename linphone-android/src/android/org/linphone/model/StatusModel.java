package org.linphone.model;

public class StatusModel {
    private String status;
    private int  resId;
    private boolean online;

    public StatusModel(String status, int resId,boolean online) {
        this.status = status;
        this.resId = resId;
        this.online=online;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
