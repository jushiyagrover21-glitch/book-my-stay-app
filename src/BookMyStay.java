import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * BookingService
 *
 * Processes booking requests from the queue and performs
 * room allocation while ensuring inventory consistency
 * and preventing duplicate room assignments.
 *
 * @author Jushi
 * @version 6.0
 */

public class BookingService {

    private RoomInventory inventory;
    private BookingRequestQueue requestQueue;

    // Map room type -> allocated room IDs
    private Map<String, Set<String>> allocatedRooms;

    // Track all allocated room IDs to prevent duplicates
    private Set<String> allocatedRoomIds;

    public BookingService(RoomInventory inventory, BookingRequestQueue requestQueue) {
        this.inventory = inventory;
        this.requestQueue = requestQueue;

        allocatedRooms = new HashMap<>();
        allocatedRoomIds = new HashSet<>();
    }

    /**
     * Process booking requests from queue
     */
    public void processBookings() {

        System.out.println("Processing booking requests...\n");

        while (!requestQueue.isEmpty()) {

            Reservation reservation = requestQueue.getNextRequest();

            String roomType = reservation.getRoomType();
            int available = inventory.getAvailability(roomType);

            if (available > 0) {

                String roomId = generateRoomId(roomType);

                // Store allocated room ID
                allocatedRoomIds.add(roomId);

                allocatedRooms.putIfAbsent(roomType, new HashSet<>());
                allocatedRooms.get(roomType).add(roomId);

                // Update inventory
                inventory.updateAvailability(roomType, available - 1);

                System.out.println("Reservation Confirmed!");
                System.out.println("Guest: " + reservation.getGuestName());
                System.out.println("Room Type: " + roomType);
                System.out.println("Assigned Room ID: " + roomId);
                System.out.println("-----------------------------");

            } else {
                System.out.println("Reservation Failed for " + reservation.getGuestName());
                System.out.println("No rooms available for " + roomType);
                System.out.println("-----------------------------");
            }
        }
    }

    /**
     * Generate unique room ID
     */
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