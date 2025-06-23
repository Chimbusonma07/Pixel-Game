# 2D Adventure Game

A top-down 2D Java adventure game where players explore a tile-based world, interact with NPCs, battle monsters, collect items, and manage their character's stats and inventory.

## 🎮 Features

* 🧍‍♂️ Player movement with arrow keys
* 🧠 NPC dialogue interactions
* 🧟 Monster AI and combat system
* 💬 Dialogue and pause menus
* 💼 Inventory system with item descriptions
* ❤️ Dynamic health bar with full/half/empty hearts
* 🔥 Fireball projectile attack (press 'S')
* ⏸️ Game states: Title, Play, Pause, Dialogue, Character, Game Over

## 🛠️ Built With

* Java (Core language)
* Java Swing/AWT (Graphics & UI)
* Custom object-oriented architecture
* Threading for real-time game loop

## 🧪 How to Run

1. Clone or download the repository.
2. Open the project in your preferred Java IDE (e.g., IntelliJ, Eclipse).
3. Run `MainGame.java`.

## 🎥 Gameplay Preview
[Here is the gameplay preview](https://drive.google.com/file/d/1mLOP0K-b97thCAAhES0pzcJB3sQwvwu7/view?usp=sharing)

## ⌨️ Controls

| Key        | Action                |
| ---------- | --------------------- |
| Arrow Keys | Move                  |
| Enter      | Interact with NPC / Use weapon    |
| Space      | Pause / Unpause       |
| Shift      | Open character screen (character attributes like character's level, inventory) |
| S          | Shoot fireball        |

## 📁 Project Structure

```
main/
├── MainGame.java         # Launches the game
├── GamePanel.java        # Core game loop and state management
├── KeyHandler.java       # Handles user input
├── UserInterface.java    # All UI rendering (menus, dialogue, stats)
├── UtilityTools.java     # Helper functions (e.g. image scaling)
├── EventRectangle.java   # Event triggers (for maps/NPCs)
├── entity/               # Player, NPC, Monster, and Entity classes
├── tile/                 # TileManager and map rendering
├── obj/                  # Game objects (keys, chests, etc.)
```

## 🙌 Acknowledgements

* Font: Retro Gaming by [Dafont](https://www.dafont.com/retro-gaming.font)
* Sprites and tiles made by yours truly.

## 📬 Contact

Built with ❤️ by \[Chimbusonma Ike-Nwodo].
Feel free to reach out via [LinkedIn](https://www.linkedin.com/in/chimbusonma-ike-nwodo/).
