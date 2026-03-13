import java.util.*;

// -------------------- Reservation --------------------
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
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

    public int getAvailability(String roomType) {
        return availability.getOrDefault(roomType, 0);
    }

    public void updateAvailability(String roomType, int count) {
        availability.put(roomType, count);
    }

    public void displayInventory() {
        for (String room : availability.keySet()) {
            System.out.println(room + " : " + availability.get(room) + " rooms available");
        }
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
        System.out.println("Processing booking requests...\n");
        while (!requestQueue.isEmpty()) {
            Reservation r = requestQueue.getNextRequest();
            String roomType = r.getRoomType();
            int available = inventory.getAvailability(roomType);
            if (available > 0) {
                String roomId = generateRoomId(roomType);
                allocatedRoomIds.add(roomId);
                allocatedRooms.putIfAbsent(roomType, new HashSet<>());
                allocatedRooms.get(roomType).add(roomId);
                inventory.updateAvailability(roomType, available - 1);
                System.out.println("Reservation Confirmed!");
                System.out.println("Guest: " + r.getGuestName());
                System.out.println("Room Type: " + roomType);
                System.out.println("Assigned Room ID: " + roomId);
                System.out.println("-----------------------------");
            } else {
                System.out.println("Reservation Failed for " + r.getGuestName() +
                        ". No rooms available for " + roomType);
            }
        }
    }

    private String generateRoomId(String roomType) {
        String prefix = roomType.replace(" ", "").substring(0, 3).toUpperCase();
        int counter = allocatedRoomIds.size() + 1;
        String roomId = prefix + counter;
        while (allocatedRoomIds.contains(roomId)) {
            counter++;
            roomId = prefix + counter;
        }
        return roomId;
    }
}

// -------------------- Service --------------------
class Service {
    private String name;
    private double cost;

    public Service(String name, double cost) { this.name = name; this.cost = cost; }
    public String getName() { return name; }
    public double getCost() { return cost; }
    public void displayService() { System.out.println("- " + name + " ($" + cost + ")"); }
}

// -------------------- Add-On Service Manager --------------------
class AddOnServiceManager {
    private Map<String, List<Service>> reservationServices;
    public AddOnServiceManager() { reservationServices = new HashMap<>(); }

    public void addService(String reservationId, Service service) {
        reservationServices.putIfAbsent(reservationId, new ArrayList<>());
        reservationServices.get(reservationId).add(service);
        System.out.println("Added service " + service.getName() + " to reservation " + reservationId);
    }

    public void displayServices(String reservationId) {
        List<Service> services = reservationServices.get(reservationId);
        if (services == null || services.isEmpty()) {
            System.out.println("No add-on services for reservation " + reservationId);
            return;
        }
        System.out.println("Add-On Services for reservation " + reservationId + ":");
        for (Service s : services) s.displayService();
    }

    public double calculateTotalCost(String reservationId) {
        List<Service> services = reservationServices.get(reservationId);
        if (services == null) return 0;
        double total = 0; for (Service s : services) total += s.getCost();
        return total;
    }
}

// -------------------- Booking History --------------------
class BookingHistory {
    private List<Reservation> confirmedBookings;
    public BookingHistory() { confirmedBookings = new ArrayList<>(); }
    public void addReservation(Reservation r) { confirmedBookings.add(r); }
    public List<Reservation> getAllReservations() { return new ArrayList<>(confirmedBookings); }
    public int getTotalReservations() { return confirmedBookings.size(); }
}

// -------------------- Booking Report Service --------------------
class BookingReportService {
    private BookingHistory history;
    public BookingReportService(BookingHistory history) { this.history = history; }
    public void displayAllReservations() {
        System.out.println("\n=== Confirmed Reservations ===");
        List<Reservation> res = history.getAllReservations();
        if (res.isEmpty()) { System.out.println("No confirmed reservations."); return; }
        for (Reservation r : res) System.out.println("Guest: " + r.getGuestName() + " | Room: " + r.getRoomType());
    }
    public void displaySummaryReport() {
        System.out.println("\n=== Booking Summary ===");
        System.out.println("Total confirmed reservations: " + history.getTotalReservations());
    }
}

// -------------------- Main Program --------------------
public class MyStayApp {

    public static void main(String[] args) {

        System.out.println("===== My Stay App =====\n");

        // Initialize inventory, queue, validator, history
        RoomInventory inventory = new RoomInventory();
        BookingRequestQueue queue = new BookingRequestQueue();
        BookingValidator validator = new BookingValidator(inventory);
        BookingHistory history = new BookingHistory();
        AddOnServiceManager serviceManager = new AddOnServiceManager();

        // Create some reservation requests (some invalid to test error handling)
        Reservation r1 = new Reservation("Alice", "Single Room");
        Reservation r2 = new Reservation("", "Double Room"); // Invalid guest name
        Reservation r3 = new Reservation("Bob", "Triple Room"); // Invalid room type
        Reservation r4 = new Reservation("Charlie", "Suite Room");

        Reservation[] requests = {r1, r2, r3, r4};

        // Validate and enqueue requests
        for (Reservation r : requests) {
            try {
                validator.validateReservation(r);
                queue.addRequest(r);
                history.addReservation(r); // add to confirmed for simplicity
            } catch (InvalidBookingException e) {
                System.out.println("Booking failed: " + e.getMessage());
            }
        }

        System.out.println("\nInitial Inventory:");
        inventory.displayInventory();
        System.out.println();

        // Process bookings
        BookingService bookingService = new BookingService(inventory, queue);
        bookingService.processBookings();

        System.out.println("\nUpdated Inventory:");
        inventory.displayInventory();

        // Add-on services
        Service breakfast = new Service("Breakfast", 20.0);
        Service spa = new Service("Spa Package", 50.0);

        serviceManager.addService("SIN1", breakfast);
        serviceManager.addService("SIN1", spa);

        serviceManager.displayServices("SIN1");
        System.out.println("Total Add-On Cost: $" + serviceManager.calculateTotalCost("SIN1"));

        // Reporting
        BookingReportService reportService = new BookingReportService(history);
        reportService.displayAllReservations();
        reportService.displaySummaryReport();

        System.out.println("\nSystem demonstration completed.");
    }
}