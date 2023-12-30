import java.util.List;

public class Card {
	private String name;
	private String[] description;
	private String[] effect;
	private double[] value;
	private double cost;

	boolean pierced = false;
	boolean twice = false;

	public Card(String name, String [] description, String[] effect, double[] value, double cost) {
		this.name = name; this.description = description; this.effect = effect; this.cost = cost; this.value = value;
	}

	String getName() {return name;}
	String getName2() {return "[" + name + "]";}
	String[] getEffect() {return effect;}
	String[] getDes() {return description;}
	double[] getValue() {return value;}
	double getCost() {return cost;}
	
	void setName(String name) {this.name = name;}

	public String toString() {
		String s = "(" + this.cost + ")	" + this.name + "\n";
		for (int i = 0; i < description.length; i++) {
			s += description[i] + "\n";
		}
		return s;
	}

	void activate(Character self, Character target) {
		System.out.println(self.getName() + " activates " + this.getName());
		int i = 0;
		for (String action : this.getEffect()) {
			String des = this.description[i];
			double num = this.value[i];
			i++;

			if (num == 0) 
				continue; // for visual ONLY description
			else if (num == -1) 
				System.out.println();

			switch (action) {
			case "Heals":
				if (self.hasCondition("poison")) { //Double check this
					num -= num/3;
					System.out.println("[Poison lowered healing effectiveness!]");
				}
				self.setHealth(self.getHealth() + num);
				System.out.println(self.getName() + " Status | Health: " + self.getHealth() + " | Shield: " + self.getShield());
				break;
			case "Blocks":
				self.setShield(self.getShield() + num);
				System.out.println(self.getName() + " Status | Health: " + self.getHealth() + " | Shield: " + self.getShield());
				break;
			case "Lifesteals": // Does armor-ignoring damage
				target.setHealth(target.getHealth() - num);
				self.setHealth(self.getHealth() + num);
				System.out.println(self.getName() + " Status | Health: " + self.getHealth() + " | Shield: " + self.getShield());
				System.out.println(target.getName() + " Status | Health: " + target.getHealth() + " | Shield: " + target.getShield());
				break;
			case "Twice":
				twice = (int) (Math.random() * (100)) < num;
				break;
			case "Pierce":
				pierced = (int) (Math.random() * (100)) < num;
				break;
			case "Reflects":
				self.isReflect = true;
				break;

			case "Deals":
				if (self.hasCondition("blindness")) {
					System.out.println(self.getName() + " missed. (ㅠ﹏ㅠ)");
					continue;
				}
				else if (self.hasCondition("weakness")) {
					System.out.println(self.getName() + " is weakened");
					num  = num * 0.5;
				}
				if (target.isReflect) {
					System.out.println("(☉_☉) Damage reflected");
					damageCalculation(num, self);
					target.isReflect = false;
				}
				else damageCalculation(num, target);
				if (target.getHealth() <= 0 && target.hasItem("Resurrect")) 
					target.checkPassiveItem();
				break;

			case "Adds":
				int to = des.indexOf("to");
				String destination = des.substring(to + 3).trim();
				String desiredName = des.substring(des.indexOf("(") + 1, des.indexOf(")"));
				Card toAdd = self.getCard(desiredName);
				for (int n = 0; n < num; n++) {
					switch (destination) {
					case "hand":
						self.hand.add(toAdd);
						break;
					case "grave":
						self.grave.add(toAdd);
						break;
					case "deck":
						self.deck.add(toAdd);
						break;
						// Add more cases as needed for other destinations
					}
				}
				self.addedThisBattle.add(toAdd);
				// Handle other cases related to destination
				System.out.println("  *Added [" + desiredName + "] to " + destination);
				
				// System.out.println(self.getCardIndex(desiredName, self.deck));
				break;

			case "Inflicts": // Condition damage
				// CODE IN 3rd PERSON
				int of = des.indexOf("of");
				String key = des.trim().substring(of+3);

				if (target.hasCondition(key)) { //Reapply condition
					Condition toReapply = target.getCondition(key);
					if (!toReapply.getDamagingCondi().contains(key)) { // For all non-damaging conditions
						if (key.equals("blindness")) 
							// A Card with longer blindness duration may be play but it cannot stack
							toReapply.setDuration((int) num);
						else toReapply.setDuration(toReapply.getDuration() + (int) num);
					}
					else {
						toReapply.setStack(toReapply.getStack() + (int) num);
						// Reapply the condition duration
						if (key.equals("burning"))
							toReapply.setBurnDuration(toReapply.getStack());
						else 
							toReapply.setDuration(toReapply.getOriginalDuration());
					}
				}
				else { // New condition apply
					Condition toApply = new Condition();
					if (!toApply.getDamagingCondi().contains(key)) 
						toApply = new Condition((int) num, key);
					else 
						toApply = new Condition(key, (int) num);
					target.conditionsOnSelf.add(toApply);
				}
				break;

			default:
				// Handle any other cases
				break;
			}
		}
		
		//post card play
		self.playedThisTurn.add(this);
		self.playedThisBattle.add(this);
		self.grave.add(this);

	}

	void damageCalculation(double num, Character target) {
		// System.out.println("In DAMAGE CALCULATION");
		double shield = target.getShield();
		if (twice) {
			num = num * 2;
			twice = false;
		}
		if (target.hasCondition("vulnerable")) 
			num = num * 1.25;
		if (pierced) {
			target.setHealth(target.getHealth() - num);
			System.out.println("(☉_☉) " + this.getName() + " pierced through " + target.getName() + " shield!");
			pierced = false;
		}
		else if (num < shield) {
			target.setShield(shield - num);
		}
		else {
			target.setHealth(target.getHealth() + shield - num);
			target.setShield(0);
		}
		System.out.println("  *Deals " + num + " damages");
		System.out.println(target.getName() + " Status | Health: " + target.getHealth() + " | Shield: " + target.getShield());

	}

}
