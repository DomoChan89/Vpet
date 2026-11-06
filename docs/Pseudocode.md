# Virtual Pet: Pseudocode

This is the logic of the Virtual Pet game written in plain-English pseudocode. Each section names the Java class and method it describes, so you can read the pseudocode next to the real code. The matching flowcharts are in [Flowcharts.md](Flowcharts.md).

**Change log**

| Date       | Changes |
|------------|---------|
| 2026-11-06 | Created pseudocode for the whole game. |

---

## 1. Starting the game (`VirtualPetGame.main`, `run`)

```
PROGRAM VirtualPetGame
    IF the player started the game with "--plain" THEN
        turn colors off for the pixel art
    END IF

    SET coins TO 20
    SET bag TO empty
    SET memorial TO empty list

    DISPLAY "Welcome to the Virtual Pet Adoption Center!"
    LOOP forever
        SET pet TO AdoptPet()
        DISPLAY pet's greeting
        DISPLAY coins
        DISPLAY the command list

        CareForPet()

        IF pet is still alive THEN      // the player typed quit
            END PROGRAM
        END IF

        MournPet()
        ASK "Would you like to adopt a new pet? (yes/no)"
        IF the answer does not start with "y" THEN
            DISPLAY "Thanks for playing Virtual Pet. Goodbye!"
            END PROGRAM
        END IF
    END LOOP
END PROGRAM
```

## 2. Adopting a pet (`adoptPet`, `pickAnimal`, `printCertificate`)

```
FUNCTION AdoptPet
    LOOP forever
        DISPLAY pixel art of Dog, Cat, Dinosaur, and Bird, numbered 1-4
        SET candidate TO PickAnimal()
        DISPLAY candidate's pixel art
        ASK "Adopt <name> the <breed>? (yes/no)"
        IF the answer starts with "y" THEN
            PrintCertificate(candidate)
            RETURN candidate
        END IF
        DISPLAY "No problem! Let's take another look."
    END LOOP
END FUNCTION

FUNCTION PickAnimal
    LOOP forever
        ASK "Which pet would you like to adopt?"
        IF choice is "1" or "dog"            THEN ASK name and breed,   RETURN new Dog
        ELSE IF choice is "2" or "cat"       THEN ASK name and breed,   RETURN new Cat
        ELSE IF choice is "3" or "dinosaur"  THEN ASK name and species, RETURN new Dinosaur
        ELSE IF choice is "4" or "bird"      THEN ASK name and species, RETURN new Bird
        ELSE DISPLAY "Please type 1, 2, 3, or 4"
        END IF
    END LOOP
END FUNCTION

FUNCTION PrintCertificate(pet)
    SET width TO length of the longest line + 4
    DISPLAY a border of width dashes
    FOR EACH line IN certificate text
        DISPLAY the line centered between "|" characters
    END FOR
    DISPLAY a border of width dashes
END FUNCTION
```

A new pet starts with: hunger meter 80, happiness 80, energy 80, cleanliness 80, health 100, affection 0, age 0.

## 3. The command loop (`careForPet`)

Commands that care for the pet use up one turn (time passes). Commands that only look at things (`status`, `help`, `tricks`, `shop`, `bag`) and typos do not.

```
PROCEDURE CareForPet
    WHILE pet is alive
        DISPLAY "> "
        READ input
        IF input is empty THEN CONTINUE
        SPLIT input INTO command and argument

        SELECT command
            CASE "feed":
                IF there is no argument THEN
                    DISPLAY "Feed what?"
                    CONTINUE                              // no time passes
                END IF
                IF food is not "Chow" AND pet is not full AND bag has none of food THEN
                    DISPLAY "You don't have any <food>"
                    CONTINUE
                END IF
                remove one food from the bag (unless it is Chow)
                DISPLAY pet.Feed(food)
            CASE "greet":   DISPLAY pet.Greet()
            CASE "play":    DISPLAY pet.Play()
            CASE "games":
                SET result TO Games.ChooseAndPlay(pet)
                IF result is nothing (player typed back) THEN CONTINUE
                DISPLAY result
            CASE "train":
                SET result TO Training.Train(pet)
                IF result is nothing THEN CONTINUE
                DISPLAY result
            CASE "trick":
                IF Training.Perform(pet, argument) failed THEN CONTINUE
            CASE "sleep":   DISPLAY pet.Sleep()
            CASE "bathe":   DISPLAY pet.Bathe()
            CASE "speak":   DISPLAY pet.Speak()
            CASE "foods":   DISPLAY pet's favorite foods
            CASE "tricks":  DISPLAY trick progress bars;          CONTINUE
            CASE "shop":    Store.Visit(pet);                     CONTINUE
            CASE "bag":     DISPLAY coins and food;               CONTINUE
            CASE "status":  DISPLAY pet's pixel art and stats;    CONTINUE
            CASE "help":    DISPLAY command list;                 CONTINUE
            CASE "quit":    DISPLAY goodbye;                      RETURN
            OTHERWISE:      DISPLAY "Unknown command";            CONTINUE
        END SELECT

        pet.Tick()                                        // one turn passes
        IF pet is alive THEN WarnIfNeeded()
    END WHILE
END PROCEDURE
```

