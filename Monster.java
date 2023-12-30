import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Monster extends Character implements Enemy_Unit{
	int notAttackProc = 0;
	static File archetype = new File ("Troll");

	public Monster(File archetype) throws IOException {
		super(archetype);
		// Health will varied
		super.maxHealth = (int) (Math.random() * (110-50)) + 75;
		super.setHealth(super.maxHealth); // do something with level here (maybe in constructor args)
		super.setRole("monster");
		super.makeGallery(archetype);
		
		//Eventually, make a file that generate all the monsters
	}

	//DUMMY MONSTER
	public Monster() throws IOException{
		 this(archetype);
		 this.setHealth(1000);
	}

	@Override
	public Card selectCardToPlay(double mana) { // Make sure to keep monster card to be simple
		Card toPlay = null;
		String typeEffect = "";
		double typeValue = -1;
		int proc = 20;

		for (Card card : hand) {
			if (card.getCost() <= mana) {
	            int i = 0;
	            List<String> effectList = new ArrayList<>();
	            boolean isNonAttackingCard = false; // Flag to track if a non-attacking card is found
	            
	            for (String eff : card.getEffect()) {
	            	effectList.add(eff);
	                if (eff.equals("Deals")) {
	                    if (toPlay == null || card.getValue()[i] > toPlay.getValue()[i]) { // Find the highest damage card
	                    	toPlay = card;
	                        typeEffect = eff;
	                        typeValue = toPlay.getValue()[i];
	                    }
	                }
	                i++;
	            }
	            if (!effectList.contains("Deals")) { // Confirmed non-attacking card
	            	isNonAttackingCard = true;
	            }
	            if (isNonAttackingCard && (int) (Math.random() * 100) < this.notAttackProc) { //Non-attacking card proc
	            	toPlay = card;
	                this.notAttackProc = -proc; // Help adjust notAttackProc back to zero after non-attacking card is played
	                break;
	            }
	        }
		}
				
		if (toPlay == null) {
			toPlay = this.hand.get(0);
		}
		else {
			if (this.notAttackProc < 100) 
				this.notAttackProc += proc;
			System.out.println("\n" + this.getName() + " intended to use " + toPlay.getName2() + 
					"\n|  " + typeEffect + " " + typeValue + "  |");
		}
		return toPlay; // Default to the first card if no suitable card found
	}
	

}
