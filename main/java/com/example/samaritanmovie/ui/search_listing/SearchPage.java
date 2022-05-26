package com.example.samaritanmovie.ui.search_listing;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.samaritanmovie.MainActivity;
import com.example.samaritanmovie.R;
import com.example.samaritanmovie.Resources;
import com.example.samaritanmovie.movie_list.EndlessListener;
import com.example.samaritanmovie.movie_list.Movie;
import com.example.samaritanmovie.movie_list.MovieAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchPage extends AppCompatActivity implements View.OnClickListener{

    private View root;
    private EditText editText;
    private RecyclerView searchListing;
    //movies -> matching search query
    //notShown -> loaded, but not matching query
    private ArrayList<Movie> movies, notShown;
    private EndlessListener scrollListener;
    private SearchAdapter searchingAdapter;
    private int prevLength, curPage = 0;
    private int maxPage = 3;
    //check if search query changes
    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        //start is beginning index of last word
        //count is length of last word
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String input = s.toString();
            int inputLength = input.length();
            //keep track of last word in query
            //rather than check whole string, we can check the last word in the string
            //checking the last character can be dangerous because more than one character might be added
            String added = input.substring(start, start+count);

            //for each added character(s) to search query, movies will be removed from visibility
            //for each deleted character(s), more movies will come into visibility
            if (inputLength>prevLength){//characters have been added to search query
                //check each movie in movies
                //remove movies that do not match search query anymore
                for (int i = 0; i<movies.size(); i++){
                    Movie curMovie = movies.get(i);
                    String curTitle = curMovie.getTitle();
                    //if title is shorter than search query
                    if (curTitle.length()<inputLength){
                        removeMovie(i);
                        i--;
                        continue;
                    }

                    //only check the new characters (the ones that match the newly added characters to the search query)
                    if (curTitle.substring(start, start+count).toLowerCase().equals(added)){
                        movies.get(i).setSearchLength(inputLength);
                    }else{
                        removeMovie(i);
                        i--;
                    }
                }
                //if can't scroll down, load more until data is exhausted or enough data is loaded
                while (!searchListing.canScrollVertically(1) && curPage<=maxPage){
                    scrollListener.onLoadMore(curPage);
                }
                //only update if query has 3 characters or more
                if (inputLength>=3){
                    searchingAdapter.notifyDataSetChanged();
                    if (inputLength<=5){
                        //make sure to make recyclerview visible
                        //a buffer of 3 (between 3 and 5 characters)
                        //ensures that this change in visibility will not be neglected
                        searchListing.setVisibility(View.VISIBLE);
                    }
                }
            }else if (inputLength<prevLength){//characters have been deleted from search query

                //update the searched length of each movie
                for (int i = 0; i<movies.size(); i++){
                    movies.get(i).setSearchLength(inputLength);
                }

                //take movies from ones not currently shown
                for (int i = 0; i<notShown.size(); i++){
                    Movie curMovie = notShown.get(i);
                    //move movie from notShown to movies if it matches the search query
                    if (curMovie.getTitle().length()>=inputLength && curMovie.getTitle().substring(0, inputLength).toLowerCase().equals(input)){
                        Movie removed = notShown.remove(i);
                        removed.setSearchLength(inputLength);
                        movies.add(removed);
                        i--;
                    }
                }

                if (inputLength>=3){
                    searchingAdapter.notifyDataSetChanged();
                }else{
                    //don't show results unless query has >= 3 characters
                    searchListing.setVisibility(View.INVISIBLE);
                }
            }
            prevLength = inputLength;
        }

        @Override
        public void afterTextChanged(Editable editable) { }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);
        ImageButton cancel = (ImageButton) findViewById(R.id.cancelButton);
        //set click listener
        cancel.setOnClickListener(this);
        editText = (EditText) findViewById(R.id.inputMovie);
        editText.addTextChangedListener(watcher);

        //see top for explanation of variables
        searchListing = (RecyclerView) findViewById(R.id.movie_listing_search);
        movies = new ArrayList<Movie>();
        notShown = new ArrayList<Movie>();

        //load the first JSON page
        loadPage(1);
        curPage++;

        //column count is dependent on orientation of device
        int columns = 0;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            columns = 3;
        }else{
            columns = 5;
        }
        GridLayoutManager layoutManager = new GridLayoutManager(this, columns);
        searchListing.setLayoutManager(layoutManager);

        searchingAdapter = new SearchAdapter(movies);
        searchListing.setAdapter(searchingAdapter);
        //start out invisible
        searchListing.setVisibility(View.INVISIBLE);

        scrollListener = new EndlessListener(layoutManager) {
            @Override
            public void onLoadMore(int page) {
                loadPage(curPage);
            }
        };
        searchListing.addOnScrollListener(scrollListener);
    }

    //remove movie from movies array and add to notShown
    public void removeMovie(int num){
        Movie removed = movies.remove(num);
        removed.setSearchLength(-1);
        notShown.add(removed);
    }

    //load another page of data
    public void loadPage(int pageNum) {
        if (pageNum>maxPage){
            return;
        }
        HashMap<String, Drawable> images = Resources.getPosters(this);
        String search = editText.getText().toString();
        int searchLength = search.length();
        try {
            //convert JSON string to JSON object
            JSONObject jObj = new JSONObject(Resources.loadJSONFromAsset(pageNum, this));
            JSONObject json = jObj.getJSONObject("page");
            int size = json.getInt("page-size");
            JSONArray arr = json.getJSONObject("content-items").getJSONArray("content");
            for (int i = 0; i < size; i++) {
                JSONObject movieObj = arr.getJSONObject(i);
                String name = movieObj.getString("name");
                String posterName = movieObj.getString("poster-image");
                //use hashmap to retrive drawable for the movie
                Drawable image = images.get(posterName);
                Movie newMovie = new Movie(name, image);
                //if movie matches search query, then add it to recyclerview
                //otherwise, add it to notShown
                if (shouldShow(search, name, searchLength)){
                    newMovie.setSearchLength(searchLength);
                    movies.add(newMovie);
                }else{
                    notShown.add(newMovie);
                }
            }
            //only show if search query has >=3 characters
            if (editText.getText().length()>=3){
                searchingAdapter.notifyDataSetChanged();
            }
            curPage++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //determine whether sub is the first "length" letters of str
    //used to determine if search query is part of movie title
    public boolean shouldShow(String sub, String str, int length){
        str = str.toLowerCase();
        return str.substring(0, length).equals(sub);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}