<<<<<<< HEAD
import java.util.HashMap;
import java.util.Map;

/**
 * RoomInventory
 *
 * Manages room availability using a centralized HashMap.
 * This class acts as the single source of truth for room counts.
 *
 * @author Jushi
 * @version 3.0
 */
public class RoomInventory {

    private Map<String, Integer> inventory;

    /**
     * Constructor initializes room inventory.
     */
    public RoomInventory() {
        inventory = new HashMap<>();

        // Initial room availability
        inventory.put("Single Room", 10);
        inventory.put("Double Room", 5);
        inventory.put("Suite Room", 2);
    }

    /**
     * Retrieve availability for a specific room type.
     */
    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    /**
     * Update availability for a specific room type.
     */
    public void updateAvailability(String roomType, int count) {
        inventory.put(roomType, count);
    }

    /**
     * Display all inventory information.
     */
    public void displayInventory() {
        System.out.println("Current Room Inventory:");

        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue() + " rooms available");
        }
=======
/**
 * UseCase1HotelBookingApp
 *
 * This class represents the entry point of the Hotel Booking Management System.
 * It demonstrates how a Java application starts execution and prints
 * a welcome message to the console.
 *
 * @author Jushi
 * @version 1.0
 */

public class BookMyStay {


    /**
     * Main method – entry point of the application.
     * JVM starts execution from this method.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {

        // Display welcome message
        System.out.println("=================================");
        System.out.println(" Welcome to My Stay App ");
        System.out.println(" Hotel Booking System v1.0 ");
        System.out.println("=================================");

        // Inform user application has started
        System.out.println("Application started successfully!");

        // Inform user application is terminating
        System.out.println("Application execution completed.");
>>>>>>> f12a941800e3e92e495acc88abe1574e9c92383e
    }
}






