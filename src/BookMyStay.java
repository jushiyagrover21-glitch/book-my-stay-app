/**
 * Service
 *
 * Represents an optional add-on service for a reservation.
 * Each service has a name and an associated cost.
 *
 * Author: Jushi
 * Version: 7.0
 */

public class Service {

    private String name;
    private double cost;

    public Service(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    public void displayService() {
        System.out.println("- " + name + " ($" + cost + ")");
    }
}