package com.example.samaritanmovie.ui.search_listing;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.samaritanmovie.R;
import com.example.samaritanmovie.movie_list.Movie;
import com.example.samaritanmovie.movie_list.MovieAdapter;

import java.util.List;

//very similar to MovieAdapter except for the binding of data to recyclerview
public class SearchAdapter extends MovieAdapter {
    //list of movies currently in the listing page
    public SearchAdapter(List<Movie> m){
        super(m);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        //get the current movie
        Movie curMovie = super.getMovies().get(position);
        String movTitle = curMovie.getTitle();
        //display movie title and poster
        TextView titleView = holder.title;
        ImageView posterView = holder.poster;

        //shorten title if needed
        if (movTitle.length()>15){
            if (curMovie.getSearchLength()<10) {
                movTitle = movTitle.substring(0, curMovie.getSearchLength()+6)+"...";
            }else {
                movTitle = movTitle.substring(0, curMovie.getSearchLength())+"...";
            }
        }
        titleView.setText(movTitle);

        //whatever part has been compared against search query
        //will be highlighted in yellow
        Spannable spannable = new SpannableString(movTitle);
        spannable.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, curMovie.getSearchLength(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        titleView.setText(spannable);

        posterView.setImageDrawable(curMovie.getPoster());
    }
}
