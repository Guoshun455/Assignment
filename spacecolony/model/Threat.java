package com.example.spacecolony.model;


import java.io.Serializable;
import java.util.Random;

public class Threat implements Serializable {
    private String name;
    private String type;
    private int skill;
    private int resilience;
    private int energy;
    private int maxEnergy;
    private int missionDifficulty;

    private static final String[] THREAT_TYPES = {
            "Asteroid Storm", "Solar Flare", "Alien Attack", "System Failure",
            "Fire in Kitchen", "Fuel Leakage", "Heating Malfunction",
            "Biological Hazard", "Radiation Spike", "Mechanical Failure"
    };

    private static final Random random = new Random();

    public Threat(int completedMissions) {
        this.missionDifficulty = completedMissions;
        generateThreat();
    }

    private void generateThreat() {
        this.type = THREAT_TYPES[random.nextInt(THREAT_TYPES.length)];
        this.name = generateName();
        this.skill = 4 + (missionDifficulty / 2);
        this.resilience = 2 + (missionDifficulty / 5);
        this.maxEnergy = 25 + (missionDifficulty * 3);
        this.energy = this.maxEnergy;
    }

    private String generateName() {
        String[] prefixes = {"Intense", "Severe", "Moderate", "Critical", "Minor", "Major"};
        String prefix = prefixes[random.nextInt(prefixes.length)];
        return prefix + " " + type;
    }

    public int attack(CrewMember target) {
        int randomBonus = random.nextInt(3);
        int damage = skill + randomBonus;
        if (target.hasBonusOnMission(type)) {
            damage -= 1;
        }
        return Math.max(1, damage);
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

    public String getType() {
        return type;
    }

    public String getName() { return name; }
    public int getSkill() { return skill; }
    public int getResilience() { return resilience; }
    public int getEnergy() { return energy; }
    public int getMaxEnergy() { return maxEnergy; }

    @Override
    public String toString() {
        return String.format("%s (Skill:%d, Res:%d, Energy:%d/%d)",
                name, skill, resilience, energy, maxEnergy);
    }
}
