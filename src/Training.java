import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Trick training. Every pet can learn Fetch, Stay, and Speak. Each training session
 * may or may not work. A happy pet with lots of affection learns faster. After
 * Pet.SESSIONS_TO_LEARN successful sessions the pet knows the trick and can show it off.
 *
 * Change log:
 *   2026-10-22  Created as an empty placeholder.
 *   2026-10-26  Added class comment.
 *   2026-10-26  Added training for the Fetch, Stay, and Speak tricks, and showing them off.
 */
public class Training {
    /** The tricks every pet can learn. */
    public static final List<String> TRICKS = List.of("Fetch", "Stay", "Speak");
    private static final Random RANDOM = new Random();

    private final Scanner scanner;

    /** Creates the trainer, reading the player's choices from the scanner. */
    public Training(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Shows the tricks and the pet's progress, then runs one training session for the
     * trick the player picks. Returns the result message, or null if the player typed
     * "back" without training.
     */
    public String train(Pet pet) {
        if (pet.isTooTired()) return pet.getName() + " is too tired to train. Let them sleep first.";

        System.out.println("\n=== Trick Training ===");
        System.out.println(describeTricks(pet));
        System.out.println("Chance a session works right now: " + successChance(pet) + "% (happier pets with more affection learn faster)");

        while (true) {
            System.out.print("Which trick do you want to practice? (1-" + TRICKS.size() + ", or 'back') ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("back")) return null;

            String trick = findTrick(input);
            if (trick == null) {
                System.out.println("Please type a trick's number or name, or 'back'.");
            } else if (pet.knowsTrick(trick)) {
                System.out.println(pet.getName() + " already knows " + trick + "! Show it off with 'trick " + trick.toLowerCase() + "'.");
            } else {
                return runSession(pet, trick);
            }
        }
    }

    /** Runs one training session: rolls for success, records it on the pet, and describes what happened. */
    private String runSession(Pet pet, String trick) {
        boolean success = RANDOM.nextInt(100) < successChance(pet);
        boolean justLearned = pet.practiceTrick(trick, success);

        String result = success
                ? "Good job! " + successMessage(pet, trick)
                : "Not quite... " + failMessage(pet, trick);
        result += " (" + trick + ": " + pet.getTrickProgress(trick) + "/" + Pet.SESSIONS_TO_LEARN + ")";
        if (justLearned) {
            result += "\n*** " + pet.getName() + " learned " + trick + "! *** (+5 happiness, +5 affection)"
                    + "\nShow it off any time with 'trick " + trick.toLowerCase() + "'.";
        }
        return result;
    }

    /**
     * Has the pet perform a trick it already knows, which makes it happier and raises affection.
     * Prints what happens. Returns false (and nothing changes) if there's no such trick
     * or the pet hasn't learned it yet.
     */
    public boolean perform(Pet pet, String input) {
        String trick = findTrick(input);
        if (trick == null) {
            System.out.println("There's no trick called \"" + input + "\". Tricks: " + String.join(", ", TRICKS) + ".");
            return false;
        }
        if (!pet.knowsTrick(trick)) {
            System.out.println(pet.getName() + " doesn't know " + trick + " yet. Type 'train' to practice it.");
            return false;
        }
        pet.showOffTrick();
        System.out.println(pet.getName() + " shows off " + trick + ": " + successMessage(pet, trick) + " (+5 happiness, +2 affection)");
        return true;
    }

    /** Lists every trick with a progress bar, e.g. "1) Fetch   [##-] 2/3". */
    public String describeTricks(Pet pet) {
        StringBuilder out = new StringBuilder(pet.getName() + "'s tricks:\n");
        for (int i = 0; i < TRICKS.size(); i++) {
            String trick = TRICKS.get(i);
            int progress = pet.getTrickProgress(trick);
            String bar = "#".repeat(progress) + "-".repeat(Pet.SESSIONS_TO_LEARN - progress);
            out.append(String.format("  %d) %-6s [%s] %d/%d%s%n", i + 1, trick, bar, progress,
                    Pet.SESSIONS_TO_LEARN, pet.knowsTrick(trick) ? "  learned!" : ""));
        }
        return out.toString().stripTrailing();
    }

    /**
     * Chance (in percent) that a training session works: 40%, plus half the pet's
     * affection, plus 10% if it's happy (over 70). Never more than 90%.
     */
    private int successChance(Pet pet) {
        int chance = 40 + pet.getAffection() / 2 + (pet.getHappiness() > 70 ? 10 : 0);
        return Math.min(90, chance);
    }

    /** Matches what the player typed (a number or a name, any case) to a trick, or returns null. */
    private static String findTrick(String input) {
        for (int i = 0; i < TRICKS.size(); i++) {
            if (input.equals(String.valueOf(i + 1)) || input.equalsIgnoreCase(TRICKS.get(i))) {
                return TRICKS.get(i);
            }
        }
        return null;
    }

    /** What the pet does when a trick goes right. */
    private static String successMessage(Pet pet, String trick) {
        return switch (trick) {
            case "Fetch" -> pet.getName() + " brings the ball right back to you!";
            case "Stay" -> pet.getName() + " stays perfectly still until you say \"okay!\"";
            case "Speak" -> pet.getName() + " speaks on command: " + pet.randomSound();
            default -> pet.getName() + " does the trick!";
        };
    }

    /** What the pet does when a trick goes wrong. */
    private static String failMessage(Pet pet, String trick) {
        return switch (trick) {
            case "Fetch" -> pet.getName() + " chases the ball... then lies down and chews on it.";
            case "Stay" -> pet.getName() + " gets up and wanders right over to you.";
            case "Speak" -> pet.getName() + " just tilts their head and stares at you.";
            default -> pet.getName() + " doesn't get it yet.";
        };
    }
}
