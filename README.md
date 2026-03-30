# MAGS FURNITURE — Inventory & Billing Management System

A console-based Java application built to manage the day-to-day inventory and billing operations of **MAGS FURNITURE**, a retail furniture store. The system lets store staff view and manage stock, create customer bills with automatic GST calculation, and store all data in persistent text files.

---

## What This Project Does

Running a furniture store involves a lot of moving parts — tracking which items are in stock, calculating taxes correctly on every sale, and keeping a record of every bill raised. Doing all of that manually with notebooks or spreadsheets is slow and error-prone.

This program replaces that manual process with a simple CLI (command-line) tool that:

- Shows the current furniture inventory at any time
- Lets staff add new products, update stock levels, or remove discontinued items
- Walks through a billing session step by step — pick items, set quantities, review the cart
- Automatically applies **18% GST** (the standard rate for furniture under India's GST regime)
- Saves the final bill as a `.txt` file inside a `bills/` folder
- Persists all inventory data in `inventory.txt` so nothing is lost when the program closes

---

## Project Structure

```
MagsFurniture/
│
├── src/
│   ├── FurnitureItem.java    # Blueprint class for a single product
│   ├── Inventory.java        # Manages the full stock list + file read/write
│   ├── Billing.java          # Cart logic, GST calculation, bill generation
│   └── Main.java             # Entry point, menus, and user input handling
│
├── inventory.txt             # Persistent data file (auto-created/updated)
├── bills/                    # Folder where generated bills are saved
│   └── Bill_CustomerName_YYYYMMDD_HHmmss.txt
│
└── README.md
```

---

## How to Run

**Requirements:** Java JDK 8 or above. No external libraries needed.

**Step 1 — Compile all four files:**

```bash
cd MagsFurniture/src
javac FurnitureItem.java Inventory.java Billing.java Main.java
```

**Step 2 — Run from the project root** (so the program finds `inventory.txt` correctly):

```bash
cd ..
java -cp src Main
```

You'll see the welcome banner and the main menu right away.

---

## Using the Application

### Main Menu Options

| Option | What It Does |
|--------|-------------|
| 1 | View current stock (all items with price and quantity) |
| 2 | Add a new furniture product to inventory |
| 3 | Update the stock count for an existing item |
| 4 | Remove an item from inventory |
| 5 | Start a billing session for a customer |
| 6 | Exit the program |

### Billing Session

When you choose option 5, you enter a billing sub-menu. Here you:
1. Enter the customer's name
2. View the inventory to note down item IDs
3. Add items to the cart (by ID and quantity)
4. View the cart to review totals before finalizing
5. Generate the bill — stock is automatically reduced and a `.txt` bill file is saved

The generated bill shows a full breakdown: each item, subtotal, GST amount, and grand total.

---

## Key Java Concepts Used

**Object-Oriented Programming**
Each furniture item is represented by a `FurnitureItem` object with private fields and public getters/setters — a straightforward application of encapsulation. The `Inventory` and `Billing` classes each have their own responsibilities and don't step on each other's toes.

**Collections (ArrayList)**
`Inventory` uses an `ArrayList<FurnitureItem>` to store the full product list. `Billing` uses a separate `ArrayList<CartEntry>` for the shopping cart. Both are iterated using enhanced for-loops.

**File I/O**
`Inventory` reads `inventory.txt` at startup using `BufferedReader` and writes it back using `BufferedWriter` after every change. `Billing` writes each customer's invoice to a uniquely named file in the `bills/` directory.

**Exception Handling**
`try-catch` blocks cover file not found, I/O errors, and malformed data in the inventory file. In `Main.java`, all integer and double inputs are wrapped in `try-catch` for `InputMismatchException` — so typing a letter when a number is expected won't crash the program.

---

## Sample Output

```
  ************************************************************
  *                                                          *
  *             MAGS FURNITURE                               *
  *       Inventory & Billing Management System              *
  *                                                          *
  ************************************************************

  [i] Inventory loaded successfully. 15 item(s) found.
```

```
  ************************************************************
                    MAGS FURNITURE                            
          Quality Furniture for Every Home & Office           
  ************************************************************
  Date & Time : 30-03-2025  14:22:08
  Customer    : Ramesh Gupta
  ------------------------------------------------------------
  Item                         Qty   Unit Price Total     
  ------------------------------------------------------------
  King Size Bed (Teak Wood)    1     Rs.24999.00 Rs.24999.00
  Office Chair (Mesh Back)     2     Rs.4999.00  Rs.9998.00 
  ------------------------------------------------------------
  Subtotal:                            Rs.  34997.00
  GST @ 18%:                           Rs.   6299.46
  ============================================================
  GRAND TOTAL:                         Rs.  41296.46
  ============================================================
     Thank you for shopping at MAGS FURNITURE!               
  ************************************************************
```

---

## GST Note

India's GST Council places most furniture under the **18% GST slab** (HSN Chapter 94 — furniture, bedding, mattresses). This rate is hardcoded in `Billing.java` as `GST_RATE = 0.18` and applied to the subtotal before displaying the grand total.

---

## Development Timeline (Commits)

| Commit | Description |
|--------|------------|
| 1 | Initial setup — empty files, folder structure, basic README |
| 2 | Added `FurnitureItem.java` with all fields, getters, and setters |
| 3 | Implemented `Inventory.java` with ArrayList and file read/write |
| 4 | Built `Billing.java` — cart logic, GST calculation, bill file output |
| 5 | Completed `Main.java` with full menu, input validation, and exception handling |
| 6 | Added sample `inventory.txt`, tested edge cases, polished README |

---

## Author

Built as a Java programming course project demonstrating OOP, Collections, File I/O, and Exception Handling.
Store: **MAGS FURNITURE**
