/**
* CSC 1061 1H2
* Instructor: Jim Eberle
* Homework #: Final Programming Project
* Date: 11/19/2026
* Class: Store.java
* @author Dominique Wigginton
* Description:
*   The shop, where the player spends coins from mini-games on food. It stocks
*   your current pet's favorite foods plus snacks and candy for every pet.
*   Bought food goes into the player's Inventory.
*
* Inputs:  The current pet, and the item number (or 'back') typed by the player.
* Outputs: The shop menu, purchase messages, coins spent, and food added to the bag.
*
* Change log:
*   Date        Description
*   2026-10-22  Created as an empty placeholder.
*   2026-10-26  Added class comment.
*   2026-10-26  Now a working shop that sells each pet's favorite foods for coins.
*   2026-10-26  Also sells snacks and candy (Food.TREATS), which raise affection.
*   2026-11-19  The shop's stock is now my own MyLinkedList<Food> instead of an ArrayList.
*               The menu walks the list with a for-each loop instead of get(i).
*   2026-11-19  Added the CSC 1061 file header (course, instructor, date, author,
*               description, inputs, outputs) to the top of the file.
*   2026-11-19  Reads input with VirtualPetGame.readLine() so running out of input can't crash it.
*/

import java.util.Map;
import java.util.Scanner;

public class Store {
    /** Price in coins of every food the shop can sell. */
    private static final Map<String, Integer> PRICES = Map.ofEntries(
            // Dog
            Map.entry("Bacon", 12), Map.entry("Chicken", 10), Map.entry("Peanut Butter", 8),
            Map.entry("Steak", 15), Map.entry("Kibble", 5), Map.entry("Bone", 6),
            // Cat
            Map.entry("Tuna", 10), Map.entry("Salmon", 12), Map.entry("Milk", 5),
            Map.entry("Shrimp", 12), Map.entry("Catnip", 8),
            // Dinosaur
            Map.entry("Ferns", 6), Map.entry("Ribs", 14), Map.entry("Leaves", 4),
            Map.entry("Fish", 10), Map.entry("Giant Egg", 20),
            // Bird
            Map.entry("Seeds", 4), Map.entry("Millet", 5), Map.entry("Apple", 6),
            Map.entry("Berries", 7), Map.entry("Mealworms", 9), Map.entry("Sunflower Seeds", 6)
    );
    private static final int DEFAULT_PRICE = 10;

    private final Scanner scanner;
    private final Inventory inventory;

    /** Creates the shop, reading the player's choices from the scanner and using their inventory. */
    public Store(Scanner scanner, Inventory inventory) {
        this.scanner = scanner;
        this.inventory = inventory;
    }

    /** Returns what the shop sells for this pet: its favorite foods (meals), then the snacks and candy. */
    public MyLinkedList<Food> stockFor(Pet pet) {
        MyLinkedList<Food> stock = new MyLinkedList<>();
        for (String name : pet.getFavoriteFoods()) {
            stock.add(new Food(name, PRICES.getOrDefault(name, DEFAULT_PRICE), Food.Type.MEAL));
        }
        for (Food treat : Food.TREATS) {
            stock.add(treat);
        }
        return stock;
    }

    /**
     * Shows the shop and lets the player buy items by number until they type "back".
     * Buying something takes coins and adds the food to the inventory.
     */
    public void visit(Pet pet) {
        MyLinkedList<Food> stock = stockFor(pet);
        System.out.println("\n=== Pet Shop ===");

        while (true) {
            System.out.println("\n" + pet.getName() + "'s favorite meals (fill the hunger meter all the way):");
            int number = 1;
            Food.Type previousType = Food.Type.MEAL;
            for (Food food : stock) {
                if (food.getType() != Food.Type.MEAL && previousType == Food.Type.MEAL) {
                    System.out.println("Snacks and candy (fill a little, but raise affection):");
                }
                String label = food.getType() == Food.Type.MEAL ? "" : food.getType() == Food.Type.SNACK ? "snack" : "candy";
                System.out.printf("  %2d) %-16s %3d coins  %-6s (you have %d)%n",
                        number, food.getName(), food.getPrice(), label, inventory.countFood(food.getName()));
                previousType = food.getType();
                number++;
            }
            System.out.println("You have " + inventory.getCoins() + " coins.");
            System.out.print("Buy which item? (1-" + stock.size() + ", or 'back') ");

            String choice = VirtualPetGame.readLine(scanner).trim().toLowerCase();
            if (choice.equals("back")) {
                System.out.println("Thanks for shopping!");
                return;
            }

            int index;
            try {
                index = Integer.parseInt(choice) - 1;
            } catch (NumberFormatException e) {
                index = -1;
            }
            if (index < 0 || index >= stock.size()) {
                System.out.println("Please type a number from 1 to " + stock.size() + ", or 'back'.");
                continue;
            }

            Food food = stock.get(index);
            if (inventory.spendCoins(food.getPrice())) {
                inventory.addFood(food.getName());
                System.out.println("You bought " + food.getName() + "! Coins left: " + inventory.getCoins());
            } else {
                System.out.println("Not enough coins for " + food.getName() + ". Play some games to earn more!");
            }
        }
    }
}
