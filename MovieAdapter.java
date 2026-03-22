package com.example.week10.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.week10.R;

import java.util.List;
import java.util.ArrayList;

/**
 * Adapter for displaying movies in RecyclerView.
 * Handles the creation and binding of ViewHolders.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieViewHolder> {
    private List<Movie> movies;
    public MovieAdapter(){
        this.movies = new ArrayList<>();
    }
    public MovieAdapter(List<Movie> movies) {
        this.movies = (movies != null) ? movies : new ArrayList<>();
    }
    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bind(movie);
    }
    @Override
    public int getItemCount() {
        return movies.size();
    }
    /**
     * Updates the movie list and refreshes the RecyclerView.
     * @param newMovies New list of movies
     */
    public void updateMovies(List<Movie> newMovies) {
        this.movies = (newMovies != null) ? newMovies : new ArrayList<>();
        notifyDataSetChanged();
    }
    /**
     * Returns the current movie list.
     */
    public List<Movie> getMovies() {
        return new ArrayList<>(movies);
    }
}
