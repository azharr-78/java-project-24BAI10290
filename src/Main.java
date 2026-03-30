
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    // One Scanner shared across the whole program to avoid resource leaks.
    private static Scanner scanner = new Scanner(System.in);
    private static Inventory inventory;

    public static void main(String[] args) {

        printWelcomeBanner();

        // Load inventory from file (happens inside the Inventory constructor).
        inventory = new Inventory();

        boolean running = true;

        while (running) {
            printMainMenu();
            int choice = readInt("  Enter your choice: ");

            switch (choice) {
                case 1:
                    // View the full stock list
                    inventory.displayInventory();
                    break;

                case 2:
                    // Add a new item to inventory
                    handleAddItem();
                    break;

                case 3:
                    // Update stock quantity for an existing item
                    handleUpdateStock();
                    break;

                case 4:
                    // Remove an item from inventory
                    handleRemoveItem();
                    break;

                case 5:
                    // Start a billing session for a customer
                    handleBillingSession();
                    break;

                case 6:
                    // Exit
                    System.out.println("\n  Thank you for using MAGS FURNITURE System. Goodbye!\n");
                    running = false;
                    break;

                default:
                    System.out.println("  [!] Invalid option. Please enter a number between 1 and 6.");
            }
        }

        scanner.close();
    }

    // --- Main menu display ---
    private static void printMainMenu() {
        System.out.println("  ============================================================");
        System.out.println("                    MAIN MENU                                 ");
        System.out.println("  ============================================================");
        System.out.println("    1. View Current Stock");
        System.out.println("    2. Add New Item to Inventory");
        System.out.println("    3. Update Item Stock");
        System.out.println("    4. Remove Item from Inventory");
        System.out.println("    5. Create Customer Bill");
        System.out.println("    6. Exit");
        System.out.println("  ============================================================");
    }

    // --- Welcome banner shown once at startup ---
    private static void printWelcomeBanner() {
        System.out.println();
        System.out.println("  ************************************************************");
        System.out.println("  *                                                          *");
        System.out.println("  *             MAGS FURNITURE                               *");
        System.out.println("  *       Inventory & Billing Management System              *");
        System.out.println("  *                                                          *");
        System.out.println("  ************************************************************");
        System.out.println();
    }

    // --- Option 2: Add a new item ---
    private static void handleAddItem() {
        System.out.println("\n  --- Add New Furniture Item ---");
        int id = readInt("  Enter Item ID (number): ");
        System.out.print("  Enter Item Name: ");
        scanner.nextLine(); // consume leftover newline
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("  [!] Item name cannot be blank.");
            return;
        }
        double price = readDouble("  Enter Price (Rs.): ");
        int qty = readInt("  Enter Stock Quantity: ");
        inventory.addItem(id, name, price, qty);
    }

    // --- Option 3: Update stock ---
    private static void handleUpdateStock() {
        System.out.println("\n  --- Update Item Stock ---");
        inventory.displayInventory();
        int id = readInt("  Enter Item ID to update: ");
        int newQty = readInt("  Enter new stock quantity: ");
        inventory.updateStock(id, newQty);
    }

    // --- Option 4: Remove item ---
    private static void handleRemoveItem() {
        System.out.println("\n  --- Remove Item from Inventory ---");
        inventory.displayInventory();
        int id = readInt("  Enter Item ID to remove: ");
        System.out.print("  Are you sure you want to remove this item? (yes/no): ");
        scanner.nextLine(); // consume leftover newline
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (confirm.equals("yes")) {
            inventory.removeItem(id);
        } else {
            System.out.println("  [i] Removal cancelled.");
        }
    }

    // --- Option 5: Full billing session ---
    private static void handleBillingSession() {
        System.out.println("\n  --- New Customer Billing Session ---");
        System.out.print("  Enter Customer Name: ");
        scanner.nextLine(); // consume leftover newline
        String customerName = scanner.nextLine().trim();
        if (customerName.isEmpty()) {
            System.out.println("  [!] Customer name cannot be blank.");
            return;
        }

        // Create a fresh Billing object for this customer.
        Billing billing = new Billing(inventory, customerName);

        boolean billing_active = true;

        while (billing_active) {
            printBillingMenu();
            int choice = readInt("  Enter your choice: ");

            switch (choice) {
                case 1:
                    // Show full inventory so staff can see IDs and prices.
                    inventory.displayInventory();
                    break;

                case 2:
                    // Add an item to the cart.
                    int itemId = readInt("  Enter Item ID: ");
                    int qty = readInt("  Enter Quantity: ");
                    billing.addToCart(itemId, qty);
                    break;

                case 3:
                    // Remove an item from the cart.
                    int removeId = readInt("  Enter Item ID to remove from cart: ");
                    billing.removeFromCart(removeId);
                    break;

                case 4:
                    // Preview the cart before finalizing.
                    billing.viewCart();
                    break;

                case 5:
                    // Generate and save the final bill.
                    billing.generateBill();
                    billing_active = false; // session ends after billing
                    break;

                case 6:
                    // Cancel the session - no bill generated, no stock changed.
                    System.out.println("  [i] Billing session cancelled. No changes made.");
                    billing_active = false;
                    break;

                default:
                    System.out.println("  [!] Invalid option. Enter 1-6.");
            }
        }
    }

    // --- Billing sub-menu ---
    private static void printBillingMenu() {
        System.out.println();
        System.out.println("  -------- BILLING SESSION MENU --------");
        System.out.println("    1. View Inventory (to pick items)");
        System.out.println("    2. Add Item to Cart");
        System.out.println("    3. Remove Item from Cart");
        System.out.println("    4. View Cart");
        System.out.println("    5. Generate Bill & Finish");
        System.out.println("    6. Cancel Session");
        System.out.println("  ---------------------------------------");
    }

    // --- Helper: reads an integer safely, handles bad input ---
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = scanner.nextInt();
                return value;
            } catch (InputMismatchException e) {
                // User typed something that isn't a number.
                System.out.println("  [!] Please enter a valid whole number.");
                scanner.nextLine(); // clear the bad input from the buffer
            }
        }
    }

    // --- Helper: reads a double safely, handles bad input ---
    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = scanner.nextDouble();
                if (value < 0) {
                    System.out.println("  [!] Value cannot be negative. Try again.");
                    continue;
                }
                return value;
            } catch (InputMismatchException e) {
                System.out.println("  [!] Please enter a valid number (e.g. 4999.00).");
                scanner.nextLine(); // clear bad input
            }
        }
    }
}
