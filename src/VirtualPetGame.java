import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The game itself: the player visits the adoption center and adopts a pet,
 * then types commands (feed, play, sleep, ...) in a loop until they quit or the pet dies.
 *
 * Change log:
 *   2026-10-22  Created as the game's starting point, replacing IntelliJ's starter Main.java.
 *               Added pet selection, the command loop, and low-stat warnings.
 *   2026-10-26  Added the bird as a pet choice.
 *   2026-10-26  Added comments to every method.
 *   2026-10-26  Replaced the simple pet choice with an adoption center: pick from
 *               pixel-art animals, name your pet, confirm, and get an adoption certificate.
 *               The status command now shows your pet's picture. Added the --plain option.
 *   2026-10-26  When a pet dies, the game shows a tombstone and a memorial of every pet
 *               you've lost, then lets you adopt a new pet and keep playing.
 *   2026-10-26  Added the games command for the Higher or Lower and Left or Right mini-games.
 *   2026-10-26  Added coins and the shop: games pay coins, the shop command spends them on
 *               food, and the bag command shows what you have. Feeding now uses food from
 *               your bag, except Chow, which is always free. Added the greet command, and
 *               pets now greet you based on their affection.
 */
public class VirtualPetGame {
    /** Pixel art: a gray tombstone with a cross, shown when a pet dies. */
    private static final String[] TOMBSTONE = {
            "...AAAAAA...",
            "..AAAAAAAA..",
            ".AAAAKKAAAA.",
            ".AAAKKKKAAA.",
            ".AAAAKKAAAA.",
            ".AAAAKKAAAA.",
            ".AAAAAAAAAA.",
            ".AAAAAAAAAA.",
            "GGAAAAAAAAGG",
            "EGGGGGGGGGGE",
    };

    private static final int STARTING_COINS = 20;
    private static final String FREE_FOOD = "Chow"; // basic food that never runs out

    private final Scanner scanner = new Scanner(System.in);
    private final Inventory inventory = new Inventory(STARTING_COINS);
    private final Games games = new Games(scanner, inventory);
    private final Store store = new Store(scanner, inventory);
    private final List<Pet> memorial = new ArrayList<>();
    private Pet pet;

    /** Starts the game. Run this to play. Pass --plain if colors don't show up in your console. */
    public static void main(String[] args) {
        if (List.of(args).contains("--plain")) {
            PixelArt.setUseColor(false);
        }
        new VirtualPetGame().run();
    }

    /**
     * Runs the whole game: adopt a pet and take care of it. If the pet dies, the
     * player can adopt a new one and keep going, like hatching a new egg on a
     * Digimon V-Pet. The game ends when the player quits or doesn't adopt again.
     */
    public void run() {
        System.out.println("=== Welcome to the Virtual Pet Adoption Center! ===");
        while (true) {
            pet = adoptPet();
            System.out.println("\nSay hello to " + pet.getName() + "! " + pet.greet());
            System.out.println("You have " + inventory.getCoins() + " coins. Win mini-games to earn more, and spend them at the shop.");
            printHelp();

            careForPet();
            if (pet.isAlive()) return; // the player quit

            mournPet();
            String again = ask("Would you like to adopt a new pet? (yes/no) ").toLowerCase();
            if (!again.startsWith("y")) {
                System.out.println("Thanks for playing Virtual Pet. Goodbye!");
                return;
            }
        }
    }

    /**
     * Command loop for the current pet. Each care command moves time forward one step
     * (pet.tick()) and shows warnings for low stats. status, help, foods, and typos
     * don't use up time. Returns when the player quits or the pet dies.
     */
    private void careForPet() {
        while (pet.isAlive()) {
            System.out.print("\n> ");
            if (!scanner.hasNextLine()) break;
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            String[] parts = input.split("\\s+", 2);
            String command = parts[0].toLowerCase();

            switch (command) {
                case "feed" -> {
                    if (parts.length < 2) {
                        System.out.println("Feed what? Type 'bag' to see your food, or 'feed " + FREE_FOOD + "' for free basic food.");
                        continue;
                    }
                    String food = parts[1];
                    // Food comes out of the bag, except free Chow. A full pet doesn't eat, so nothing is used up.
                    if (!food.equalsIgnoreCase(FREE_FOOD) && !pet.isFull() && !inventory.useFood(food)) {
                        System.out.println("You don't have any " + food + ". Buy some at the shop, or feed " + FREE_FOOD + " for free.");
                        continue;
                    }
                    System.out.println(pet.feed(food));
                }
                case "greet", "hello", "hi" -> System.out.println(pet.greet());
                case "shop", "store" -> {
                    store.visit(pet);
                    continue; // shopping doesn't make time pass
                }
                case "bag", "inventory", "coins" -> {
                    System.out.println(inventory);
                    continue;
                }
                case "play" -> System.out.println(pet.play());
                case "games", "game" -> {
                    String result = games.chooseAndPlay(pet);
                    if (result == null) continue; // backed out without playing
                    System.out.println(result);
                }
                case "sleep" -> System.out.println(pet.sleep());
                case "bathe", "bath" -> System.out.println(pet.bathe());
                case "speak", "talk" -> System.out.println(pet.speak());
                case "foods" -> System.out.println(pet.getName() + " loves: " + String.join(", ", pet.getFavoriteFoods()));
                case "status" -> {
                    System.out.print(PixelArt.render(pet.getSprite()));
                    System.out.println(pet);
                    continue; // checking status doesn't make time pass
                }
                case "help" -> {
                    printHelp();
                    continue;
                }
                case "quit", "exit" -> {
                    System.out.println("Bye! " + pet.getName() + " will miss you. " + pet.speak());
                    return;
                }
                default -> {
                    System.out.println("Unknown command. Type 'help' to see what you can do.");
                    continue;
                }
            }

            pet.tick();
            warnIfNeeded();
        }
    }

