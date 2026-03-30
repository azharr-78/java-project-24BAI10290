// Inventory.java
// This class manages the full list of furniture items.
// It reads from a file when the program starts, and saves back to file after any change.
// An ArrayList keeps all the FurnitureItem objects together in one place.

import java.io.*;
import java.util.ArrayList;

public class Inventory {

    // The master list of all furniture items currently in the system.
    private ArrayList<FurnitureItem> itemList;

    // The file where inventory data is stored between program runs.
    private static final String FILE_PATH = "inventory.txt";

    // Constructor - loads existing inventory from file right away.
    public Inventory() {
        itemList = new ArrayList<>();
        loadFromFile();
    }

    // Returns the full list (used by Billing to search for items).
    public ArrayList<FurnitureItem> getItemList() {
        return itemList;
    }

    // Searches for an item by its ID number.
    // Returns the item if found, null if no match.
    public FurnitureItem findItemById(int id) {
        for (FurnitureItem item : itemList) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    // Adds a brand new furniture product to the inventory.
    public void addItem(int id, String name, double price, int quantity) {
        // Quick check - make sure the ID isn't already taken.
        if (findItemById(id) != null) {
            System.out.println("  [!] An item with ID " + id + " already exists. Please use a different ID.");
            return;
        }
        FurnitureItem newItem = new FurnitureItem(id, name, price, quantity);
        itemList.add(newItem);
        saveToFile();
        System.out.println("  [+] \"" + name + "\" has been added to inventory successfully.");
    }

    // Updates the stock of an existing item (useful when new stock arrives).
    public void updateStock(int id, int newQuantity) {
        FurnitureItem item = findItemById(id);
        if (item == null) {
            System.out.println("  [!] No item found with ID " + id + ".");
            return;
        }
        item.setStockQuantity(newQuantity);
        saveToFile();
        System.out.println("  [~] Stock for \"" + item.getName() + "\" updated to " + newQuantity + " units.");
    }

    // Removes an item from inventory entirely.
    public void removeItem(int id) {
        FurnitureItem item = findItemById(id);
        if (item == null) {
            System.out.println("  [!] No item found with ID " + id + ".");
            return;
        }
        itemList.remove(item);
        saveToFile();
        System.out.println("  [-] \"" + item.getName() + "\" has been removed from inventory.");
    }

    // Prints the full inventory to the console in a readable table format.
    public void displayInventory() {
        if (itemList.isEmpty()) {
            System.out.println("  [!] Inventory is currently empty.");
            return;
        }
        System.out.println();
        System.out.println("  ============================================================");
        System.out.println("               MAGS FURNITURE - CURRENT STOCK                ");
        System.out.println("  ============================================================");
        System.out.printf("  %-6s %-30s %-12s %-8s%n", "ID", "Product Name", "Price (Rs.)", "Stock");
        System.out.println("  ------------------------------------------------------------");
        for (FurnitureItem item : itemList) {
            item.displayItem();
        }
        System.out.println("  ============================================================");
        System.out.println();
    }

    // --- File I/O: Reading ---
    // Reads inventory.txt line by line when the program starts.
    // Each line is split by commas and parsed into a FurnitureItem object.
    private void loadFromFile() {
        File file = new File(FILE_PATH);

        // If the file doesn't exist yet, that's fine - maybe first run.
        if (!file.exists()) {
            System.out.println("  [i] No existing inventory file found. Starting fresh.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue; // skip blank lines

                String[] parts = line.split(",");
                if (parts.length < 4) continue; // skip malformed lines

                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                double price = Double.parseDouble(parts[2].trim());
                int qty = Integer.parseInt(parts[3].trim());

                itemList.add(new FurnitureItem(id, name, price, qty));
            }
            System.out.println("  [i] Inventory loaded successfully. " + itemList.size() + " item(s) found.");
        } catch (IOException e) {
            System.out.println("  [!] Error reading inventory file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("  [!] Corrupt data found in inventory file. Some items may not have loaded.");
        }
    }

    // --- File I/O: Writing ---
    // Rewrites the entire inventory.txt file after any change.
    // Simple but effective for a small inventory system.
    public void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (FurnitureItem item : itemList) {
                writer.write(item.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("  [!] Error saving inventory to file: " + e.getMessage());
        }
    }
}
