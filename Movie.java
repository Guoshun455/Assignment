package com.example.week10.model;

/**
 * Movie model class representing a movie entity.
 * Contains proper validation for all fields.
 */
public class Movie {
    private String title;
    private Integer year;
    private String genre;
    private String posterResource;
    /**
     * Constructor with validation
     * @param title Movie title (required, cannot be null or empty)
     * @param year Release year (required, must be between 1888 and current year + 5)
     * @param genre Movie genre (defaults to "Unknown")
     * @param posterResource Poster image resource name
     */
    public Movie(String title, Integer year, String genre, String posterResource){
        setTitle(title);
        setYear(year);
        setGenre(genre);
        setPosterResource(posterResource);
    }
    // Title validation: required field
    public void setTitle(String title){
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        this.title = title.trim();
    }
    public String getTitle(){
        return title;
    }
    // Year validation: must be reasonable year
    public void setYear(Integer year){
        if (year == null) {
            throw new IllegalArgumentException("Year cannot be null");
        }
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        if (year < 1888 || year > currentYear + 5){
            throw new IllegalArgumentException("Year must be between 1888 and " + (currentYear + 5));
        }
        this.year = year;
    }
    public Integer getYear(){
        return year;
    }
    public void setGenre(String genre){
        if (genre == null || genre.trim().isEmpty()) {
            this.genre = "Unknown";
        } else {
            this.genre = genre.trim();
        }
    }
    public String getGenre(){
        return genre;
    }
    public void setPosterResource(String posterResource) {
        if (posterResource == null || posterResource.trim().isEmpty()) {
            this.posterResource = null;
        } else {
            this.posterResource = posterResource.trim();
        }
    }
    public String getPosterResource(){
        return posterResource;
    }
    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", year=" + year +
                ", genre='" + genre + '\'' +
                ", posterResource='" + posterResource + '\'' +
                '}';
    }
}
