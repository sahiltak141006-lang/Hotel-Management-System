# Luxe Hotel Management System

A comprehensive Java desktop application utilizing a Swing GUI frontend and a MySQL database backend to manage room reservations, user authentication, and automated billing receipts.

## Key Features
* **Interactive Dashboard Matrix:** Built a dynamic table view showcasing real-time room availability, reservation statuses, and guest tracking data.
* **CardLayout Navigation:** Configured clean view-switching architecture using `CardLayout` to seamlessly transition users from a secure login screen to the primary operations console.
* **Transactional Key Tracking:** Designed database routine interactions via `Statement.RETURN_GENERATED_KEYS` to auto-fetch transaction IDs instantly upon new room bookings.
* **Automated Receipt Generation:** Programmed a precise financial rendering module that automatically calculates stay costs, applies a 12% room tax, and formats a clean text-based customer receipt.

## Tech Stack
* **Language:** Java
* **GUI Components:** Java Swing, Java AWT (CardLayout, GridBagLayout)
* **Database:** MySQL
* **Driver:** JDBC (mysql-connector-j)

## Database Schema Requirement
To test this system locally, initialize a MySQL database named `hotel_db` and structure your tables as follows:

```sql
CREATE DATABASE hotel_db;
USE hotel_db;

CREATE TABLE users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(50) NOT NULL
);

CREATE TABLE bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    room_number INT NOT NULL,
    booking_date VARCHAR(20) NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    status VARCHAR(20) DEFAULT 'Booked'
);

-- Insert a default administrator user for testing login
INSERT INTO users VALUES ('admin', 'admin123');
