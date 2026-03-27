package com.example.week11.iterator;

import com.example.week11.model.Player;
import com.example.week11.model.Team;

import java.util.List;
public class PlayerIterator implements CustomIterator<Player> {
    private List<Player> players;
    private int position;
    public PlayerIterator(List<Player> players) {
        if (players == null)
            throw new IllegalArgumentException("Players list cannot be null");
        this.players = players;
        this.position = 0;
    }
    @Override
    public boolean hasNext(){
        return position < players.size();
    }
    @Override
    public Player next() {
        if (!hasNext())
            throw new java.util.NoSuchElementException("No more players");
        return players.get(position++);
    }
}
