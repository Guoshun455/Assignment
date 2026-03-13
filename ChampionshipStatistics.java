package main;

import java.util.*;

/**
 * Static utility class for championship statistics calculations.
 * Demonstrates effective use of static methods for operations
 * that don't require object state.
 */

public class ChampionshipStatistics {
    /**
     * Calculates average points per driver.
     * 
     * @param drivers list of drivers to calculate average for
     * @return average points, or 0.0 if list is empty
     */
    public static double calculateAveragePointsPerDriver(List<Driver> drivers){
        if (drivers.isEmpty()) return 0.0;
        int total = 0;
        for (Driver d : drivers){
            total += d.getTotalPoints();
        }
        return (double) total / drivers.size();
    }
    /**
     * Finds the country with the most total points.
     * 
     * @param drivers list of drivers to analyze
     * @return name of most successful country, or "N/A" if empty
     */
    public static String findMostSuccessfulCountry(List<Driver> drivers){
        if (drivers.isEmpty()) return "N/A";
        Map<String, Integer> countryPoints = new HashMap<>();
        for (Driver d : drivers){
            countryPoints.merge(d.getCountry(), d.getTotalPoints(), Integer::sum);
        }
        return Collections.max(countryPoints.entrySet(), Map.Entry.comparingByValue()).getKey();
    }
    /**
     * Gets total number of races held in the championship.
     * Delegates to ChampionshipManager for the count.
     * 
     * @return total races held
     */
    public static int  getTotalRacesHeld(){
        return ChampionshipManager.getTotalRaces();
    }
}
