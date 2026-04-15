package com.example.spacecolony.model;

public class Engineer extends CrewMember {

    public Engineer(String name) {
        super(name, 6, 3, 19);
        this.specialization = "Engineer";
    }

    @Override
    public String getColor() {
        return "Yellow";
    }

    @Override
    public int getImageResource() {
        return android.R.drawable.ic_menu_preferences;
    }

    @Override
    public boolean hasBonusOnMission(String missionType) {
        return missionType.toLowerCase().contains("repair") ||
                missionType.toLowerCase().contains("mechanical") ||
                missionType.toLowerCase().contains("station") ||
                missionType.toLowerCase().contains("heating") ||
                missionType.toLowerCase().contains("leakage");
    }

    @Override
    public int getMissionBonus(String missionType) {
        return hasBonusOnMission(missionType) ? 2 : 0;
    }
}
