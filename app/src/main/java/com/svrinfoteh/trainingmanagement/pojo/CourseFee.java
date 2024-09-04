package com.svrinfoteh.trainingmanagement.pojo;

import java.io.Serializable;

public class CourseFee implements Serializable {

    private String name,totalFee,feePaid,feeRemaining;

    public CourseFee() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getFeePaid() {
        return feePaid;
    }

    public void setFeePaid(String feePaid) {
        this.feePaid = feePaid;
    }

    public String getFeeRemaining() {
        return feeRemaining;
    }

    public void setFeeRemaining(String feeRemaining) {
        this.feeRemaining = feeRemaining;
    }
}
