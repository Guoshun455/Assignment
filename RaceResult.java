package main;

import java.util.List;

/**
 * Interface for recording and retrieving race results.
 * Demonstrates the Interface Segregation Principle by
 * providing a focused contract for race result operations.
 */

public interface RaceResult {
    void recordResult(Driver driver, int position, int points);
    int getDriverPoints(Driver driver);
    List<Driver> getResults();
}
