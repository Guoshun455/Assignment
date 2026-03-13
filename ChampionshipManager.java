package main;

import java.util.*;


/**
 * Singleton class managing the rally championship.
 * Demonstrates the Singleton Pattern ensuring only one
 * instance exists, and uses static members for global state.
 */

public class ChampionshipManager {
    private static ChampionshipManager instance;
    /** Static counter for total registered drivers */
    private static int totalDrivers = 0;
    /** Static counter for total races held */
    private static int totalRaces = 0;
    private List<Driver> drivers;
    private List<RallyRaceResult> races;
    private ChampionshipManager(){
        this.drivers = new ArrayList<>();
        this.races = new ArrayList<>();
    }
    /**
     * Gets the singleton instance, creating it if necessary.
     * 
     * @return the ChampionshipManager instance
     */
    public static  ChampionshipManager getInstance(){
        if (instance == null){
            instance = new ChampionshipManager();
        }
        return instance;
    }
    /**
     * Registers a new driver in the championship.
     * 
     * @param driver the driver to register
     */
    public void registerDriver(Driver driver){
        drivers.add(driver);
        totalDrivers++;
    }
    /**
     * Adds a race result to the championship.
     * 
     * @param result the race result to record
     */
    public void addRaceResult(RallyRaceResult result){
        races.add(result);
        totalRaces++;
    }
    /**
     * Gets championship standings sorted by points.
     * Static method for global access without instance.
     * 
     * @return sorted list of drivers by total points
     */
    public static List<Driver> getDriverStandings(){
        if (instance == null) return new ArrayList<>();
        List<Driver> standings = new ArrayList<>(instance.drivers);
        standings.sort((d1, d2) -> d2.getTotalPoints() - d1.getTotalPoints());
        return standings;
    }
    /**
     * Gets the current championship leader.
     * 
     * @return the leading driver, or null if no drivers
     */
    public static Driver getLeadingDriver(){
        List<Driver> standings = getDriverStandings();
        return standings.isEmpty() ? null : standings.get(0);
    }
    /**
     * Calculates total points across all drivers.
     * 
     * @return sum of all championship points
     */
    public static int getTotalChampionshipPoints(){
        if (instance == null) return 0;
        int total = 0;
        for (Driver d : instance.drivers){
            total += d.getTotalPoints();
        }
        return total;
    }
    /**
     * Gets a copy of the drivers list.
     * @return list of registered drivers
     */
    public List<Driver> getDrivers(){
        return new ArrayList<>(drivers);
    }
    /**
     * Gets a copy of the races list.
     * @return list of race results
     */
    public List<RallyRaceResult> getRaces(){
        return new ArrayList<>(races);
    }
    public static int getTotalDrivers(){
        return totalDrivers;
    }
    public static int getTotalRaces(){
        return totalRaces;
    }
    /**
     * Resets the championship manager for testing.
     * Clears instance and counters.
     */
    public static void reset(){
        instance = null;
        totalDrivers = 0;
        totalRaces = 0;
    }
}
