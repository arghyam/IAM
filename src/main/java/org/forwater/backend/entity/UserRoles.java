package org.forwater.backend.entity;

public class UserRoles {
    private String id;
    private String username;
    private Boolean admin;
    private Boolean reviewer;
    private String firstName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Boolean getReviewer() {
        return reviewer;
    }

    public void setReviewer(Boolean reviewer) {
        this.reviewer = reviewer;
    }
}