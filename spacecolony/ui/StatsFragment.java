package com.example.spacecolony.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacecolony.R;
import com.example.spacecolony.adapter.CrewAdapter;
import com.example.spacecolony.model.CrewMember;
import com.example.spacecolony.model.Storage;

import java.util.List;

public class StatsFragment extends Fragment {

    private TextView colonyStatsText;
    private RecyclerView recyclerView;
    private CrewAdapter adapter;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        colonyStatsText = view.findViewById(R.id.colony_stats);
        recyclerView = view.findViewById(R.id.recycler_stats);

        adapter = new CrewAdapter(null, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        refreshStats();
    }

    // ✅ 新增：获取Storage单例
    private Storage getStorage() {
        return Storage.getInstance();
    }

    private void refreshStats() {
        Storage.ColonyStats stats = getStorage().getColonyStats();

        String statsText = String.format(
                "🚀 COLONY STATISTICS 🚀\n\n" +
                        "Total Crew Active: %d\n" +
                        "Total Recruited: %d\n" +
                        "Missions Completed: %d\n" +
                        "Total Victories: %d\n" +
                        "Training Sessions: %d\n" +
                        "Defeated (Medbay): %d\n" +
                        "Success Rate: %.1f%%",
                stats.totalCrew,
                stats.totalRecruited,
                stats.totalMissions,
                stats.totalVictories,
                stats.totalTrainingSessions,
                stats.totalDefeated,
                stats.totalMissions > 0 ? (stats.totalVictories * 100.0 / stats.totalMissions) : 0
        );

        colonyStatsText.setText(statsText);

        // ✅ 使用listAllCrewMembers（之前叫listCrewMembers）
        List<CrewMember> allCrew = getStorage().listAllCrewMembers();
        adapter.setCrewList(allCrew);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshStats();
    }
}
