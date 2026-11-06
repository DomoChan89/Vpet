import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 *   2026-10-26  Added trick training: practiceTrick(), knowsTrick(), and showOffTrick().
 *   2026-10-26  Hunger is now a Hunger meter (100 = full, 0 = empty) instead of a number
 *               that counted up. Favorite foods fill it completely; snacks and candy fill
 *               it a little but raise affection. Training drains it more. A pet whose meter
 *               stays empty for too long starves, and an empty meter no longer costs health.
 *               Added isHungry() and getCauseOfDeath().
 *   2026-11-06  playMessage() uses getSounds().get(0) instead of getFirst(), which only
 *               exists in JDK 21+, so the game compiles on JDK 18.
 */
public abstract class Pet {
    protected static final int MAX_STAT = 100;
    protected static final Random RANDOM = new Random();
    private static final String[] AFFECTION_LEVELS = {"Shy", "Friendly", "Close", "Best Friends"};
    /** How many successful training sessions it takes to learn a trick. */
    public static final int SESSIONS_TO_LEARN = 3;

    private final String name;
    private int age;                 // number of turns the pet has lived

    // Tamagotchi-style stats, 0..100
    private final Hunger hunger = new Hunger(); // 100 = full, 0 = empty
    private int happiness = 80;
    private int energy = 80;
    private int cleanliness = 80;
    private int health = 100;
    private int affection = 0;       // how attached the pet is to you; earned by playing mini-games
    private final Map<String, Integer> trickProgress = new HashMap<>(); // trick -> successful sessions so far

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
        return name + " plays happily! " + getSounds().get(0);
    }

    /** Message shown when the pet gets a bath. */
    protected String bathMessage() {
        return name + " gets a bath.";
    }

    // --- Sounds ---

    /** Returns what the pet says, based on its mood (hungry, tired, or a random sound). */
    public String speak() {
        if (isHungry()) return name + " is hungry: " + randomSound();
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

    /** Returns true when the hunger meter is full and the pet won't eat. */
    public boolean isFull() {
        return hunger.isFull();
    }

    /** Returns true when the hunger meter is low and the pet wants food. */
    public boolean isHungry() {
        return hunger.isHungry();
    }

    /**
     * Feeds the pet. What happens depends on the food (a full pet refuses to eat):
     * - Snack: fills the hunger meter by 10, +3 happiness, +3 affection.
     * - Candy: fills the hunger meter by 5, +5 happiness, +5 affection.
     * - Favorite food: fills the hunger meter completely, +15 happiness.
     * - Any other food: fills the hunger meter by 30.
     * Only snacks and candy raise affection.
     */
    public String feed(String food) {
        if (isFull()) {
            return name + " sniffs the " + food + " but is too full to eat.";
        }
        switch (Food.typeOf(food)) {
            case SNACK -> {
                hunger.fill(10);
                happiness = clamp(happiness + 3);
                affection = clamp(affection + 3);
                return name + " happily munches the " + food + " and nuzzles your hand. (+3 affection)";
            }
            case CANDY -> {
                hunger.fill(5);
                happiness = clamp(happiness + 5);
                affection = clamp(affection + 5);
                return name + " gobbles up the " + food + " and looks at you adoringly! (+5 affection)";
            }
            default -> {
                if (isFavoriteFood(food)) {
                    hunger.fillCompletely();
                    happiness = clamp(happiness + 15);
                    return favoriteFoodMessage(food) + " " + name + " is completely full!";
                }
                hunger.fill(30);
                return name + " eats the " + food + ".";
            }
        }
    }

    // --- Care actions ---

    /** Plays with the pet: more happiness, but less energy and cleanliness, and the hunger meter drains 10. Too-tired pets refuse. */
    public String play() {
        if (isTooTired()) return name + " is too tired to play.";
        happiness = clamp(happiness + 20);
        energy = clamp(energy - 15);
        hunger.drain(10);
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
     * Games also use 10 energy and drain the hunger meter by 5.
     */
    public String rewardForGame(int wins, int rounds) {
        int happinessGain = 5 + wins * 5;
        int affectionGain = 1 + wins * 3 + (wins == rounds ? 5 : 0);
        happiness = clamp(happiness + happinessGain);
        affection = clamp(affection + affectionGain);
        energy = clamp(energy - 10);
        hunger.drain(5);
        return name + " had fun playing with you! (+" + happinessGain + " happiness, +" + affectionGain + " affection)";
    }

    // --- Tricks ---

    /** Returns how many successful training sessions the pet has had for a trick (0 to SESSIONS_TO_LEARN). */
    public int getTrickProgress(String trick) {
        return trickProgress.getOrDefault(trick, 0);
    }

    /** Returns true once the pet has fully learned a trick. */
    public boolean knowsTrick(String trick) {
        return getTrickProgress(trick) >= SESSIONS_TO_LEARN;
    }

    /**
     * Records one training session. Training uses 8 energy and drains the hunger meter
     * by 10 whether or not it works. A successful session counts toward learning the trick,
     * and learning it earns 5 happiness and 5 affection. Returns true if the pet just learned it.
     */
    public boolean practiceTrick(String trick, boolean success) {
        energy = clamp(energy - 8);
        hunger.drain(10);
        if (!success || knowsTrick(trick)) return false;

        trickProgress.merge(trick, 1, Integer::sum);
        if (knowsTrick(trick)) {
            happiness = clamp(happiness + 5);
            affection = clamp(affection + 5);
            return true;
        }
        return false;
    }

    /** The pet performs a trick it knows: 5 happiness and 2 affection, for 3 energy. */
    public void showOffTrick() {
        happiness = clamp(happiness + 5);
        affection = clamp(affection + 2);
        energy = clamp(energy - 3);
    }

    /** Puts the pet to sleep: refills energy, but it wakes up hungrier (the hunger meter drains 15). */
    public String sleep() {
        energy = MAX_STAT;
        hunger.drain(15);
        return name + " curls up and sleeps. Zzz...";
    }

    /** Bathes the pet: makes it fully clean, but slightly less happy. */
    public String bathe() {
        cleanliness = MAX_STAT;
        happiness = clamp(happiness - 5);
        return bathMessage();
    }

    /**
     * Makes one step of time pass: the hunger meter drains and the other stats go down.
     * A very dirty pet loses health. The pet also gets one turn older.
     * Call once per game turn.
     */
    public void tick() {
        age++;
        hunger.tick();
        energy = clamp(energy - 3);
        happiness = clamp(happiness - 2);
        cleanliness = clamp(cleanliness - 2);
        if (cleanliness < 20) {
            health = clamp(health - 5);
        }
    }

    /** Returns true unless the pet has starved or its health has run out. */
    public boolean isAlive() {
        return health > 0 && !hunger.hasStarved();
    }

    /** Explains how the pet died, for the memorial. */
    public String getCauseOfDeath() {
        if (hunger.hasStarved()) return "starved after going " + Hunger.STARVE_TURNS + " turns without food";
        return "got sick from not being kept clean";
    }

    /** Keeps a stat between 0 and 100. */
    protected static int clamp(int value) {
        return Math.max(0, Math.min(MAX_STAT, value));
    }

    // --- Getters ---

    public String getName() { return name; }
    public int getAge() { return age; }
    /** Returns the hunger meter level: 100 = full, 0 = empty. */
    public int getHunger() { return hunger.getLevel(); }
    /** Returns the hunger meter itself, e.g. to check the starvation countdown. */
    public Hunger getHungerMeter() { return hunger; }
    public int getHappiness() { return happiness; }
    public int getEnergy() { return energy; }
    public int getCleanliness() { return cleanliness; }
    public int getHealth() { return health; }
    public int getAffection() { return affection; }

    /** One-line status readout, e.g. "Rex the Beagle | Hunger: [########--] 80/100 | Happy: 95 | ...". */
    @Override
    public String toString() {
        return String.format("%s the %s | Hunger: %s | Happy: %d | Energy: %d | Clean: %d | Health: %d | Affection: %d (%s)",
                name, getKind(), hunger, happiness, energy, cleanliness, health, affection, getAffectionLevel());
    }
}
