package com.example.week11.fragment;

import com.example.week11.data.DataProvider;
import com.example.week11.iterator.PlayerIterator;
import com.example.week11.model.Player;
import com.example.week11.repository.PlayerRepository;
import java.util.ArrayList;
import java.util.List;

public class PlayersFragment extends BaseFragment<Player> {
    private PlayerRepository repo = new PlayerRepository();

    @Override protected void initData() {
        DataProvider.createPlayerProvider().getData().forEach(repo::add);
    }

    @Override protected String[] getSortOptions() {
        return new String[]{"By Name", "By Team"};
    }

    @Override protected void showAll() {
        List<String> list = new ArrayList<>();
        PlayerIterator it = new PlayerIterator(repo.getAll());
        while (it.hasNext()) list.add(it.next().toString());
        updateList(list);
    }

    @Override protected void search() {
        String q = searchInput.getText().toString().toLowerCase();
        List<String> r = new ArrayList<>();
        repo.filter(p -> p.getName().toLowerCase().contains(q) ||
                        p.getTeam().toLowerCase().contains(q))
                .forEach(p -> r.add(p.toString()));
        updateList(r);
    }

    @Override protected void sort(int type) {
        List<String> r = new ArrayList<>();
        if (type == 0) repo.sort((a,b) -> a.getName().compareToIgnoreCase(b.getName()));
        else repo.sort((a,b) -> a.getTeam().compareToIgnoreCase(b.getTeam()));
        repo.getAll().forEach(p -> r.add(p.toString()));
        updateList(r);
    }
}
