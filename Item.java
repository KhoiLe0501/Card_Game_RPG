
public class Item {
	//Read file to create a bunch of item
	private String name;
	private String description;
	private String effect;
	private double value;
	private boolean usable;

	// There's two type of items: usable and unusable
	// Items are acquired through the merchants or from random events

	public Item(String name, String description, String effect, double value) {
		this.name = name; this.description = description; this.effect= effect; this.value = value;
		if (effect.equals("Resurrect")) this.usable = false;
		else this.usable = true;
	}

	String getName() {return name;}
	String getName2() {return "(" + name + ")";}
	String getEffect() {return effect;}
	String getDes() {return description;}
	double getValue() {return value;}
	boolean isUsuable() {return usable;}

	void activate(Character self, Character target) {
		System.out.println(self.getName() + " used " + this.getName2());
		String eff = this.getEffect();
		String des = this.getDes();
		double num = this.getValue();

		switch (eff) {
		case "Heals":
			if (self.hasCondition("poison")) { //Double check this
				num -= num/3;
				System.out.println("[Poison lowered healing effectiveness!]");
			}
			self.setHealth(self.getHealth() + num);
			System.out.println(self.getName() + " Status | Health: " + self.getHealth() + " | Shield: " + self.getShield());
			break;
		case "Deals":
			if (self.hasCondition("blindness")) {
				System.out.println(this.getName() + " missed. (ㅠ﹏ㅠ)");
				break;
			}
			else if (self.hasCondition("weakness")) {
				System.out.println(this.getName() + " is weakened");
				num  = num * 0.5;
			}
			if (target.isReflect) {
				System.out.println("(☉_☉) Damage reflected");
				this.damageCalculation(num, self);
				target.isReflect = false;
			}
			else this.damageCalculation(num, target);
			if (target.getHealth() <= 0 && target.hasItem("Resurrect")) 
				target.checkPassiveItem();
			break;
		case "Inflicts":
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
						// Burn duration is calculated differently
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
			System.out.println("Added [" + desiredName + "] to " + destination);
			break;
			// System.out.println(self.getCardIndex(desiredName, self.deck));
		default:
			break;
		}
		self.inventory.remove(this);
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
}
