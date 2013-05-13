package jam.think;

import jam.MainState;
import jam.Sheep;
import jam.Tile;

import java.util.List;

public class ExplorePlan extends Plan {

	
	
	MovePlan subPlan = null;
	
	public ExplorePlan(Sheep owner) {
		super(owner);
		type = Type.EXPLORE;
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
			
			// No. Should we be? Find nearest unvisited tile
			// TODO: find nearest unvisited tile
			// For now, test only adjacent tiles, negating the point of this plan
			List<Tile> adjacentTiles = owner.adjacentTiles();
			
			while (subPlan == null && adjacentTiles.size() > 0) {
				int index = MainState.instance.random.nextInt(adjacentTiles.size());
				Tile choice = adjacentTiles.get(index);
				
				// TODO: less binary cost/benefit analysis for hostile sheep
				if (!choice.isClear() || (choice.currentSheep != null && choice.currentSheep.owner != owner.owner)
						|| owner.visitedTiles[choice.position.x][choice.position.y])
					// Impassable or previously visited tile
					adjacentTiles.remove(index);
				else
					// Tile found, plot a course
					subPlan = new MovePlan(owner, choice.position);				
			}
			
			if (subPlan == null) {
				// Adjacent states exhausted, time for a backup plan
				if (new WanderPlan(owner).execute())
					// How do you mess up wandering? Honestly.
					throw new RuntimeException("Vagrancy error");
				return false;
			}
		}
	}

	@Override
	public void update() {
		// This shouldn't be necessary; ExplorePlans are effectively stateless
		utility = owner.curiosity * Sheep.curiosityCoefficient;
	}
	
	

}
