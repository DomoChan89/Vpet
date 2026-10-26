import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * The shop, where the player spends coins from mini-games on food. It stocks
 * your current pet's favorite foods plus snacks and candy for every pet.
 * Bought food goes into the player's Inventory.
 *
 * Change log:
 *   2026-10-22  Created as an empty placeholder.
 *   2026-10-26  Added class comment.
 *   2026-10-26  Now a working shop that sells each pet's favorite foods for coins.
 *   2026-10-26  Also sells snacks and candy (Food.TREATS), which raise affection.
 */
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
    public List<Food> stockFor(Pet pet) {
        List<Food> stock = new ArrayList<>();
        for (String name : pet.getFavoriteFoods()) {
            stock.add(new Food(name, PRICES.getOrDefault(name, DEFAULT_PRICE), Food.Type.MEAL));
        }
        stock.addAll(Food.TREATS);
        return stock;
    }

    /**
     * Shows the shop and lets the player buy items by number until they type "back".
     * Buying something takes coins and adds the food to the inventory.
     */
    public void visit(Pet pet) {
        List<Food> stock = stockFor(pet);
        System.out.println("\n=== Pet Shop ===");

        while (true) {
            System.out.println("\n" + pet.getName() + "'s favorite meals (fill the hunger meter all the way):");
            for (int i = 0; i < stock.size(); i++) {
                Food food = stock.get(i);
                if (food.getType() != Food.Type.MEAL && stock.get(i - 1).getType() == Food.Type.MEAL) {
                    System.out.println("Snacks and candy (fill a little, but raise affection):");
                }
                String label = food.getType() == Food.Type.MEAL ? "" : food.getType() == Food.Type.SNACK ? "snack" : "candy";
                System.out.printf("  %2d) %-16s %3d coins  %-6s (you have %d)%n",
                        i + 1, food.getName(), food.getPrice(), label, inventory.countFood(food.getName()));
            }
            System.out.println("You have " + inventory.getCoins() + " coins.");
            System.out.print("Buy which item? (1-" + stock.size() + ", or 'back') ");

            String choice = scanner.nextLine().trim().toLowerCase();
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
