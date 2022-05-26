package com.example.samaritanmovie.movie_list;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.samaritanmovie.R;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder>{

    public class ViewHolder extends RecyclerView.ViewHolder{
        //components of each movie
        public TextView title;
        public ImageView poster;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //set the components
            title = (TextView) itemView.findViewById(R.id.movieTitle);
            poster = (ImageView) itemView.findViewById(R.id.moviePoster);
        }
    }

    public List<Movie> getMovies() {
        return movies;
    }

    //list of movies currently in the listing page (recyclerview data)
    private List<Movie> movies;
    public MovieAdapter(List<Movie> m){
        movies = m;
    }

    @NonNull
    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflating = LayoutInflater.from(context);
        View movieView = inflating.inflate(R.layout.movie, parent, false);
        ViewHolder holder = new ViewHolder(movieView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.ViewHolder holder, int position) {
        //get the current movie
        Movie curMovie = movies.get(position);
        String movTitle = curMovie.getTitle();
        //display movie title and poster
        TextView titleView = holder.title;
        ImageView posterView = holder.poster;
        //if movie title is too long, take only part of it
        if (movTitle.length()>15){
            titleView.setText(movTitle.substring(0, 15)+"...");
        }else{
            titleView.setText(movTitle);
        }
        posterView.setImageDrawable(curMovie.getPoster());
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }
}
