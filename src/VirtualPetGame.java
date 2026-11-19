/**
* CSC 1061 1H2
* Instructor: Jim Eberle
* Homework #: Final Programming Project
* Date: 11/19/2026
* Class: VirtualPetGame.java
* @author Dominique Wigginton
* Description:
*   The game itself: the player visits the adoption center and adopts a pet,
*   then types commands (feed, play, sleep, ...) in a loop until they quit or the pet dies.
*
* Inputs:  Commands typed by the player at the keyboard; optional --plain argument.
* Outputs: The game printed to the console: pets, stats, warnings, shop, games, history, memorial.
*
* Change log:
*   Date        Description
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
*   2026-10-26  Added trick training: the train, tricks, and trick commands.
*   2026-10-26  Warnings now count down the turns left when the hunger meter is empty,
*               and the death message says how the pet died (starved or got sick).
*   2026-11-19  The memorial is now my own MyLinkedList<Pet> instead of an ArrayList, and
*               it is listed by how long each pet lived using recursive merge sort.
*               Every command that uses a turn is saved in an ActivityHistory (a
*               MyLinkedList plus a MyQueue of the last 5). Added the history,
*               history <word>, history sort, recent, and memorial commands.
*   2026-11-19  Added the CSC 1061 file header (course, instructor, date, author,
*               description, inputs, outputs) to the top of the file.
*   2026-11-19  Added readLine(): when input runs out during a question, the game now ends
*               cleanly instead of crashing. ask(), the shop, games, and training use it.
*/

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

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
    private final Training training = new Training(scanner);
    private final MyLinkedList<Pet> memorial = new MyLinkedList<>();
    private final ActivityHistory history = new ActivityHistory();
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
                case "train" -> {
                    String result = training.train(pet);
                    if (result == null) continue; // backed out without training
                    System.out.println(result);
                }
                case "tricks" -> {
                    System.out.println(training.describeTricks(pet));
                    continue;
                }
                case "trick" -> {
                    if (parts.length < 2) {
                        System.out.println("Which trick? e.g. 'trick fetch'. Type 'tricks' to see what " + pet.getName() + " knows.");
                        continue;
                    }
                    if (!training.perform(pet, parts[1])) continue; // unknown trick: no time passes
                }
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
                case "history" -> {
                    showHistory(parts.length < 2 ? "" : parts[1].trim());
                    continue; // looking at the history doesn't make time pass
                }
                case "recent" -> {
                    ActivityHistory.display("Last " + ActivityHistory.RECENT_LIMIT + " actions (oldest first)", history.getRecent());
                    continue;
                }
                case "memorial" -> {
                    showMemorial();
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
            history.record(pet, input);
            if (pet.isAlive()) warnIfNeeded();
        }
    }

    /** Shows a tombstone for the pet that died, adds it to the memorial, and lists every pet lost so far. */
    private void mournPet() {
        memorial.add(pet);
        System.out.println("\nOh no... " + pet.getName() + " " + pet.getCauseOfDeath() + " and has passed away.\n");
        System.out.print(PixelArt.render(TOMBSTONE));
        showMemorial();
        System.out.println();
    }

    /**
     * Lists every pet that has died, longest-lived first. Uses recursive merge sort
     * on the memorial linked list (the memorial itself stays in the order pets died).
     */
    private void showMemorial() {
        if (memorial.isEmpty()) {
            System.out.println("No pets have passed away. Keep up the good work!");
            return;
        }
        MyLinkedList<Pet> byAge = memorial.mergeSort(Comparator.comparingInt(Pet::getAge).reversed());
        System.out.println("\nIn loving memory (longest-lived first):");
        for (Pet lost : byAge) {
            System.out.println("  - " + lost.getName() + " the " + lost.getKind() + " (lived " + lost.getAge() + " turns)");
        }
    }

    /**
     * Shows the activity history.
     *   history         - every action in the order it happened
     *   history sort    - grouped by action (insertion sort)
     *   history <word>  - only actions containing the word (linear search)
     * Also shows how many actions the current pet has done (recursive count).
     */
    private void showHistory(String option) {
        if (history.isEmpty()) {
            System.out.println("No activities have been recorded yet.");
            return;
        }
        if (option.isEmpty()) {
            ActivityHistory.display("Activity history (" + history.size() + " actions)", history.getAll());
        } else if (option.equalsIgnoreCase("sort")) {
            ActivityHistory.display("Activity history grouped by action", history.sortedByAction());
        } else {
            MyLinkedList<Activity> matches = history.search(option);
            if (matches.isEmpty()) {
                System.out.println("No activities match \"" + option + "\".");
                return;
            }
            ActivityHistory.display(matches.size() + " action(s) matching \"" + option + "\"", matches);
        }
        System.out.println(pet.getName() + " has done " + history.countFor(pet.getName()) + " action(s) with you.");
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

    /**
     * Reads the player's next line. If the input has run out (for example, input was
     * piped in from a file and it ended), the game says goodbye and ends cleanly
     * instead of crashing with NoSuchElementException. Every prompt in the game uses this.
     */
    public static String readLine(Scanner input) {
        if (!input.hasNextLine()) {
            System.out.println("\nNo more input. Thanks for playing Virtual Pet. Goodbye!");
            System.exit(0);
        }
        return input.nextLine();
    }

    /** Shows a question and keeps asking until the player types a non-empty answer. */
    private String ask(String prompt) {
        while (true) {
            System.out.print(prompt);
            String answer = readLine(scanner).trim();
            if (!answer.isEmpty()) return answer;
        }
    }

    /** Prints a "(!)" warning for each stat that is getting dangerously low. */
    private void warnIfNeeded() {
        Hunger hunger = pet.getHungerMeter();
        if (hunger.isEmpty()) {
            int turnsLeft = hunger.turnsUntilStarved();
            System.out.println("(!) " + pet.getName() + "'s hunger meter is EMPTY! Feed them within "
                    + turnsLeft + (turnsLeft == 1 ? " turn" : " turns") + " or they will starve!");
        } else if (hunger.isHungry()) {
            System.out.println("(!) " + pet.getName() + " is very hungry.");
        }
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
                                 favorite foods fill them up; snacks and candy raise affection
                  greet        - say hi (the more affection, the warmer the greeting)
                  play         - play with your pet
                  games        - play a mini-game (raises happiness and affection, earns coins)
                  train        - practice a trick: Fetch, Stay, or Speak
                  tricks       - see which tricks your pet knows
                  trick <name> - have your pet show off a trick it knows, e.g. trick fetch
                  shop         - spend coins on your pet's favorite foods
                  bag          - see your coins and food
                  sleep        - put your pet to bed
                  bathe        - give your pet a bath
                  speak        - hear what your pet has to say
                  foods        - list your pet's favorite foods
                  status       - check your pet's stats
                  history      - see everything you've done ('history sort' groups it,
                                 'history <word>' searches it, e.g. history bacon)
                  recent       - see your last 5 actions
                  memorial     - see the pets that have passed away
                  help         - show this list
                  quit         - leave the game""");
    }
}
