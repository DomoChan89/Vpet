import java.util.List;

/**
 * A food item sold at the shop: its name, how many coins it costs, and its type.
 * Meals fill the hunger meter. Snacks and candy only fill it a little, but they're
 * the only foods that raise affection.
 *
 * Change log:
 *   2026-10-22  Created as an empty placeholder.
 *   2026-10-26  Added class comment.
 *   2026-10-26  Now holds a food's name and price for the shop.
 *   2026-10-26  Added food types (meal, snack, candy) and the snacks and candy every pet can eat.
 */
public class Food {
    /** What kind of food it is, which decides what it does when eaten (see Pet.feed). */
    public enum Type { MEAL, SNACK, CANDY }

    /** Snacks and candy, sold at the shop for every kind of pet. */
    public static final List<Food> TREATS = List.of(
            new Food("Cookie", 6, Type.SNACK),
            new Food("Crackers", 5, Type.SNACK),
            new Food("Candy", 8, Type.CANDY),
            new Food("Lollipop", 9, Type.CANDY)
    );

    private final String name;
    private final int price;
    private final Type type;

    /** Creates a food with a name, a price in coins, and a type. */
    public Food(String name, int price, Type type) {
        this.name = name;
        this.price = price;
        this.type = type;
    }

    /** Returns the type of a food by name: SNACK or CANDY for treats, otherwise MEAL. */
    public static Type typeOf(String name) {
        for (Food treat : TREATS) {
            if (treat.name.equalsIgnoreCase(name)) return treat.type;
        }
        return Type.MEAL;
    }

    /** Returns the food's name. */
    public String getName() { return name; }

    /** Returns the price in coins. */
    public int getPrice() { return price; }

    /** Returns whether it's a meal, snack, or candy. */
    public Type getType() { return type; }
}
