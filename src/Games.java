import java.util.Random;
import java.util.Scanner;

/**
 * Mini-games to play with your pet. Each game has 3 rounds. Winning rounds makes
 * the pet happier, raises its affection for you (see Pet.rewardForGame), and earns
 * coins to spend at the shop. Every game tires the pet out a little.
 *
 * Change log:
 *   2026-10-22  Created as an empty placeholder.
 *   2026-10-26  Added class comment.
 *   2026-10-26  Added the Higher or Lower and Left or Right mini-games.
 *   2026-10-26  Games are now 3 rounds and pay coins based on how many rounds you win.
 */
public class Games {
    private static final int ROUNDS = 3;
    private static final int COINS_FOR_PLAYING = 2;
    private static final int COINS_PER_WIN = 5;
    private static final int PERFECT_GAME_BONUS = 5;
    private static final String[] RANKS = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
    private static final String[] SUITS = {"HEARTS", "DIAMONDS", "CLUBS", "SPADES"};
    private static final int RIGHT_SIDE_INDENT = 24;
    private static final Random RANDOM = new Random();

    private final Scanner scanner;
    private final Inventory inventory;

    /** Creates the mini-games, reading answers from the scanner and paying coins into the inventory. */
    public Games(Scanner scanner, Inventory inventory) {
        this.scanner = scanner;
        this.inventory = inventory;
    }

    /**
     * Shows the game menu and plays the game the player picks.
     * Returns the reward message, or null if the player typed "back" without playing.
     */
    public String chooseAndPlay(Pet pet) {
        if (pet.isTooTired()) return pet.getName() + " is too tired to play games.";

        System.out.println("""

                Mini-games:
                  1) Higher or Lower - guess if the next card is higher or lower
                  2) Left or Right   - guess which way your pet will turn""");
        while (true) {
            System.out.print("Pick a game (1-2, or 'back'): ");
            String choice = scanner.nextLine().trim().toLowerCase();
            switch (choice) {
                case "1", "higher or lower", "higher", "cards" -> {
                    return playHigherOrLower(pet);
                }
                case "2", "left or right", "left", "right" -> {
                    return playLeftOrRight(pet);
                }
                case "back" -> {
                    return null;
                }
                default -> System.out.println("Please type 1 or 2.");
            }
        }
    }

    /**
     * Higher or Lower: a card is shown and the player guesses whether the next card
     * will be higher or lower. Aces are low and Kings are high. A tie scores nothing.
     */
    public String playHigherOrLower(Pet pet) {
        System.out.println("\n=== Higher or Lower ===");
        System.out.println("Guess if the next card will be higher or lower. Aces are low, Kings are high.");

        int wins = 0;
        int currentRank = RANDOM.nextInt(RANKS.length);
        System.out.print(drawCard(currentRank, RANDOM.nextInt(SUITS.length)));

        for (int round = 1; round <= ROUNDS; round++) {
            System.out.println("\nRound " + round + " of " + ROUNDS + ". The card is " + RANKS[currentRank] + ".");
            boolean guessHigher = askOneOf("Higher or lower? (h/l) ", 'h', 'l') == 'h';

            int nextRank = RANDOM.nextInt(RANKS.length);
            System.out.println("The next card is...");
            System.out.print(drawCard(nextRank, RANDOM.nextInt(SUITS.length)));

            if (nextRank == currentRank) {
                System.out.println("Same card! Nobody wins this round.");
            } else if ((nextRank > currentRank) == guessHigher) {
                wins++;
                System.out.println("Correct! " + pet.getName() + " cheers: " + pet.randomSound());
            } else {
                System.out.println("Wrong! Better luck next round.");
            }
            currentRank = nextRank;
        }
        return finishGame(pet, wins);
    }

    /**
     * Left or Right: the classic V-Pet game. The pet turns left or right at random
     * and the player guesses which way before it turns.
     */
    public String playLeftOrRight(Pet pet) {
        System.out.println("\n=== Left or Right ===");
        System.out.println(pet.getName() + " is going to turn left or right. Guess which way!");

        int wins = 0;
        for (int round = 1; round <= ROUNDS; round++) {
            System.out.println("\nRound " + round + " of " + ROUNDS + ". " + pet.getName() + " is looking around...");
            boolean guessLeft = askOneOf("Which way will " + pet.getName() + " turn? (l/r) ", 'l', 'r') == 'l';

            boolean turnedLeft = RANDOM.nextBoolean();
            showTurn(pet, turnedLeft);

            if (guessLeft == turnedLeft) {
                wins++;
                System.out.println("You guessed it! " + pet.getName() + " is delighted: " + pet.randomSound());
            } else {
                System.out.println("Not this time!");
            }
        }
        return finishGame(pet, wins);
    }

    /**
     * Draws the pet on the left or right side of the screen, facing the way it turned.
     * Sprites face right (or forward), so turning left mirrors the picture.
     */
    private void showTurn(Pet pet, boolean turnedLeft) {
        if (turnedLeft) {
            System.out.print(PixelArt.render(PixelArt.mirror(pet.getSprite())));
            System.out.println("<<< " + pet.getName() + " turned LEFT!");
        } else {
            System.out.print(PixelArt.render(pet.getSprite(), RIGHT_SIDE_INDENT));
            System.out.println(" ".repeat(RIGHT_SIDE_INDENT) + pet.getName() + " turned RIGHT! >>>");
        }
    }

    /**
     * Prints the final score, pays the coins, and gives the pet its reward.
     * Coins: 2 for playing, 5 per round won, and a 5 bonus for winning every round
     * (so 2, 7, 12, or 22 coins for 0-3 wins).
     */
    private String finishGame(Pet pet, int wins) {
        boolean perfect = wins == ROUNDS;
        int coins = COINS_FOR_PLAYING + wins * COINS_PER_WIN + (perfect ? PERFECT_GAME_BONUS : 0);
        inventory.addCoins(coins);

        System.out.println("\nYou won " + wins + " of " + ROUNDS + " rounds!" + (perfect ? " A perfect game!" : ""));
        System.out.println("You earned " + coins + " coins. You now have " + inventory.getCoins() + ".");
        return pet.rewardForGame(wins, ROUNDS);
    }

    /** Draws a playing card as a small text box with its rank in the corners and its suit in the middle. */
    private String drawCard(int rank, int suit) {
        String r = RANKS[rank];
        return "+----------+\n"
                + String.format("| %-2s       |%n", r)
                + "|          |\n"
                + "|" + center(SUITS[suit], 10) + "|\n"
                + "|          |\n"
                + String.format("|       %2s |%n", r)
                + "+----------+\n";
    }

    /** Pads text with spaces on both sides so it's centered in the given width. */
    private static String center(String text, int width) {
        int left = (width - text.length()) / 2;
        return " ".repeat(left) + text + " ".repeat(width - text.length() - left);
    }

    /**
     * Asks until the player's answer starts with one of the two allowed letters
     * (so "h" and "higher" both work), and returns that letter.
     */
    private char askOneOf(String prompt, char first, char second) {
        while (true) {
            System.out.print(prompt);
            String answer = scanner.nextLine().trim().toLowerCase();
            if (!answer.isEmpty() && (answer.charAt(0) == first || answer.charAt(0) == second)) {
                return answer.charAt(0);
            }
            System.out.println("Please type " + first + " or " + second + ".");
        }
    }
}
