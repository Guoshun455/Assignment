package com.example.spacecolony.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Storage implements Serializable {
    private static final long serialVersionUID = 1L;

    private static volatile Storage instance;
    private static final Object lock = new Object();

    private String name;
    private HashMap<Integer, CrewMember> crewMembers;
    private int completedMissions;
    private int totalCrewRecruited;

    private Storage(String name) {
        this.name = name;
        this.crewMembers = new HashMap<>();
        this.completedMissions = 0;
        this.totalCrewRecruited = 0;
    }

    public static Storage getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new Storage("SpaceColony");
                }
            }
        }
        return instance;
    }

    public static void setInstance(Storage storage) {
        synchronized (lock) {
            instance = storage;
        }
    }

    public static void resetInstance() {
        synchronized (lock) {
            instance = null;
        }
    }

    public void addCrewMember(CrewMember cm) {
        if (cm != null) {
            // 强制重置所有状态
            cm.setCurrentLocation("Quarters");
            cm.setInMedbay(false);
            cm.restoreEnergy();

            crewMembers.put(cm.getId(), cm);
            totalCrewRecruited++;
        }
    }

    public CrewMember getCrewMember(int id) {
        return crewMembers.get(id);
    }

    public void removeCrewMember(int id) {
        crewMembers.remove(id);
    }

    public List<CrewMember> listAllCrewMembers() {
        return new ArrayList<>(crewMembers.values());
    }

    public List<CrewMember> getCrewByLocation(String location) {
        List<CrewMember> result = new ArrayList<>();
        for (CrewMember cm : crewMembers.values()) {
            if (location.equals(cm.getCurrentLocation())) {
                result.add(cm);
            }
        }
        return result;
    }


    public void moveCrewMember(int id, String newLocation) {
        CrewMember cm = crewMembers.get(id);
        if (cm == null) {
            System.out.println("[ERROR] CrewMember with id " + id + " not found!");
            return;
        }

        String oldLocation = cm.getCurrentLocation();

        // 设置新位置
        cm.setCurrentLocation(newLocation);

        // 如果移动到 Quarters，恢复能量并清除 Medbay 状态
        if ("Quarters".equals(newLocation)) {
            cm.restoreEnergy();
            cm.setInMedbay(false);
        }
        // 如果移动到 Medbay，设置 Medbay 状态
        else if ("Medbay".equals(newLocation)) {
            cm.setInMedbay(true);
        }
        // 如果移动到 Mission 或其他地方，清除 Medbay 状态
        else {
            cm.setInMedbay(false);
        }

        System.out.println("[MOVE] " + cm.getName() + ": " + oldLocation + " -> " + newLocation
                + (cm.isInMedbay() ? " [MEDBAY]" : ""));
    }

    public void incrementCompletedMissions() {
        completedMissions++;
    }

    public ColonyStats getColonyStats() {
        ColonyStats stats = new ColonyStats();
        stats.totalCrew = crewMembers.size();
        stats.totalMissions = completedMissions;
        stats.totalRecruited = totalCrewRecruited;

        int totalWins = 0;
        int totalTraining = 0;
        int defeated = 0;
        int active = 0;

        for (CrewMember cm : crewMembers.values()) {
            totalWins += cm.getMissionsWon();
            totalTraining += cm.getTrainingSessions();
            defeated += cm.getTimesDefeated();
            if (!cm.isInMedbay() && !cm.isDefeated()) active++;
        }

        stats.totalVictories = totalWins;
        stats.totalTrainingSessions = totalTraining;
        stats.totalDefeated = defeated;
        stats.activeCrew = active;

        if (completedMissions > 0) {
            stats.successRate = (totalWins * 100.0 / completedMissions);
        } else {
            stats.successRate = 0.0;
        }

        return stats;
    }

    public String getName() { return name; }
    public int getCompletedMissions() { return completedMissions; }
    public int getTotalCrewRecruited() { return totalCrewRecruited; }
    public HashMap<Integer, CrewMember> getCrewMap() { return crewMembers; }

    public static class ColonyStats {
        public int totalCrew;
        public int activeCrew;
        public int totalRecruited;
        public int totalMissions;
        public int totalVictories;
        public int totalTrainingSessions;
        public int totalDefeated;
        public double successRate;
    }
}