/**
 * A food item sold at the shop: its name and how many coins it costs.
 *
 * Change log:
 *   2026-10-22  Created as an empty placeholder.
 *   2026-10-26  Added class comment.
 *   2026-10-26  Now holds a food's name and price for the shop.
 */
public class Food {
    private final String name;
    private final int price;

    /** Creates a food with a name and a price in coins. */
    public Food(String name, int price) {
        this.name = name;
        this.price = price;
    }

    /** Returns the food's name. */
    public String getName() { return name; }

    /** Returns the price in coins. */
    public int getPrice() { return price; }
}
