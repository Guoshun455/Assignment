package com.example.week10;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.week10.model.JsonUtils;
import com.example.week10.model.Movie;
import com.example.week10.model.MovieAdapter;

import java.util.List;

/**
 * Main Activity displaying the movie list.
 * Handles RecyclerView setup, data loading, and error display.
 */
public class MainActivity extends AppCompatActivity {
    private RecyclerView movieRecyclerView;
    private MovieAdapter adapter;
    private ProgressBar progressBar;
    private TextView errorTextView;
    private TextView emptyTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Initialize views
        initializeViews();
        // Setup RecyclerView
        setupRecyclerView();
        // Load movie data
        loadMovieData();
    }
    /**
     * Initializes all view references.
     */
    private void initializeViews() {
        movieRecyclerView = findViewById(R.id.movieRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        errorTextView = findViewById(R.id.errorTextView);
        emptyTextView = findViewById(R.id.emptyTextView);
    }
    /**
     * Configures RecyclerView with LayoutManager and Adapter.
     */
    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        movieRecyclerView.setLayoutManager(layoutManager);
        adapter = new MovieAdapter();
        movieRecyclerView.setAdapter(adapter);
        movieRecyclerView.setHasFixedSize(true);
        movieRecyclerView.setNestedScrollingEnabled(true);
    }
    /**
     * Loads movie data from JSON file in background thread.
     */
    private void loadMovieData() {
        showLoading();
        new Thread(() -> {
            try {
                List<Movie> movies = JsonUtils.loadMoviesFromJson(this);
                runOnUiThread(() -> {
                    if (movies.isEmpty()) {
                        showEmpty("No valid movies found. Check JSON file for errors.");
                    } else {
                        showMovies(movies);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showError("Failed to load movies: " + e.getMessage());
                });
            }
        }).start();
    }
    /**
     * Displays the loaded movies in RecyclerView.
     */
    private void showMovies(List<Movie> movies) {
        progressBar.setVisibility(View.GONE);
        errorTextView.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.GONE);
        movieRecyclerView.setVisibility(View.VISIBLE);
        adapter.updateMovies(movies);
    }
    /**
     * Shows loading indicator.
     */
    private void showLoading(){
        progressBar.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.GONE);
        movieRecyclerView.setVisibility(View.GONE);
    }
    /**
     * Displays error message to user.
     */
    private void showError(String message) {
        progressBar.setVisibility(View.GONE);
        movieRecyclerView.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.GONE);
        errorTextView.setText(message);
        errorTextView.setVisibility(View.VISIBLE);
    }
    /**
     * Displays empty state message.
     */
    private void showEmpty(String message) {
        progressBar.setVisibility(View.GONE);
        movieRecyclerView.setVisibility(View.GONE);
        errorTextView.setVisibility(View.GONE);
        emptyTextView.setText(message);
        emptyTextView.setVisibility(View.VISIBLE);
    }
}