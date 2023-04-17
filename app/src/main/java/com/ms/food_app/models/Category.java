package com.ms.food_app.models;

import java.io.Serializable;

public class Category implements Serializable {
    private Long id;
    private String name;
    private String image;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImages() {
        return image;
    }

    public void setImages(String image) {
        this.image = image;
    }
}
