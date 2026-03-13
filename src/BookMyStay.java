import java.util.*;

// -------------------- Reservation --------------------
class Reservation {
    private String guestName;
    private String roomType;
    private String roomId;

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

    // synchronized to prevent race conditions
    public synchronized boolean allocateRoom(String roomType, String roomId) {
        int available = availability.getOrDefault(roomType, 0);
        if (available <= 0) return false;
        availability.put(roomType, available - 1);
        return true;
    }

    public synchronized void releaseRoom(String roomType) {
        availability.put(roomType, availability.getOrDefault(roomType, 0) + 1);
    }

    public synchronized void displayInventory() {
        System.out.println("=== Current Inventory ===");
        for (String room : availability.keySet())
            System.out.println(room + " : " + availability.get(room));
        System.out.println("========================");
    }
}

// -------------------- Booking Queue --------------------
class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public synchronized void addRequest(Reservation r) { queue.add(r); }

    public synchronized Reservation getNextRequest() { return queue.poll(); }

    public synchronized boolean isEmpty() { return queue.isEmpty(); }
}

// -------------------- Concurrent Booking Processor --------------------
class ConcurrentBookingProcessor implements Runnable {
    private BookingQueue queue;
    private RoomInventory inventory;

    public ConcurrentBookingProcessor(BookingQueue queue, RoomInventory inventory) {
        this.queue = queue;
        this.inventory = inventory;
    }

    @Override
    public void run() {
        while (true) {
            Reservation r;
            synchronized (queue) {
                if (queue.isEmpty()) return;
                r = queue.getNextRequest();
            }

            String roomType = r.getRoomType();
            String roomId = roomType.substring(0, 3).toUpperCase() + new Random().nextInt(1000);

            boolean allocated = inventory.allocateRoom(roomType, roomId);
            if (allocated) {
                r.setRoomId(roomId);
                System.out.println(Thread.currentThread().getName() + " allocated " +
                        r.getRoomType() + " to " + r.getGuestName() + " | Room ID: " + roomId);
            } else {
                System.out.println(Thread.currentThread().getName() + " failed to allocate " +
                        r.getRoomType() + " for " + r.getGuestName());
            }

            try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }
}

// -------------------- Main Program --------------------
public class UC11ConcurrentBooking {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("===== My Stay App - UC11 Concurrent Booking =====\n");

        RoomInventory inventory = new RoomInventory();
        BookingQueue bookingQueue = new BookingQueue();

        // Simulate multiple guests
        Reservation[] reservations = {
                new Reservation("Alice", "Single Room"),
                new Reservation("Bob", "Single Room"),
                new Reservation("Charlie", "Double Room"),
                new Reservation("Diana", "Suite Room"),
                new Reservation("Eve", "Double Room"),
                new Reservation("Frank", "Single Room"),
                new Reservation("Grace", "Suite Room"),
                new Reservation("Hank", "Double Room")
        };

        // Add all reservations to shared queue
        for (Reservation r : reservations) bookingQueue.addRequest(r);

        // Create multiple threads to process bookings concurrently
        Thread t1 = new Thread(new ConcurrentBookingProcessor(bookingQueue, inventory), "Thread-1");
        Thread t2 = new Thread(new ConcurrentBookingProcessor(bookingQueue, inventory), "Thread-2");
        Thread t3 = new Thread(new ConcurrentBookingProcessor(bookingQueue, inventory), "Thread-3");

        // Start threads
        t1.start();
        t2.start();
        t3.start();

        // Wait for threads to finish
        t1.join();
        t2.join();
        t3.join();

        System.out.println("\nFinal Inventory State:");
        inventory.displayInventory();
        System.out.println("\nConcurrent booking simulation completed.");
    }
}