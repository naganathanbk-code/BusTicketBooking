import java.sql.*;
import java.util.Scanner;

public class BusBookingSystem {

    static final String URL = "jdbc:mysql://localhost:3306/busdb";
    static final String USER = "root";
    static final String PASS = "";

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== BUS TICKET BOOKING ===");
            System.out.println("1. View Available Seats");
            System.out.println("2. Book Ticket");
            System.out.println("3. View All Bookings");
            System.out.println("4. Exit");
            System.out.print("Choose: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    viewSeats();
                    break;
                case 2:
                    bookTicket(sc);
                    break;
                case 3:
                    viewBookings();
                    break;
                case 4:
                    System.out.println("Thank you");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // VIEW AVAILABLE SEATS
    static void viewSeats() {
        try {
            Connection con = DriverManager.getConnection(URL, USER, PASS);
            String sql = "SELECT * FROM bus_seats WHERE bus_id = 1";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            if (rs.next()) {
                System.out.println("\nBus Name: " + rs.getString("bus_name"));
                System.out.println("Total Seats: " + rs.getInt("total_seats"));
                System.out.println("Available Seats: " + rs.getInt("available_seats"));
            }
            con.close();

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    // BOOK TICKET
    static void bookTicket(Scanner sc) {
        try {
            Connection con = DriverManager.getConnection(URL, USER, PASS);

            // Check available seats
            String seatQuery = "SELECT available_seats FROM bus_seats WHERE bus_id = 1";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(seatQuery);
            rs.next();
            int available = rs.getInt("available_seats");

            System.out.println("\nAvailable Seats: " + available);

            System.out.print("Enter Passenger Name: ");
            String name = sc.nextLine();

            System.out.print("Enter from: ");
            String source = sc.nextLine();

            System.out.print("Enter Destination: ");
            String dest = sc.nextLine();

            System.out.print("Enter Seat Count: ");
            int seats = sc.nextInt();
            sc.nextLine();

            if (seats > available) {
                System.out.println("Not enough seats available!");
                return;
            }

            // Insert booking
            String insertSQL = "INSERT INTO bookings(passenger_name, source, destination, seats) VALUES(?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(insertSQL);

            ps.setString(1, name);
            ps.setString(2, source);
            ps.setString(3, dest);
            ps.setInt(4, seats);

            ps.executeUpdate();

            // Reduce seats
            String updateSQL = "UPDATE bus_seats SET available_seats = available_seats - ? WHERE bus_id = 1";
            PreparedStatement ps2 = con.prepareStatement(updateSQL);
            ps2.setInt(1, seats);
            ps2.executeUpdate();

            System.out.println("\n🎉 TICKET BOOKED SUCCESSFULLY!");
            System.out.println("Passenger: " + name);
            System.out.println("Seats Booked: " + seats);

            con.close();

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    // VIEW ALL BOOKINGS
    static void viewBookings() {
        try {
            Connection con = DriverManager.getConnection(URL, USER, PASS);

            String sql = "SELECT * FROM bookings";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            System.out.println("\n=== ALL BOOKINGS ===");

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Name: " + rs.getString("passenger_name"));
                System.out.println("From: " + rs.getString("source"));
                System.out.println("To: " + rs.getString("destination"));
                System.out.println("Seats: " + rs.getInt("seats"));
                System.out.println("Time: " + rs.getString("booking_time"));
                System.out.println("----------------------------");
            }

            con.close();

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

}