    /** Shows a tombstone for the pet that died, adds it to the memorial, and lists every pet lost so far. */
    private void mournPet() {
        memorial.add(pet);
        System.out.println("\nOh no... " + pet.getName() + " wasn't taken care of and has passed away.\n");
        System.out.print(PixelArt.render(TOMBSTONE));
        System.out.println("\nIn loving memory:");
        for (Pet lost : memorial) {
            System.out.println("  - " + lost.getName() + " the " + lost.getKind() + " (lived " + lost.getAge() + " turns)");
        }
        System.out.println();
    }

    /**
     * Adoption process: shows the animals, lets the player pick one and name it,
     * asks them to confirm, then prints an adoption certificate. Saying "no" goes
     * back to the adoption center.
     */
    private Pet adoptPet() {
        while (true) {
            showAdoptionCenter();
            Pet candidate = pickAnimal();
            System.out.println();
            System.out.print(PixelArt.render(candidate.getSprite()));
            String answer = ask("Adopt " + candidate.getName() + " the " + candidate.getKind() + "? (yes/no) ").toLowerCase();
            if (answer.startsWith("y")) {
                printCertificate(candidate);
                return candidate;
            }
            System.out.println("No problem! Let's take another look.");
        }
    }

    /** Draws all the animals available for adoption, two per row, numbered 1-4. */
    private void showAdoptionCenter() {
        System.out.println("\nThese animals are looking for a home:\n");
        System.out.println(PixelArt.renderSideBySide(List.of(Dog.SPRITE, Cat.SPRITE), List.of("1) Dog", "2) Cat")));
        System.out.println(PixelArt.renderSideBySide(List.of(Dinosaur.SPRITE, Bird.SPRITE), List.of("3) Dinosaur", "4) Bird")));
    }

    /** Asks which animal to adopt (by number or name) and creates it with a name and breed/species. */
    private Pet pickAnimal() {
        while (true) {
            String choice = ask("Which pet would you like to adopt? (1-4 or its name) ").toLowerCase();
            switch (choice) {
                case "1", "dog" -> {
                    return new Dog(ask("Name your dog: "), ask("What breed is it? "));
                }
                case "2", "cat" -> {
                    return new Cat(ask("Name your cat: "), ask("What breed is it? "));
                }
                case "3", "dinosaur", "dino" -> {
                    return new Dinosaur(ask("Name your dinosaur: "), ask("What species is it? "));
                }
                case "4", "bird" -> {
                    return new Bird(ask("Name your bird: "), ask("What species is it? "));
                }
                default -> System.out.println("Please type 1, 2, 3, or 4 (or dog, cat, dinosaur, bird).");
            }
        }
    }

    /** Prints a framed adoption certificate for the newly adopted pet. */
    private void printCertificate(Pet adopted) {
        String petLine = adopted.getName() + " the " + adopted.getKind();
        List<String> lines = List.of(
                "CERTIFICATE OF ADOPTION",
                "",
                petLine,
                "is now officially part of your family!",
                "",
                "Take good care of them!"
        );
        int width = lines.stream().mapToInt(String::length).max().orElse(0) + 4;
        String border = "+" + "-".repeat(width) + "+";

        System.out.println("\n" + border);
        for (String line : lines) {
            int left = (width - line.length()) / 2;
            int right = width - line.length() - left;
            System.out.println("|" + " ".repeat(left) + line + " ".repeat(right) + "|");
        }
        System.out.println(border);
    }

    /** Shows a question and keeps asking until the player types a non-empty answer. */
    private String ask(String prompt) {
        while (true) {
            System.out.print(prompt);
            String answer = scanner.nextLine().trim();
            if (!answer.isEmpty()) return answer;
        }
    }

    /** Prints a "(!)" warning for each stat that is getting dangerously low. */
    private void warnIfNeeded() {
        if (pet.getHunger() > 70) System.out.println("(!) " + pet.getName() + " is very hungry.");
        if (pet.getEnergy() < 20) System.out.println("(!) " + pet.getName() + " is exhausted.");
        if (pet.getCleanliness() < 30) System.out.println("(!) " + pet.getName() + " needs a bath.");
        if (pet.getHappiness() < 30) System.out.println("(!) " + pet.getName() + " is feeling sad.");
        if (pet.getHealth() < 50) System.out.println("(!) " + pet.getName() + "'s health is low!");
    }

    /** Prints the list of commands the player can type. */
    private void printHelp() {
        System.out.println("""

                Commands:
                  feed <food>  - feed your pet food from your bag ('feed chow' is always free)
                  greet        - say hi (the more affection, the warmer the greeting)
                  play         - play with your pet
                  games        - play a mini-game (raises happiness and affection, earns coins)
                  shop         - spend coins on your pet's favorite foods
                  bag          - see your coins and food
                  sleep        - put your pet to bed
                  bathe        - give your pet a bath
                  speak        - hear what your pet has to say
                  foods        - list your pet's favorite foods
                  status       - check your pet's stats
                  help         - show this list
                  quit         - leave the game""");
    }
}
