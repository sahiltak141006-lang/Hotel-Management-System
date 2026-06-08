import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    // 1. User authentication routine
    public static boolean authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next(); 
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Unified Reservation Insertion routine extracting transactional Auto-Increment Keys
    public static int bookRoomAndGetID(int roomNumber, String date, String customerName) {
        String query = "INSERT INTO bookings (room_number, booking_date, customer_name, status) VALUES (?, ?, ?, 'Booked')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, roomNumber);
            ps.setString(2, date);
            ps.setString(3, customerName);
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); 
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return -1; 
    }

    // 3. Operational cancellation matching targeting unique internal Booking IDs directly
    public static boolean cancelBooking(int bookingId) {
        String query = "UPDATE bookings SET status = 'Cancelled' WHERE booking_id = ? AND status = 'Booked'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}