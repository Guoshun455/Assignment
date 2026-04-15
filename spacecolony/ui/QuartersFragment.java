package com.example.spacecolony.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacecolony.R;
import com.example.spacecolony.adapter.CrewAdapter;
import com.example.spacecolony.model.CrewMember;
import com.example.spacecolony.model.Storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class QuartersFragment extends Fragment implements CrewAdapter.OnCrewClickListener {

    private RecyclerView recyclerView;
    private CrewAdapter adapter;
    private TextView emptyText;
    private Button moveToSimButton;
    private Button moveToMissionButton;
    private Button healButton;
    private LinearLayout normalActionsLayout;
    private LinearLayout medbayActionsLayout;
    private TextView tabQuarters;
    private TextView tabMedbay;

    private boolean showingMedbay = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quarters, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_quarters);
        emptyText = view.findViewById(R.id.empty_text);
        moveToSimButton = view.findViewById(R.id.btn_to_simulator);
        moveToMissionButton = view.findViewById(R.id.btn_to_mission);
        healButton = view.findViewById(R.id.btn_heal);
        normalActionsLayout = view.findViewById(R.id.normal_actions);
        medbayActionsLayout = view.findViewById(R.id.medbay_actions);
        tabQuarters = view.findViewById(R.id.tab_quarters);
        tabMedbay = view.findViewById(R.id.tab_medbay);

        // 检查视图是否找到
        if (tabQuarters == null || tabMedbay == null) {
            System.out.println("[ERROR] Tab views not found!");
            return;
        }

        adapter = new CrewAdapter(null, true);
        adapter.setOnCrewClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        moveToSimButton.setOnClickListener(v -> moveSelectedTo("Simulator"));
        moveToMissionButton.setOnClickListener(v -> moveSelectedTo("Mission"));
        healButton.setOnClickListener(v -> healSelected());

        // Tab切换 - 确保点击事件正确绑定
        tabQuarters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("[QuartersFragment] Quarters tab clicked");
                showQuarters();
            }
        });

        tabMedbay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("[QuartersFragment] Medbay tab clicked");
                showMedbay();
            }
        });

        // 默认显示Quarters
        showQuarters();
    }

    private Storage getStorage() {
        return Storage.getInstance();
    }

    private void showQuarters() {
        showingMedbay = false;

        // 视觉反馈 - 使用背景色区分
        tabQuarters.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        tabMedbay.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        // 显示/隐藏对应布局
        normalActionsLayout.setVisibility(View.VISIBLE);
        medbayActionsLayout.setVisibility(View.GONE);

        System.out.println("[QuartersFragment] Showing Quarters");
        refreshCrewList();
    }

    private void showMedbay() {
        showingMedbay = true;

        // 视觉反馈
        tabMedbay.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        tabQuarters.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        // 显示/隐藏对应布局
        normalActionsLayout.setVisibility(View.GONE);
        medbayActionsLayout.setVisibility(View.VISIBLE);

        System.out.println("[QuartersFragment] Showing Medbay");
        refreshMedbayList();
    }

    private void refreshCrewList() {
        // 获取Quarters的船员（排除Medbay）
        List<CrewMember> allInQuarters = getStorage().getCrewByLocation("Quarters");
        List<CrewMember> crew = new ArrayList<>();

        for (CrewMember cm : allInQuarters) {
            if (!cm.isInMedbay()) {
                crew.add(cm);
            }
        }

        System.out.println("[QuartersFragment] Quarters tab: found " + crew.size() + " crew");

        adapter.setCrewList(crew);

        if (crew.isEmpty()) {
            emptyText.setText("No crew in quarters");
            emptyText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            moveToSimButton.setEnabled(false);
            moveToMissionButton.setEnabled(false);
        } else {
            emptyText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void refreshMedbayList() {
        // 获取在Medbay的船员
        List<CrewMember> crew = getStorage().getCrewByLocation("Medbay");

        System.out.println("[QuartersFragment] Medbay tab: found " + crew.size() + " crew");

        adapter.setCrewList(crew);

        if (crew.isEmpty()) {
            emptyText.setText("No crew in medbay");
            emptyText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            healButton.setEnabled(false);
        } else {
            emptyText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            healButton.setEnabled(!adapter.getSelectedIds().isEmpty());
        }
    }

    private void moveSelectedTo(String location) {
        Set<Integer> selected = adapter.getSelectedIds();
        if (selected.isEmpty()) {
            Toast.makeText(requireContext(), "Select crew members first", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int id : selected) {
            getStorage().moveCrewMember(id, location);
        }

        Toast.makeText(requireContext(),
                "Moved " + selected.size() + " crew to " + location, Toast.LENGTH_SHORT).show();

        adapter.clearSelection();

        // 刷新当前显示的列表
        if (showingMedbay) {
            refreshMedbayList();
        } else {
            refreshCrewList();
        }
    }

    private void healSelected() {
        Set<Integer> selected = adapter.getSelectedIds();
        if (selected.isEmpty()) {
            Toast.makeText(requireContext(), "Select crew to heal", Toast.LENGTH_SHORT).show();
            return;
        }

        int healedCount = 0;
        for (int id : selected) {
            CrewMember cm = getStorage().getCrewMember(id);
            if (cm != null && cm.isInMedbay()) {
                // No Death机制：扣除一半经验作为惩罚
                int currentExp = cm.getExperience();
                int penaltyExp = currentExp / 2;

                // 扣除经验
                for (int i = 0; i < penaltyExp; i++) {
                    cm.gainExperience(-1);
                }

                // 恢复状态
                cm.setInMedbay(false);
                cm.restoreEnergy();

                // 移动到Quarters
                getStorage().moveCrewMember(id, "Quarters");

                healedCount++;
            }
        }

        Toast.makeText(requireContext(),
                "Healed " + healedCount + " crew (exp penalty applied)", Toast.LENGTH_SHORT).show();

        adapter.clearSelection();
        refreshMedbayList();
    }

    @Override
    public void onCrewClick(CrewMember crew, int position) {}

    @Override
    public void onCrewLongClick(CrewMember crew, int position) {
        Toast.makeText(requireContext(),
                crew.getName() + " - " + crew.getSpecialization() +
                        "\nHP: " + crew.getEnergy() + "/" + crew.getMaxEnergy() +
                        "\nExp: " + crew.getExperience() +
                        (crew.isInMedbay() ? "\n[IN MEDBAY]" : ""),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSelectionChanged(Set<Integer> selectedIds) {
        boolean hasSelection = !selectedIds.isEmpty();

        if (showingMedbay) {
            healButton.setEnabled(hasSelection);
        } else {
            moveToSimButton.setEnabled(hasSelection);
            moveToMissionButton.setEnabled(hasSelection);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // 根据当前状态刷新对应列表
        if (showingMedbay) {
            refreshMedbayList();
        } else {
            refreshCrewList();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView.setAdapter(null);
        adapter = null;
    }
}