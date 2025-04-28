package mini_proj;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Scanner;
public class PharmacyManagementSystem {
    static class User {
        protected String username;
        protected String password;
        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }
        public boolean login(String username, String password) {
            return this.username.equals(username) && this.password.equals(password);
        }
    }
    static class Admin extends User {
        public Admin(String username, String password) {
            super(username, password);
        }
        public void addMedicine(Medicine med, Inventory inventory, boolean showPopup) {
            Medicine existingMed = inventory.getMedicine(med.getName(), med.getPrice(), med.getExpiryDate());
            if (existingMed != null) {
                existingMed.setQuantity(existingMed.getQuantity() + med.getQuantity());
                if (showPopup) {
                    JOptionPane.showMessageDialog(null, "Medicine updated successfully: Quantity added.", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                inventory.addMedicine(med);
                if (showPopup) {
                    JOptionPane.showMessageDialog(null, "New medicine added to inventory.", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }
    static class Salesperson extends User {
        public Salesperson(String username, String password) {
            super(username, password);
        }
        public void sellMedicine(String name, int quantity, Inventory inventory, Billing billing) {
            Medicine med = inventory.getMedicine(name);
            if (med != null && med.getQuantity() >= quantity) {
                billing.generateBill(med, quantity);
                med.setQuantity(med.getQuantity() - quantity);
                JOptionPane.showMessageDialog(null, "Sale successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error: Medicine not found or insufficient stock.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    static class Medicine {
        private String name;
        private double price;
        private int quantity;
        private String expiryDate;
        public Medicine(String name, double price, int quantity, String expiryDate) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
            this.expiryDate = expiryDate;
        }
        public String getName() { return name; }
        public double getPrice() { return price; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public String getExpiryDate() { return expiryDate; }
        public String info() {
            return String.format("Name: %s | Price: ₹%.2f | Quantity: %d | Expiry: %s",
                    name, price, quantity, expiryDate);
        }
    }
    static class Inventory {
        private ArrayList<Medicine> stock = new ArrayList<>();
        public void addMedicine(Medicine med) {
            stock.add(med);
        }
        public Medicine getMedicine(String name, double price, String expiryDate) {
            for (Medicine m : stock) {
                if (m.getName().equalsIgnoreCase(name) && m.getPrice() == price && m.getExpiryDate().equals(expiryDate)) {
                    return m;
                }
            }
            return null;
        }
        public Medicine getMedicine(String name) {
            for (Medicine m : stock) {
                if (m.getName().equalsIgnoreCase(name)) {
                    return m;
                }
            }
            return null;
        }
        public String displayStock() {
            StringBuilder inventoryDisplay = new StringBuilder();
            for (Medicine m : stock) {
                inventoryDisplay.append(m.info()).append("\n");
            }
            return inventoryDisplay.toString();
        }
    }
    static class Billing {
        public void generateBill(Medicine med, int quantity) {
            double total = med.getPrice() * quantity;
            JOptionPane.showMessageDialog(null, String.format("Medicine: %s\nQuantity: %d\nTotal Price: ₹%.2f",
                    med.getName(), quantity, total), "Bill", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Admin admin = new Admin("admin", "admin123");
        Salesperson sales = new Salesperson("sales", "sales123");
        Inventory inventory = new Inventory();
        Billing billing = new Billing();
        admin.addMedicine(new Medicine("Paracetamol", 10.0, 100, "12/2025"), inventory, false);
        admin.addMedicine(new Medicine("Amoxicillin", 20.0, 50, "11/2026"), inventory, false);
        admin.addMedicine(new Medicine("Ibuprofen", 15.0, 80, "10/2025"), inventory, false);
        admin.addMedicine(new Medicine("Cetirizine", 5.0, 120, "08/2026"), inventory, false);
        admin.addMedicine(new Medicine("Azithromycin", 25.0, 60, "09/2025"), inventory, false);
        //  User Login
        String uname = JOptionPane.showInputDialog(null, "Enter username:", "Login", JOptionPane.PLAIN_MESSAGE);
        String pass = JOptionPane.showInputDialog(null, "Enter password:", "Login", JOptionPane.PLAIN_MESSAGE);
        //  Admin flow
        if (admin.login(uname, pass)) {
            JOptionPane.showMessageDialog(null, "Admin logged in.", "Login Successful", JOptionPane.INFORMATION_MESSAGE);
            int response = JOptionPane.showConfirmDialog(null, "Do you want to add a new medicine?", "Add Medicine", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                String name = JOptionPane.showInputDialog(null, "Enter medicine name:", "Add Medicine", JOptionPane.PLAIN_MESSAGE);
                double price = Double.parseDouble(JOptionPane.showInputDialog(null, "Enter medicine price:", "Add Medicine", JOptionPane.PLAIN_MESSAGE));
                int quantity = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter medicine quantity:", "Add Medicine", JOptionPane.PLAIN_MESSAGE));
                String expiry = JOptionPane.showInputDialog(null, "Enter expiry date (dd/mm/yyyy):", "Add Medicine", JOptionPane.PLAIN_MESSAGE);
                Medicine newMed = new Medicine(name, price, quantity, expiry);
                admin.addMedicine(newMed, inventory, true);
                JOptionPane.showMessageDialog(null, inventory.displayStock(), "Updated Inventory", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        //  Salesperson flow
        else if (sales.login(uname, pass)) {
            JOptionPane.showMessageDialog(null, "Salesperson logged in.", "Login Successful", JOptionPane.INFORMATION_MESSAGE);
            JOptionPane.showMessageDialog(null, inventory.displayStock(), "Current Inventory", JOptionPane.INFORMATION_MESSAGE);
            String medName = JOptionPane.showInputDialog(null, "Enter medicine name to sell:", "Sell Medicine", JOptionPane.PLAIN_MESSAGE);
            int qty = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter quantity:", "Sell Medicine", JOptionPane.PLAIN_MESSAGE));
            sales.sellMedicine(medName, qty, inventory, billing);
            JOptionPane.showMessageDialog(null, inventory.displayStock(), "Updated Inventory", JOptionPane.INFORMATION_MESSAGE);
        }
        else {
            JOptionPane.showMessageDialog(null, "Login failed. Invalid credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
        scanner.close();
    }
}
