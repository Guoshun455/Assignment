package com.example.week10.model;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.week10.R;

/**
 * ViewHolder for movie items in RecyclerView.
 * Holds references to views for efficient recycling.
 */
public class MovieViewHolder extends RecyclerView.ViewHolder{
    private final ImageView posterImageView;
    private final TextView titleTextView;
    private final TextView yearTextView;
    private final TextView genreTextView;
    public MovieViewHolder(@NonNull View itemView) {
        super(itemView);
        posterImageView = itemView.findViewById(R.id.posterImageView);
        titleTextView = itemView.findViewById(R.id.titleTextView);
        yearTextView = itemView.findViewById(R.id.yearTextView);
        genreTextView = itemView.findViewById(R.id.genreTextView);
    }
    /**
     * Binds movie data to the views.
     * @param movie Movie object to display
     */
    public void bind(Movie movie){
        if (movie == null) {
            return;
        }
        // Set title
        titleTextView.setText(movie.getTitle());
        // Set year
        yearTextView.setText(String.valueOf(movie.getYear()));
        // Set genre
        genreTextView.setText(movie.getGenre());
        // Set poster placeholder
        if (movie.getPosterResource() != null) {
            // Try to get resource ID from name
            int resourceId = itemView.getContext().getResources().getIdentifier(movie.getPosterResource(), "drawable", itemView.getContext().getPackageName());
            if (resourceId != 0) {
                posterImageView.setImageResource(resourceId);
            } else {
                // Use placeholder if resource not found
                posterImageView.setImageResource(R.drawable.placeholder_poster);
            }
        } else {
            // Use placeholder for missing poster
            posterImageView.setImageResource(R.drawable.placeholder_poster);
        }
    }
}
