package com.example.week10.model;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.week10.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for loading and parsing movie data from JSON file.
 * Handles various error scenarios gracefully.
 */
public class JsonUtils {
    private static final String TAG = "JSONUtils";
    /**
     * Loads movies from the raw resource JSON file.
     * @param context Application context
     * @return List of valid Movie objects
     */
    public static List<Movie> loadMoviesFromJson(Context context) {
        List<Movie> movies = new ArrayList<>();
        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.movies);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            String jsonString = stringBuilder.toString();
            JSONArray jsonArray = new JSONArray(jsonString);
            Log.d(TAG, "Found " + jsonArray.length() + " entries in JSON");
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Movie movie = parseMovie(jsonObject, i);
                    if (movie != null) {
                        movies.add(movie);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing movie at index " + i + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading JSON: " + e.getMessage());
        }
        return movies;
    }
    private static Movie parseMovie(JSONObject json, int index) throws JSONException {
        /**
         * Null object check
         */
        if (json.length() == 0) {
            Log.w(TAG, "Empty object at index " + index);
            return null;
        }
        /**
         * check title
         */
        if (!json.has("title") || json.isNull("title")) {
            Log.w(TAG, "Missing title at index " + index);
            return null;
        }
        String title = json.getString("title");
        /**
         * check year(address String/Integer/Double)
         */
        if (!json.has("year") || json.isNull("year")) {
            Log.w(TAG, "Missing year at index " + index);
            return null;
        }

        Integer year = null;
        Object yearObj = json.get("year");
        try {
            if (yearObj instanceof Integer) {
                year = (Integer) yearObj;
            } else if (yearObj instanceof String) {
                year = Integer.parseInt((String) yearObj);
            } else if (yearObj instanceof Double) {
                double d = (double) yearObj;
                if (d == Math.floor(d)) {
                    year = (int) d;
                } else {
                    Log.w(TAG, "Non-integer year for: " + title);
                    return null;
                }
            }
        } catch (NumberFormatException e) {
            Log.w(TAG, "Invalid year format for: " + title);
            return null;
        }
        /**
         * address genre
         */
        String genre = json.has("genre") && !json.isNull("genre")
            ? json.getString("genre")
            : "Unknown";
        /**
         * address poster
         */
        String poster = json.has("poster") && !json.isNull("poster")
            ? json.getString("poster")
            : null;
        try {
            return new Movie(title, year, genre, poster);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "Invalid data for " + title + ": " + e.getMessage());
            return null;
        }
    }
}
