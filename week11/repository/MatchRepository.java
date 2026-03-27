package com.example.week11.repository;

import com.example.week11.model.Match;
import com.example.week11.model.Player;

import java.util.List;
public class MatchRepository extends Repository<Match> {
    public List<Match> filterByTeam(String team){
        return filter(match -> match.getHomeTeam().equalsIgnoreCase(team) ||
                match.getAwayTeam().equalsIgnoreCase(team));
    }
    public List<Match> filterByLeague(String league){
        return filter(match -> match.getLeague().equalsIgnoreCase(league));
    }
}
