
public class MageCard extends Card{

	public MageCard(String name, String[] description, String[] effect, double[] value, double cost) {
		super(name, description, effect, value, cost);
		// TODO Auto-generated constructor stub
	}

	void activate(Character self, Character target) {
		super.activate(self, target);
		int i = 0;
		for (String action: this.getEffect()) {
			String des = this.getDes()[i];
			int of = des.indexOf("of");
			String key = des.trim().substring(of+3);

			if (((Mage) self).isOverload()) {}
			else {
				if (action.equals("Deals")) 
					((Mage) self).overloadCount++;
				else if (action.equals("Inflicts")) {
					Condition toCheck = new Condition();
					if (toCheck.getDamagingCondi().contains(key)) 
						((Mage) self).overloadCount++;
				}
			}
			i++;
		}
		if (!((Mage) self).isOverload())
			System.out.println("Overload Count:	" + ((Mage)self).overloadCount);

		if (((Mage) self).overloadCount == 7) System.out.println("!!!The next card played will be overloaded!!!");
	}

}
