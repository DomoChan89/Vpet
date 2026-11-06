# Virtual Pet: Flowcharts

These flowcharts show how the Virtual Pet game works. They match the pseudocode in [Pseudocode.md](Pseudocode.md).

The charts are written in [Mermaid](https://mermaid.js.org/), which GitHub and IntelliJ's Markdown preview draw as pictures. To save one as an image, paste its code into <https://mermaid.live> and export it as PNG or SVG.

Shapes: rounded box = start/end, rectangle = a step, diamond = a decision, slanted box = input/output.

**Change log**

| Date       | Changes |
|------------|---------|
| 2026-11-06 | Created flowcharts for the whole game. |

---

## 1. Overall game flow (`VirtualPetGame.run`)

```mermaid
flowchart TD
    A([Start]) --> B{Started with<br/>the plain option?}
    B -- Yes --> C[Turn off colors]
    B -- No --> D
    C --> D[/Show welcome message/]
    D --> E[Adopt a pet<br/>see chart 2]
    E --> F[/Pet greets you, show coins and commands/]
    F --> G[Care for pet<br/>command loop, see chart 3]
    G --> H{Is the pet alive?}
    H -- "Yes (player quit)" --> Z([End])
    H -- No --> I[/Show tombstone and memorial/]
    I --> J[/"Adopt a new pet? (yes/no)"/]
    J --> K{Answer starts with y?}
    K -- Yes --> E
    K -- No --> L[/Thanks for playing/]
    L --> Z
```

## 2. Adopting a pet (`adoptPet`, `pickAnimal`)

```mermaid
flowchart TD
    A([Start adoption]) --> B[/Show Dog, Cat, Dinosaur, Bird pixel art/]
    B --> C[/Which pet? 1-4 or name/]
    C --> D{Valid choice?}
    D -- No --> E[/Please type 1, 2, 3, or 4/]
    E --> C
    D -- Yes --> F[/Ask name and breed or species/]
    F --> G[Create the pet]
    G --> H[/Show its pixel art<br/>Adopt this pet?/]
    H --> I{Yes?}
    I -- No --> B
    I -- Yes --> J[/Print adoption certificate/]
    J --> K([Return the new pet])
```

## 3. Command loop (`careForPet`)

```mermaid
flowchart TD
    A([Start]) --> B{Is the pet alive?}
    B -- No --> Z([Return: pet died])
    B -- Yes --> C[/Read a command/]
    C --> D{Which command?}

    D -- "status, help, tricks,<br/>shop, bag" --> E[Show info or open shop]
    E --> B
    D -- "unknown or empty" --> F[/Unknown command/]
    F --> B
    D -- quit --> Q[/Goodbye message/]
    Q --> Y([Return: player quit])

    D -- "feed" --> G{Have that food<br/>or is it Chow<br/>or is pet full?}
    G -- No --> H[/You don't have any/]
    H --> B
    G -- Yes --> I[Feed the pet<br/>see chart 5]

    D -- "games / train / trick" --> J{Did the player<br/>back out or fail?}
    J -- Yes --> B
    J -- No --> K[Play game, train,<br/>or show off trick]

    D -- "play, sleep, bathe,<br/>speak, greet, foods" --> L[Do the action]

    I --> T[One turn passes<br/>see chart 4]
    K --> T
    L --> T
    T --> U{Still alive?}
    U -- Yes --> W[/Show low-stat warnings/]
    W --> B
    U -- No --> B
```

## 4. One turn passing (`Pet.tick`, `Hunger.tick`, `isAlive`)

```mermaid
flowchart TD
    A([Tick]) --> B[Age + 1]
    B --> C[Hunger meter - 5]
    C --> D{Hunger meter = 0?}
    D -- Yes --> E[Empty-turn counter + 1]
    D -- No --> F
    E --> F[Energy - 3<br/>Happiness - 2<br/>Cleanliness - 2]
    F --> G{Cleanliness < 20?}
    G -- Yes --> H[Health - 5]
    G -- No --> I
    H --> I{"Health > 0 AND<br/>empty turns < 10?"}
    I -- Yes --> J([Pet is alive])
    I -- No --> K([Pet has died])
```

## 5. Feeding (`Pet.feed`)

```mermaid
flowchart TD
    A([Feed food]) --> B{Hunger meter full?}
    B -- Yes --> C([Too full to eat])
    B -- No --> D{What kind of food?}
    D -- "Snack<br/>(Cookie, Crackers)" --> E[Hunger + 10<br/>Happiness + 3<br/>Affection + 3]
    D -- "Candy<br/>(Candy, Lollipop)" --> F[Hunger + 5<br/>Happiness + 5<br/>Affection + 5]
    D -- Meal --> G{Favorite food?}
    G -- Yes --> H[Hunger = 100<br/>Happiness + 15]
    G -- No --> I[Hunger + 30]
    E --> J[Reset starvation countdown]
    F --> J
    H --> J
    I --> J
    J --> K([Show eating message])
```

## 6. Mini-games (`Games`)

```mermaid
flowchart TD
    A([games]) --> B{Energy < 15?}
    B -- Yes --> C([Too tired to play])
    B -- No --> D[/Pick a game: 1, 2, or back/]
    D --> E{Choice?}
    E -- back --> F([Return, no time passes])
    E -- other --> D
    E -- "1 Higher or Lower" --> G[wins = 0<br/>Draw a card]
    E -- "2 Left or Right" --> P[wins = 0]

    G --> H[/Guess higher or lower/]
    H --> I[Draw next card]
    I --> J{Same rank?}
    J -- Yes --> K[No winner]
    J -- No --> L{Guess correct?}
    L -- Yes --> M[wins + 1]
    L -- No --> N[Wrong]
    K --> O{3 rounds done?}
    M --> O
    N --> O
    O -- No --> H
    O -- Yes --> X

    P --> Q[/Guess left or right/]
    Q --> R[Pet turns at random]
    R --> S{Guess matches?}
    S -- Yes --> T[wins + 1]
    S -- No --> U[Not this time]
    T --> V{3 rounds done?}
    U --> V
    V -- No --> Q
    V -- Yes --> X

    X[Coins += 2 + 5 x wins<br/>+5 bonus if 3 wins] --> Y[Happiness += 5 + 5 x wins<br/>Affection += 1 + 3 x wins, +5 if 3 wins<br/>Energy - 10, Hunger - 5]
    Y --> Z([Return reward message])
```

## 7. The shop (`Store.visit`)

```mermaid
flowchart TD
    A([shop]) --> B[Stock = pet's favorite meals<br/>+ snacks and candy]
    B --> C[/Show items, prices, and coins/]
    C --> D[/Buy which item?/]
    D --> E{back?}
    E -- Yes --> F([Thanks for shopping])
    E -- No --> G{Valid item number?}
    G -- No --> H[/Please type a number/]
    H --> C
    G -- Yes --> I{Enough coins?}
    I -- Yes --> J[Coins - price<br/>Add food to bag]
    J --> K[/You bought it!/]
    I -- No --> L[/Not enough coins/]
    K --> C
    L --> C
```

## 8. Trick training (`Training.train`, `Pet.practiceTrick`)

```mermaid
flowchart TD
    A([train]) --> B{Energy < 15?}
    B -- Yes --> C([Too tired to train])
    B -- No --> D[/Show tricks, progress,<br/>and success chance/]
    D --> E[/Which trick?/]
    E --> F{back?}
    F -- Yes --> G([Return, no time passes])
    F -- No --> H{Real trick?}
    H -- No --> E
    H -- Yes --> I{Already learned?}
    I -- Yes --> E
    I -- No --> J["chance = 40 + affection / 2<br/>+10 if happiness > 70<br/>max 90"]
    J --> K[Energy - 8<br/>Hunger - 10]
    K --> L{Random 0-99 < chance?}
    L -- No --> M([Not quite... show progress])
    L -- Yes --> N[Progress + 1]
    N --> O{Progress = 3?}
    O -- No --> P([Good job! show progress])
    O -- Yes --> Q[Happiness + 5<br/>Affection + 5]
    Q --> R([Pet learned the trick!])
```
