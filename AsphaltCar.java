package main;

/**
 * Concrete implementation of RallyCar for asphalt surface racing.
 * Demonstrates the Single Responsibility Principle by handling
 * asphalt-specific performance calculations.
 */

public class AsphaltCar extends RallyCar {
    private double downforce;
    public AsphaltCar(String make, String model, int horsePower, double downforce){
        super(make, model, horsePower);
        this.downforce = downforce;
    }
    /**
     * Gets the aerodynamic downforce value.
     * @return the downforce in Newtons
     */
    public double getDownforce(){
        return downforce;
    }
    /**
     * Calculates performance rating for asphalt surfaces.
     * Formula: horsepower * 0.9 + downforce * 100
     * 
     * @return the calculated performance rating
     */
    @Override
    public double calculatePerformance(){
        return horsePower * 0.9 + downforce * 100;
    }
}
