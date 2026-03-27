package com.example.week11.fragment;

import com.example.week11.data.DataProvider;
import com.example.week11.iterator.TeamIterator;
import com.example.week11.model.Team;
import com.example.week11.repository.TeamRepository;
import java.util.ArrayList;
import java.util.List;

public class TeamsFragment extends BaseFragment<Team> {
    private TeamRepository repo = new TeamRepository();

    @Override protected void initData() {
        DataProvider.createTeamProvider().getData().forEach(repo::add);
    }

    @Override protected String[] getSortOptions() {
        return new String[]{"By Name", "By League"};
    }

    @Override protected void showAll() {
        List<String> list = new ArrayList<>();
        TeamIterator it = new TeamIterator(repo.getAll());
        while (it.hasNext()) list.add(it.next().toString());
        updateList(list);
    }

    @Override protected void search() {
        String q = searchInput.getText().toString().toLowerCase();
        List<String> r = new ArrayList<>();
        repo.filter(t -> t.getName().toLowerCase().contains(q) ||
                        t.getLeague().toLowerCase().contains(q))
                .forEach(t -> r.add(t.toString()));
        updateList(r);
    }

    @Override protected void sort(int type) {
        List<String> r = new ArrayList<>();
        if (type == 0) repo.sort((a,b) -> a.getName().compareToIgnoreCase(b.getName()));
        else repo.sort((a,b) -> a.getLeague().compareToIgnoreCase(b.getLeague()));
        repo.getAll().forEach(t -> r.add(t.toString()));
        updateList(r);
    }
}
