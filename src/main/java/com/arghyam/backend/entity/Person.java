package com.arghyam.backend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Person {

    private String springCode;

    public String getSpringCode() {
        return springCode;
    }

    public void setSpringCode(String springCode) {
        this.springCode= springCode;
    }
}