## 4. Time passing and death (`Pet.tick`, `Hunger.tick`, `isAlive`)

```
PROCEDURE Pet.Tick
    ADD 1 TO age
    Hunger.Tick()
    SUBTRACT 3 FROM energy
    SUBTRACT 2 FROM happiness
    SUBTRACT 2 FROM cleanliness
    IF cleanliness < 20 THEN
        SUBTRACT 5 FROM health
    END IF
    keep every stat between 0 and 100
END PROCEDURE

PROCEDURE Hunger.Tick
    SUBTRACT 5 FROM hunger level (not below 0)
    IF hunger level is 0 THEN
        ADD 1 TO turnsEmpty
    END IF
END PROCEDURE

FUNCTION Pet.IsAlive
    RETURN health > 0 AND turnsEmpty < 10
END FUNCTION

PROCEDURE WarnIfNeeded
    IF hunger level is 0 THEN
        DISPLAY "hunger meter is EMPTY! Feed within (10 - turnsEmpty) turns"
    ELSE IF hunger level < 30 THEN
        DISPLAY "very hungry"
    END IF
    IF energy < 20      THEN DISPLAY "exhausted"
    IF cleanliness < 30 THEN DISPLAY "needs a bath"
    IF happiness < 30   THEN DISPLAY "feeling sad"
    IF health < 50      THEN DISPLAY "health is low"
END PROCEDURE

PROCEDURE MournPet
    ADD pet TO memorial
    DISPLAY how the pet died (starved, or got sick from being dirty)
    DISPLAY tombstone pixel art
    FOR EACH lost pet IN memorial
        DISPLAY its name, kind, and how many turns it lived
    END FOR
END PROCEDURE
```

## 5. Care actions (`Pet`)

```
FUNCTION Pet.Feed(food)
    IF hunger level is 100 THEN
        RETURN "too full to eat"
    END IF
    IF food is a SNACK (Cookie, Crackers) THEN
        ADD 10 TO hunger, ADD 3 TO happiness, ADD 3 TO affection
    ELSE IF food is CANDY (Candy, Lollipop) THEN
        ADD 5 TO hunger, ADD 5 TO happiness, ADD 5 TO affection
    ELSE IF food is one of the pet's favorite foods THEN
        SET hunger TO 100, ADD 15 TO happiness
    ELSE                                   // any other meal, like Chow
        ADD 30 TO hunger
    END IF
    IF hunger level > 0 THEN SET turnsEmpty TO 0     // stops the starvation countdown
    RETURN the pet's eating message
END FUNCTION

FUNCTION Pet.Play
    IF energy < 15 THEN RETURN "too tired to play"
    ADD 20 TO happiness
    SUBTRACT 15 FROM energy
    SUBTRACT 10 FROM hunger
    SUBTRACT 10 FROM cleanliness
    RETURN the pet's play message
END FUNCTION

FUNCTION Pet.Sleep
    SET energy TO 100
    SUBTRACT 15 FROM hunger
    RETURN "sleeps. Zzz..."
END FUNCTION

FUNCTION Pet.Bathe
    SET cleanliness TO 100
    SUBTRACT 5 FROM happiness
    RETURN the pet's bath message
END FUNCTION

FUNCTION Pet.Speak
    IF hunger level < 30 THEN RETURN hungry message
    IF energy < 20 THEN RETURN "yawns"
    (Dog, Cat, Dinosaur, Bird) IF energy >= 20 AND happiness > 80 THEN RETURN happy message
    RETURN a random sound
END FUNCTION

FUNCTION Pet.Greet
    IF affection < 20      THEN RETURN Shy greeting
    ELSE IF affection < 50 THEN RETURN Friendly greeting
    ELSE IF affection < 80 THEN RETURN Close greeting
    ELSE                        RETURN Best Friends greeting
END FUNCTION
```

## 6. Mini-games (`Games`)

