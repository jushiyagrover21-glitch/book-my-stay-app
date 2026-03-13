import java.io.*;
import java.util.*;

// -------------------- Reservation --------------------
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
    private String guestName;
    private String roomType;
    private String roomId;

    public Reservation(String guestName, String roomType, String roomId) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public String getRoomId() { return roomId; }

    @Override
    public String toString() {
        return guestName + " | " + roomType + " | Room ID: " + roomId;
    }
}

// -------------------- Room Inventory --------------------
class RoomInventory implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, Integer> availability;

    public RoomInventory() {
        availability = new HashMap<>();
        availability.put("Single Room", 10);
        availability.put("Double Room", 5);
        availability.put("Suite Room", 2);
    }

    public int getAvailability(String roomType) { return availability.getOrDefault(roomType, 0); }
    public void updateAvailability(String roomType, int count) { availability.put(roomType, count); }
    public void incrementAvailability(String roomType) { availability.put(roomType, getAvailability(roomType) + 1); }

    public void displayInventory() {
        System.out.println("=== Current Inventory ===");
        for (String room : availability.keySet())
            System.out.println(room + " : " + availability.get(room));
        System.out.println("========================");
    }
}

// -------------------- Booking History --------------------
class BookingHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Reservation> confirmedBookings;

    public BookingHistory() { confirmedBookings = new ArrayList<>(); }

    public void addReservation(Reservation r) { confirmedBookings.add(r); }

    public List<Reservation> getAllReservations() { return confirmedBookings; }

    public void displayHistory() {
        System.out.println("=== Booking History ===");
        for (Reservation r : confirmedBookings) System.out.println(r);
        System.out.println("=======================");
    }
}

// -------------------- Persistence Service --------------------
class PersistenceService {

    private static final String FILE_NAME = "booking_system_state.ser";

    public static void saveState(RoomInventory inventory, BookingHistory history) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(inventory);
            oos.writeObject(history);
            System.out.println("System state saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving system state: " + e.getMessage());
        }
    }

    public static Object[] loadState() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            RoomInventory inventory = (RoomInventory) ois.readObject();
            BookingHistory history = (BookingHistory) ois.readObject();
            System.out.println("System state loaded successfully.");
            return new Object[]{inventory, history};
        } catch (FileNotFoundException e) {
            System.out.println("No previous state found. Starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading system state: " + e.getMessage());
        }
        return null;
    }
}

// -------------------- Main Program --------------------
public class UC12PersistenceRecovery {

    public static void main(String[] args) {

        System.out.println("===== My Stay App - UC12 Data Persistence & Recovery =====\n");

        // Attempt to load previous state
        Object[] state = PersistenceService.loadState();
        RoomInventory inventory;
        BookingHistory history;

        if (state != null) {
            inventory = (RoomInventory) state[0];
            history = (BookingHistory) state[1];
        } else {
            inventory = new RoomInventory();
            history = new BookingHistory();
        }

        // Display restored state
        inventory.displayInventory();
        history.displayHistory();

        // Simulate adding new bookings
        Reservation r1 = new Reservation("Alice", "Single Room", "SIN101");
        Reservation r2 = new Reservation("Bob", "Double Room", "DOU101");

        history.addReservation(r1);
        history.addReservation(r2);

        inventory.updateAvailability("Single Room", inventory.getAvailability("Single Room") - 1);
        inventory.updateAvailability("Double Room", inventory.getAvailability("Double Room") - 1);

        System.out.println("\nAfter adding new bookings:");
        inventory.displayInventory();
        history.displayHistory();

        // Save state
        PersistenceService.saveState(inventory, history);

        System.out.println("\nSystem shutdown simulation complete. State persisted for recovery.");
    }
}