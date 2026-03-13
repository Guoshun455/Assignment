package main;

import java.util.*;

/**
 * Implementation of RaceResult for rally races.
 * Maintains race information and results using Maps
 * for efficient driver-position lookups.
 */

public class RallyRaceResult implements RaceResult {
    private String raceName;
    private String location;
    /** Maps drivers to their earned points */
    private Map<Driver, Integer> results;
    private Map<Integer, Driver> positions;
    public RallyRaceResult(String raceName, String location){
        this.raceName = raceName;
        this.location = location;
        this.results = new HashMap<>();
        this.positions = new HashMap<>();
    }
    public String getRaceName(){
        return raceName;
    }
    public String getLocation(){
        return location;
    }
    /**
     * Records a driver's result and updates their points.
     * 
     * @param driver the participating driver
     * @param position the finishing position
     * @param points points to award
     */
    @Override
    public void recordResult(Driver driver, int position,int points){
        results.put(driver, points);
        positions.put(position, driver);
        driver.addPoints(points);
    }
    /**
     * Gets points earned by a specific driver.
     * 
     * @param driver the driver to query
     * @return points earned, or 0 if not found
     */
    @Override
    public int getDriverPoints(Driver driver){
        return results.getOrDefault(driver, 0);
    }
    /**
     * Gets results ordered by finishing position.
     * @return ordered list of drivers
     */
    @Override
    public List<Driver> getResults(){
        List<Driver> orderedResults = new ArrayList<>();
        for (int i = 1; i <= positions.size(); i++){
            orderedResults.add(positions.get(i));
        }
        return orderedResults;
    }
    /**
     * Gets the position-to-driver mapping.
     * @return sorted map of positions
     */
    public Map<Integer, Driver> getPositions(){
        return new TreeMap<>(positions);
    }
}
