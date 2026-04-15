package com.example.spacecolony.model;

public class Medic extends CrewMember {

    public Medic(String name) {
        super(name, 7, 2, 18);
        this.specialization = "Medic";
    }

    @Override
    public String getColor() {
        return "Green";
    }

    @Override
    public int getImageResource() {
        return android.R.drawable.ic_menu_add;
    }

    @Override
    public boolean hasBonusOnMission(String missionType) {
        return missionType.toLowerCase().contains("medical") ||
                missionType.toLowerCase().contains("biological") ||
                missionType.toLowerCase().contains("plague") ||
                missionType.toLowerCase().contains("disease");
    }

    @Override
    public int getMissionBonus(String missionType) {
        return hasBonusOnMission(missionType) ? 2 : 0;
    }


    public int heal(CrewMember target) {
        int healAmount = 5 + experience;
        int actualHeal = Math.min(healAmount, target.maxEnergy - target.energy);
        target.energy = Math.min(target.maxEnergy, target.energy + healAmount);
        return actualHeal;
    }
}
