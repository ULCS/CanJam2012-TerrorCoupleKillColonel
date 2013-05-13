package jam.think;

import jam.Grass;
import jam.Sheep;
import jam.Tile;

import java.awt.Point;

public class FoodPlan extends Plan {
	
	public Tile target;
	
	MovePlan subPlan = null;

	public FoodPlan(Sheep owner, Tile target) {
		super(owner);		
		type = Type.FOOD;
		this.target = target;
		
		if (target != null)
			subPlan = new MovePlan(owner, target.position);
	}
	
	@Override
	public boolean execute() {
		// Are we at pasture? Any pasture will do
		if (owner.currentTile().grass != null && owner.currentTile().grass.amount > 0)
			// Yes. Graze.
			return false; // Sheep presently graze by default
			
		// No. Head for objective. If already there (and no grass remains), end plan
		return subPlan.execute();
		
	}

	@Override
	public void update() {
		// Is there food?
		if (target.grass == null) {
			utility = 0;
			return;
		}
			
		
		Grass g = target.grass;
		
		// Is the tile occupied? By another sheep?
		Sheep s = target.currentSheep;
					
		if (s != null && s != owner) {
			utility = 0;
			return;
		}
		
		// How much food is there? Ignore any in excess of what we can eat (even though we might be hungrier when we get there)
		utility = Math.min(g.amount, owner.maxNutrition - owner.nutrition);
		// Distance penalty
		utility -= owner.utilityCostPerTile * owner.distance(target.position);
		
		// Personality
		utility *= owner.gluttony;
	}
	
	

}
