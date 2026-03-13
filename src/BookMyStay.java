/**
 * Reservation
 *
 * Represents a guest booking request for a specific room type.
 * This class only stores request information and does not
 * interact with inventory or allocation logic.
 *
 * @author Jushi
 * @version 5.0
 */

public class Reservation {

    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void displayReservation() {
        System.out.println("Guest: " + guestName + " | Requested Room: " + roomType);
    }
}
