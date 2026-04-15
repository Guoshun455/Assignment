package com.example.spacecolony.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacecolony.R;
import com.example.spacecolony.model.CrewMember;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CrewAdapter extends RecyclerView.Adapter<CrewAdapter.CrewViewHolder> {

    // 选择模式
    public static final int SELECTION_MODE_NONE = 0;      // 无选择
    public static final int SELECTION_MODE_MULTIPLE = 1; // 多选（用于选择任务小队）
    public static final int SELECTION_MODE_SINGLE = 2;   // 单选（用于选择行动船员）
    public static final int SELECTION_MODE_ACTOR = 3;    // 选择行动者（Tactical Combat）

    private List<CrewMember> crewList;
    private Set<Integer> selectedIds;
    private OnCrewClickListener listener;
    private int selectionMode;
    private int selectedActorId = -1;  // 当前选中的行动船员
    private boolean missionActive = false; // 是否处于任务中

    public interface OnCrewClickListener {
        void onCrewClick(CrewMember crew, int position);
        void onCrewLongClick(CrewMember crew, int position);
        void onSelectionChanged(Set<Integer> selectedIds);
    }

    public CrewAdapter(List<CrewMember> crewList, boolean showCheckbox) {
        this.crewList = new ArrayList<>();
        if (crewList != null) {
            this.crewList.addAll(crewList);
        }
        this.selectedIds = new HashSet<>();
        this.selectionMode = showCheckbox ? SELECTION_MODE_MULTIPLE : SELECTION_MODE_NONE;
    }

    public void setOnCrewClickListener(OnCrewClickListener listener) {
        this.listener = listener;
    }

    public void setCrewList(List<CrewMember> crewList) {
        this.crewList.clear();
        if (crewList != null) {
            this.crewList.addAll(crewList);
        }
        // 清除已选择但不在新列表中的ID
        Set<Integer> validIds = new HashSet<>();
        for (CrewMember cm : this.crewList) {
            validIds.add(cm.getId());
        }
        selectedIds.retainAll(validIds);
        if (!validIds.contains(selectedActorId)) {
            selectedActorId = -1;
        }

        notifyDataSetChanged();

        if (listener != null && selectionMode == SELECTION_MODE_MULTIPLE) {
            listener.onSelectionChanged(selectedIds);
        }
    }

    public void clearSelection() {
        selectedIds.clear();
        selectedActorId = -1;
        notifyDataSetChanged();
        if (listener != null) {
            listener.onSelectionChanged(selectedIds);
        }
    }

    public Set<Integer> getSelectedIds() {
        return new HashSet<>(selectedIds);
    }

    public List<CrewMember> getSelectedCrew() {
        List<CrewMember> selected = new ArrayList<>();
        for (CrewMember cm : crewList) {
            if (selectedIds.contains(cm.getId())) {
                selected.add(cm);
            }
        }
        return selected;
    }

    // 设置选择模式
    public void setSelectionMode(int mode) {
        this.selectionMode = mode;
        if (mode != SELECTION_MODE_MULTIPLE) {
            selectedIds.clear();
        }
        if (mode != SELECTION_MODE_ACTOR) {
            selectedActorId = -1;
        }
        notifyDataSetChanged();
    }

    // 设置当前选中的行动船员（用于高亮显示）
    public void setSelectedActor(int id) {
        this.selectedActorId = id;
        notifyDataSetChanged();
    }

    public void clearActorSelection() {
        this.selectedActorId = -1;
        notifyDataSetChanged();
    }

    public int getSelectedActorId() {
        return selectedActorId;
    }

    public void setMissionActive(boolean active) {
        this.missionActive = active;
        notifyDataSetChanged();
    }

    public void toggleSelection(int id) {
        if (selectedIds.contains(id)) {
            selectedIds.remove(id);
        } else {
            selectedIds.add(id);
        }
        notifyDataSetChanged();
        if (listener != null) {
            listener.onSelectionChanged(selectedIds);
        }
    }

    // 关键新增：切换行动船员选择（支持取消选择）
    public void toggleActorSelection(int id) {
        if (selectedActorId == id) {
            // 再次点击同一船员，取消选择
            selectedActorId = -1;
        } else {
            // 选择新船员
            selectedActorId = id;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CrewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crew_member, parent, false);
        return new CrewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CrewViewHolder holder, int position) {
        CrewMember cm = crewList.get(position);
        boolean isSelected = selectedIds.contains(cm.getId());
        boolean isActor = (cm.getId() == selectedActorId);
        boolean canAct = missionActive && !cm.isDefeated() && !cm.hasActedThisTurn();

        holder.bind(cm, isSelected, isActor, canAct);
    }

    @Override
    public int getItemCount() {
        return crewList.size();
    }

    class CrewViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView crewImage;
        TextView nameText;
        TextView specText;
        TextView statsText;
        TextView locationText;
        TextView statusText;
        CheckBox checkBox;
        View colorIndicator;

        CrewViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            crewImage = itemView.findViewById(R.id.crew_image);
            nameText = itemView.findViewById(R.id.crew_name);
            specText = itemView.findViewById(R.id.crew_specialization);
            statsText = itemView.findViewById(R.id.crew_stats);
            locationText = itemView.findViewById(R.id.crew_location);
            statusText = itemView.findViewById(R.id.status_text);
            checkBox = itemView.findViewById(R.id.crew_checkbox);
            colorIndicator = itemView.findViewById(R.id.color_indicator);

            if (statusText == null) {
                statusText = new TextView(itemView.getContext());
            }
        }

        void bind(CrewMember cm, boolean isSelected, boolean isActor, boolean canAct) {
            nameText.setText(cm.getName());
            specText.setText(cm.getSpecialization());
            locationText.setText("📍 " + cm.getCurrentLocation());

            String stats = String.format("⚔️%d(+%d) 🛡️%d ❤️%d/%d ⭐%d",
                    cm.getEffectiveSkill(), cm.getExperience(),
                    cm.getResilience(), cm.getEnergy(), cm.getMaxEnergy(),
                    cm.getExperience());
            statsText.setText(stats);

            int color = getColorForSpecialization(cm.getColor());
            colorIndicator.setBackgroundColor(color);
            crewImage.setImageResource(getImageForSpecialization(cm.getSpecialization()));

            // 设置状态文字
            if (missionActive) {
                statusText.setVisibility(View.VISIBLE);
                if (cm.isDefeated()) {
                    statusText.setText("💀 DEFEATED");
                    statusText.setTextColor(Color.parseColor("#FF4444"));
                } else if (cm.hasActedThisTurn()) {
                    statusText.setText("✓ ACTED");
                    statusText.setTextColor(Color.parseColor("#888888"));
                } else if (isActor) {
                    statusText.setText("▶ SELECTED (tap to cancel)");
                    statusText.setTextColor(Color.parseColor("#00FF00"));
                } else {
                    statusText.setText("READY");
                    statusText.setTextColor(Color.parseColor("#44FF44"));
                }
            } else {
                statusText.setVisibility(View.GONE);
            }

            // 设置CheckBox
            checkBox.setVisibility(selectionMode == SELECTION_MODE_MULTIPLE ? View.VISIBLE : View.GONE);
            checkBox.setChecked(isSelected);
            checkBox.setOnClickListener(v -> {
                if (selectionMode == SELECTION_MODE_MULTIPLE) {
                    toggleSelection(cm.getId());
                }
            });

            // 设置卡片背景色
            int bgColor;
            if (isActor) {
                bgColor = Color.parseColor("#2E7D32"); // 深绿色 - 当前选中行动者
            } else if (isSelected) {
                bgColor = Color.parseColor("#E3F2FD"); // 浅蓝色 - 普通选中
            } else if (cm.isDefeated()) {
                bgColor = Color.parseColor("#3E2723"); // 深棕色 - 战败
            } else if (missionActive && cm.hasActedThisTurn()) {
                bgColor = Color.parseColor("#424242"); // 灰色 - 已行动
            } else {
                bgColor = Color.WHITE;
            }
            cardView.setCardBackgroundColor(bgColor);

            // 关键修复：点击处理 - 只通知 Fragment，不自动 toggle
            itemView.setOnClickListener(v -> {
                if (selectionMode == SELECTION_MODE_ACTOR && missionActive) {
                    if (cm.isDefeated()) return;
                    if (cm.hasActedThisTurn()) return;

                    // 关键：不要在这里 toggle，只通知 Fragment，让 Fragment 控制状态
                    if (listener != null) {
                        listener.onCrewClick(cm, getAdapterPosition());
                    }
                } else if (selectionMode == SELECTION_MODE_MULTIPLE) {
                    toggleSelection(cm.getId());
                } else if (listener != null) {
                    listener.onCrewClick(cm, getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onCrewLongClick(cm, getAdapterPosition());
                    return true;
                }
                return false;
            });

            // 设置可点击状态
            if (selectionMode == SELECTION_MODE_ACTOR) {
                boolean clickable = !cm.isDefeated() && !cm.hasActedThisTurn();
                itemView.setAlpha(clickable ? 1.0f : 0.5f);
                itemView.setClickable(clickable);
            } else {
                itemView.setAlpha(1.0f);
                itemView.setClickable(true);
            }
        }

        private int getColorForSpecialization(String colorName) {
            if (colorName == null) return Color.GRAY;
            switch (colorName) {
                case "Blue": return Color.parseColor("#2196F3");
                case "Yellow": return Color.parseColor("#FFEB3B");
                case "Green": return Color.parseColor("#4CAF50");
                case "Purple": return Color.parseColor("#9C27B0");
                case "Red": return Color.parseColor("#F44336");
                default: return Color.GRAY;
            }
        }

        private int getImageForSpecialization(String specialization) {
            if (specialization == null) return android.R.drawable.ic_menu_help;
            switch (specialization) {
                case "Pilot": return android.R.drawable.ic_menu_compass;
                case "Engineer": return android.R.drawable.ic_menu_preferences;
                case "Medic": return android.R.drawable.ic_menu_add;
                case "Scientist": return android.R.drawable.ic_menu_search;
                case "Soldier": return android.R.drawable.ic_menu_close_clear_cancel;
                default: return android.R.drawable.ic_menu_help;
            }
        }
    }
}