import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Warrior extends Character{
	
	static File archetype = new File("Warrior_Gallery");

	public Warrior() throws IOException {
		super(archetype);
		super.maxHealth = 110;
		super.setHealth(maxHealth);
		super.setRole("warrior");
		super.makeGallery(archetype);
	}

}
