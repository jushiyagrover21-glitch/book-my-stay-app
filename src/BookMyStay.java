import java.util.*;

// -------------------- Reservation --------------------
class Reservation {
    private String guestName;
    private String roomType;
    private String roomId; // assigned room ID

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = "";
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getRoomId() { return roomId; }
}

// -------------------- Room Inventory --------------------
class RoomInventory {
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
        for (String room : availability.keySet())
            System.out.println(room + " : " + availability.get(room) + " rooms available");
    }
}

// -------------------- Booking Request Queue --------------------
class BookingRequestQueue {
    private Queue<Reservation> bookingQueue;
    public BookingRequestQueue() { bookingQueue = new LinkedList<>(); }
    public void addRequest(Reservation r) { bookingQueue.add(r); }
    public Reservation getNextRequest() { return bookingQueue.poll(); }
    public boolean isEmpty() { return bookingQueue.isEmpty(); }
}

// -------------------- Booking Validator --------------------
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) { super(message); }
}

class BookingValidator {
    private RoomInventory inventory;
    private List<String> validRoomTypes = Arrays.asList("Single Room", "Double Room", "Suite Room");

    public BookingValidator(RoomInventory inventory) { this.inventory = inventory; }

    public void validateReservation(Reservation r) throws InvalidBookingException {
        if (r.getGuestName() == null || r.getGuestName().isEmpty())
            throw new InvalidBookingException("Guest name cannot be empty.");
        if (!validRoomTypes.contains(r.getRoomType()))
            throw new InvalidBookingException("Invalid room type: " + r.getRoomType());
        if (inventory.getAvailability(r.getRoomType()) <= 0)
            throw new InvalidBookingException("No rooms available for " + r.getRoomType());
    }
}

// -------------------- Booking Service --------------------
class BookingService {
    private RoomInventory inventory;
    private BookingRequestQueue requestQueue;
    private Map<String, Set<String>> allocatedRooms;
    private Set<String> allocatedRoomIds;

    public BookingService(RoomInventory inventory, BookingRequestQueue queue) {
        this.inventory = inventory;
        this.requestQueue = queue;
        allocatedRooms = new HashMap<>();
        allocatedRoomIds = new HashSet<>();
    }

    public void processBookings() {
        while (!requestQueue.isEmpty()) {
            Reservation r = requestQueue.getNextRequest();
            String roomType = r.getRoomType();
            int available = inventory.getAvailability(roomType);
            if (available > 0) {
                String roomId = generateRoomId(roomType);
                allocatedRoomIds.add(roomId);
                allocatedRooms.putIfAbsent(roomType, new HashSet<>());
                allocatedRooms.get(roomType).add(roomId);
                r.setRoomId(roomId);
                inventory.updateAvailability(roomType, available - 1);
                System.out.println("Booking Confirmed: " + r.getGuestName() + " | Room ID: " + roomId);
            } else {
                System.out.println("Booking Failed: No rooms available for " + r.getGuestName());
            }
        }
    }

    private String generateRoomId(String roomType) {
        String prefix = roomType.replace(" ", "").substring(0, 3).toUpperCase();
        int counter = allocatedRoomIds.size() + 1;
        String roomId = prefix + counter;
        while (allocatedRoomIds.contains(roomId)) { counter++; roomId = prefix + counter; }
        return roomId;
    }

    public boolean releaseRoom(Reservation r) {
        String roomType = r.getRoomType();
        String roomId = r.getRoomId();
        if (roomId.isEmpty()) return false;
        allocatedRooms.get(roomType).remove(roomId);
        allocatedRoomIds.remove(roomId);
        inventory.incrementAvailability(roomType);
        r.setRoomId(""); // clear assignment
        return true;
    }
}

// -------------------- Booking History --------------------
class BookingHistory {
    private List<Reservation> confirmedBookings;
    public BookingHistory() { confirmedBookings = new ArrayList<>(); }
    public void addReservation(Reservation r) { confirmedBookings.add(r); }
    public void removeReservation(Reservation r) { confirmedBookings.remove(r); }
    public List<Reservation> getAllReservations() { return new ArrayList<>(confirmedBookings); }
    public int getTotalReservations() { return confirmedBookings.size(); }
}

// -------------------- Booking Cancellation --------------------
class CancellationService {
    private Stack<Reservation> rollbackStack;
    private BookingService bookingService;
    private BookingHistory history;

    public CancellationService(BookingService bookingService, BookingHistory history) {
        this.bookingService = bookingService;
        this.history = history;
        rollbackStack = new Stack<>();
    }

    public void cancelReservation(Reservation r) {
        if (r.getRoomId().isEmpty()) {
            System.out.println("Cannot cancel reservation for " + r.getGuestName() + ". No room assigned.");
            return;
        }
        rollbackStack.push(r);
        boolean released = bookingService.releaseRoom(r);
        if (released) {
            history.removeReservation(r);
            System.out.println("Reservation cancelled for " + r.getGuestName() + " | Room ID: " + r.getRoomId());
        }
    }

    public void rollbackLastCancellation() {
        if (rollbackStack.isEmpty()) {
            System.out.println("No cancellations to rollback.");
            return;
        }
        Reservation r = rollbackStack.pop();
        System.out.println("Rollback: Reservation restored for " + r.getGuestName() + " | Room ID: " + r.getRoomId());
        history.addReservation(r);
    }
}

// -------------------- Main Program --------------------
public class UC10BookingCancellation {

    public static void main(String[] args) {

        System.out.println("===== My Stay App - UC10 =====\n");

        RoomInventory inventory = new RoomInventory();
        BookingRequestQueue queue = new BookingRequestQueue();
        BookingValidator validator = new BookingValidator(inventory);
        BookingHistory history = new BookingHistory();

        BookingService bookingService = new BookingService(inventory, queue);
        CancellationService cancellationService = new CancellationService(bookingService, history);

        // Sample reservations
        Reservation r1 = new Reservation("Alice", "Single Room");
        Reservation r2 = new Reservation("Bob", "Double Room");
        Reservation r3 = new Reservation("Charlie", "Suite Room");

        Reservation[] reservations = {r1, r2, r3};

        // Validate and enqueue bookings
        for (Reservation r : reservations) {
            try {
                validator.validateReservation(r);
                queue.addRequest(r);
                history.addReservation(r);
            } catch (InvalidBookingException e) {
                System.out.println("Booking failed: " + e.getMessage());
            }
        }

        System.out.println("\nInitial Inventory:");
        inventory.displayInventory();
        System.out.println();

        // Process bookings
        bookingService.processBookings();

        System.out.println("\nInventory after booking:");
        inventory.displayInventory();
        System.out.println();

        // Perform cancellations
        System.out.println("Cancelling reservation for Bob...");
        cancellationService.cancelReservation(r2);

        System.out.println("\nInventory after cancellation:");
        inventory.displayInventory();

        // Rollback last cancellation
        System.out.println("\nRolling back last cancellation...");
        cancellationService.rollbackLastCancellation();

        System.out.println("\nInventory after rollback:");
        inventory.displayInventory();
    }
}