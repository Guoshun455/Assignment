package com.example.spacecolony.model;

public class Soldier extends CrewMember {

    public Soldier(String name) {
        super(name, 9, 0, 16);
        this.specialization = "Soldier";
    }

    @Override
    public String getColor() {
        return "Red";
    }

    @Override
    public int getImageResource() {
        return android.R.drawable.ic_menu_close_clear_cancel;
    }

    @Override
    public boolean hasBonusOnMission(String missionType) {
        return missionType.toLowerCase().contains("combat") ||
                missionType.toLowerCase().contains("alien") ||
                missionType.toLowerCase().contains("attack") ||
                missionType.toLowerCase().contains("invasion");
    }

    @Override
    public int getMissionBonus(String missionType) {
        return hasBonusOnMission(missionType) ? 2 : 0;
    }

    @Override
    public int act() {
        return getEffectiveSkill() + (int)(Math.random() * 2);
    }
}