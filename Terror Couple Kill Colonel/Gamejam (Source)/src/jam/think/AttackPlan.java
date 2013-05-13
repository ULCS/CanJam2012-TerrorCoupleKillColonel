package jam.think;

import jam.Sheep;

import java.awt.Point;


public class AttackPlan extends Plan {
	public Sheep target;
	public Point lastTargetPos;
	
	MovePlan subPlan;
	
	public AttackPlan(Sheep owner, Sheep target) {
		super(owner);
		type = Type.ATTACK;
		this.target = target;
		lastTargetPos = new Point(target.currentX, target.currentY);
		subPlan = new MovePlan(owner, lastTargetPos);
	}

	@Override
	public boolean execute() {
		// Is the other target still there? Like, in existence?
		if (target.dead)
			return true;
		
		// OK, but is it still there? Like, in the tile?
		if (!new Point(target.currentX, target.currentY).equals(lastTargetPos))
			subPlan = new MovePlan(owner, lastTargetPos);
				
		subPlan.execute();
		
		return false;
	}

	
	@Override
	public void update() {
		// Is our target still there? Also, is this code redundant?
		if (target.dead) {
			target = null;
			utility = 0;
			return;
		}
		// Yes. Can we take it?
		float advantage = owner.threatLevel() / target.threatLevel();
		// And is it worth it?
		utility = target.worth() * advantage;
		// Even if it's really far away?
		utility -= owner.utilityCostPerTile * owner.distance(target);
		
		// Personality
		utility *= owner.aggression;
	}
	
	
	
}
