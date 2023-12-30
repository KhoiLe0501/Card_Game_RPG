
# Card Battle Game - Readme
**Overview**

This Java program is a simple card battle game where the player engages in one-on-one combat against a computer-controlled enemy. The game features a deck-building mechanic, turn-based battles, and a reward system for the player to enhance their deck.

**How to Play**

Character Selection:

The player starts by selecting the role of their character. Currently, the available roles are "warrior" and "mage."

Deck Building:

The player is rewarded with new cards after each battle.
The player can choose cards to add to their deck from a reward pool.

Battle:

The combat is turn-based, with the player and enemy taking alternating turns.
Each turn, the player draws cards from their deck and uses them to attack the enemy.
The enemy has a basic AI to select cards for its attacks.

Game Progression:

The game aims to provide a sense of progression through battles and rewards.
The player can defeat monsters and receive new cards to strengthen their deck.

Conditions and Items:

The player and enemy can be affected by conditions such as bleeding, burning, poison, etc.
The player can use items from their inventory to gain advantages or recover from conditions.

End of Battle:

After the battle, the player is informed whether they won or lost.
Winning rewards the player with new cards.

**File Structure**

Main.java: The main class containing the game logic and user interface.
Character.java: The class defining the player and enemy characters, including their attributes, inventory, and methods for combat.
Card.java: The base class for cards, with subclasses like WarriorCard and MageCard for different character roles.
Item.java: The class representing items that the player can use for various effects.
Condition.java: The class defining conditions that can affect characters during battles.

**Future Improvements**

Storyline: Enhance the game with a proper storyline and character progression.
Randomized Monsters: Implement randomly generated monsters with different attributes and titles.
Events: Introduce random events during battles or exploration.
Infinite Mode: Create a mode where monsters scale up in difficulty with each battle.
Special Characters: Add powerful boss characters with unique abilities.
GUI: Implement a graphical user interface for a more immersive experience.

**Execution**

The game can be executed by running the Main class.
Ensure that Java is installed on your system.

**Contributions**

Contributions to enhance the game, add new features, or improve existing code are welcome. Please create a fork of the repository, make your changes, and submit a pull request.

Enjoy playing the card battle game!
