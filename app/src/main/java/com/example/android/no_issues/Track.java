package com.example.android.no_issues;

public class Track {
    String id;
    String name;
    int rating;

    public Track(){

    }
    public Track(String id, String name, int rating){
        this.id = id;
        this.name = name;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }
}
