package com.example.spacecolony.model;

import java.util.ArrayList;
import java.util.List;

public class Simulator {
    private Storage storage;

    public Simulator(Storage storage) {
        this.storage = storage;
    }

    public void train(CrewMember cm) {
        cm.train();
    }

    public List<CrewMember> getTrainingCrew() {
        return storage.getCrewByLocation("Simulator");
    }
}
