import java.util.List;

public class WarriorCard extends Card{

	public WarriorCard(String name, String[] description, String[] effect, double[] value, double cost) {
		super(name, description, effect, value, cost);
		// TODO Auto-generated constructor stub
	}

	void activate(Character self, Character target) {
		super.activate(self, target);
		String name = this.getName();
		int countThisTurn = this.getPlayedCount(name, self.playedThisTurn);
		int countThisBattle = this.getPlayedCount(name, self.playedThisBattle);

		if (name.equals("Sun and Moon Slash")) {
			Card toAdd = self.getCard("Quick Strike");
			self.playedThisTurn.add(toAdd);
			self.playedThisTurn.add(toAdd);
		}

		if (name.equals("Quick Strike")) {
			if (countThisTurn >= 2) {
				System.out.println(this.getName2() + " dealt " + (countThisTurn-1) + " more damage.");
				super.damageCalculation(countThisTurn-1, target);
			}
		}

		if (name.equals("Dragon Slash")) {
			boolean isTwice = ((int)(Math.random()*100)) < (countThisBattle * this.getValue()[2]);
			if (isTwice) {
				System.out.println("(ง •̀_•́)ง DRAGON ROAR! " + this.getName2() + " deals twice the damage");
				super.damageCalculation(this.getValue()[0], target);
			}
		}

	}

	public int getPlayedCount(String name, List<Card> list) {
		int count = 0;
		for (Card c : list) 
			if (c.getName().contains(name)) 
				count++;
		return count;
	}

}
