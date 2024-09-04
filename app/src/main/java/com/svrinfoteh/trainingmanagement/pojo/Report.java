package com.svrinfoteh.trainingmanagement.pojo;

import java.io.Serializable;

public class Report implements Serializable {

    String fileName,filePath;
    Long uploadDate;

    public Report() {

    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Long uploadDate) {
        this.uploadDate = uploadDate;
    }
}
