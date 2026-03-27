package com.example.week11.model;

import java.util.UUID;
public class Player implements SoccerEntity{
    private String id;
    private String name;
    private int age;
    private String nationality;
    private String position;
    private String team;
    private int jerseyNumber;
    public Player(String name, int age, String nationality, String position, String team, int jerseyNumber){
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Player name cannot be null or empty");
        if (age < 15 || age > 50)
            throw new IllegalArgumentException("Invalid age");
        if (position == null || position.trim().isEmpty())
            throw new IllegalArgumentException("Position cannot be null or empty");
        if (team == null || team.trim().isEmpty())
            throw new IllegalArgumentException("Team cannot be null or empty");
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.age = age;
        this.nationality = nationality;
        this.position = position;
        this.team = team;
        this.jerseyNumber = jerseyNumber;
    }
    @Override
    public String getId(){
        return id;
    }
    @Override
    public String getName(){
        return name;
    }
    public int getAge() {
        return age;
    }
    public String getNationality() {
        return nationality;
    }
    public String getPosition(){
        return position;
    }
    public String getTeam(){
        return team;
    }
    public int getJerseyNumber() {
        return jerseyNumber;
    }
    @Override
    public String toString(){
        return name + " (#" + jerseyNumber + ", " + position + ", " + team + "," + age + "yo)";
    }

}
