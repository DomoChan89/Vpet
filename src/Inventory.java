import java.util.Map;
import java.util.TreeMap;

/**
 * The player's bag: the coins earned from mini-games and the food bought at the shop.
 * It belongs to the player, not the pet, so it carries over if a pet dies and you adopt again.
 *
 * Change log:
 *   2026-10-26  Created to hold coins and food for the shop.
 */
public class Inventory {
    private int coins;
    private final Map<String, Integer> foods = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

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

    /** Puts one of a food in the bag. */
    public void addFood(String food) {
        foods.merge(food, 1, Integer::sum);
    }

    /** Returns how many of a food are in the bag (ignores upper/lower case). */
    public int countFood(String food) {
        return foods.getOrDefault(food, 0);
    }

    /** Takes one of a food out of the bag. Returns false if there isn't any. */
    public boolean useFood(String food) {
        int count = countFood(food);
        if (count == 0) return false;
        if (count == 1) foods.remove(food);
        else foods.put(food, count - 1);
        return true;
    }

    /** Lists the coins and food in the bag, e.g. "Coins: 12 | Food: 2x Bacon, 1x Tuna". */
    @Override
    public String toString() {
        if (foods.isEmpty()) return "Coins: " + coins + " | Food: (none)";
        StringBuilder list = new StringBuilder();
        for (Map.Entry<String, Integer> entry : foods.entrySet()) {
            if (!list.isEmpty()) list.append(", ");
            list.append(entry.getValue()).append("x ").append(entry.getKey());
        }
        return "Coins: " + coins + " | Food: " + list;
    }
}