```
FUNCTION ChooseAndPlay(pet)
    IF energy < 15 THEN RETURN "too tired to play games"
    DISPLAY game menu
    LOOP forever
        ASK "Pick a game (1-2, or 'back')"
        IF choice is "1" THEN RETURN PlayHigherOrLower(pet)
        IF choice is "2" THEN RETURN PlayLeftOrRight(pet)
        IF choice is "back" THEN RETURN nothing
        DISPLAY "Please type 1 or 2."
    END LOOP
END FUNCTION

FUNCTION PlayHigherOrLower(pet)
    SET wins TO 0
    SET current TO a random card (Ace = 1 ... King = 13)
    DISPLAY current
    FOR round FROM 1 TO 3
        ASK "Higher or lower? (h/l)"
        SET next TO a random card
        DISPLAY next
        IF next = current THEN
            DISPLAY "Same card! Nobody wins this round."
        ELSE IF (next > current AND guess is higher) OR (next < current AND guess is lower) THEN
            ADD 1 TO wins
            DISPLAY "Correct!"
        ELSE
            DISPLAY "Wrong!"
        END IF
        SET current TO next
    END FOR
    RETURN FinishGame(pet, wins)
END FUNCTION

FUNCTION PlayLeftOrRight(pet)
    SET wins TO 0
    FOR round FROM 1 TO 3
        ASK "Which way will the pet turn? (l/r)"
        SET turnedLeft TO a random true or false
        IF turnedLeft THEN draw the pet mirrored on the left
        ELSE draw the pet on the right
        IF guess matches the turn THEN
            ADD 1 TO wins
            DISPLAY "You guessed it!"
        ELSE
            DISPLAY "Not this time!"
        END IF
    END FOR
    RETURN FinishGame(pet, wins)
END FUNCTION

FUNCTION FinishGame(pet, wins)
    SET coinsEarned TO 2 + 5 * wins
    IF wins = 3 THEN ADD 5 TO coinsEarned            // perfect game bonus
    ADD coinsEarned TO coins
    DISPLAY wins and coinsEarned

    // Pet.rewardForGame
    ADD 5 + 5 * wins TO happiness
    SET affectionGain TO 1 + 3 * wins
    IF wins = 3 THEN ADD 5 TO affectionGain
    ADD affectionGain TO affection
    SUBTRACT 10 FROM energy
    SUBTRACT 5 FROM hunger
    RETURN "had fun playing with you!"
END FUNCTION
```

## 7. The shop (`Store.visit`)

```
PROCEDURE Store.Visit(pet)
    SET stock TO the pet's 6 favorite foods (meals) + Cookie, Crackers, Candy, Lollipop
    LOOP forever
        DISPLAY each item with its number, price, and how many are in the bag
        DISPLAY coins
        ASK "Buy which item? (number, or 'back')"
        IF choice is "back" THEN
            DISPLAY "Thanks for shopping!"
            RETURN
        END IF
        IF choice is not a number from 1 to the size of stock THEN
            DISPLAY "Please type a number"
            CONTINUE
        END IF
        SET item TO stock[choice]
        IF coins >= item's price THEN
            SUBTRACT item's price FROM coins
            ADD item TO bag
            DISPLAY "You bought <item>!"
        ELSE
            DISPLAY "Not enough coins"
        END IF
    END LOOP
END PROCEDURE
```

## 8. Trick training (`Training`, `Pet.practiceTrick`)

```
FUNCTION Training.Train(pet)
    IF energy < 15 THEN RETURN "too tired to train"
    DISPLAY each trick (Fetch, Stay, Speak) with a progress bar
    DISPLAY SuccessChance(pet)
    LOOP forever
        ASK "Which trick do you want to practice?"
        IF answer is "back" THEN RETURN nothing
        IF answer is not a trick THEN
            DISPLAY "Please type a trick's number or name"
        ELSE IF pet already knows the trick THEN
            DISPLAY "already knows it"
        ELSE
            RETURN RunSession(pet, trick)
        END IF
    END LOOP
END FUNCTION

FUNCTION SuccessChance(pet)
    SET chance TO 40 + affection / 2
    IF happiness > 70 THEN ADD 10 TO chance
    RETURN the smaller of chance and 90
END FUNCTION

FUNCTION RunSession(pet, trick)
    SET success TO (random number 0-99) < SuccessChance(pet)
    SUBTRACT 8 FROM energy
    SUBTRACT 10 FROM hunger
    IF success THEN
        ADD 1 TO progress for trick
        IF progress = 3 THEN                          // just learned it
            ADD 5 TO happiness, ADD 5 TO affection
            DISPLAY "<pet> learned <trick>!"
        END IF
    END IF
    RETURN success or fail message with progress (for example "2/3")
END FUNCTION

FUNCTION Training.Perform(pet, trick)
    IF trick does not exist OR pet does not know it THEN
        DISPLAY why not
        RETURN false                                  // no time passes
    END IF
    ADD 5 TO happiness, ADD 2 TO affection, SUBTRACT 3 FROM energy
    DISPLAY the trick
    RETURN true
END FUNCTION
```
