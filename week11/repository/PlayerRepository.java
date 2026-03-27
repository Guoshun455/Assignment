package com.example.week11.repository;

import com.example.week11.model.Player;
import com.example.week11.model.Team;

import java.util.List;
public class PlayerRepository extends Repository<Player>{
    public List<Player> filterByTeam(String team){
        return filter(player -> player.getTeam().equalsIgnoreCase(team));
    }
    public List<Player> filterByPosition(String position){
        return filter(player -> player.getPosition().equalsIgnoreCase(position));
    }
    public List<Player> sortByFoundedAge() {
        return sort((p1, p2) -> Integer.compare(p1.getAge(), p2.getAge()));
    }
}
