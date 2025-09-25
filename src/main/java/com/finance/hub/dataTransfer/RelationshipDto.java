package com.finance.hub.dataTransfer;

import java.time.LocalDateTime;

public class RelationshipDto {

    private Long id;
    private String name;
    private String website;
    private String email;

    public RelationshipDto(){}


    public RelationshipDto(Long id, String name, String website, String email) {
        this.id = id;
        this.name = name;
        this.website = website;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
