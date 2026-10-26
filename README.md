# Virtual Pet

A text-based virtual pet game in Java, inspired by the Tamagotchi. Adopt a dog, cat, dinosaur, or bird and keep it fed, happy, rested, and clean, play mini-games together to build its affection for you, and teach it tricks. If you neglect it, its health drops, and when health reaches 0 it passes away, but you can adopt a new pet and try again.

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
| `feed <food>` | Feed your pet food from your bag, e.g. `feed tuna`. Favorite foods fill it up more and make it happier. `feed chow` is always free. |
| `greet`       | Say hi. The more affection your pet has, the warmer its greeting.    |
| `play`        | Play together. Makes it happier, but more tired, hungry, and dirty.  |
| `games`       | Play a mini-game: Higher or Lower, or Left or Right. Raises happiness and affection, and earns coins. |
| `train`       | Practice a trick: Fetch, Stay, or Speak.                             |
| `tricks`      | See which tricks your pet knows and its training progress.           |
| `trick <name>`| Have your pet show off a trick it knows, e.g. `trick fetch`.          |
| `shop`        | Spend coins on your pet's favorite foods.                            |
| `bag`         | See your coins and food.                                             |
| `sleep`       | Restores energy.                                                     |
| `bathe`       | Makes your pet clean (most pets aren't fans).                        |
| `speak`       | Hear your pet. What it says depends on its mood.                     |
| `foods`       | List your pet's favorite foods.                                      |
| `status`      | Show your pet's picture and stats.                                   |
| `help`        | Show the command list.                                               |
| `quit`        | Leave the game.                                                      |

Each action moves time forward one step: hunger rises and energy, happiness, and cleanliness fall. The game prints a warning when a stat gets low. `status`, `help`, `foods`, `shop`, `bag`, and `tricks` don't use up time.

### Mini-games

Type `games` to pick one. Each game is 3 rounds.

- **Higher or Lower:** a card is shown, and you guess whether the next card will be higher or lower. Aces are low and Kings are high, and a tie scores nothing.
- **Left or Right:** the classic V-Pet game. Your pet is about to turn, and you guess which way. Then it appears on that side of the screen, facing that way.

Every game raises happiness by 5, plus 5 per round won, and affection by 1, plus 3 per round won. Winning all 3 rounds earns a 5-point affection bonus. Games use 10 energy and add 5 hunger, and a pet that is too tired won't play. Type `back` at the game menu to leave without playing.

Games also pay **coins**: 2 for playing, 5 per round won, and a 5-coin bonus for a perfect game (2, 7, 12, or 22 coins for 0 to 3 wins).

### Coins and the shop

You start with 20 coins. Type `shop` to see your pet's favorite foods and their prices (4 to 20 coins), then buy by number. Bought food goes in your bag, and `feed` takes it from there. If you run out, `feed chow` is a free basic meal that never runs out, but it isn't a favorite. Your coins and bag carry over if you adopt a new pet. A full pet won't eat, so you never waste food.

### Affection and greetings

Affection grows as you play mini-games, and it changes how your pet greets you when you adopt it and whenever you type `greet`:

| Affection | Level        | Example (cat)                                              |
|-----------|--------------|------------------------------------------------------------|
| 0-19      | Shy          | Luna watches you from across the room and doesn't come any closer. |
| 20-49     | Friendly     | Luna strolls over and gives you a polite little meow.      |
| 50-79     | Close        | Luna rubs against your legs, purring.                      |
| 80-100    | Best Friends | Luna headbutts you lovingly and curls up in your lap. Purrrr... |

`status` shows your pet's affection and level.

### Training tricks

Every pet can learn **Fetch**, **Stay**, and **Speak**. Type `train`, pick a trick, and your pet practices it once. A session might not work. The chance is 40%, plus half your pet's affection, plus 10% if it's happy (over 70), up to 90%. So a pet that loves you learns faster.

After 3 successful sessions, your pet learns the trick and gets 5 happiness and 5 affection. Then `trick fetch` (or `stay`, `speak`) has it show off for 5 happiness and 2 affection. Each training session uses 8 energy and adds 5 hunger, and a pet that is too tired won't train. `tricks` shows progress bars, e.g. `Fetch [##-] 2/3`.

### If your pet dies

Like hatching a new egg on a Digimon V-Pet, losing a pet isn't the end of the game. You'll see a tombstone and a memorial listing every pet you've lost and how many turns each one lived. Then you can go back to the adoption center and adopt a new pet, or end the game.

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
| `Pet.java`            | Abstract base class with the shared stats (hunger, happiness, energy, cleanliness, health, affection) and care actions. |
| `Dog.java`, `Cat.java`, `Dinosaur.java`, `Bird.java` | Each animal's sounds, favorite foods, and its own reactions. |
| `PixelArt.java`       | Draws the pixel-art animal pictures in the console.                     |
| `Games.java`          | The Higher or Lower and Left or Right mini-games.                       |
| `Store.java`, `Food.java` | The shop and the foods it sells, with prices.                       |
| `Inventory.java`      | Your bag: coins and the food you've bought.                           |
| `Training.java`       | Trick training: Fetch, Stay, and Speak.                                  |

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

`Hunger` is a placeholder for more detailed hunger rules.

## Change log

| Date       | Changes |
|------------|---------|
| 2026-10-22 | Created the `Pet` base class and the `Dog`, `Cat`, and `Dinosaur` pets. Added the `VirtualPetGame` game loop (replacing IntelliJ's starter `Main.java`) and this README. |
| 2026-10-26 | Added the `Bird` pet and comments on every class and method, including a change log in each file. |
| 2026-10-26 | Added the adoption center: pixel-art animals to choose from, naming, a confirmation step, and an adoption certificate. `status` now shows your pet's picture. Added the `--plain` option. |
| 2026-10-26 | When a pet dies, the game shows a tombstone and a memorial of every pet you've lost, then lets you adopt a new one. A pet's age now counts the turns it has lived. |
| 2026-10-26 | Added the Higher or Lower and Left or Right mini-games (`games` command) and the affection stat, which mini-games raise along with happiness. |
| 2026-10-26 | Mini-games are now 3 rounds and pay coins based on how many rounds you win. Added the shop (`shop`), your bag of coins and food (`bag`), and free Chow; feeding now uses food from your bag. Pets now greet you based on affection (`greet`), with four affection levels shown in `status`. |
| 2026-10-26 | Added trick training: every pet can learn Fetch, Stay, and Speak (`train`), check progress (`tricks`), and show off learned tricks (`trick <name>`). |
