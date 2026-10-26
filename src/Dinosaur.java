import java.util.List;

/**
 * A pet dinosaur. Roars and stomps, eats huge meals, and bathes in the lake.
 *
 * Change log:
 *   2026-10-22  Created as a Pet with sounds, favorite foods, and dinosaur messages.
 *   2026-10-26  Added comments to every method.
 *   2026-10-26  Added pixel-art sprite for the adoption center.
 */
public class Dinosaur extends Pet {
    /** Pixel art: a little green T-Rex with white teeth. */
    public static final String[] SPRITE = {
            "......EGGGGE",
            "......GKGGGG",
            "......GGGGGG",
            "......GGGWWW",
            ".....GGGG...",
            "G...GGGGGGG.",
            "GG.GGGGGG.G.",
            ".GGGGGGGG...",
            "..GGGGGG....",
            "...GG.GG....",
            "...G...G....",
    };

    private static final List<String> SOUNDS = List.of(
            "ROOOAR!", "Rawr!", "*stomp stomp*", "Grrrumble...", "*snort*", "SKREEEE!"
    );

    private static final List<String> FAVORITE_FOODS = List.of(
            "Steak", "Ferns", "Ribs", "Leaves", "Fish", "Giant Egg"
    );

    private final String species;

    /** Creates a dinosaur with a name and species, e.g. new Dinosaur("Chompy", "T-Rex"). */
    public Dinosaur(String name, String species) {
        super(name);
        this.species = species;
    }

    /** Returns the dinosaur's sounds. */
    @Override
    public List<String> getSounds() { return SOUNDS; }

    /** Returns the dinosaur's favorite foods. */
    @Override
    public List<String> getFavoriteFoods() { return FAVORITE_FOODS; }

    /** Returns the dinosaur's pixel art. */
    @Override
    public String[] getSprite() { return SPRITE; }

    /** Uses the species in the status line, e.g. "Chompy the T-Rex". */
    @Override
    public String getKind() { return species; }

    /** Stomach rumbles when hungry, roars when happy, otherwise makes a normal sound. */
    @Override
    public String speak() {
        if (getHunger() > 70) return getName() + "'s stomach rumbles like thunder: GRRRUMBLE!";
        if (getEnergy() >= 20 && getHappiness() > 80) return getName() + " lets out a mighty happy ROOOAR!";
        return super.speak();
    }

    /** Message shown when the dinosaur eats a favorite food. */
    @Override
    protected String favoriteFoodMessage(String food) {
        return getName() + " chomps the " + food + " in one giant bite! ROAR!";
    }

    /** Message shown when the dinosaur plays. */
    @Override
    protected String playMessage() {
        return getName() + " stomps around chasing a giant ball! *stomp stomp*";
    }

    /** Message shown when the dinosaur gets a bath. */
    @Override
    protected String bathMessage() {
        return getName() + " splashes in the lake and makes a huge wave!";
    }

    /** Returns the dinosaur's species. */
    public String getSpecies() { return species; }
}
