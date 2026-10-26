# Virtual Pet

A text-based virtual pet game in Java, inspired by the Tamagotchi. Adopt a dog, cat, dinosaur, or bird and keep it fed, happy, rested, and clean. If you neglect it, its health drops, and when health reaches 0 the game is over.

## Running the game

Requires **JDK 25**.

**In IntelliJ IDEA:** open `src/VirtualPetGame.java` and click the green run arrow next to `main`. Play by typing in the Run window.

**From the command line:**

```sh
javac -d out src/*.java
java -cp out VirtualPetGame
```

The animals are drawn as colored pixel art. If your console shows odd symbols instead of colors, run with `--plain` to draw them with `#` characters instead:

```sh
java -cp out VirtualPetGame --plain
```

## How to play

### Adopting a pet

The game starts at the adoption center, which shows pixel-art pictures of every animal looking for a home. Pick one by number (1-4) or by name, give it a name and a breed (or a species for a dinosaur or bird), and confirm. Say "yes" and you get an adoption certificate. Say "no" and you go back to the adoption center.

### Taking care of your pet

Once you've adopted, type commands:

| Command       | What it does                                                         |
|---------------|----------------------------------------------------------------------|
| `feed <food>` | Feed your pet, e.g. `feed tuna`. Favorite foods fill it up more and make it happier. |
| `play`        | Play together. Makes it happier, but more tired, hungry, and dirty.  |
| `sleep`       | Restores energy.                                                     |
| `bathe`       | Makes your pet clean (most pets aren't fans).                        |
| `speak`       | Hear your pet. What it says depends on its mood.                     |
| `foods`       | List your pet's favorite foods.                                      |
| `status`      | Show your pet's picture and stats.                                   |
| `help`        | Show the command list.                                               |
| `quit`        | Leave the game.                                                      |

Each action moves time forward one step: hunger rises and energy, happiness, and cleanliness fall. The game prints a warning when a stat gets low. `status`, `help`, and `foods` don't use up time.

## The pets

| Pet      | Favorite foods                                    | Sounds                         |
|----------|---------------------------------------------------|--------------------------------|
| Dog      | Bacon, Chicken, Peanut Butter, Steak, Kibble, Bone | Woof!, Arf!, Awoooo!, ...      |
| Cat      | Tuna, Salmon, Chicken, Milk, Shrimp, Catnip       | Meow!, Purrr..., Hiss!, ...    |
| Dinosaur | Steak, Ferns, Ribs, Leaves, Fish, Giant Egg       | ROOOAR!, Rawr!, *stomp stomp*, ... |
| Bird     | Seeds, Millet, Apple, Berries, Mealworms, Sunflower Seeds | Tweet!, Squawk!, *whistle*, ... |

## Project structure

| File                  | Purpose                                                                 |
|-----------------------|-------------------------------------------------------------------------|
| `VirtualPetGame.java` | Entry point and game loop: adoption center, commands, warnings.         |
| `Pet.java`            | Abstract base class with the shared stats (hunger, happiness, energy, cleanliness, health) and care actions. |
| `Dog.java`, `Cat.java`, `Dinosaur.java`, `Bird.java` | Each animal's sounds, favorite foods, and its own reactions. |
| `PixelArt.java`       | Draws the pixel-art animal pictures in the console.                     |

### Adding a new pet

Extend `Pet` and provide sounds, favorite foods, and a pixel-art sprite (rows of color letters; see the color list in `PixelArt.java`). You can optionally override `speak()`, `playMessage()`, `bathMessage()`, or `favoriteFoodMessage()` to give it its own personality.

```java
public class Bunny extends Pet {
    public Bunny(String name) { super(name); }
    @Override public List<String> getSounds() { return List.of("*thump*", "*squeak*"); }
    @Override public List<String> getFavoriteFoods() { return List.of("Carrot", "Lettuce"); }
    @Override public String[] getSprite() { return new String[] { ".W..W.", ".W..W.", "WWWWWW", "WKWWKW", "WWPPWW", ".WWWW." }; }
}
```

Then add it to `showAdoptionCenter()` and `pickAnimal()` in `VirtualPetGame.java`.

## Planned

`Food`, `Store`, `Games`, `Training`, and `Hunger` are placeholders for upcoming features, such as a store where you buy food with coins earned from mini-games, and tricks your pet can learn.

## Change log

| Date       | Changes |
|------------|---------|
| 2026-10-22 | Created the `Pet` base class and the `Dog`, `Cat`, and `Dinosaur` pets. Added the `VirtualPetGame` game loop (replacing IntelliJ's starter `Main.java`) and this README. |
| 2026-10-26 | Added the `Bird` pet and comments on every class and method, including a change log in each file. |
| 2026-10-26 | Added the adoption center: pixel-art animals to choose from, naming, a confirmation step, and an adoption certificate. `status` now shows your pet's picture. Added the `--plain` option. |
