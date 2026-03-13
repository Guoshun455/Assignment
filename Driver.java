package main;

/**
 * Represents a rally driver in the championship.
 * Demonstrates Dependency Injection through car assignment
 * and follows the Single Responsibility Principle.
 */

public class Driver {
    private String name;
    private String country;
    private int totalPoints;
    /** The car currently assigned to this driver */
    private RallyCar car;

    public Driver(String name, String country, RallyCar car){
        this.name = name;
        this.country = country;
        this.car = car;
        this.totalPoints = 0;
    }
    public String getName(){
        return name;
    }
    public String getCountry(){
        return country;
    }
    /**
     * Gets the total championship points.
     * @return accumulated points
     */
    public int getTotalPoints(){
        return totalPoints;
    }
    public RallyCar getCar(){
        return car;
    }
    /**
     * Assigns a new car to the driver.
     * Demonstrates Dependency Injection by allowing
     * car switching between races.
     */
    public void setCar(RallyCar car){
        this.car = car;
    }
    /**
     * Adds points to the driver's total.
     */
    public void addPoints(int points){
        this.totalPoints += points;
    }
    /**
     * Returns a formatted string representation.
     * @return string with name, country and points
     */
    @Override
    public String toString(){
        return name + " (" + country + "): " + totalPoints + " points";
    }
}
