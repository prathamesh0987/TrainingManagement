package com.svrinfoteh.trainingmanagement.pojo;

public class RevisionPoints {

    private String point,reason;

    public RevisionPoints() {

    }

    public RevisionPoints(String point, String reason) {
        this.point = point;
        this.reason = reason;
    }

    public String getPoint() {
        return point;
    }

    public String getReason() {
        return reason;
    }


}
