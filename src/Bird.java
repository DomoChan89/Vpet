import java.util.List;

/**
 * A pet bird. Tweets and sings, loves seeds and fruit, and splashes in the birdbath.
 *
 * Change log:
 *   2026-10-26  Created as a Pet with sounds, favorite foods, and bird messages.
 *   2026-10-26  Added comments to every method.
 *   2026-10-26  Added pixel-art sprite for the adoption center.
 *   2026-10-26  Added greetings that get warmer as affection grows.
 */
public class Bird extends Pet {
    /** Pixel art: a blue bird with a white belly and yellow beak and feet. */
    public static final String[] SPRITE = {
            "....LLLL....",
            "...LLLLLL...",
            "..LLLLKLLL..",
            "..LLLLLLLYY.",
            "..LWWLLLLL..",
            ".LLWWWWLLL..",
            "LLLWWWWLL...",
            ".LLLWWWLL...",
            "...LLLLL....",
            "....Y..Y....",
            "...YY..YY...",
    };

    private static final List<String> SOUNDS = List.of(
            "Tweet!", "Chirp chirp!", "Squawk!", "*whistle*", "Pretty bird!", "*flap flap*"
    );

    private static final List<String> FAVORITE_FOODS = List.of(
            "Seeds", "Millet", "Apple", "Berries", "Mealworms", "Sunflower Seeds"
    );

    private final String species;

    /** Creates a bird with a name and species, e.g. new Bird("Kiwi", "Parakeet"). */
    public Bird(String name, String species) {
        super(name);
        this.species = species;
    }

    /** Returns the bird's sounds. */
    @Override
    public List<String> getSounds() { return SOUNDS; }

    /** Returns the bird's favorite foods. */
    @Override
    public List<String> getFavoriteFoods() { return FAVORITE_FOODS; }

    /** Returns the bird's pixel art. */
    @Override
    public String[] getSprite() { return SPRITE; }

    /** Bird greetings, from Shy to Best Friends. */
    @Override
    protected String[] getGreetings() {
        return new String[] {
                getName() + " ruffles their feathers and hops to the far end of the perch.",
                getName() + " tilts their head and chirps hello.",
                getName() + " flutters over and lands on your finger. Tweet!",
                getName() + " lands on your shoulder, nuzzles your cheek, and sings you a song!",
        };
    }

    /** Uses the species in the status line, e.g. "Kiwi the Parakeet". */
    @Override
    public String getKind() { return species; }

    /** Squawks when hungry, sings when happy, otherwise makes a normal sound. */
    @Override
    public String speak() {
        if (getHunger() > 70) return getName() + " squawks at the empty feeder: SQUAWK!";
        if (getEnergy() >= 20 && getHappiness() > 80) return getName() + " sings a happy little song: Tweet tweet tweee!";
        return super.speak();
    }

    /** Message shown when the bird eats a favorite food. */
    @Override
    protected String favoriteFoodMessage(String food) {
        return getName() + " pecks up every last bit of the " + food + " and chirps happily!";
    }

    /** Message shown when the bird plays. */
    @Override
    protected String playMessage() {
        return getName() + " flutters around the room and lands on your shoulder! Tweet!";
    }

    /** Message shown when the bird gets a bath. */
    @Override
    protected String bathMessage() {
        return getName() + " splashes in the birdbath and fluffs up their feathers!";
    }

    /** Returns the bird's species. */
    public String getSpecies() { return species; }
}
