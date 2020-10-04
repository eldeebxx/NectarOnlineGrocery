package com.example.nectaronlinegrocery.model;

public class Category {
    private Integer id;
    private String name;
    private String shortName;
    private String image;

    public Category() {

    }

    public Category(Integer id, String name, String shortName, String image) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.image = image;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

