package jam.think;

import jam.MainState;
import jam.Sheep;
import jam.Tile;

import java.util.List;

public class WanderPlan extends Plan {
	
	Plan subPlan = null;
	
	public WanderPlan(Sheep owner) {
		super(owner);
		type = Type.WANDER;
	}
	
	@Override
	public boolean execute() {
		
		while (true) {
			// Are we already headed somewhere
			if (subPlan != null)
				if (!subPlan.execute())
					// Yeah, carry on
					return false;
				else
					// No, already there
					subPlan = null;

			List<Tile> adjacentTiles = owner.adjacentVacantTiles();
			if (adjacentTiles.size() == 0)
				// Idle. No choice
				return false;
			
			Tile choice = adjacentTiles.get(MainState.instance.random.nextInt(adjacentTiles.size()));
			subPlan = new MovePlan(owner, choice.position);
		}
	}

	@Override
	public void update() {
		// Nothing to see here
	}
	
	
}
