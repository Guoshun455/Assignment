package com.example.week11.repository;

import com.example.week11.model.Team;
import java.util.List;
public class TeamRepository extends Repository<Team> {
    public List<Team> filterByLeague(String league) {
        return filter(team -> team.getLeague().equalsIgnoreCase(league));
    }
    public List<Team> filterByCountry(String country) {
        return filter(team -> team.getCountry().equalsIgnoreCase(country));
    }
    public List<Team> sortByFoundedYear() {
        return sort((t1, t2) -> Integer.compare(t1.getFoundedYear(), t2.getFoundedYear()));
    }
}
