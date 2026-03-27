package com.example.week11.model;

import java.util.UUID;
public class Team implements SoccerEntity {
    private String id;
    private String name;
    private String country;
    private String league;
    private String stadium;
    private int foundedYear;
    public Team(String name, String country, String league, String stadium, int foundedYear) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Team name cannot be null or empty");
        if (country == null || country.trim().isEmpty())
            throw new IllegalArgumentException("Country name cannot be null or empty");
        if (league == null || country.trim().isEmpty())
            throw new IllegalArgumentException("League name cannot be null or empty");
        if (foundedYear < 1800 || foundedYear > 2100)
            throw new IllegalArgumentException("Invalid founded year");
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.country = country;
        this.league = league;
        this.stadium = stadium;
        this.foundedYear = foundedYear;
    }
    @Override
    public String getId(){
        return id;
    }
    @Override
    public String getName(){
        return name;
    }
    public String getCountry(){
        return country;
    }
    public String getLeague(){
        return league;
    }
    public String getStadium() { return stadium; };
    public int getFoundedYear() { return foundedYear; }
    @Override
    public String toString(){
        return name + " (" + country + ", " + league + ", Est." + foundedYear + ")";
    }
}
