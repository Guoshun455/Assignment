package main;

/**
 * Concrete implementation of RallyCar for gravel surface racing.
 * Demonstrates the Single Responsibility Principle by handling
 * gravel-specific performance calculations.
 */

public class GravelCar extends RallyCar {
    /** The suspension travel distance in millimeters */
    private double suspensionTravel;
    public GravelCar(String make, String model, int horsePower, double suspensionTravel){
        super(make, model, horsePower);
        this.suspensionTravel = suspensionTravel;
    }
    public double getSuspensionTravel(){
        return suspensionTravel;
    }
    /**
     * Calculates performance rating for gravel surfaces.
     * Formula: horsepower * 0.8 + suspensionTravel * 50
     * 
     * @return the calculated performance rating
     */
    @Override
    public double calculatePerformance(){
        return horsePower * 0.8 + suspensionTravel * 50;
    }
}
