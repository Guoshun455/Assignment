package com.example.spacecolony.model;

public class Scientist extends CrewMember {

    public Scientist(String name) {
        super(name, 8, 1, 17);
        this.specialization = "Scientist";
    }

    @Override
    public String getColor() {
        return "Purple";
    }

    @Override
    public int getImageResource() {
        return android.R.drawable.ic_menu_search;
    }

    @Override
    public boolean hasBonusOnMission(String missionType) {
        return missionType.toLowerCase().contains("research") ||
                missionType.toLowerCase().contains("anomaly") ||
                missionType.toLowerCase().contains("solar") ||
                missionType.toLowerCase().contains("radiation");
    }

    @Override
    public int getMissionBonus(String missionType) {
        return hasBonusOnMission(missionType) ? 2 : 0;
    }

    @Override
    public int act() {
        int baseDamage = super.act();
        if (Math.random() < 0.2) {
            baseDamage += 3;
        }
        return baseDamage;
    }
}
