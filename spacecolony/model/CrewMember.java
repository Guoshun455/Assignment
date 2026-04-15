package com.example.spacecolony.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public abstract class CrewMember implements Serializable {
    protected String name;
    protected String specialization;
    protected int skill;
    protected int resilience;
    protected int experience;
    protected int energy;
    protected int maxEnergy;
    protected int id;
    private static int idCounter = 0;

    protected int missionsCompleted = 0;
    protected int missionsWon = 0;
    protected int trainingSessions = 0;
    protected int timesDefeated = 0;
    protected String currentLocation = "Quarters";
    protected boolean isInMedbay = false;


    protected transient boolean defending = false;

    public CrewMember(String name, int skill, int resilience, int maxEnergy) {
        this.id = ++idCounter;
        this.name = name;
        this.skill = skill;
        this.resilience = resilience;
        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy;
        this.experience = 0;
    }


    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        defending = false;
    }
    private boolean actedThisTurn = false;

    public boolean hasActedThisTurn() { return actedThisTurn; }
    public void setActedThisTurn(boolean acted) { this.actedThisTurn = acted; }

    public int getEffectiveSkill() {
        return skill + experience;
    }

    public int act() {
        // 随机性（Randomness bonus）
        int randomBonus = (int)(Math.random() * 3);
        return getEffectiveSkill() + randomBonus;
    }

    public int defend(int incomingDamage) {
        int actualDamage = Math.max(0, incomingDamage - resilience);
        energy -= actualDamage;
        if (energy < 0) energy = 0;
        return actualDamage;
    }

    public boolean isDefeated() {
        return energy <= 0;
    }

    public void restoreEnergy() {
        this.energy = maxEnergy;
    }

    // 训练：增加经验并返回是否成功
    public boolean train() {
        if (!currentLocation.equals("Simulator")) {
            return false;
        }
        experience++;
        trainingSessions++;
        return true;
    }

    // 获得经验（用于任务奖励）
    public void gainExperience(int amount) {
        this.experience += amount;
        if (this.experience < 0) {
            this.experience = 0;
        }
    }

    // 防御状态getter/setter
    public boolean isDefending() {
        return defending;
    }

    public void setDefending(boolean defending) {
        this.defending = defending;
    }

    // 抽象方法（子类实现）
    public abstract String getColor();
    public abstract int getImageResource();
    public abstract boolean hasBonusOnMission(String missionType);
    public abstract int getMissionBonus(String missionType);

    // Getters and Setters
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
    public int getSkill() { return skill; }
    public int getResilience() { return resilience; }
    public int getExperience() { return experience; }
    public int getEnergy() { return energy; }
    public void setEnergy(int energy) {
        this.energy = energy;
        if (this.energy > maxEnergy) {
            this.energy = maxEnergy;
        }
        if (this.energy < 0) {
            this.energy = 0;
        }
    }
    public int getMaxEnergy() { return maxEnergy; }
    public int getId() { return id; }
    public int getMissionsCompleted() { return missionsCompleted; }
    public int getMissionsWon() { return missionsWon; }
    public int getTrainingSessions() { return trainingSessions; }
    public int getTimesDefeated() { return timesDefeated; }
    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String location) { this.currentLocation = location; }
    public boolean isInMedbay() { return isInMedbay; }
    public void setInMedbay(boolean inMedbay) {
        this.isInMedbay = inMedbay;
        if (inMedbay) {
            this.currentLocation = "Medbay";
        }
    }
    public void incrementMissionsCompleted() { missionsCompleted++; }
    public void incrementMissionsWon() { missionsWon++; }
    public void incrementTimesDefeated() { timesDefeated++; }
    public void incrementTrainingSessions() { trainingSessions++; }
    public static int getNumberOfCreated() { return idCounter; }


    public static void resetIdCounter() {
        idCounter = 0;
    }

    public void setId(int id) {
        this.id = id;
        if (id > idCounter) {
            idCounter = id;
        }
    }

    @Override
    public String toString() {
        return String.format("%s [%s] Skill:%d(+%d) Res:%d Energy:%d/%d",
                name, specialization, skill, experience, resilience, energy, maxEnergy);
    }
}