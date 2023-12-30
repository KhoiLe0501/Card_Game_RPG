import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.*;


public class Character {
	private String name;
	private String title;
	private String role;
	private double health;
	private double shield;
	// private int level;

	//Damage Modifier
	boolean isReflect = false;

	//	
	List<Card> deck = new ArrayList<>();
	List<Card> hand = new ArrayList<>();
	List<Card> grave = new ArrayList<>();
	List<Card> gallery = new ArrayList<>();

	// Battle Management
	List<Card> playedThisTurn = new ArrayList<>();
	List<Card> playedThisBattle = new ArrayList<>();
	List<Card> addedThisBattle = new ArrayList<>();

	//Condition Damage Management
	List<Condition> conditionsOnSelf = new ArrayList<>();

	//Inventory Management
	File itemList = new File("Items");
	List<Item> inventory = new ArrayList<>();
	List<Item> itemsGallery = new ArrayList<>();
	int maxHealth;

	public Character(String placeholder) {}

	public Character(File archetype) throws IOException {
		this.shield = 0;
		this.makeItemsGallery(itemList);
	}

	double getHealth() {return this.health;}
	double getShield() {return this.shield;}
	String getRole() {return this.role;}
	String getName() {return this.name;}

	void setHealth(double num) {this.health = num;}
	void setShield(double num) {this.shield = num;}
	void setRole(String s) {this.role = s;}
	void setName(String s) {this.name = s;}
	void setTitle(String title) {this.title = title;}

	void titleCheck() { //Mostly for the story
		if (this.title.contains("Dark Traveler")) {

		}
	}

	void makeGallery(File archetype) throws IOException {
		Scanner in = new Scanner(archetype);
		String line;
		while (true) {
			line = in.nextLine().trim();
			if (line.isEmpty()) line = in.nextLine().trim();
			if (line.equals("End of Deck")) break;

			List<String> descriptionList = new ArrayList<>();
			List<String> effectList = new ArrayList<>();
			List<Double> valueList = new ArrayList<>();
			int bracket = line.indexOf("[");
			int bracket2 = line.indexOf("]");
			String name = line.substring(0, bracket-1);
			double cost = Double.parseDouble(line.substring(bracket + 1, bracket2));
			line = in.nextLine().trim();

			while ((bracket = line.indexOf("[")) != -1) {
				bracket2 = line.indexOf("]");
				descriptionList.add(line.substring(0,bracket));
				effectList.add(line.substring(0, line.indexOf(" ")));
				valueList.add(Double.parseDouble(line.substring(bracket + 1, bracket2)));
				line = in.nextLine().trim();
			}
			double[] valueArray = new double[valueList.size()];
			for (int i = 0; i < valueList.size(); i++) 
				valueArray[i] = valueList.get(i);

			if (this.role.equals("warrior")) {
				gallery.add(new WarriorCard(name, descriptionList.toArray(new String[0]), 
						effectList.toArray(new String[0]), valueArray, cost));
			}
			else if (this.role.equals("mage")) {
				gallery.add(new MageCard(name, descriptionList.toArray(new String[0]), 
						effectList.toArray(new String[0]), valueArray, cost));
			}
			else if (this.role.equals("monster")) {
				gallery.add(new Card(name, descriptionList.toArray(new String[0]), 
						effectList.toArray(new String[0]), valueArray, cost));
			}
			// Continue to expand whenever a new class is added
		}
		in.close();
		this.starterDeck();
	}

	// Inventory
	void makeItemsGallery(File itemList) throws IOException {
		Scanner in = new Scanner(itemList);
		String line;
		while (true) {
			line = in.nextLine().trim();
			if (line.isEmpty()) line = in.nextLine().trim();
			if (line.equals("End of list")) break;

			String name = line;
			line = in.nextLine().trim();
			int bracket = line.indexOf("[");
			int bracket2 = line.indexOf("]");
			String description = line.substring(0,bracket);
			String effect = line.substring(0, line.indexOf(" "));
			double value = Double.parseDouble(line.substring(bracket + 1, bracket2));
			line = in.nextLine().trim();
			Item toAdd = new Item(name, description, effect, value);
			itemsGallery.add(toAdd);
		}
		in.close();
	}

