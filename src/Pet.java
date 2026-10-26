import java.util.List;
import java.util.Random;

/**
 * Base class for every virtual pet. Holds the shared stats (hunger, happiness,
 * energy, cleanliness, health) and the care actions that change them.
 * Each animal extends this class and supplies its own sounds, favorite foods,
 * pixel-art sprite, and messages.
 *
 * Change log:
 *   2026-10-22  Created. Moved the shared stats and care actions out of Dog so
 *               every animal can reuse them.
 *   2026-10-26  Added comments to every method.
 *   2026-10-26  Added getSprite() so each pet has a pixel-art picture.
 */
public abstract class Pet {
    protected static final int MAX_STAT = 100;
    protected static final Random RANDOM = new Random();

    private final String name;
    private int age;

    // Tamagotchi-style stats, 0..100
    private int hunger = 0;          // 0 = full, 100 = starving
    private int happiness = 80;
    private int energy = 80;
    private int cleanliness = 80;
    private int health = 100;

    /** Creates a pet with the given name and default starting stats. */
    protected Pet(String name) {
        this.name = name;
    }

    // --- What makes each animal different ---

    /** Returns the sounds this animal can make. */
    public abstract List<String> getSounds();

    /** Returns the foods this animal loves; these fill it up more and make it happier. */
    public abstract List<String> getFavoriteFoods();

    /** Returns the pet's pixel-art picture (see PixelArt for how it's drawn). */
    public abstract String[] getSprite();

    /** Short description used in toString, e.g. "Beagle" or "T-Rex". Defaults to the class name. */
    public String getKind() {
        return getClass().getSimpleName();
    }

    /** Message shown when the pet eats one of its favorite foods. */
    protected String favoriteFoodMessage(String food) {
        return name + " devours the " + food + " happily!";
    }

    /** Message shown when the pet plays. */
    protected String playMessage() {
        return name + " plays happily! " + getSounds().getFirst();
    }

    /** Message shown when the pet gets a bath. */
    protected String bathMessage() {
        return name + " gets a bath.";
    }

    // --- Sounds ---

    /** Returns what the pet says, based on its mood (hungry, tired, or a random sound). */
    public String speak() {
        if (hunger > 70) return name + " is hungry: " + randomSound();
        if (energy < 20) return name + " yawns: *yaaawn*";
        return name + " says: " + randomSound();
    }

    /** Picks one of the animal's sounds at random. */
    protected String randomSound() {
        List<String> sounds = getSounds();
        return sounds.get(RANDOM.nextInt(sounds.size()));
    }

    // --- Food ---

    /** Checks whether the food is one of this pet's favorites (ignores upper/lower case). */
    public boolean isFavoriteFood(String food) {
        return getFavoriteFoods().stream().anyMatch(f -> f.equalsIgnoreCase(food));
    }

    /**
     * Feeds the pet. Favorite foods lower hunger by 40 and add 15 happiness;
     * other foods lower hunger by 25. A full pet refuses to eat.
     */
    public String feed(String food) {
        if (hunger == 0) {
            return name + " sniffs the " + food + " but is too full to eat.";
        }
        if (isFavoriteFood(food)) {
            hunger = clamp(hunger - 40);
            happiness = clamp(happiness + 15);
            return favoriteFoodMessage(food);
        }
        hunger = clamp(hunger - 25);
        return name + " eats the " + food + ".";
    }

    // --- Care actions ---

    /** Plays with the pet: more happiness, but less energy and cleanliness and more hunger. Too-tired pets refuse. */
    public String play() {
        if (energy < 15) return name + " is too tired to play.";
        happiness = clamp(happiness + 20);
        energy = clamp(energy - 15);
        hunger = clamp(hunger + 10);
        cleanliness = clamp(cleanliness - 10);
        return playMessage();
    }

    /** Puts the pet to sleep: refills energy, but it wakes up a bit hungrier. */
    public String sleep() {
        energy = MAX_STAT;
        hunger = clamp(hunger + 15);
        return name + " curls up and sleeps. Zzz...";
    }

    /** Bathes the pet: makes it fully clean, but slightly less happy. */
    public String bathe() {
        cleanliness = MAX_STAT;
        happiness = clamp(happiness - 5);
        return bathMessage();
    }

    /**
     * Makes one step of time pass: hunger goes up, the other stats go down.
     * A very hungry or dirty pet loses health. Call once per game turn.
     */
    public void tick() {
        hunger = clamp(hunger + 5);
        energy = clamp(energy - 3);
        happiness = clamp(happiness - 2);
        cleanliness = clamp(cleanliness - 2);
        if (hunger > 80 || cleanliness < 20) {
            health = clamp(health - 5);
        }
    }

    /** Returns true while the pet's health is above 0. */
    public boolean isAlive() {
        return health > 0;
    }

    /** Keeps a stat between 0 and 100. */
    protected static int clamp(int value) {
        return Math.max(0, Math.min(MAX_STAT, value));
    }

    // --- Getters ---

    public String getName() { return name; }
    public int getAge() { return age; }
    public int getHunger() { return hunger; }
    public int getHappiness() { return happiness; }
    public int getEnergy() { return energy; }
    public int getCleanliness() { return cleanliness; }
    public int getHealth() { return health; }

    /** One-line status readout, e.g. "Rex the Beagle | Hunger: 10 | Happy: 95 | ...". */
    @Override
    public String toString() {
        return String.format("%s the %s | Hunger: %d | Happy: %d | Energy: %d | Clean: %d | Health: %d",
                name, getKind(), hunger, happiness, energy, cleanliness, health);
    }
}
