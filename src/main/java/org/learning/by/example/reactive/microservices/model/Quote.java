package org.learning.by.example.reactive.microservices.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Quote {

    @JsonProperty("ID")
    private Integer ID;
    private String title;
    private String content;
    private String link;

    public Quote() {
        this.ID = 0;
        this.title = "";
        this.content = "";
        this.link = "";
    }

    public Integer getID() {
        return ID;
    }

    public void setID(final Integer ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public String getLink() {
        return link;
    }

    public void setLink(final String link) {
        this.link = link;
    }
}
