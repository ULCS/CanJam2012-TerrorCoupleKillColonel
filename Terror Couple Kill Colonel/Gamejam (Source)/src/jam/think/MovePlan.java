package jam.think;

import jam.MainState;
import jam.Sheep;

import java.awt.Point;

import org.newdawn.slick.util.pathfinding.Path;

public class MovePlan extends Plan {
	
	Point target;
	Path path;
	int pathIndex;
	
	Point lastPos;
	
	public MovePlan(Sheep owner, Point target) {
		super(owner);
		type = Type.MOVETO;
		this.target = target;
		
		// Find path
		path = MainState.instance.map.pathfinder.findPath(null, owner.currentX, owner.currentY, target.x, target.y);
		if (path == null)
			// Path not found. I suppose we're already there
			if (!target.equals(new Point(owner.currentX, owner.currentY)))
					throw new RuntimeException("Pathfinding error " + new Point(owner.currentX, owner.currentY) +" "+target +
							" " + MainState.instance.map.getTile(target.x, target.y).type);
			else {
				path = new Path();
				path.appendStep(owner.currentX, owner.currentY);
			}
		pathIndex = 0;
		
		lastPos = new Point(-1, -1);
	}

	/// @todo Adapt to unexpected obstacles
	@Override
	public boolean execute() {		
		// OK, so apparently this belongs here
		// Are we at pasture? Any pasture will do
		if (owner.type == Sheep.Type.SHEEP &&
				owner.currentTile().grass != null && owner.currentTile().grass.amount > 0
				// Can we eat another bite?
				&& owner.maxNutrition - owner.nutrition > owner.metabolism * 2)
			// Yes. Graze.
			return false; // Sheep presently graze by default
		
		// Have we moved?
		if (!new Point(owner.currentX, owner.currentY).equals(lastPos)) {
			lastPos = new Point(owner.currentX, owner.currentY);
			++pathIndex;
		} else {
			// No. Keep stupidly doing whatever it is we were doing, maybe we'll break through eventually
			owner.beginMove();
			
			return false;
		}
		
		/* I'm watching the guy beside me design a map with the UDK, and I get the impression he's done this sort of thing before,
		 * but he's trying to texture, like, 60 individual stairs on a staircase, and he does it by constantly dragging and dropping
		 * the same texture. There has to be a hotkey for that. This world has gone mad. */
		
		// Are we there yet?
		if (pathIndex == path.getLength()) {
			System.out.println(MainState.instance.map.getTile(target.x, target.y));
			if (!(owner.currentX == target.x && owner.currentY == target.y))
				throw new RuntimeException();
			
			return true;
		}
		
		owner.faceTile(path.getX(pathIndex), path.getY(pathIndex));
		
		owner.beginMove();
		
		return false;
	}

	@Override
	public void update() {
		// MovePlan utilities are externally defined and/or immaterial		
	}
	
	

}
