import java.io.File;
import java.io.IOException;

public class Mage extends Character{
	
	static File archetype = new File("Mage_Gallery");
	int overloadCount = 0;
	
	public Mage() throws IOException {
		super(archetype);
		super.maxHealth = 100;
		super.setHealth(maxHealth);
		super.setRole("mage");
		super.makeGallery(archetype);
	}
	
	boolean isOverload() {
		return this.overloadCount > 7;
	}
	
	void use(Card toPlay, Character target) {
		//Can be further implement differently for different role
		boolean overloaded = false;
		String original = "";
		System.out.println("\n------------------------------------------------------------------------------");
		toPlay.activate(this, target);
		if (isOverload()) {
			original = toPlay.getName();
			toPlay.setName("OVERLOADED " + original);
			overloaded = true;
			toPlay.activate(this, target);
			this.overloadCount = 0;
		}
		System.out.println("------------------------------------------------------------------------------");
		if (overloaded) {
			toPlay.setName(original);
		}
		this.hand.remove(this.getCardIndex(toPlay.getName(), this.hand));
		
	}


}
