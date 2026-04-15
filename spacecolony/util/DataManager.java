package com.example.spacecolony.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.spacecolony.model.CrewMember;
import com.example.spacecolony.model.Storage;
import com.example.spacecolony.model.Pilot;
import com.example.spacecolony.model.Engineer;
import com.example.spacecolony.model.Medic;
import com.example.spacecolony.model.Scientist;
import com.example.spacecolony.model.Soldier;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static final String PREFS_NAME = "SpaceColonyData";
    private static final String KEY_CREW_DATA = "crew_data";
    private static final String KEY_MISSION_COUNT = "mission_count";
    private static final String KEY_RECRUITED_COUNT = "recruited_count";
    private static final String KEY_STORAGE_NAME = "storage_name";

    private final SharedPreferences prefs;
    private final Gson gson;

    public DataManager(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void saveGameData() {
        Storage storage = Storage.getInstance();
        SharedPreferences.Editor editor = prefs.edit();

        List<CrewMemberData> crewDataList = new ArrayList<>();
        for (CrewMember cm : storage.listAllCrewMembers()) {
            crewDataList.add(new CrewMemberData(cm));
        }

        editor.putString(KEY_CREW_DATA, gson.toJson(crewDataList));
        editor.putInt(KEY_MISSION_COUNT, storage.getCompletedMissions());
        editor.putInt(KEY_RECRUITED_COUNT, storage.getTotalCrewRecruited());
        editor.putString(KEY_STORAGE_NAME, storage.getName());

        editor.apply();
    }

    public boolean loadGameData() {
        if (!hasSavedData()) return false;

        String crewJson = prefs.getString(KEY_CREW_DATA, null);
        int completedMissions = prefs.getInt(KEY_MISSION_COUNT, 0);
        int totalRecruited = prefs.getInt(KEY_RECRUITED_COUNT, 0);
        String storageName = prefs.getString(KEY_STORAGE_NAME, "Main Colony");

        // 重置并创建新实例
        Storage.resetInstance();
        Storage newStorage = Storage.getInstance();

        // 恢复船员（不通过addCrewMember，直接放入map以保留原始状态）
        if (crewJson != null) {
            Type listType = new TypeToken<List<CrewMemberData>>(){}.getType();
            List<CrewMemberData> crewDataList = gson.fromJson(crewJson, listType);

            if (crewDataList != null) {
                // 找到最大ID以重置计数器
                int maxId = 0;
                for (CrewMemberData data : crewDataList) {
                    if (data.id > maxId) maxId = data.id;
                }

                // 重置ID计数器
                CrewMember.resetIdCounter();
                // 临时增加计数器到maxId
                while (CrewMember.getNumberOfCreated() < maxId) {
                    // 使用反射或创建临时对象来增加计数器
                    try {
                        java.lang.reflect.Field field = CrewMember.class.getDeclaredField("idCounter");
                        field.setAccessible(true);
                        field.setInt(null, maxId);
                        break;
                    } catch (Exception e) {
                        // 如果反射失败，创建临时对象
                        new Pilot("Temp").setId(0);
                    }
                }

                for (CrewMemberData data : crewDataList) {
                    CrewMember cm = createCrewMemberFromData(data);
                    if (cm != null) {
                        // 直接设置ID（不通过构造函数）
                        cm.setId(data.id);
                        // 恢复所有状态
                        restoreCrewStats(cm, data);
                        // 直接放入storage（不调用addCrewMember以避免重置位置）
                        newStorage.getCrewMap().put(cm.getId(), cm);
                    }
                }
            }
        }

        // 恢复统计信息（通过反射设置，因为increment方法会触发其他逻辑）
        try {
            java.lang.reflect.Field completedField = Storage.class.getDeclaredField("completedMissions");
            completedField.setAccessible(true);
            completedField.setInt(newStorage, completedMissions);

            java.lang.reflect.Field recruitedField = Storage.class.getDeclaredField("totalCrewRecruited");
            recruitedField.setAccessible(true);
            recruitedField.setInt(newStorage, totalRecruited);
        } catch (Exception e) {
            e.printStackTrace();
            // 如果反射失败，使用increment方法（不完全准确但可用）
            for (int i = 0; i < completedMissions; i++) {
                newStorage.incrementCompletedMissions();
            }
        }

        return true;
    }

    public boolean hasSavedData() {
        return prefs.contains(KEY_CREW_DATA);
    }

    public void clearSavedData() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    private CrewMember createCrewMemberFromData(CrewMemberData data) {
        switch (data.specialization) {
            case "Pilot": return new Pilot(data.name);
            case "Engineer": return new Engineer(data.name);
            case "Medic": return new Medic(data.name);
            case "Scientist": return new Scientist(data.name);
            case "Soldier": return new Soldier(data.name);
            default: return null;
        }
    }

    private void restoreCrewStats(CrewMember cm, CrewMemberData data) {
        // 恢复经验值
        int expDiff = data.experience - cm.getExperience();
        if (expDiff > 0) {
            for (int i = 0; i < expDiff; i++) cm.gainExperience(1);
        } else if (expDiff < 0) {
            for (int i = 0; i < -expDiff; i++) cm.gainExperience(-1);
        }

        // 恢复能量
        try {
            java.lang.reflect.Field energyField = CrewMember.class.getDeclaredField("energy");
            energyField.setAccessible(true);
            energyField.setInt(cm, data.energy);
        } catch (Exception e) {
            e.printStackTrace();
            // 如果失败，使用restoreEnergy（会设置为maxEnergy，不完全准确）
            if (data.energy < cm.getMaxEnergy()) {
                // 先恢复满，然后扣减（近似）
                cm.restoreEnergy();
                int damage = cm.getMaxEnergy() - data.energy;
                cm.defend(0); // 这不会减少能量，需要其他方式
            }
        }

        // 恢复位置
        cm.setCurrentLocation(data.currentLocation);
        cm.setInMedbay(data.isInMedbay);

        // 恢复统计数据
        try {
            java.lang.reflect.Field missionsField = CrewMember.class.getDeclaredField("missionsCompleted");
            missionsField.setAccessible(true);
            missionsField.setInt(cm, data.missionsCompleted);

            java.lang.reflect.Field wonField = CrewMember.class.getDeclaredField("missionsWon");
            wonField.setAccessible(true);
            wonField.setInt(cm, data.missionsWon);

            java.lang.reflect.Field trainingField = CrewMember.class.getDeclaredField("trainingSessions");
            trainingField.setAccessible(true);
            trainingField.setInt(cm, data.trainingSessions);

            java.lang.reflect.Field defeatedField = CrewMember.class.getDeclaredField("timesDefeated");
            defeatedField.setAccessible(true);
            defeatedField.setInt(cm, data.timesDefeated);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class CrewMemberData {
        int id;
        String name;
        String specialization;
        int experience;
        int energy;
        int missionsCompleted;
        int missionsWon;
        int trainingSessions;
        int timesDefeated;
        String currentLocation;
        boolean isInMedbay;

        CrewMemberData(CrewMember cm) {
            this.id = cm.getId();
            this.name = cm.getName();
            this.specialization = cm.getSpecialization();
            this.experience = cm.getExperience();
            this.energy = cm.getEnergy();
            this.missionsCompleted = cm.getMissionsCompleted();
            this.missionsWon = cm.getMissionsWon();
            this.trainingSessions = cm.getTrainingSessions();
            this.timesDefeated = cm.getTimesDefeated();
            this.currentLocation = cm.getCurrentLocation();
            this.isInMedbay = cm.isInMedbay();
        }
    }
}