	boolean hasItem(String eff) {
		for (Item item: this.inventory) {
			if (item.getEffect().contains(eff))
				return true;
		}
		return false;
	}

	boolean hasActiveItem() {
		for (Item item: this.inventory) {
			if (item.isUsuable()) 
				return true;
		}
		return false;
	}

	Item getItem(String itemName) {
		for (Item item: this.itemsGallery)
			if (item.getName().equals(itemName)) 
				return item;
		return null;
	}

	void checkPassiveItem() {		
		if (this.inventory.size() == 0) return;
		boolean isUsed = false;
		List<Item> toRemove = new ArrayList<>();

		for (Item item: this.inventory) {
			// String name = item.getName();
			String eff = item.getEffect();
			double value = item.getValue();

			switch (eff) {
			case "Resurrect":
				if (this.health <= 0) {
					isUsed = true;
					System.out.println(item.getName2() + " is automatically used. \nRevived " + this.getName() + 
							" with 30% of max health");
					this.health += Math.round(this.maxHealth * value / 100);
					System.out.println(this.getName() + " Status | Health: " + this.getHealth() + " | Shield: " + this.getShield());
				}
				break;
			default:
				break;
			}
			if (isUsed) {
				toRemove.add(item);
				isUsed = false;
			}
		}
		this.inventory.removeAll(toRemove);
	}

	int getIntegerForItem(int min, int max, Scanner sc) { // max is NOT included
		System.out.print("Select an item to use (Type 99 to exit inventory): ");
		int input = min - 1;
		while (true) {
			if (sc.hasNextInt()) {
				input = sc.nextInt();
				if (input == 99) return input;
				else if (input >= min && input < max) {
					Item toUse = this.inventory.get(input);
					if (toUse.isUsuable()) 
						return input;
					else 
						System.out.println("The chosen item cannot be use. Choose a different item: ");
				}
				else 
					System.out.print("Invalid input. Reselect a valid index: ");
			}
			else {
				System.out.print("Invalid input. Reselect a valid index: ");
				sc.next();
			}
		}
	}

	void damageCalculation(double num, Character target) {
		double shield = target.getShield();
		if (num < shield) {
			target.setShield(shield - num);
		}
		else {
			target.setHealth(target.getHealth() + shield - num);
			target.setShield(0);
		}
		System.out.println(target.getName() + " Status | Health: " + target.getHealth() + " | Shield: " + target.getShield());
	}

	String printInventory() {
		String s = "";
		int i = 0;
		for (Item item: this.inventory) {
			s += "[" + i + "]  " + item.getName2() + ":  " + item.getDes() + "\n";
			i++;
		}
		return s;
	}

	// CONDITION DAMAGE
	// Code in first person
	// Imagine accounting for condition on yourself
	public void applyConditionEffects() {
		int defaultValue = 1;
		boolean toRemove = false;
		List<Condition> removeList = new ArrayList<>();

		if (this.conditionsOnSelf.size() != 0)
			System.out.println(this.getName() + " condition check: ");

		for (Condition condition : this.conditionsOnSelf) {
			String name = condition.getName();
			int duration = condition.getDuration();
			int stack = condition.getStack();
			int scale = condition.getScale();

			switch (name) {
			case "bleeding":
				this.health -= stack * scale;
				break;
			case "burning":
				this.health -= stack * scale;
				condition.setStack(stack/2);
				break;
			case "poison":
				this.health -= stack * scale;
				break;
				// Add more condition cases as needed
			case "disease":
				this.health -= stack * scale;
				if ((Math.random() * 100) < 50) 
					condition.increDuration(defaultValue);
				break;
			case "confusion":
				for (int i=0 ; i<this.playedThisTurn.size() ; i++) {
					this.health -= stack * scale * 3;
				}
				break;
			default:
				break;
			}

			condition.decreDuration(defaultValue);
			if (condition.getDuration() <= 0) {
				toRemove = true;
				removeList.add(condition);
			}
			System.out.println("[" + name + "] - Duration: " + duration + " - Stack: " + stack);
		}
		if (this.conditionsOnSelf.size() != 0) {
			System.out.println(this.getName() + " Status | Health: " + this.getHealth());
			System.out.println("------------------------------------------------------------------------------");
		}
		if (toRemove) {
			for (Condition condition: removeList) {
				this.conditionsOnSelf.remove(condition);
				System.out.println(condition.getName() + " is removed from " + this.getName());
			}
		}
	}

