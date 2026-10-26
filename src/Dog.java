import java.util.List;

/**
 * A pet dog. Barks, loves meaty food and fetch, and shakes water everywhere after a bath.
 *
 * Change log:
 *   2026-10-22  Created with sounds, favorite foods, stats, and care actions.
 *   2026-10-22  Now extends Pet; shared stats and actions moved there.
 *   2026-10-26  Added comments to every method.
 *   2026-10-26  Added pixel-art sprite for the adoption center.
 *   2026-10-26  Added greetings that get warmer as affection grows.
 */
public class Dog extends Pet {
    /** Pixel art: a brown puppy face with floppy ears and a pink tongue. */
    public static final String[] SPRITE = {
            "..BBBBBBBB..",
            ".DBBBBBBBBD.",
            "DDBKBBBBKBDD",
            "DDBBBBBBBBDD",
            "DD.BBKKBB.DD",
            "D..BBKKBB..D",
            "...BBBBBB...",
            "....BPPB....",
            ".....PP.....",
    };

    private static final List<String> SOUNDS = List.of(
            "Woof!", "Bark bark!", "Arf!", "Grrr...", "Awoooo!", "*whimper*", "*pant pant*"
    );

    private static final List<String> FAVORITE_FOODS = List.of(
            "Bacon", "Chicken", "Peanut Butter", "Steak", "Kibble", "Bone"
    );

    private final String breed;

    /** Creates a dog with a name and breed, e.g. new Dog("Rex", "Beagle"). */
    public Dog(String name, String breed) {
        super(name);
        this.breed = breed;
    }

    /** Returns the dog's sounds. */
    @Override
    public List<String> getSounds() { return SOUNDS; }

    /** Returns the dog's favorite foods. */
    @Override
    public List<String> getFavoriteFoods() { return FAVORITE_FOODS; }

    /** Returns the dog's pixel art. */
    @Override
    public String[] getSprite() { return SPRITE; }

    /** Dog greetings, from Shy to Best Friends. */
    @Override
    protected String[] getGreetings() {
        return new String[] {
                getName() + " sniffs your hand cautiously, tail held low.",
                getName() + " wags their tail and trots over to say hi. Woof!",
                getName() + " bounds over, tail wagging like crazy, and licks your hand!",
                getName() + " leaps into your arms and covers you in puppy kisses! Woof woof!",
        };
    }

    /** Uses the breed in the status line, e.g. "Rex the Beagle". */
    @Override
    public String getKind() { return breed; }

    /** Whines when hungry, barks happily when happy, otherwise makes a normal sound. */
    @Override
    public String speak() {
        if (getHunger() > 70) return getName() + " whines: *whimper* (I'm hungry!)";
        if (getEnergy() >= 20 && getHappiness() > 80) return getName() + " barks happily: Woof woof!";
        return super.speak();
    }

    /** Message shown when the dog eats a favorite food. */
    @Override
    protected String favoriteFoodMessage(String food) {
        return getName() + " devours the " + food + " and wags their tail like crazy!";
    }

    /** Message shown when the dog plays. */
    @Override
    protected String playMessage() {
        return getName() + " fetches the ball! Woof!";
    }

    /** Message shown when the dog gets a bath. */
    @Override
    protected String bathMessage() {
        return getName() + " gets a bath and shakes water everywhere!"; // most dogs aren't fans of baths
    }

    /** Returns the dog's breed. */
    public String getBreed() { return breed; }
}
