import java.util.ArrayList;
import java.util.List;

public class Condition {
	private String name;
	private int duration;
	@SuppressWarnings("unused")
	private int originalDuration;
	private int stack;
	// for the future, when buffing or lowering condition effectiveness
	@SuppressWarnings("unused")
	private int scale = 1;
	/*
	 * List of conditions
	 * poison
	 * burning
	 * bleed
	 * disease
	 * confusion
	 * 
	 * weakness
	 * vulnerable
	 * blindness
	 * freeze???
	 * stun???
	 */

	public Condition() {
		this.name = "dummy";
		this.duration = 999;
		this.stack = 0;
	}

	public Condition(int duration, String name) { // Damage modifiers
		this.name = name;
		this.duration = duration;
	}

	public Condition(String name, int stack) { // Damaging conditions
		this.name = name;
		this.stack = stack;
		if (name.equals("bleeding")) {
			this.duration = 3;
		}
		else if (name.equals("poison") || name.equals("confusion")) 
			this.duration = 2;
		else if (name.equals("disease")) 
			this.duration = 1;
		else if (this.name.equals("burning")) 
			this.setBurnDuration(this.stack);
		
		this.originalDuration = this.duration;
	}

	String getName() {return this.name;}
	int getStack() {return this.stack;}
	int getDuration() {return this.duration;}
	int getOriginalDuration() {return this.originalDuration;}
	int getScale() {return this.scale;}

	void setStack(int num) {this.stack = num;}
	void setScale(int num) {this.scale = num;}
	void setDuration(int num) {this.duration = num;}
	
	void setBurnDuration(int stack) {
		int count = 0;
		while (stack >= 2) {
			count++;
			stack /= 2;
		}
		this.duration = count + 1;
	}
	//void setScale(int num) {this.scale = scale;}

	void decreDuration(int lowerBy) {
		this.duration -= lowerBy;
	}
	void increDuration(int increBy) {
		this.duration += increBy;
	}

	List<String> getDamagingCondi() {
		List<String> damagingCondi = new ArrayList<>();
		damagingCondi.add("bleeding");
		damagingCondi.add("poison");
		damagingCondi.add("burning");
		damagingCondi.add("disease");
		damagingCondi.add("confusion");
		return damagingCondi;
	}

}