	boolean hasCondition(String conditionName) {
		for (Condition condition : this.conditionsOnSelf) {
			if (condition.getName().contains(conditionName)) {
				return true;
			}
		}
		return false;
	}

	Condition getCondition(String conditionName) {
		for (Condition condition: this.conditionsOnSelf) {
			if (condition.getName().contains(conditionName)) {
				return condition;
			}
		}
		return null;
	}
	//END of condition damage

	//REWARD SYSTEM
	void rewardFromGallery(int choice) {
		this.sortList(this.deck);
		Scanner sc = new Scanner (System.in);
		int input = -1;
		List<Card> toReward = new ArrayList<>();

		System.out.println("\"***************************************************************************");
		for (int i=0; i<choice ; i++) {
			int random = (int)(Math.random() * (this.gallery.size()-3)) + 3;
			Card toAdd = this.gallery.get(random);
			if (toReward.contains(toAdd)) {
				// To slightly diminish repeating reward
				// Not to completely prevent its occurance
				random = (int)(Math.random() * (this.gallery.size()-3)) + 3;
				if (toReward.contains(this.gallery.get(random))) 
					// To further prevent repeating
					random = (int)(Math.random() * (this.gallery.size()-3)) + 3;
				toReward.add(this.gallery.get(random));
			}
			else toReward.add(toAdd);
		}
		System.out.print(this.printCardList(toReward) + "\nSelect a card to add to " + this.getName() + " deck  ╰(◕ᗜ◕)╯ ");
		input = this.getIntegerInRange(0, choice, sc);
		Card chosen = toReward.get(input);

		System.out.print(this.getName() + " have selected " + chosen.getName2() + "\n\n"
				+ "0 - [replace a card in deck] \n1 - [add the selected card to deck]" + "\n"
				+ "Select an option to proceed: ");
		input = this.getIntegerInRange(0, 2, sc); // 1 + 1 for max since (input < max)
		if (input == 0) {

			System.out.print(this.getName() + "'s deck:\n" + this.printCardList(this.deck) + "\n"
					+ "Select a card to replace: ");
			input = this.getIntegerInRange(0, this.deck.size(), sc);
			Card toRemove = this.deck.get(input);
			System.out.println(toRemove.getName2() + " has been replaced with " + chosen.getName2());
			this.deck.remove(input);
		}
		else 
			System.out.println(chosen.getName2() + " has been added to your deck  (っ◕‿◕)っ");
		this.deck.add(chosen);
		System.out.println("****************************************************************************");
		sc.close();
	}

	int getIntegerInRange(int min, int max, Scanner sc) { // max is NOT included
		int input = min - 1;
		while (true) {
			if (sc.hasNextInt()) {
				input = sc.nextInt();
				if (input >= min && input < max) return input;
				else System.out.print("Invalid input. Reselect a valid index: ");
			}
			else {
				System.out.print("Invalid input. Reselect a valid index: ");
				sc.next();
			}
		}
	}

	void sortList(List<Card> list) {
		Set<Card> unique = new HashSet<>(list);
		List<Card> organizedList = new ArrayList<>();

		for (Card card : unique) {
			for (Card each : list) {
				if (each.equals(card)) {
					organizedList.add(each);
				}
			}
		}
		this.deck = organizedList;
	}

	public String printCardList(List<Card> list) {
		String s = "";
		int i = 0;
		for (Card c: list) {
			if (i < 10)
				s += "[" + i + "]  " + "(" + c.getCost() + ") " + c.getName2() + "\n"; 
			else 
				s += "[" + i + "] " + "(" + c.getCost() + ") " + c.getName2() + "\n"; 
			i++;
		}
		return s;
	}

