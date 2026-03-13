import java.util.ArrayList;
import java.util.List;

/**
 * BookingHistory
 *
 * Maintains a chronological record of all confirmed reservations.
 * Uses a List to preserve insertion order for reporting and audits.
 * Core booking and inventory logic remain unaffected.
 *
 * Author: Jushi
 * Version: 8.0
 */

public class BookingHistory {

    private List<Reservation> confirmedBookings;

    public BookingHistory() {
        confirmedBookings = new ArrayList<>();
    }

    /**
     * Add confirmed reservation to history
     */
    public void addReservation(Reservation reservation) {
        confirmedBookings.add(reservation);
        System.out.println("Reservation added to history: " + reservation.getGuestName() +
                " (" + reservation.getRoomType() + ")");
    }

    /**
     * Retrieve all reservations (read-only)
     */
    public List<Reservation> getAllReservations() {
        return new ArrayList<>(confirmedBookings); // defensive copy
    }

    /**
     * Get total number of confirmed reservations
     */
    public int getTotalReservations() {
        return confirmedBookings.size();
    }
}