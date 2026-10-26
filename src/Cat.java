import java.util.List;

/**
 * A pet cat. Meows and purrs, loves fish, chases laser dots, and hates baths.
 *
 * Change log:
 *   2026-10-22  Created as a Pet with sounds, favorite foods, and cat messages.
 *   2026-10-26  Added comments to every method.
 *   2026-10-26  Added pixel-art sprite for the adoption center.
 *   2026-10-26  Added greetings that get warmer as affection grows.
 *   2026-10-26  Hungry check now uses isHungry() for the new hunger meter.
 */
public class Cat extends Pet {
    /** Pixel art: an orange cat face with green eyes and whiskers. */
    public static final String[] SPRITE = {
            ".O........O.",
            ".OO......OO.",
            ".OPO....OPO.",
            ".OOOOOOOOOO.",
            ".OGKOOOOKGO.",
            ".OOOOOOOOOO.",
            "W.OOOPPOOO.W",
            ".WOOWOOWOOW.",
            "..OOOWWOOO..",
            "...OOOOOO...",
    };

    private static final List<String> SOUNDS = List.of(
            "Meow!", "Mew!", "Purrr...", "Hiss!", "Mrrrow?", "*chirp chirp*"
    );

    private static final List<String> FAVORITE_FOODS = List.of(
            "Tuna", "Salmon", "Chicken", "Milk", "Shrimp", "Catnip"
    );

    private final String breed;

    /** Creates a cat with a name and breed, e.g. new Cat("Luna", "Siamese"). */
    public Cat(String name, String breed) {
        super(name);
        this.breed = breed;
    }

    /** Returns the cat's sounds. */
    @Override
    public List<String> getSounds() { return SOUNDS; }

    /** Returns the cat's favorite foods. */
    @Override
    public List<String> getFavoriteFoods() { return FAVORITE_FOODS; }

    /** Returns the cat's pixel art. */
    @Override
    public String[] getSprite() { return SPRITE; }

    /** Cat greetings, from Shy to Best Friends. */
    @Override
    protected String[] getGreetings() {
        return new String[] {
                getName() + " watches you from across the room and doesn't come any closer.",
                getName() + " strolls over and gives you a polite little meow.",
                getName() + " rubs against your legs, purring.",
                getName() + " headbutts you lovingly and curls up in your lap. Purrrr...",
        };
    }

    /** Uses the breed in the status line, e.g. "Luna the Siamese". */
    @Override
    public String getKind() { return breed; }

    /** Yowls when hungry, purrs when happy, otherwise makes a normal sound. */
    @Override
    public String speak() {
        if (isHungry()) return getName() + " yowls at the food bowl: MEOOOW!";
        if (getEnergy() >= 20 && getHappiness() > 80) return getName() + " purrs contentedly: Purrr...";
        return super.speak();
    }

    /** Message shown when the cat eats a favorite food. */
    @Override
    protected String favoriteFoodMessage(String food) {
        return getName() + " gobbles up the " + food + " and purrs loudly!";
    }

    /** Message shown when the cat plays. */
    @Override
    protected String playMessage() {
        return getName() + " pounces on the laser dot! Mew!";
    }

    /** Message shown when the cat gets a bath. */
    @Override
    protected String bathMessage() {
        return getName() + " hisses and glares at you, soaking wet."; // cats really hate baths
    }

    /** Returns the cat's breed. */
    public String getBreed() { return breed; }
}
