package com.arghyam.backend.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "springuser")
public class Springuser implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "userid")
    private UUID userid;

    @Column(name = "name")
    private String name;

    @Column(name = "phonenumber")
    private String phonenumber;

    public UUID getUserid() {
        return userid;
    }

    public void setUserid(UUID userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
}
