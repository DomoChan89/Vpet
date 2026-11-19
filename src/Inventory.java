/**
* CSC 1061 1H2
* Instructor: Jim Eberle
* Homework #: Final Programming Project
* Date: 11/19/2026
* Class: Inventory.java
* @author Dominique Wigginton
* Description:
*   The player's bag: the coins earned from mini-games and the food bought at the shop.
*   It belongs to the player, not the pet, so it carries over if a pet dies and you adopt
*   again.
*
* Inputs:  Coins earned, coins spent, and food names bought or fed.
* Outputs: Coin and food counts, true/false for spend and use, and the bag listing (A-Z).
*
* Change log:
*   Date        Description
*   2026-10-26  Created to hold coins and food for the shop.
*   2026-11-19  Food is now kept in my own MyLinkedList of FoodEntry (name + count)
*               instead of a java.util TreeMap. Looking up a food is a linear search,
*               and toString() uses insertion sort to list the food in A-Z order.
*   2026-11-19  Added the CSC 1061 file header (course, instructor, date, author,
*               description, inputs, outputs) to the top of the file.
*/
public class Inventory {

    /** One kind of food in the bag and how many of it there are. */
    private static class FoodEntry {
        private final String name;
        private int count;

        FoodEntry(String name) {
            this.name = name;
        }
    }

    private int coins;
    private final MyLinkedList<FoodEntry> foods = new MyLinkedList<>();

    /** Creates a bag with some starting coins and no food. */
    public Inventory(int startingCoins) {
        this.coins = startingCoins;
    }

    /** Returns how many coins the player has. */
    public int getCoins() {
        return coins;
    }

    /** Adds coins, e.g. after winning a mini-game. */
    public void addCoins(int amount) {
        coins += amount;
    }

    /** Takes coins away if the player has enough. Returns false (and takes nothing) if they don't. */
    public boolean spendCoins(int amount) {
        if (amount > coins) return false;
        coins -= amount;
        return true;
    }

    /** Linear search for a food in the bag (ignores upper/lower case). Returns null if it isn't there. */
    private FoodEntry findEntry(String food) {
        return foods.findFirst(entry -> entry.name.equalsIgnoreCase(food));
    }

    /** Puts one of a food in the bag. */
    public void addFood(String food) {
        FoodEntry entry = findEntry(food);
        if (entry == null) {
            entry = new FoodEntry(food);
            foods.add(entry);
        }
        entry.count++;
    }

    /** Returns how many of a food are in the bag (ignores upper/lower case). */
    public int countFood(String food) {
        FoodEntry entry = findEntry(food);
        return (entry == null) ? 0 : entry.count;
    }

    /** Takes one of a food out of the bag. Returns false if there isn't any. */
    public boolean useFood(String food) {
        FoodEntry entry = findEntry(food);
        if (entry == null) return false;
        entry.count--;
        if (entry.count == 0) {
            foods.remove(entry);
        }
        return true;
    }

    /** Lists the coins and food in the bag in A-Z order, e.g. "Coins: 12 | Food: 2x Bacon, 1x Tuna". */
    @Override
    public String toString() {
        if (foods.isEmpty()) return "Coins: " + coins + " | Food: (none)";
        MyLinkedList<FoodEntry> sorted = foods.insertionSort((a, b) -> a.name.compareToIgnoreCase(b.name));
        StringBuilder list = new StringBuilder();
        for (FoodEntry entry : sorted) {
            if (list.length() > 0) list.append(", ");
            list.append(entry.count).append("x ").append(entry.name);
        }
        return "Coins: " + coins + " | Food: " + list;
    }
}