	void starterDeck() {
		for (int i=0 ; i<5 ; i++) {
			this.deck.add(this.gallery.get(0));
			this.deck.add(this.gallery.get(1));
			if (i==2 || i==4)
				this.deck.add(this.gallery.get(2));
		}
		Collections.shuffle(this.deck);
	}

	void draw(int times) {
		for (int i=0 ; i<times; i++) {
			this.isDeckEmpty();
			if (this.isHandFull())
				this.grave.add(this.deck.get(0));
			else
				this.hand.add(this.deck.get(0));  
			this.deck.remove(0);
		}
	}

	void isDeckEmpty() {
		if (this.deck.size() < 4) {
			Collections.shuffle(this.grave);
			this.deck.addAll(this.grave);
			this.grave.clear();
		}
	}
	
	boolean isHandFull() {
		return this.hand.size() >= 9;
	}

	void postPlay() {
		this.applyConditionEffects();
		this.checkPassiveItem();

		// Reset time
		this.playedThisTurn.clear();
		this.grave.addAll(this.hand);
		this.hand.clear();
		this.setShield(0);
	}

	void postBattle() {
		// All cards return to deck
		this.deck.addAll(this.hand);
		this.deck.addAll(this.grave);
		this.deck.removeAll(this.addedThisBattle);
		// Clear everything
		this.hand.clear();
		this.grave.clear();
		this.addedThisBattle.clear();
		this.playedThisBattle.clear();
		
		Collections.shuffle(this.deck);
	}

	Card getCard(String name) {
		for (Card toGet: this.gallery) {
			if (toGet.getName().equals(name)) return toGet;
		}
		return null;
	}

	int getCardIndex(String name, List<Card> list) {
		int i = 0;
		for (Card toGet: list) {
			if (toGet.getName().equals(name)) return i;
			i++;
		}
		return -1;
	}

	void use(Card toPlay, Character target) {
		//Can be further implement differently for different role
		System.out.println("\n------------------------------------------------------------------------------");
		toPlay.activate(this, target);
		System.out.println("------------------------------------------------------------------------------");
		this.hand.remove(this.getCardIndex(toPlay.getName(), this.hand));
	}

	void use(Item toUse, Character target) {
		System.out.println("\n------------------------------------------------------------------------------");
		toUse.activate(this, target);
		System.out.println("------------------------------------------------------------------------------");
	}

	//It prioritizes cards that have an effect containing the keyword "Deals" and selects the one with the highest damage value. 
	//If no suitable card is found, it defaults to the first card in the hand.
	public Card selectCardToPlay(double mana) {
		Card cardToPlay = null;
		int cardIndex = -1;
		int currentIndex = 0;
		String typeEffect = "";
		double typeValue = -1;

		for (Card card : hand) {
			if (card.getCost() <= mana) {
				int i = 0;
				for (String eff: card.getEffect()) {
					if (eff.contains("Deals")) {
						if (cardToPlay == null || card.getValue()[i] > cardToPlay.getValue()[i]) {
							cardToPlay = card;
							cardIndex = currentIndex;
							typeEffect = eff;
							typeValue = cardToPlay.getValue()[i];
						}
					}
					i++;
				}
			}
			currentIndex++;
		}
		if (cardToPlay == null) {
			cardToPlay = this.hand.get(0);
			this.hand.remove(0);
			System.out.println("\n" + this.getName() + " intended to use [" + cardToPlay.getName() + "]");
		}
		else {
			this.hand.remove(cardIndex);
			System.out.println("\n" + this.getName() + " intended to use [" + cardToPlay.getName() + 
					"]\n|  " + typeEffect + " " + typeValue + "  |");
		}
		return cardToPlay; // Default to the first card if no suitable card found
	}

	public void setSpecialization() {
		this.setRole("PlACEHOLDER");
	}

	/*
	public static void main(String[] args) throws IOException {
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
	 */

}
