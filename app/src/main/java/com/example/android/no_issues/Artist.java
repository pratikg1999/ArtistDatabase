package com.example.android.no_issues;

public class Artist {
    public String name;
    public String band;
    public String id;

    public Artist(){

    }

    public Artist(String id, String name,String band){
        this.band = band;
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
