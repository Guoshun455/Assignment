package main;

/**
 * Abstract base class representing a rally car.
 * Demonstrates the Open/Closed Principle by allowing extension
 * through subclasses without modifying existing code.
 */

public abstract class RallyCar {
    protected String make;
    protected String model;
    protected int horsePower;
    /**
     * Constructs a new RallyCar with specified attributes.
     */
    public RallyCar(String make, String model, int horsePower){
        this.make = make;
        this.model = model;
        this.horsePower = horsePower;
    }
    public String getMake(){
        return make;
    }
    public String getModel(){
        return model;
    }
    public int getHorsePower(){
        return horsePower;
    }
    /**
     * Calculates the performance rating of the car.
     * This abstract method must be implemented by subclasses
     * to provide specific performance calculations.
     * 
     * @return the calculated performance rating
     */
    public abstract double calculatePerformance();
    /**
     * Returns a string representation of the car.
     * @return formatted string with make, model and horsepower
     */
    @Override
    public String toString(){
        return make + " " + model + "(" + horsePower + " HP)";
    }
}
