/**
* CSC 1061 1H2
* Instructor: Jim Eberle
* Homework #: Final Programming Project
* Date: 11/19/2026
* Class: Dinosaur.java
* @author Dominique Wigginton
* Description:
*   A pet dinosaur. Roars and stomps, eats huge meals, and bathes in the lake.
*
* Inputs:  Name and species from the adoption center; care actions called by the game.
* Outputs: Dinosaur sounds, greetings, messages, and its pixel-art sprite.
*
* Change log:
*   Date        Description
*   2026-10-22  Created as a Pet with sounds, favorite foods, and dinosaur messages.
*   2026-10-26  Added comments to every method.
*   2026-10-26  Added pixel-art sprite for the adoption center.
*   2026-10-26  Added greetings that get warmer as affection grows.
*   2026-10-26  Hungry check now uses isHungry() for the new hunger meter.
*   2026-11-19  Added the CSC 1061 file header (course, instructor, date, author,
*               description, inputs, outputs) to the top of the file.
*/

import java.util.List;

public class Dinosaur extends Pet {
    /** Pixel art: a little green T-Rex with white teeth. */
    public static final String[] SPRITE = {
            "......EGGGGE",
            "......GKGGGG",
            "......GGGGGG",
            "......GGGWWW",
            ".....GGGG...",
            "G...GGGGGGG.",
            "GG.GGGGGG.G.",
            ".GGGGGGGG...",
            "..GGGGGG....",
            "...GG.GG....",
            "...G...G....",
    };

    private static final List<String> SOUNDS = List.of(
            "ROOOAR!", "Rawr!", "*stomp stomp*", "Grrrumble...", "*snort*", "SKREEEE!"
    );

    private static final List<String> FAVORITE_FOODS = List.of(
            "Steak", "Ferns", "Ribs", "Leaves", "Fish", "Giant Egg"
    );

    private final String species;

    /** Creates a dinosaur with a name and species, e.g. new Dinosaur("Chompy", "T-Rex"). */
    public Dinosaur(String name, String species) {
        super(name);
        this.species = species;
    }

    /** Returns the dinosaur's sounds. */
    @Override
    public List<String> getSounds() { return SOUNDS; }

    /** Returns the dinosaur's favorite foods. */
    @Override
    public List<String> getFavoriteFoods() { return FAVORITE_FOODS; }

    /** Returns the dinosaur's pixel art. */
    @Override
    public String[] getSprite() { return SPRITE; }

    /** Dinosaur greetings, from Shy to Best Friends. */
    @Override
    protected String[] getGreetings() {
        return new String[] {
                getName() + " eyes you warily and gives a low growl. Grrr...",
                getName() + " stomps over and gives you a curious sniff.",
                getName() + " nuzzles you with their big snout. Rawr!",
                getName() + " does a happy stomp dance and gives you a gentle dino hug! ROOOAR!",
        };
    }

    /** Uses the species in the status line, e.g. "Chompy the T-Rex". */
    @Override
    public String getKind() { return species; }

    /** Stomach rumbles when hungry, roars when happy, otherwise makes a normal sound. */
    @Override
    public String speak() {
        if (isHungry()) return getName() + "'s stomach rumbles like thunder: GRRRUMBLE!";
        if (getEnergy() >= 20 && getHappiness() > 80) return getName() + " lets out a mighty happy ROOOAR!";
        return super.speak();
    }

    /** Message shown when the dinosaur eats a favorite food. */
    @Override
    protected String favoriteFoodMessage(String food) {
        return getName() + " chomps the " + food + " in one giant bite! ROAR!";
    }

    /** Message shown when the dinosaur plays. */
    @Override
    protected String playMessage() {
        return getName() + " stomps around chasing a giant ball! *stomp stomp*";
    }

    /** Message shown when the dinosaur gets a bath. */
    @Override
    protected String bathMessage() {
        return getName() + " splashes in the lake and makes a huge wave!";
    }

    /** Returns the dinosaur's species. */
    public String getSpecies() { return species; }
}
