package jam.think;

import jam.Sheep;

public abstract class Plan implements Comparable<Plan> {
	
	public enum Type {
		WANDER, EXPLORE, FLOCK, ATTACK, MOVETO, FOOD }
		
		public String toString() {
			switch (type){
			case WANDER:
			return "Wander";
			
			case EXPLORE:
			return "Explore";
			
			case FLOCK:
			return "Flock";
			
			case ATTACK:
			return "Attack!";
			
			case MOVETO:
			return "Move to";
			
			case FOOD:
			return "Feed";
		
			default: return "ERROR";
			}
			 }
		
		
	public Type type;
	
	public float utility;
	
	/// The sheep carrying out this Plan
	Sheep owner;
	
	public Plan(Sheep owner) {
		this.owner = owner;
	}
	
	/// @return true if can no longer pursue plan
	public abstract boolean execute();

	/// @brief Recalculate utility
	public abstract void update();
	@Override
	public int compareTo(Plan other) {
		//return utility - other.utility;
		if (utility > other.utility)
			return 1;
		if (utility == other.utility)
			return 0;
		return -1;
	}

}
