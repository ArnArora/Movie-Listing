package com.example.samaritanmovie;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.samaritanmovie.movie_list.EndlessListener;
import com.example.samaritanmovie.movie_list.Movie;
import com.example.samaritanmovie.movie_list.MovieAdapter;
import com.example.samaritanmovie.ui.search_listing.SearchPage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EndlessListener scrollListener;
    //keeps track of currently loaded movies
    private ArrayList<Movie> movies;
    private MovieAdapter adapter;
    private RecyclerView movieListing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //add listeners so that buttons can go to different activities
        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        ImageButton search = (ImageButton) findViewById(R.id.searchButton);

        search.setOnClickListener(this);
        backButton.setOnClickListener(this);

        movieListing = (RecyclerView) findViewById(R.id.movie_listing_main);
        movies = new ArrayList<Movie>();

        adapter = new MovieAdapter(movies);
        movieListing.setAdapter(adapter);

        //load first page of data to display
        loadPage(1);

        int columns = 0;
        //choose column count based on orientation of device
        if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
            columns = 3;
        }else{
            columns = 5;
        }
        GridLayoutManager layoutManager = new GridLayoutManager(this, columns);
        movieListing.setLayoutManager(layoutManager);

        scrollListener = new EndlessListener(layoutManager) {
            @Override
            public void onLoadMore(int page) {
                loadPage(page);
            }
        };
        movieListing.addOnScrollListener(scrollListener);
    }

    //method to load data for scroll listener
    public void loadPage(int pageNum) {
        HashMap<String, Drawable> images = Resources.getPosters(this);
        try {
            //convert to JSON object and read each attribute from there
            JSONObject jObj = new JSONObject(Resources.loadJSONFromAsset(pageNum, this));
            JSONObject json = jObj.getJSONObject("page");
            int size = json.getInt("page-size");
            JSONArray arr = json.getJSONObject("content-items").getJSONArray("content");
            for (int i = 0; i < size; i++) {
                JSONObject movieObj = arr.getJSONObject(i);
                String name = movieObj.getString("name");
                String posterName = movieObj.getString("poster-image");
                //use hashmap to retrieve image/drawable for poster
                Drawable image = images.get(posterName);
                Movie newMovie = new Movie(name, image);
                movies.add(newMovie);
            }
            //update recyclerview
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        //check view and perform action accordingly
        if (view.getId()==R.id.backButton){
            Intent intent = new Intent(this, MovieHome.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this, SearchPage.class);
            startActivity(intent);
        }
    }
}