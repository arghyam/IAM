package com.arghyam.backend.dto;

import java.util.Map;

public class Request {

    private Map<String, Object> requestObject;

    public Map<String, Object> getRequestObject() {
        return requestObject;
    }

    public void setRequestObject(Map<String, Object> requestObject) {
        this.requestObject = requestObject;
    }
}
