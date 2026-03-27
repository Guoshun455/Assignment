package com.example.week11.iterator;
import com.example.week11.model.Match;
import java.util.List;
public class MatchIterator implements CustomIterator<Match> {
    private List<Match> matches;
    private int position;
    public MatchIterator(List<Match> matches) {
        if (matches == null)
            throw new IllegalArgumentException("Matches list cannot be null");
        this.matches = matches;
        this.position = 0;
    }
    @Override
    public boolean hasNext(){
        return position < matches.size();
    }
    @Override
    public Match next() {
        if (!hasNext())
            throw new java.util.NoSuchElementException("No more matches");
        return matches.get(position++);
    }
}
