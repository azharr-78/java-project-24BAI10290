// FurnitureItem.java
// This class acts as the blueprint for every furniture product in our store.
// Each item has an ID, a name, a price, and how many units we currently have in stock.

public class FurnitureItem {

    // Private fields - we don't let outside code touch these directly.
    // That's the whole point of encapsulation.
    private int id;
    private String name;
    private double price;
    private int stockQuantity;

    // Constructor - called whenever we create a new FurnitureItem object
    public FurnitureItem(int id, String name, double price, int stockQuantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    // --- Getters ---
    // These let other classes read the values without changing them directly.

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    // --- Setters ---
    // These allow controlled updates to the fields.

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    // Reduces the stock by a given amount when a customer buys something.
    // Returns false if there isn't enough stock to fulfill the request.
    public boolean reduceStock(int quantity) {
        if (quantity > this.stockQuantity) {
            return false; // not enough stock
        }
        this.stockQuantity -= quantity;
        return true;
    }

    // A clean, readable way to display one item's info in the console.
    public void displayItem() {
        System.out.printf("  [%d] %-30s Rs. %8.2f   Stock: %d%n",
                id, name, price, stockQuantity);
    }

    // This format is what we write to inventory.txt for saving data between runs.
    // Format: id,name,price,stockQuantity
    public String toFileString() {
        return id + "," + name + "," + price + "," + stockQuantity;
    }
}
