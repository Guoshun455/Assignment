package com.example.week11.model;

import java.util.UUID;
public class Match implements SoccerEntity{
    private String id;
    private String name;
    private String homeTeam;
    private String awayTeam;
    private String score;
    private String league;
    private String date;
    private String stadium;
    public Match(String homeTeam, String awayTeam, String score, String league, String date, String stadium){
        if (homeTeam == null || homeTeam.trim().isEmpty())
            throw new IllegalArgumentException("Home team cannot be null or empty");
        if (awayTeam == null || awayTeam.trim().isEmpty())
            throw new IllegalArgumentException("Away team cannot be null or empty");
        if (score == null || score.trim().isEmpty())
            throw new IllegalArgumentException("Score cannot be null or empty");
        this.id = UUID.randomUUID().toString();
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.name = homeTeam + " vs" + awayTeam;
        this.score = score;
        this.league = league;
        this.date = date;
        this.stadium = stadium;
    }
    @Override
    public String getId(){
        return id;
    }
    @Override
    public String getName(){
        return name;
    }
    public String getHomeTeam(){
        return homeTeam;
    }
    public String getAwayTeam(){
        return awayTeam;
    }
    public String getScore(){
        return score;
    }
    public String getLeague() {
        return league;
    }
    public String getDate() {
        return date;
    }
    public String getStadium() {
        return stadium;
    }
    @Override
    public String toString(){
        return homeTeam + " " + score + " " + awayTeam +" (" + league + ", " + date + ")";
    }
}
