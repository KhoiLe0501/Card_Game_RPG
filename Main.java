import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws IOException {
		Character player;

		System.out.println("Please select the role of your character: ");
		String role = "warrior"; // Example role

		if (role.toLowerCase().trim().contains("warrior")) {
			player = new Warrior();
		} else if (role.toLowerCase().trim().contains("mage")) {
			player = new Mage();
		} else {
			player = new Character("Placeholder");
		}

		File enFile = new File ("Troll");
		Character enemy = new Monster(enFile);
		player.setName("PLAYER");
		enemy.setName("Monster");

		player.inventory.add(player.getItem("Fire Bolt Incantation"));
		battle(player, enemy);

		//test();

		/* Scope of game
		 * Have a proper story
		 * 	Charm, Integrity, Ferocity
		 * Reward players with new card selection after every battle
		 * Have monster randomly generated with buff and title, etc
		 * 	Malicious, Righteous, Unholy, Cursed
		 * Have event 
		 * Inventory that serves as buff
		 * 	Treasure chest; spoiled goods
		 * Infinite Mode
		 * 	Monster scale up with battles fought
		 * Demetrios the Sentinel. Indred, Lord of Blasphemy
		 */
	}

	// combat will always be one-v-one
	static void battle(Character player, Character enemy) {
		int turn = 1;
		Scanner in = new Scanner(System.in);
		System.out.println("--------------------------Battle------------------------------");
		Collections.shuffle(player.deck);
		Collections.shuffle(enemy.deck);
		while (enemy.getHealth() > 0 && player.getHealth() > 0) { // Both are alive
			player.draw(4);
			enemy.draw(4);
			System.out.println("\n*	*	*	Turn " + turn + "		*	*	*");
			System.out.println(player.getName() + " Status	| Health: " + player.getHealth() + "	| Shield: " + player.getShield());
			System.out.println(enemy.getName() + " Status	| Health: " + enemy.getHealth() + "	| Shield: " + enemy.getShield());

			//Enemy AI
			double enMana = 3;
			Card enPlay = enemy.selectCardToPlay(enMana); //Will also remove card in hand
			enMana -= enPlay.getCost();

			//Player Interface
			double mana = 3;
			userLoop:
				while (isPlayable(player, enemy, mana)) {
					// Every input is a loop
					//System.out.println("\nSTART OF USER LOOP")
					System.out.println("\nMana:	" + mana + "\n" + player.getName() + "'s hand:\n" + player.printCardList(player.hand));
					if (player.inventory.size() != 0) {
						System.out.println(player.getName() + "'s inventory:\n" + player.printInventory());
					}
					System.out.print(promptUserInput());
					String input = in.nextLine();

					int inInt = -1;
					boolean isComboInput = false;
					Card iPlay = null;
					input = input.trim();

					// Begin of user input
					while (true) {
						// Do not quit this loop until the user input is valid. Fix the input if must be
						try {
							inInt = Integer.parseInt(input);
							if (inInt == 99) break;
							iPlay = player.hand.get(inInt); // to test for index out of bounds
							if (iPlay.getCost() > mana) {
								System.out.print("\nNot enough mana orbs for this action.\n" + promptUserInput());
								input = in.nextLine();
								if (input.equals("99")) 
									break userLoop;
								continue;
							}
							break;
						}
						catch (NumberFormatException e) {
							//INVENTORY INPUT
							if (input.toLowerCase().equals("inventory") || input.toLowerCase().equals("i")) {
								if (player.inventory.isEmpty()) {
									System.out.print("\nThere is no item in your inventory.\n" + promptUserInput());
									input = in.nextLine();
									if (input.equals("99")) 
										break userLoop;
									continue;
								}
								System.out.println("\n" + player.getName() + "'s inventory: \n" + player.printInventory());
								int input2 = player.getIntegerForItem(0, player.inventory.size(), in);
								if (input2 == 99) {
									in.nextLine();
									continue userLoop; // Jump to start of userLoop
								}
								Item toUse = player.inventory.get(input2);
								player.use(toUse, enemy);
								in.nextLine();
								continue userLoop;
							}
							else {
								isComboInput = true;
							}
							break;
						}
						catch (IndexOutOfBoundsException f) { // Comes to this if inInt > player's hand.size()
							System.out.print("\nChosen card index is not in hand.\n" + promptUserInput());
							input = in.nextLine();
							if (input.equals("99")) 
								break userLoop;
							continue;
						}
					}

					if (isComboInput) {
						List<Card> combo = new ArrayList<>();
						List<Card> handCopied = new ArrayList<>(player.hand);
						String s = "\nThe following input is NOT valid: ";
						boolean toPrint = false;
						int space = input.indexOf(" ");
						input += " ";

						while (space != -1) {
							String toConvert = input.substring(0,space);
							try {
								inInt = Integer.parseInt(toConvert);
								if (inInt == 99) 
									break userLoop;
								iPlay = handCopied.get(inInt);
								if (iPlay.getCost() > mana) { 
									s += toConvert + " ";
									toPrint = true;
									input = input.substring(space+1);
									space = input.indexOf(" ");
									continue;
								}
								mana -= iPlay.getCost();
								combo.add(iPlay);
							}
							catch (NumberFormatException | IndexOutOfBoundsException e) {
								// No need to be specific since cannot ask user to re-enter the input
								s += toConvert + " ";
								toPrint = true;
							}
							input = input.substring(space+1);
							space = input.indexOf(" ");
						}
						for (Card toPlay: combo) 
							player.use(toPlay, enemy);
						if (toPrint) 
							System.out.println(s);
					}
					else { // for non-combo input
						if (inInt == 99) 
							break;
						iPlay = player.hand.get(inInt);
						player.use(iPlay, enemy);
						mana -= iPlay.getCost();
					}

					//System.out.println("\nEND of USER Loop");
				}
			// End of user interface
			if (enemy.getHealth() > 0) 
				// Prevent a dying enemy from activating enPlay
				enemy.use(enPlay, player);

			// Post all card play
			//System.out.println("POST PLAY");
			player.postPlay();
			enemy.postPlay();
			turn++;
		}
		//Post battle
		player.postBattle();
		enemy.postBattle();
		System.out.println("-----------------------------End of Battle---------------------------------");

		if (player.getHealth() > 0) {
			System.out.println(player.getName() + " defeated " + enemy.getName());
			player.rewardFromGallery(4);
		} else {
			System.out.println("Player loses!");
		}
		in.close();
	}

	static void endGame() {}

	static void savedGame() {}

	static void test() throws IOException {
		Character player = new Warrior();
		player.setName("PLAYER");
		Character dummy = new Monster();
		dummy.setName("Dummy");
		Scanner in = new Scanner(System.in);
		boolean skip = false;

		System.out.println("-----****TESTING STATION****-----");
		while (true) {
			System.out.println("Player's Grave Size: " + player.grave.size());
			player.draw(4);
			System.out.println("\n-----NEW TURN-----");
			System.out.println(player.printCardList(player.hand));
			int input = in.nextInt();
			if (input == 99999) 
				break;
			if (input == 99) {
				skip = true;
			}

			if (skip) {
				dummy.postPlay();
				player.postPlay();
				System.out.println("\nDummy Health: " + dummy.getHealth()); 
				skip = false;
				continue;
			}
			Card toPlay2 = player.hand.get(input);;
			player.use(toPlay2, dummy);			
			dummy.playedThisTurn.add(toPlay2);
			dummy.postPlay();
			player.postPlay();
			System.out.println("\nDummy Health: " + dummy.getHealth()); 

		}
		player.postBattle();
		in.close();
	}

	static boolean isPlayable(Character player, Character enemy, double mana) {
		//Test for player.isUsableItem() 
		if (enemy.getHealth() <= 0 || player.hand.isEmpty()) return false;
		if (player.hasActiveItem()) return true;
		for (Card inHand: player.hand) {
			if (inHand.getCost() <= mana) return true;
		}
		return false;
	}

	// WORK IN PROGRESS
	static void battleToCome(Character self, Character enemy) {
		/*
		 * if (input instanceof Integer) {
				int intValue = (int) input;
				// Handle integer input
			} else if (input instanceof String) {
				String stringValue = (String) input;
				// Handle string input
			}		
		 */
	}

	static boolean isOnMenu() {
		return true;
	}

	static String promptUserInput() {
		return "Select a card to play (Type 99 to end turn OR [i] for inventory): ";
	}
}