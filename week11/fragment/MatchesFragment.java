package com.example.week11.fragment;

import com.example.week11.data.DataProvider;
import com.example.week11.iterator.MatchIterator;
import com.example.week11.model.Match;
import com.example.week11.repository.MatchRepository;
import java.util.ArrayList;
import java.util.List;

public class MatchesFragment extends BaseFragment<Match> {
    private MatchRepository repo = new MatchRepository();

    @Override protected void initData() {
        DataProvider.createMatchProvider().getData().forEach(repo::add);
    }

    @Override protected String[] getSortOptions() {
        return new String[]{"By Date", "By League"};
    }

    @Override protected void showAll() {
        List<String> list = new ArrayList<>();
        MatchIterator it = new MatchIterator(repo.getAll());
        while (it.hasNext()) list.add(it.next().toString());
        updateList(list);
    }

    @Override protected void search() {
        String q = searchInput.getText().toString().toLowerCase();
        List<String> r = new ArrayList<>();
        repo.filter(m -> m.getHomeTeam().toLowerCase().contains(q) ||
                        m.getAwayTeam().toLowerCase().contains(q))
                .forEach(m -> r.add(m.toString()));
        updateList(r);
    }

    @Override protected void sort(int type) {
        List<String> r = new ArrayList<>();
        if (type == 0) repo.sort((a,b) -> a.getDate().compareTo(b.getDate()));
        else repo.sort((a,b) -> a.getLeague().compareToIgnoreCase(b.getLeague()));
        repo.getAll().forEach(m -> r.add(m.toString()));
        updateList(r);
    }
}
