package com.example.spacecolony.model;

import java.util.ArrayList;
import java.util.List;

public class MissionControl {

    private static volatile MissionControl instance;
    private static final Object lock = new Object();

    private List<MissionListener> listeners;
    private Threat currentThreat;
    private List<CrewMember> currentSquad;
    private boolean missionInProgress;
    private StringBuilder missionLog;
    private int currentRound;

    public enum ActionType {
        ATTACK, DEFEND, SPECIAL
    }

    public interface MissionListener {
        void onMissionUpdate(String message);
        void onCrewTurn(CrewMember crew, Threat threat);
        void onThreatTurn(CrewMember target, Threat threat);
        void onMissionComplete(boolean success, List<CrewMember> survivors);
        void onCrewDefeated(CrewMember crew);
        void onRoundStart(int round);
    }

    private MissionControl() {
        this.listeners = new ArrayList<>();
        this.currentSquad = new ArrayList<>();
        this.missionLog = new StringBuilder();
        this.currentRound = 0;
    }

    public static MissionControl getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new MissionControl();
                }
            }
        }
        return instance;
    }

    public static void resetInstance() {
        synchronized (lock) {
            instance = null;
        }
    }

    public static void setInstance(MissionControl mc) {
        synchronized (lock) {
            instance = mc;
        }
    }

    private Storage getStorage() {
        return Storage.getInstance();
    }

    public void addMissionListener(MissionListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeMissionListener(MissionListener listener) {
        listeners.remove(listener);
    }

    public void clearAllListeners() {
        listeners.clear();
    }

    public void launchMission(List<CrewMember> squad) {
        if (squad == null || squad.size() < 2 || squad.size() > 3) {
            throw new IllegalArgumentException("Squad must have 2-3 members");
        }

        for (CrewMember cm : squad) {
            if (cm.isDefeated()) {
                throw new IllegalStateException(cm.getName() + " is defeated and cannot join mission");
            }
            if (cm.isInMedbay()) {
                throw new IllegalStateException(cm.getName() + " is in Medbay and cannot join mission");
            }
        }

        this.currentSquad = new ArrayList<>(squad);
        this.missionInProgress = true;
        this.missionLog = new StringBuilder();
        this.currentRound = 1;

        for (CrewMember cm : currentSquad) {
            cm.setDefending(false);
            cm.setActedThisTurn(false);
        }

        int difficulty = getStorage().getCompletedMissions();
        this.currentThreat = new Threat(difficulty);

        for (CrewMember cm : squad) {
            getStorage().moveCrewMember(cm.getId(), "Mission");
        }

        logMessage("=== MISSION START ===");
        logMessage("Threat: " + currentThreat.getName() +
                " (Skill:" + currentThreat.getSkill() +
                " Res:" + currentThreat.getResilience() +
                " HP:" + currentThreat.getEnergy() + "/" + currentThreat.getMaxEnergy() + ")");
        logMessage("Squad: " + squad.size() + " members");
        for (CrewMember cm : squad) {
            logMessage("  - " + cm.getName() + " (" + cm.getSpecialization() +
                    ") HP:" + cm.getEnergy() + "/" + cm.getMaxEnergy() +
                    " Skill:" + cm.getEffectiveSkill());
        }
        logMessage("");

        notifyRoundStart(currentRound);
    }

    // 关键修复：执行战术回合，修复重复通知和状态问题
    public void executeTacticalTurn(CrewMember actor, ActionType action) {
        if (!missionInProgress || currentThreat == null) {
            return;
        }

        // 检查威胁是否已被击败
        if (currentThreat.isDefeated()) {
            completeMission(true);
            return;
        }

        // 验证actor
        if (!currentSquad.contains(actor)) {
            logMessage("⚠️ " + actor.getName() + " is not in squad");
            return;
        }

        if (actor.isDefeated()) {
            logMessage("⚠️ " + actor.getName() + " is defeated and cannot act");
            return;
        }

        if (actor.hasActedThisTurn()) {
            logMessage("⚠️ " + actor.getName() + " has already acted this turn");
            return;
        }

        // 执行动作
        boolean actionCompleted = false;
        switch (action) {
            case ATTACK:
                executeAttack(actor);
                actionCompleted = true;
                break;
            case DEFEND:
                executeDefend(actor);
                actionCompleted = true;
                break;
            case SPECIAL:
                actionCompleted = executeSpecial(actor);
                break;
        }

        // 如果动作未完成（如Medic治疗失败转为攻击），不继续
        if (!actionCompleted) {
            return;
        }

        // 标记已行动
        actor.setActedThisTurn(true);

        // 检查威胁是否被击败
        if (currentThreat.isDefeated()) {
            completeMission(true);
            return;
        }

        // 威胁反击
        executeThreatRetaliation(actor);

        // 检查任务是否结束（actor被击败或全灭）
        boolean missionEnded = checkMissionEndAfterRetaliation(actor);
        if (missionEnded) {
            return; // 任务已结束，不再继续
        }

        // 检查是否需要结束回合
        if (haveAllActed()) {
            endRound();
        }
        // 否则继续等待玩家选择下一个船员
    }

    // 检查任务是否在反击后结束
    private boolean checkMissionEndAfterRetaliation(CrewMember actor) {
        // 检查actor是否战败
        if (actor.isDefeated()) {
            handleCrewDefeated(actor);
        }

        // 检查是否全灭
        if (areAllDefeated()) {
            completeMission(false);
            return true;
        }

        // 检查威胁是否被击败（可能在反击中死亡）
        if (currentThreat.isDefeated()) {
            completeMission(true);
            return true;
        }

        return false;
    }

    private boolean haveAllActed() {
        for (CrewMember cm : currentSquad) {
            if (!cm.isDefeated() && !cm.hasActedThisTurn()) {
                return false;
            }
        }
        return true;
    }

    public void endRound() {
        if (!missionInProgress) return;

        logMessage("");
        logMessage("--- Enemy Turn ---");

        // 威胁攻击所有存活船员
        for (CrewMember cm : new ArrayList<>(currentSquad)) { // 使用副本避免并发修改
            if (!cm.isDefeated() && !cm.isInMedbay()) { // 关键：跳过Medbay的船员
                int threatDamage = currentThreat.getSkill();
                if (cm.isDefending()) {
                    threatDamage = Math.max(1, threatDamage - 2);
                }
                cm.defend(threatDamage);
                logMessage(currentThreat.getName() + " attacks " + cm.getName() + " for " + threatDamage);
                notifyThreatTurn(cm, currentThreat);

                if (cm.isDefeated()) {
                    handleCrewDefeated(cm);
                }
            }
        }

        // 检查任务是否结束
        if (areAllDefeated()) {
            completeMission(false);
            return;
        }

        if (currentThreat.isDefeated()) {
            completeMission(true);
            return;
        }


        currentRound++;
        logMessage("");
        logMessage("=== ROUND " + currentRound + " ===");

        boolean hasAvailableCrew = false;
        for (CrewMember cm : currentSquad) {
            if (!cm.isDefeated() && !cm.isInMedbay()) {
                cm.setActedThisTurn(false);
                cm.setDefending(false);
                hasAvailableCrew = true;
            }
        }

        if (!hasAvailableCrew) {
            logMessage("❌ No available crew members!");
            completeMission(false);
            return;
        }

        notifyRoundStart(currentRound);
    }

    private void executeAttack(CrewMember actor) {
        int attackPower = actor.getEffectiveSkill();

        if (actor.hasBonusOnMission(currentThreat.getType())) {
            int bonus = actor.getMissionBonus(currentThreat.getType());
            attackPower += bonus;
            logMessage("⭐ " + actor.getName() + " uses specialization advantage! +" + bonus + " damage");
        }

        int randomBonus = (int)(Math.random() * 3);
        attackPower += randomBonus;

        int damageDealt = currentThreat.defend(attackPower);
        logMessage("⚔️ " + actor.getName() + " attacks " + currentThreat.getName() + "!");
        if (randomBonus > 0) {
            logMessage("   🎲 Random bonus: +" + randomBonus);
        }
        logMessage("   Damage: " + attackPower + " - " + currentThreat.getResilience() + " = " + damageDealt);
        logMessage("   Threat HP: " + currentThreat.getEnergy() + "/" + currentThreat.getMaxEnergy());

        notifyCrewTurn(actor, currentThreat);
    }

    private void executeDefend(CrewMember actor) {
        actor.setDefending(true);
        logMessage("🛡️ " + actor.getName() + " takes defensive stance!");
        logMessage("   (Damage will be reduced by 2)");

        notifyCrewTurn(actor, currentThreat);
    }

    // 关键修复：返回boolean表示是否成功执行特殊动作
    private boolean executeSpecial(CrewMember actor) {
        if (actor instanceof Medic) {
            CrewMember lowest = findLowestHealthAlly(actor);
            if (lowest != null) {
                int healAmount = ((Medic) actor).heal(lowest);
                logMessage("💚 " + actor.getName() + " heals " + lowest.getName() + " for " + healAmount + " HP!");
                logMessage("   " + lowest.getName() + " HP: " + lowest.getEnergy() + "/" + lowest.getMaxEnergy());
                notifyCrewTurn(actor, currentThreat);
                return true;
            } else {
                // 没有需要治疗的队友，转为普通攻击
                logMessage("💚 " + actor.getName() + " finds no one needs healing, attacks instead!");
                executeAttack(actor); // 这会调用notifyCrewTurn
                return true;
            }
        } else if (actor instanceof Engineer) {
            logMessage("🔧 " + actor.getName() + " reinforces team armor!");
            for (CrewMember cm : currentSquad) {
                if (cm != actor && !cm.isDefeated()) {
                    cm.setDefending(true);
                }
            }
            notifyCrewTurn(actor, currentThreat);
            return true;
        } else if (actor instanceof Scientist) {
            int specialDamage = actor.getEffectiveSkill() + 5;
            int damageDealt = currentThreat.defend(specialDamage);
            logMessage("🔬 " + actor.getName() + " analyzes weak point!");
            logMessage("   Critical Damage: " + specialDamage + " - " + currentThreat.getResilience() + " = " + damageDealt);
            logMessage("   Threat HP: " + currentThreat.getEnergy() + "/" + currentThreat.getMaxEnergy());
            notifyCrewTurn(actor, currentThreat);
            return true;
        } else if (actor instanceof Pilot) {
            int specialDamage = actor.getEffectiveSkill() + 3;
            int damageDealt = currentThreat.defend(specialDamage);
            logMessage("🚀 " + actor.getName() + " performs precision strike!");
            logMessage("   Damage: " + specialDamage + " - " + currentThreat.getResilience() + " = " + damageDealt);
            logMessage("   Threat HP: " + currentThreat.getEnergy() + "/" + currentThreat.getMaxEnergy());
            notifyCrewTurn(actor, currentThreat);
            return true;
        } else if (actor instanceof Soldier) {
            int specialDamage = actor.getEffectiveSkill() + 4;
            int damageDealt = currentThreat.defend(specialDamage);
            logMessage("💥 " + actor.getName() + " unleashes heavy assault!");
            logMessage("   Damage: " + specialDamage + " - " + currentThreat.getResilience() + " = " + damageDealt);
            logMessage("   Threat HP: " + currentThreat.getEnergy() + "/" + currentThreat.getMaxEnergy());
            notifyCrewTurn(actor, currentThreat);
            return true;
        } else {
            int specialDamage = actor.getEffectiveSkill() + 3;
            int damageDealt = currentThreat.defend(specialDamage);
            logMessage("💥 " + actor.getName() + " uses SPECIAL ATTACK!");
            logMessage("   Damage: " + specialDamage + " - " + currentThreat.getResilience() + " = " + damageDealt);
            notifyCrewTurn(actor, currentThreat);
            return true;
        }
    }

    private CrewMember findLowestHealthAlly(CrewMember exclude) {
        CrewMember lowest = null;
        int minHealthPercent = Integer.MAX_VALUE;
        for (CrewMember cm : currentSquad) {
            if (cm != exclude && !cm.isDefeated()) {
                int healthPercent = (cm.getEnergy() * 100) / cm.getMaxEnergy();
                if (healthPercent < minHealthPercent) {
                    minHealthPercent = healthPercent;
                    lowest = cm;
                }
            }
        }
        return lowest;
    }

    private void executeThreatRetaliation(CrewMember target) {
        if (target.isDefeated()) {
            return;
        }

        int threatDamage = currentThreat.attack(target);

        if (target.isDefending()) {
            threatDamage = Math.max(1, threatDamage - 2);
            logMessage("   🛡️ " + target.getName() + "'s defense reduces damage by 2!");
        }

        int actualDamage = target.defend(threatDamage);
        logMessage("👾 " + currentThreat.getName() + " attacks " + target.getName() + "!");
        logMessage("   Damage: " + threatDamage + " - " + target.getResilience() + " = " + actualDamage);
        logMessage("   " + target.getName() + " HP: " + target.getEnergy() + "/" + target.getMaxEnergy());

        notifyThreatTurn(target, currentThreat);
    }

    // 关键修复：handleCrewDefeated 确保船员被正确标记为 Medbay
    private void handleCrewDefeated(CrewMember cm) {
        logMessage("💀 " + cm.getName() + " has been defeated!");
        notifyCrewDefeated(cm);

        // 关键：先移动到 Medbay，标记状态（必须在恢复能量之前）
        getStorage().moveCrewMember(cm.getId(), "Medbay");
        cm.setInMedbay(true);
        cm.incrementTimesDefeated();

        // 恢复一半能量，但保持在 Medbay
        int halfEnergy = cm.getMaxEnergy() / 2;
        try {
            java.lang.reflect.Field energyField = CrewMember.class.getDeclaredField("energy");
            energyField.setAccessible(true);
            energyField.setInt(cm, halfEnergy);
        } catch (Exception e) {
            // 如果反射失败，使用 defend 调整（不推荐，但备用）
            int currentEnergy = cm.getEnergy();
            if (currentEnergy > halfEnergy) {
                // 通过多次 defend 减少能量（不推荐）
                int damage = currentEnergy - halfEnergy;
                for (int i = 0; i < damage; i++) {
                    cm.defend(1);
                }
            }
        }

        logMessage("🏥 " + cm.getName() + " sent to Medbay for recovery");
    }

    private boolean areAllDefeated() {
        for (CrewMember cm : currentSquad) {
            if (!cm.isDefeated()) {
                return false;
            }
        }
        return true;
    }

    private void completeMission(boolean success) {
        missionInProgress = false;

        if (success) {
            logMessage("");
            logMessage("=== MISSION COMPLETE ===");
            logMessage("✅ Threat neutralized!");
            logMessage("");

            List<CrewMember> survivors = getSurvivors();

            for (CrewMember cm : currentSquad) {
                if (!cm.isDefeated()) {
                    // 存活船员：获得奖励，移回 Quarters（任务完成，返回宿舍）
                    cm.gainExperience(1);
                    cm.incrementMissionsCompleted();
                    cm.incrementMissionsWon();

                    getStorage().moveCrewMember(cm.getId(), "Mission");
                    cm.setInMedbay(false);
                    logMessage("⭐ " + cm.getName() + " gains 1 XP (Total: " +
                            cm.getExperience() + ", Effective Skill: " + cm.getEffectiveSkill() + ")");
                } else {
                    // 死亡船员：送到 Med Bay 恢复
                    getStorage().moveCrewMember(cm.getId(), "Medbay");
                    cm.setInMedbay(true);
                    // 恢复一半 energy（根据你的描述，这是预期行为）
                    cm.setEnergy(cm.getMaxEnergy() / 2);
                    logMessage("💀 " + cm.getName() + " is in Medbay recovering");
                }
            }

            getStorage().incrementCompletedMissions();
            notifyMissionComplete(true, survivors);

        } else {
            logMessage("");
            logMessage("=== MISSION FAILED ===");
            logMessage("❌ All crew members defeated!");
            logMessage("");

            for (CrewMember cm : currentSquad) {
                if (cm.isDefeated()) {
                    if (!"Medbay".equals(cm.getCurrentLocation())) {
                        getStorage().moveCrewMember(cm.getId(), "Medbay");
                        cm.setInMedbay(true);
                    }
                    logMessage("💀 " + cm.getName() + " requires medical attention");
                }
            }

            notifyMissionComplete(false, new ArrayList<>());
        }

        currentThreat = null;
        currentSquad.clear();
        currentRound = 0;
    }
    // 添加在 completeMission 方法之后
    public void executeAutoMission(List<CrewMember> squad, AutoMissionCallback callback) {
        new Thread(() -> {
            currentSquad = new ArrayList<>(squad);
            missionInProgress = true;
            missionLog = new StringBuilder();
            currentRound = 1;

            for (CrewMember cm : currentSquad) {
                cm.setDefending(false);
                cm.setActedThisTurn(false);
            }

            int difficulty = getStorage().getCompletedMissions();
            currentThreat = new Threat(difficulty);

            for (CrewMember cm : squad) {
                getStorage().moveCrewMember(cm.getId(), "Mission");
            }

            logMessage("=== AUTO MISSION START ===");
            logMessage("Threat: " + currentThreat.getName() +
                    " (Skill:" + currentThreat.getSkill() +
                    " Res:" + currentThreat.getResilience() +
                    " HP:" + currentThreat.getEnergy() + "/" + currentThreat.getMaxEnergy() + ")");
            logMessage("Squad: " + squad.size() + " members");
            for (CrewMember cm : squad) {
                logMessage("  - " + cm.getName() + " (" + cm.getSpecialization() +
                        ") HP:" + cm.getEnergy() + "/" + cm.getMaxEnergy() +
                        " Skill:" + cm.getEffectiveSkill());
            }
            logMessage("");

            // 自动执行所有回合
            while (missionInProgress && !currentSquad.isEmpty() && currentThreat != null && !currentThreat.isDefeated()) {
                // 船员回合
                for (CrewMember cm : currentSquad) {
                    if (cm.isDefeated() || cm.isInMedbay()) continue;

                    // 自动选择动作
                    ActionType action;
                    if (cm.getEnergy() < cm.getMaxEnergy() * 0.3) {
                        action = ActionType.DEFEND;
                        cm.setDefending(true);
                        logMessage(cm.getName() + " defends!");
                    } else {
                        action = ActionType.ATTACK;
                        int attackPower = cm.getEffectiveSkill();
                        int randomBonus = (int)(Math.random() * 3);
                        attackPower += randomBonus;
                        int actualDamage = currentThreat.defend(attackPower);
                        logMessage(cm.getName() + " attacks for " + actualDamage + " damage!");
                    }

                    cm.setActedThisTurn(true);

                    // 检查威胁是否被击败
                    if (currentThreat.isDefeated()) {
                        break;
                    }

                    // 威胁反击
                    if (!cm.isDefeated()) {
                        int threatDamage = currentThreat.attack(cm);
                        int actualDamage = cm.defend(threatDamage);
                        logMessage(currentThreat.getName() + " retaliates for " + actualDamage + " damage!");

                        if (cm.isDefeated()) {
                            handleCrewDefeated(cm);
                        }
                    }
                }

                // 检查任务是否结束
                if (currentThreat.isDefeated()) {
                    completeMission(true);
                    break;
                }

                // 检查是否所有船员都被击败
                boolean allDefeated = true;
                for (CrewMember cm : currentSquad) {
                    if (!cm.isDefeated() && !cm.isInMedbay()) {
                        allDefeated = false;
                        break;
                    }
                }

                if (allDefeated) {
                    completeMission(false);
                    break;
                }

                // 下一回合
                currentRound++;
                for (CrewMember cm : currentSquad) {
                    cm.setActedThisTurn(false);
                    cm.setDefending(false);
                }
            }

            // 回调通知完成
            if (callback != null) {
                callback.onComplete(!missionInProgress, getSurvivors());
            }
        }).start();
    }

    public interface AutoMissionCallback {
        void onComplete(boolean success, List<CrewMember> survivors);
    }

    private List<CrewMember> getSurvivors() {
        List<CrewMember> survivors = new ArrayList<>();
        for (CrewMember cm : currentSquad) {
            if (!cm.isDefeated()) {
                survivors.add(cm);
            }
        }
        return survivors;
    }

    private void logMessage(String message) {
        missionLog.append(message).append("\n");
        notifyMissionUpdate(message);
    }

    private void notifyMissionUpdate(String message) {
        List<MissionListener> listenersCopy = new ArrayList<>(listeners);
        for (MissionListener listener : listenersCopy) {
            try {
                listener.onMissionUpdate(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyCrewTurn(CrewMember crew, Threat threat) {
        List<MissionListener> listenersCopy = new ArrayList<>(listeners);
        for (MissionListener listener : listenersCopy) {
            try {
                listener.onCrewTurn(crew, threat);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyThreatTurn(CrewMember target, Threat threat) {
        List<MissionListener> listenersCopy = new ArrayList<>(listeners);
        for (MissionListener listener : listenersCopy) {
            try {
                listener.onThreatTurn(target, threat);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyMissionComplete(boolean success, List<CrewMember> survivors) {
        List<MissionListener> listenersCopy = new ArrayList<>(listeners);
        for (MissionListener listener : listenersCopy) {
            try {
                listener.onMissionComplete(success, survivors);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyCrewDefeated(CrewMember crew) {
        List<MissionListener> listenersCopy = new ArrayList<>(listeners);
        for (MissionListener listener : listenersCopy) {
            try {
                listener.onCrewDefeated(crew);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyRoundStart(int round) {
        List<MissionListener> listenersCopy = new ArrayList<>(listeners);
        for (MissionListener listener : listenersCopy) {
            try {
                listener.onRoundStart(round);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isMissionInProgress() {
        return missionInProgress;
    }

    public Threat getCurrentThreat() {
        return currentThreat;
    }

    public List<CrewMember> getCurrentSquad() {
        return new ArrayList<>(currentSquad);
    }

    public String getMissionLog() {
        return missionLog.toString();
    }

    public int getCurrentRound() {
        return currentRound;
    }
}