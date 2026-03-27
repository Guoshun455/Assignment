package com.example.week11.iterator;

import com.example.week11.model.Team;
import java.util.List;
public class TeamIterator implements CustomIterator<Team> {
    private List<Team> teams;
    private int position;
    public TeamIterator(List<Team> teams){
        if (teams == null)
            throw new IllegalArgumentException("Teams list cannot be null");
        this.teams = teams;
        this.position = 0;
    }
    @Override
    public boolean hasNext(){
        return position < teams.size();
    }
    @Override
    public Team next(){
        if (!hasNext())
            throw new java.util.NoSuchElementException("No more teams");
        return teams.get(position++);
    }
}
