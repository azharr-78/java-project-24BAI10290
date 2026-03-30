
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Billing {

    // 18% is the standard GST rate applicable to furniture in India.
    private static final double GST_RATE = 0.18;

    // A simple inner structure to hold cart entries.
    // Each entry pairs a FurnitureItem with how many units the customer wants.
    private static class CartEntry {
        FurnitureItem item;
        int quantity;
        double lineTotal; // price * quantity, before tax

        CartEntry(FurnitureItem item, int quantity) {
            this.item = item;
            this.quantity = quantity;
            this.lineTotal = item.getPrice() * quantity;
        }
    }

    // The customer's cart - a list of CartEntry objects.
    private ArrayList<CartEntry> cart;

    // The Inventory reference lets us check stock and reduce it after purchase.
    private Inventory inventory;

    // Customer name for personalizing the bill.
    private String customerName;

    // Constructor
    public Billing(Inventory inventory, String customerName) {
        this.inventory = inventory;
        this.customerName = customerName;
        this.cart = new ArrayList<>();
        System.out.println("\n  [i] New billing session started for: " + customerName);
    }

    // Adds an item to the cart if it exists and stock is available.
    public void addToCart(int itemId, int quantity) {
        FurnitureItem item = inventory.findItemById(itemId);

        if (item == null) {
            System.out.println("  [!] Item ID " + itemId + " not found in inventory.");
            return;
        }
        if (quantity <= 0) {
            System.out.println("  [!] Quantity must be at least 1.");
            return;
        }
        if (item.getStockQuantity() < quantity) {
            System.out.println("  [!] Not enough stock. Only " + item.getStockQuantity()
                    + " unit(s) of \"" + item.getName() + "\" available.");
            return;
        }

        // Check if the item is already in the cart - if so, just increase quantity.
        for (CartEntry entry : cart) {
            if (entry.item.getId() == itemId) {
                int totalWanted = entry.quantity + quantity;
                if (item.getStockQuantity() < totalWanted) {
                    System.out.println("  [!] Cannot add more. Only " + item.getStockQuantity()
                            + " unit(s) available in total.");
                    return;
                }
                entry.quantity = totalWanted;
                entry.lineTotal = item.getPrice() * totalWanted;
                System.out.println("  [+] Updated cart: \"" + item.getName()
                        + "\" x" + totalWanted);
                return;
            }
        }

        // New item in cart.
        cart.add(new CartEntry(item, quantity));
        System.out.println("  [+] Added to cart: \"" + item.getName() + "\" x" + quantity);
    }

    // Removes an item from the cart entirely.
    public void removeFromCart(int itemId) {
        CartEntry toRemove = null;
        for (CartEntry entry : cart) {
            if (entry.item.getId() == itemId) {
                toRemove = entry;
                break;
            }
        }
        if (toRemove == null) {
            System.out.println("  [!] Item ID " + itemId + " is not in the cart.");
            return;
        }
        cart.remove(toRemove);
        System.out.println("  [-] Removed \"" + toRemove.item.getName() + "\" from cart.");
    }

    // Shows what's currently in the cart without generating a bill.
    public void viewCart() {
        if (cart.isEmpty()) {
            System.out.println("  [i] Cart is empty.");
            return;
        }
        System.out.println("\n  ---------- CURRENT CART ----------");
        double subtotal = 0;
        for (CartEntry entry : cart) {
            System.out.printf("  %-28s x%-3d  Rs. %8.2f%n",
                    entry.item.getName(), entry.quantity, entry.lineTotal);
            subtotal += entry.lineTotal;
        }
        double gstAmount = subtotal * GST_RATE;
        double grandTotal = subtotal + gstAmount;
        System.out.println("  ----------------------------------");
        System.out.printf("  %-33s Rs. %8.2f%n", "Subtotal:", subtotal);
        System.out.printf("  %-33s Rs. %8.2f%n", "GST (18%):", gstAmount);
        System.out.printf("  %-33s Rs. %8.2f%n", "GRAND TOTAL:", grandTotal);
        System.out.println("  ----------------------------------\n");
    }

    // The main billing action - deducts stock and writes a bill file.
    // Returns true if bill was generated, false if cart was empty.
    public boolean generateBill() {
        if (cart.isEmpty()) {
            System.out.println("  [!] Cannot generate bill - cart is empty.");
            return false;
        }

        // Calculate totals.
        double subtotal = 0;
        for (CartEntry entry : cart) {
            subtotal += entry.lineTotal;
        }
        double gstAmount = subtotal * GST_RATE;
        double grandTotal = subtotal + gstAmount;

        // Reduce stock for each item purchased.
        for (CartEntry entry : cart) {
            entry.item.reduceStock(entry.quantity);
        }
        // Save updated inventory to file.
        inventory.saveToFile();

        // Build the bill text.
        String billContent = buildBillText(subtotal, gstAmount, grandTotal);

        // Print the bill to the console.
        System.out.println(billContent);

        // Save the bill to a uniquely named .txt file.
        saveBillToFile(billContent, grandTotal);

        // Clear the cart after the transaction.
        cart.clear();
        return true;
    }

    // Builds the formatted bill as a string (used for both console and file output).
    private String buildBillText(double subtotal, double gstAmount, double grandTotal) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy  HH:mm:ss");
        String timestamp = LocalDateTime.now().format(dtf);

        StringBuilder bill = new StringBuilder();
        bill.append("\n");
        bill.append("  ************************************************************\n");
        bill.append("                    MAGS FURNITURE                            \n");
        bill.append("          Quality Furniture for Every Home & Office           \n");
        bill.append("  ************************************************************\n");
        bill.append("  Date & Time : ").append(timestamp).append("\n");
        bill.append("  Customer    : ").append(customerName).append("\n");
        bill.append("  ------------------------------------------------------------\n");
        bill.append(String.format("  %-28s %-5s %-10s %-10s%n",
                "Item", "Qty", "Unit Price", "Total"));
        bill.append("  ------------------------------------------------------------\n");

        for (CartEntry entry : cart) {
            bill.append(String.format("  %-28s %-5d Rs.%-7.2f Rs.%-7.2f%n",
                    entry.item.getName(),
                    entry.quantity,
                    entry.item.getPrice(),
                    entry.lineTotal));
        }

        bill.append("  ------------------------------------------------------------\n");
        bill.append(String.format("  %-36s Rs. %8.2f%n", "Subtotal:", subtotal));
        bill.append(String.format("  %-36s Rs. %8.2f%n", "GST @ 18%:", gstAmount));
        bill.append("  ============================================================\n");
        bill.append(String.format("  %-36s Rs. %8.2f%n", "GRAND TOTAL:", grandTotal));
        bill.append("  ============================================================\n");
        bill.append("     Thank you for shopping at MAGS FURNITURE!               \n");
        bill.append("       We hope to see you again soon.                        \n");
        bill.append("  ************************************************************\n");

        return bill.toString();
    }

    // Writes the bill to a .txt file named after the customer and timestamp.
    private void saveBillToFile(String billContent, double grandTotal) {
        // Create a bills directory if it doesn't exist.
        File billsDir = new File("bills");
        if (!billsDir.exists()) {
            billsDir.mkdir();
        }

        // File name uses customer name + timestamp to stay unique.
        String safeCustomerName = customerName.replaceAll("[^a-zA-Z0-9]", "_");
        DateTimeFormatter filenameDtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(filenameDtf);
        String fileName = "bills/Bill_" + safeCustomerName + "_" + timestamp + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(billContent);
            System.out.println("  [i] Bill saved to file: " + fileName);
        } catch (IOException e) {
            System.out.println("  [!] Could not save bill to file: " + e.getMessage());
        }
    }
}
