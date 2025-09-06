package com.finance.hub.dataTransfer;

import java.time.LocalDateTime;

public class RelationshipDto {

    private final Long id;
    private final String name;
    private final String website;
    private final String email;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedOn;

    private RelationshipDto(Builder builder){
        this.id = builder.id;
        this.name = builder.name;
        this.website = builder.website;
        this.email = builder.email;
        this.createdAt = builder.createdAt;
        this.modifiedOn = builder.modifiedOn;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getWebsite() {
        return website;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getModifiedOn() {
        return modifiedOn;
    }

    public static class Builder{
        private Long id;
        private String name;
        private String website;
        private String email;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedOn;

        public Builder id(Long id){
            this.id = id;
            return this;
        }

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder website(String website){
            this.website = website;
            return this;
        }

        public Builder email(String email){
            this.email = email;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt){
            this.createdAt = createdAt;
            return this;
        }

        public Builder modifiedOn(LocalDateTime modifiedOn){
            this.modifiedOn = modifiedOn;
            return this;
        }

        public RelationshipDto build(){
            return new RelationshipDto(this);
        }


    }
}
