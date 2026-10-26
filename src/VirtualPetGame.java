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
 */
public class VirtualPetGame {
    private final Scanner scanner = new Scanner(System.in);
    private Pet pet;

    /** Starts the game. Run this to play. Pass --plain if colors don't show up in your console. */
    public static void main(String[] args) {
        if (List.of(args).contains("--plain")) {
            PixelArt.setUseColor(false);
        }
        new VirtualPetGame().run();
    }

    /**
     * Main game loop. Each care command moves time forward one step (pet.tick())
     * and shows warnings for low stats. status, help, foods, and typos don't use up time.
     */
    public void run() {
        System.out.println("=== Welcome to the Virtual Pet Adoption Center! ===");
        pet = adoptPet();
        System.out.println("\nSay hello to " + pet.getName() + "! " + pet.speak());
        printHelp();

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
                        System.out.println("Feed what? Try: feed " + pet.getFavoriteFoods().getFirst());
                        continue;
                    }
                    System.out.println(pet.feed(parts[1]));
                }
                case "play" -> System.out.println(pet.play());
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

        if (!pet.isAlive()) {
            System.out.println("\nOh no... " + pet.getName() + " wasn't taken care of and has passed away. Game over.");
        }
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
                  feed <food>  - feed your pet (try one of their favorite foods!)
                  play         - play with your pet
                  sleep        - put your pet to bed
                  bathe        - give your pet a bath
                  speak        - hear what your pet has to say
                  foods        - list your pet's favorite foods
                  status       - check your pet's stats
                  help         - show this list
                  quit         - leave the game""");
    }
}
