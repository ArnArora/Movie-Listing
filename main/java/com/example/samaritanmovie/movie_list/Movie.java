package com.example.samaritanmovie.movie_list;

import android.graphics.drawable.Drawable;

public class Movie {
    //stores data for each movie from JSON file
    //consists of movie title and movie poster
    private String title;
    private Drawable poster;
    //also has search length, which tells how much of the title has been
    //compared against the search query
    private int searchLength;

    public Movie(String t, Drawable p){
        title = t;
        poster = p;
        searchLength = -1;
    }

    public int getSearchLength() {
        return searchLength;
    }

    public void setSearchLength(int s) {
        searchLength = s;
    }

    public Drawable getPoster() { return poster; }

    public void setPoster(Drawable p) { poster = p; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
