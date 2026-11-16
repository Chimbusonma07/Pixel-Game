# 2D Adventure Game

A top-down 2D Java adventure game where players explore a tile-based world, interact with NPCs, battle monsters, collect items, and manage their character's stats and inventory.

## 🗺️ The Quest

1. **The island:** talk to the wizard, find the hidden teleport tile and the key, and reach Level 3 to open the chest. XP is capped here, so you can't go past Level 3 before the chest.
2. **The maze:** the chest pulls you into the middle of a brick maze. *Find the wizard!* His door is hidden in the maze's farthest dead end.
3. **The wizard's house:** walk up to him for the key to the second chest, then leave the way you came in.
4. **The Grand Treasure:** find it in the far corner of the maze, guarded by ogres. Open it at **Level 6** to win.

Dying in the maze or the house respawns you at the maze centre, keeping your level, XP and keys.

## 🎮 Features

* 🧍‍♂️ Player movement with corner assist (slides you into side corridors)
* 🧠 Bump into NPCs to talk to them; Enter steps through the conversation
* 🧟 Monster AI: monsters patrol, spot you by line of sight, chase you along the shortest path and retreat when hit
  * **Beetle:** chases, and runs when badly hurt
  * **Blue:** keeps its distance and spits slime down corridors
  * **Ogre:** guards its post, charges, and walks back if lured too far away
  * Stray monster shots hurt other monsters, weakening them but never killing them
* 🪙 Health Coins: hidden in maze dead ends, each guarded by a monster. They stack in one inventory slot and each restores half a heart (maze only)
* 🔥 Fireball (press 'S'): flies until it hits a wall or a monster
* 💼 Inventory with item descriptions and stack counts
* ❤️ Hearts health display (full/half/empty)
* ⏸️ Game states: Title, Play, Pause, Dialogue, Character, Game Over, Victory

## 🛠️ Built With

* Java (Core language)
* Java Swing/AWT (Graphics & UI)
* Custom object-oriented architecture
* Threading for real-time game loop

## 🧪 How to Run

1. Clone or download the repository.
2. Open the project in your preferred Java IDE (e.g., IntelliJ, Eclipse, VS Code).
3. Run `MainGame.java`, with `res/` on the classpath (the maps, sprites and font live there).

## 🎥 Gameplay Preview
[Here is the gameplay preview](https://drive.google.com/file/d/1mLOP0K-b97thCAAhES0pzcJB3sQwvwu7/view?usp=sharing)

## ⌨️ Controls

| Key        | Action                |
| ---------- | --------------------- |
| Arrow Keys | Move (walk into an NPC to talk) |
| Enter      | Next line of dialogue / Swing sword / Use the selected item in the inventory |
| Space      | Pause / Unpause       |
| Shift      | Open/close the character screen (stats and inventory) |
| S          | Shoot fireball        |

## 📁 Project Structure

```
main/
├── MainGame.java         # Launches the game
├── GamePanel.java        # Core game loop, game states and map switching
├── KeyHandler.java       # Handles user input
├── UserInterface.java    # All UI rendering (menus, dialogue, stats, victory screen)
├── AssetSetter.java      # Places objects, NPCs, monsters and coins on each map
├── EventHandler.java     # Map events (teleports, door hint, wizard's key)
├── PathFinder.java       # Monster pathfinding and line of sight
├── CheckCollision.java   # Tile, object and entity collision
├── entity/               # Player, Wizard, FireBall and the base Entity class
├── Monsters/             # Monster AI base class + Beetle, Blue, Ogre
├── tile/                 # TileManager and map rendering
├── obj/                  # Keys, chests, door, health coin, projectiles
res/Maps/
├── WorldVer2.txt         # Starting island
├── dungeon.txt           # The maze
├── World01.txt           # The wizard's house
```

## 🙌 Acknowledgements

* Font: Retro Gaming by [Dafont](https://www.dafont.com/retro-gaming.font)
* Sprites and tiles made by yours truly.

## 📬 Contact

Built with ❤️ by \[Chimbusonma Ike-Nwodo].
Feel free to reach out via [LinkedIn](https://www.linkedin.com/in/chimbusonma-ike-nwodo/).
