package com.poovarasan.tamiltv.core;

public class UpdateModel {
    int version;
    String title;
    String body;
    String updateButtonText;
    Boolean isRequired;

    public int getVersion() {
        return version;
    }
    public void setVersion(int version) {
        this.version = version;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public String getUpdateButtonText() {
        return updateButtonText;
    }
    public void setUpdateButtonText(String updateButtonText) {
        this.updateButtonText = updateButtonText;
    }
    public Boolean getRequired() {
        return isRequired;
    }
    public void setRequired(Boolean required) {
        isRequired = required;
    }
}
