package com.example.spacecolony.model;

public class Pilot extends CrewMember {

    public Pilot(String name) {
        super(name, 5, 4, 20);
        this.specialization = "Pilot";
    }

    @Override
    public String getColor() {
        return "Blue";
    }

    @Override
    public int getImageResource() {
        return android.R.drawable.ic_menu_compass;
    }

    @Override
    public boolean hasBonusOnMission(String missionType) {
        return missionType.toLowerCase().contains("asteroid") ||
                missionType.toLowerCase().contains("navigation") ||
                missionType.toLowerCase().contains("storm");
    }

    @Override
    public int getMissionBonus(String missionType) {
        return hasBonusOnMission(missionType) ? 2 : 0;
    }
}
