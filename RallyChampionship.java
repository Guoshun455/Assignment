package main;

import java.util.*;

/**
 * Main class demonstrating the Rally Championship Management System.
 * Creates drivers, simulates races on different surfaces, switches cars
 * between races, and displays all championship information.
 * 
 * Demonstrates:
 * - Singleton Pattern with ChampionshipManager
 * - Dependency Injection with car switching
 * - Liskov Substitution with different car types
 * - Interface usage with RaceResult
 */

public class RallyChampionship {
    public static void main(String[] args){
        // Get singleton manager instance
        ChampionshipManager manager = ChampionshipManager.getInstance();
        // Create cars for different surfaces
        GravelCar gravelCar1 = new GravelCar("Toyota", "GR Yaris", 342, 2.99);
        GravelCar gravelCar2 = new GravelCar("Hyundai", "i20 N", 320, 2.8);
        AsphaltCar asphaltCar1 = new AsphaltCar("Ford", "Puma Rally1", 360, 1.48);
        AsphaltCar asphaltCar2 = new AsphaltCar("M-Sport", "Fiesta", 340, 1.3);
        // Create drivers with initial cars
        Driver ogier = new Driver("Sébastien Ogier", "France", gravelCar1);
        Driver rovanpera = new Driver("Kalle Rovanperä", "Finland", asphaltCar1);
        Driver tanak = new Driver("Ott Tänak", "Estonia", gravelCar2);
        Driver neuville = new Driver("Thierry Neuville", "Belgium", asphaltCar2);
        // Register all drivers
        manager.registerDriver(ogier);
        manager.registerDriver(rovanpera);
        manager.registerDriver(tanak);
        manager.registerDriver(neuville);
        // Simulate first race on gravel (Rally Finland)
        System.out.println("Simulating Rally Finland...");
        RallyRaceResult race1 =  new RallyRaceResult("Rally Finland", "Jyväskylä");
        // Switch to gravel cars for first race
        ogier.setCar(gravelCar1);
        rovanpera.setCar(gravelCar2);
        tanak.setCar(gravelCar2);
        neuville.setCar(gravelCar1);
        // Record results for race 1
        race1.recordResult(ogier, 1, 25);
        race1.recordResult(tanak, 2, 18);
        race1.recordResult(rovanpera, 3, 15);
        race1.recordResult(neuville, 4, 12);
        manager.addRaceResult(race1);
        // Simulate second race on asphalt (Monte Carlo)
        System.out.println("Simulating Monte Carlo Rally...");
        RallyRaceResult race2 = new RallyRaceResult("Monte Carlo Rally", "Monaco");
        // Switch to asphalt cars for second race
        ogier.setCar(asphaltCar2);
        rovanpera.setCar(asphaltCar1);
        tanak.setCar(asphaltCar2);
        neuville.setCar(asphaltCar1);
        // Record results for race 2
        race2.recordResult(rovanpera, 1, 25);
        race2.recordResult(neuville, 2, 18);
        race2.recordResult(ogier, 3, 15);
        race2.recordResult(tanak, 4, 12);
        manager.addRaceResult(race2);
        // Display championship standings
        System.out.println("\n===== CHAMPIONSHIP STANDING =====");
        List<Driver> standings = ChampionshipManager.getDriverStandings();
        int position = 1;
        for (Driver d : standings){
            System.out.println(position + ". " + d);
            position++;
        }
        System.out.println();
        // Display championship leader
        System.out.println("===== CHAMPIONSHIP LEADER =====");
        Driver leader = ChampionshipManager.getLeadingDriver();
        if (leader != null) {
            System.out.println(leader.getName() + " with " + leader.getTotalPoints() + " points\n");
        }
        // Display championship statistics
        System.out.println("===== CHAMPIONSHIP STATISTICS =====");
        System.out.println("Total Drivers: " + ChampionshipManager.getTotalDrivers());
        System.out.println("Total Races: " + ChampionshipManager.getTotalRaces());
        System.out.printf("Average Points Per Driver: %.2f\n", ChampionshipStatistics.calculateAveragePointsPerDriver(standings));
        System.out.println("Most Successful Country: " + ChampionshipStatistics.findMostSuccessfulCountry(standings));
        System.out.println("Total Championship Points: " + ChampionshipManager.getTotalChampionshipPoints());
        System.out.println();
        // Display detailed race results
        System.out.println("===== RACE RESULTS =====");
        for (RallyRaceResult race : manager.getRaces()){
            System.out.println("Race: " + race.getRaceName() + " (" + race.getLocation() + ")");
            Map<Integer, Driver> positions = race.getPositions();
            for (Map.Entry<Integer, Driver> entry : positions.entrySet()){
                int racePosition = entry.getKey();
                Driver driver = entry.getValue();
                System.out.println("  Position " + racePosition + ": " + driver.getName() + " - " + race.getDriverPoints(driver) + " points");
            }
            System.out.println();
        }
        // Display car performance ratings
        System.out.println("===== CAR PERFORMANCE RATINGS =====");
        System.out.printf("Gravel Car Performance: %.1f\n", gravelCar1.calculatePerformance());
        System.out.printf("Asphalt Car Performance: %.1f\n", asphaltCar1.calculatePerformance());
    }
}
