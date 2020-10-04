package com.example.nectaronlinegrocery.model;

public class Data {
    public String name;
    public Integer icon;

    public Data(String name, Integer icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public Integer getIcon() {
        return icon;
    }
}
