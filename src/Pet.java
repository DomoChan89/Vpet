import java.util.List;
import java.util.Random;

/**
 * Base class for every virtual pet. Holds the shared stats (hunger, happiness,
 * energy, cleanliness, health, affection) and the care actions that change them.
 * Each animal extends this class and supplies its own sounds, favorite foods,
 * pixel-art sprite, and messages.
 *
 * Change log:
 *   2026-10-22  Created. Moved the shared stats and care actions out of Dog so
 *               every animal can reuse them.
 *   2026-10-26  Added comments to every method.
 *   2026-10-26  Added getSprite() so each pet has a pixel-art picture.
 *   2026-10-26  Age now counts the turns the pet has lived (goes up by 1 each tick).
 *   2026-10-26  Added the affection stat and rewardForGame() for the mini-games.
 *               Added isTooTired() so play() and the mini-games share the same check.
 *   2026-10-26  Added greet(), which depends on affection, and affection levels
 *               (Shy, Friendly, Close, Best Friends) shown in the status line.
 *               Added isFull() so food bought at the shop isn't wasted on a full pet.
 */
public abstract class Pet {
    protected static final int MAX_STAT = 100;
    protected static final Random RANDOM = new Random();
    private static final String[] AFFECTION_LEVELS = {"Shy", "Friendly", "Close", "Best Friends"};

    private final String name;
    private int age;                 // number of turns the pet has lived

    // Tamagotchi-style stats, 0..100
    private int hunger = 0;          // 0 = full, 100 = starving
    private int happiness = 80;
    private int energy = 80;
    private int cleanliness = 80;
    private int health = 100;
    private int affection = 0;       // how attached the pet is to you; earned by playing mini-games

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

    /**
     * Returns four greetings, from least to most attached: Shy, Friendly, Close,
     * and Best Friends. greet() picks one based on the pet's affection.
     */
    protected abstract String[] getGreetings();

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

    /** How the pet greets you. The more affection it has, the warmer the greeting. */
    public String greet() {
        return getGreetings()[affectionTier()];
    }

    /** Returns the affection level name: Shy, Friendly, Close, or Best Friends. */
    public String getAffectionLevel() {
        return AFFECTION_LEVELS[affectionTier()];
    }

    /** Turns affection into a level from 0 to 3: under 20, under 50, under 80, then 80 and up. */
    private int affectionTier() {
        if (affection < 20) return 0;
        if (affection < 50) return 1;
        if (affection < 80) return 2;
        return 3;
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

    /** Returns true when the pet is completely full and won't eat. */
    public boolean isFull() {
        return hunger == 0;
    }

    /**
     * Feeds the pet. Favorite foods lower hunger by 40 and add 15 happiness;
     * other foods lower hunger by 25. A full pet refuses to eat.
     */
    public String feed(String food) {
        if (isFull()) {
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
        if (isTooTired()) return name + " is too tired to play.";
        happiness = clamp(happiness + 20);
        energy = clamp(energy - 15);
        hunger = clamp(hunger + 10);
        cleanliness = clamp(cleanliness - 10);
        return playMessage();
    }

    /** Returns true when the pet has too little energy to play or do mini-games. */
    public boolean isTooTired() {
        return energy < 15;
    }

    /**
     * Applies the result of a mini-game. Happiness goes up 5, plus 5 per round won.
     * Affection goes up 1, plus 3 per round won, plus a 5 bonus for winning every round.
     * Games also use 10 energy and add 5 hunger.
     */
    public String rewardForGame(int wins, int rounds) {
        int happinessGain = 5 + wins * 5;
        int affectionGain = 1 + wins * 3 + (wins == rounds ? 5 : 0);
        happiness = clamp(happiness + happinessGain);
        affection = clamp(affection + affectionGain);
        energy = clamp(energy - 10);
        hunger = clamp(hunger + 5);
        return name + " had fun playing with you! (+" + happinessGain + " happiness, +" + affectionGain + " affection)";
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
     * A very hungry or dirty pet loses health. The pet also gets one turn older.
     * Call once per game turn.
     */
    public void tick() {
        age++;
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
    public int getAffection() { return affection; }

    /** One-line status readout, e.g. "Rex the Beagle | Hunger: 10 | Happy: 95 | ...". */
    @Override
    public String toString() {
        return String.format("%s the %s | Hunger: %d | Happy: %d | Energy: %d | Clean: %d | Health: %d | Affection: %d (%s)",
                name, getKind(), hunger, happiness, energy, cleanliness, health, affection, getAffectionLevel());
    }
}
