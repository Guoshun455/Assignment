package com.example.spacecolony.model;

public class Quarters {
    private Storage storage;

    public Quarters(Storage storage) {
        this.storage = storage;
    }

    public CrewMember createCrewMember(String name, String specialization) {
        CrewMember cm;
        switch (specialization) {
            case "Pilot":
                cm = new Pilot(name);
                break;
            case "Engineer":
                cm = new Engineer(name);
                break;
            case "Medic":
                cm = new Medic(name);
                break;
            case "Scientist":
                cm = new Scientist(name);
                break;
            case "Soldier":
                cm = new Soldier(name);
                break;
            default:
                cm = new Pilot(name);
        }

        cm.setCurrentLocation("Quarters");
        storage.addCrewMember(cm);
        return cm;
    }

    public void restoreEnergy(CrewMember cm) {
        cm.restoreEnergy();
        cm.setInMedbay(false);
    }

    public void sendToMedbay(CrewMember cm) {
        cm.setInMedbay(true);
        cm.setCurrentLocation("Medbay");
        cm.energy = cm.getMaxEnergy() / 2;
        cm.incrementTimesDefeated();
    }
}
