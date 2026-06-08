import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Vector;

public class HotelMSFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    // Login Components
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    
    // Dashboard Components
    private JTable matrixTable;
    private DefaultTableModel tableModel;
    private JComboBox<Integer> roomSelector;
    private JTextField txtCustomerName;
    private JTextField txtBookingDate;

    public HotelMSFrame() {
        setTitle("Grand Hotel Luxe Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 550);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add views to CardLayout navigation panel setup
        mainPanel.add(buildLoginPanel(), "LoginView");
        mainPanel.add(buildDashboardPanel(), "DashboardView");

        add(mainPanel);
        cardLayout.show(mainPanel, "LoginView");
    }

    // =========================================================
    // 1. LOGIN INTERFACE PANEL
    // =========================================================
    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(44, 62, 80));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Using standard SwingConstants.CENTER to avoid compiler errors
        JLabel lblTitle = new JLabel("HOTEL LOGIN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        JLabel lblUser = new JLabel("Username:");
        lblUser.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(lblUser, gbc);

        txtUsername = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(txtUsername, gbc);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(lblPass, gbc);

        txtPassword = new JPasswordField(15);
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(txtPassword, gbc);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(46, 204, 113));
        btnLogin.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(btnLogin, gbc);

        // Authentication trigger linking user credentials to DB operations
        btnLogin.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());
            
            if (DatabaseManager.authenticateUser(username, password)) {
                refreshMatrixTable();
                cardLayout.show(mainPanel, "DashboardView");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials!", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    // =========================================================
    // 2. MANAGEMENT MATRIX DASHBOARD PANEL
    // =========================================================
    private JPanel buildDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top Header Info Layer
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel lblWelcome = new JLabel("Hotel Live Occupancy & Stay Matrix Dashboard", JLabel.LEFT);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel lblDate = new JLabel("System Date: " + LocalDate.now().toString(), JLabel.RIGHT);
        topPanel.add(lblWelcome, BorderLayout.WEST);
        topPanel.add(lblDate, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        // Grid Matrix Layout View showing current Booking IDs seamlessly
        tableModel = new DefaultTableModel(new Object[]{"Booking ID", "Room #", "Type", "Price", "Status", "Guest", "Date"}, 0);
        matrixTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(matrixTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Operational Control Panel Layout Form
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder("Manage Operational Reservations & Invoices"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("Room #:"), gbc);
        roomSelector = new JComboBox<>(new Integer[]{101, 102, 103, 201, 202, 301, 302});
        gbc.gridx = 1;
        controlPanel.add(roomSelector, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(new JLabel("Guest Name:"), gbc);
        txtCustomerName = new JTextField(12);
        gbc.gridx = 1;
        controlPanel.add(txtCustomerName, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        controlPanel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        txtBookingDate = new JTextField(LocalDate.now().toString(), 12);
        gbc.gridx = 1;
        controlPanel.add(txtBookingDate, gbc);

        // Core Interaction Utilities
        JButton btnBook = new ColorButton("Book Room & Create Invoice", new Color(52, 152, 219));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        controlPanel.add(btnBook, gbc);

        JButton btnCancel = new ColorButton("Cancel Selected Booking", new Color(231, 76, 60));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        controlPanel.add(btnCancel, gbc);

        panel.add(controlPanel, BorderLayout.SOUTH);

        // --- INTERACTION ACTION LISTENERS ---

        // Reserve Room execution thread
        btnBook.addActionListener(e -> {
            int room = (int) roomSelector.getSelectedItem();
            String name = txtCustomerName.getText().trim();
            String dateStr = txtBookingDate.getText().trim();

            if (name.isEmpty() || dateStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill out all operational fields.");
                return;
            }

            int newBookingId = DatabaseManager.bookRoomAndGetID(room, dateStr, name);
            
            if (newBookingId != -1) {
                refreshMatrixTable();
                showReceiptDialog(newBookingId, room, name, dateStr);
                txtCustomerName.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to Book. Room might be already occupied on this date.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Cancel Reservation execution thread
        btnCancel.addActionListener(e -> {
            int selectedRow = matrixTable.getSelectedRow();
            if (selectedRow == -1) { 
                JOptionPane.showMessageDialog(this, "Please select an active row from the table view matrix to cancel.");
                return;
            }

            Object idObj = tableModel.getValueAt(selectedRow, 0);
            String currentStatus = tableModel.getValueAt(selectedRow, 4).toString();

            if ("-".equals(idObj.toString()) || !"Booked".equals(currentStatus)) {
                JOptionPane.showMessageDialog(this, "This operational row is vacant or already cancelled.");
                return;
            }

            int bookingId = Integer.parseInt(idObj.toString());

            if (DatabaseManager.cancelBooking(bookingId)) {
                JOptionPane.showMessageDialog(this, "Booking #" + bookingId + " cancelled successfully!");
                refreshMatrixTable();
            } else {
                JOptionPane.showMessageDialog(this, "Error processing transactional cancellation request.");
            }
        });

        return panel;
    }

    // Dynamic Database matrix reload engine
    private void refreshMatrixTable() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT b.booking_id, r.room_number, r.room_type, r.price_per_night, " +
                           "IFNULL(b.status, 'Available') as status, " +
                           "IFNULL(b.customer_name, '-') as customer_name, " +
                           "IFNULL(b.booking_date, '-') as booking_date " +
                           "FROM rooms r LEFT JOIN bookings b ON r.room_number = b.room_number " +
                           "ORDER BY r.room_number ASC, b.booking_date DESC";
            
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                int id = rs.getInt("booking_id");
                row.add(id == 0 ? "-" : id);
                row.add(rs.getInt("room_number"));
                row.add(rs.getString("room_type"));
                row.add(rs.getBigDecimal("price_per_night"));
                row.add(rs.getString("status"));
                row.add(rs.getString("customer_name"));
                row.add(rs.getString("booking_date"));
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Modal dialogue box parsing clean transactional invoice strings
    private void showReceiptDialog(int bookingId, int roomNumber, String guestName, String dateStr) {
        String roomType = "Standard";
        double pricePerNight = 0.0;

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT room_type, price_per_night FROM rooms WHERE room_number = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, roomNumber);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        roomType = rs.getString("room_type");
                        pricePerNight = rs.getDouble("price_per_night");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        double taxRate = 0.12; 
        double taxAmount = pricePerNight * taxRate;
        double totalAmount = pricePerNight + taxAmount;

        StringBuilder receiptText = new StringBuilder();
        receiptText.append("=========================================\n");
        receiptText.append("           GRAND HOTEL LUXE              \n");
        receiptText.append("           INVOICE / RECEIPT             \n");
        receiptText.append("=========================================\n\n");
        receiptText.append(String.format(" Booking ID :   #%d\n", bookingId));
        receiptText.append(String.format(" Guest Name :   %s\n", guestName));
        receiptText.append(String.format(" Room No.   :   %d (%s Room)\n", roomNumber, roomType));
        receiptText.append(String.format(" Stay Date  :   %s\n\n", dateStr));
        receiptText.append("-----------------------------------------\n");
        receiptText.append(String.format(" Base Room Charge :   $%.2f\n", pricePerNight));
        receiptText.append(String.format(" Room Tax (12%%)   :   $%.2f\n", taxAmount));
        receiptText.append("-----------------------------------------\n");
        receiptText.append(String.format(" GRAND TOTAL      :   $%.2f\n\n", totalAmount));
        receiptText.append("=========================================\n");
        receiptText.append("       Thank you for choosing us!        \n");
        receiptText.append("=========================================");

        JTextArea textArea = new JTextArea(receiptText.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setBackground(new Color(245, 245, 245));
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(360, 380));

        JOptionPane.showMessageDialog(this, scrollPane, "Booking Confirmation Receipt", JOptionPane.PLAIN_MESSAGE);
    }

    private static class ColorButton extends JButton {
        public ColorButton(String text, Color bg) {
            super(text);
            setBackground(bg);
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setFont(new Font("Arial", Font.BOLD, 13));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HotelMSFrame().setVisible(true));
    }
}