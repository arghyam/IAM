package com.arghyam.backend.dto;

import com.arghyam.backend.entity.RegistryUser;

public class Request {

    private RegistryUser Person;

    public RegistryUser getPerson() {
        return Person;
    }

    public void setPerson(RegistryUser person) {
        Person = person;
    }
}
