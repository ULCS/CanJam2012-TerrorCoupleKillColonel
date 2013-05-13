package jam;
import jam.think.AttackPlan;
import jam.think.ExplorePlan;
import jam.think.FoodPlan;
import jam.think.Plan;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.newdawn.slick.Image;


public class Sheep {	
	public enum Direction {
		UP, DOWN, LEFT, RIGHT
	}
	
	public enum Type {
		SHEEP, VIKING
	}
	
	public Type type;
	
	public static float curiosityCoefficient = 200;
	public static float strengthCoefficient = 200;
	
	// Owner
	public Player owner;
	
	// Stats - Behavior
	public float curiosity = 1;
	public float aggression = 1;
	public float gluttony = 1;
	public int utilityCostPerTile = 400; // A higher value inclines the sheep to just kinda sit around
	
	// Stats - Abilities
	public int speed;
	public int baseStrength = 10;
	public int visionRange = 4;
	public int metabolism = 8; // < Base nutrition lost per step
	public int healRate = 2;
	public int grazeRate = 20;
	public int maxHealth = 10000;
	public int maxNutrition = 10000;
	
	// State
	public boolean dead = false;
	public int health = 10000;
	public int nutrition = 10000;
	
	public List<Sheep> sheepBeingFought = new ArrayList<Sheep>();
		
	public int currentX;
	public int currentY;
	
	Direction facing = Direction.UP;
	
	/// Time the sheep started its current move. If zero, sheep is not moving
	long moveStartTime = 0;
	/// Time the sheep's current move animation will end. If zero, sheep is not moving
	long moveEndTime = 0;
	/// If zero, sheep is not moving
	int moveStepsRemaining = 0;
	
	Plan plan = null;
	
	public HashMap<Direction,Image> sprites;
	public Image powSprite;
	
	List<Tile> tilesInView = new ArrayList<Tile>();
	
	/// @todo Change to timestamps, consider a tile unvisited after t time
	public boolean[][] visitedTiles;
	
	public Sheep(int x, int y, Player owner) {
		type = Type.SHEEP;
		currentX = x;
		currentY = y;
		this.owner = owner;
		MainState.instance.map.getTile(x, y).currentSheep = this;
		
		visitedTiles = new boolean[MainState.instance.map.widthTile][];
		for (int i = 0; i < visitedTiles.length; ++i)
			visitedTiles[i] = new boolean[MainState.instance.map.heightTile];
		
		visitedTiles[currentX][currentY] = true;		
	}
	
	public Image getImage() {
		// Fight sprite
		if (dead || isFighting())
			return powSprite;
		
		return sprites.get(facing);
	}
	
	void update() {
		// Metabolize
		health += healRate;
		health = Math.min(health, maxHealth);
		nutrition -= metabolism;
		
		if (nutrition < 0) {
			sustainDamage(-nutrition);
			nutrition = 0;
		}
		
		// Update sensory info
		tilesInView.clear();
		
		// Scan tiles row by row
		for (int y = currentY - visionRange; y <= currentY + visionRange; ++y) {
			int width = visionRange - Math.abs(y - currentY);
			for (int x = currentX - width; x <= currentX + width; ++x) {
				// Sheep cannot see its own tile, though it looks pretty funny when it tries
				if (x == currentX && y == currentY)
					continue;
				
				if (MainState.instance.map.inBounds(x,y))
					tilesInView.add(MainState.instance.map.getTile(x, y));
			}
				
		}
		
		// Think
		think();
		
		// Act
		if (moveStepsRemaining > 0) {
			--moveStepsRemaining;
			if (moveStepsRemaining == 0) 
				tryMove();
		} else {
			// We're not moving, ergo we're grazing
			graze();
		}						
	}
	
	public List<Tile> adjacentTiles() {
		List<Tile> result = new ArrayList<Tile>();
		
		if (MainState.instance.map.inBounds(currentX - 1, currentY))
			result.add(MainState.instance.map.getTile(currentX - 1, currentY));
		if (MainState.instance.map.inBounds(currentX + 1, currentY))
			result.add(MainState.instance.map.getTile(currentX + 1, currentY));		
		if (MainState.instance.map.inBounds(currentX, currentY - 1))
			result.add(MainState.instance.map.getTile(currentX, currentY - 1));
		if (MainState.instance.map.inBounds(currentX, currentY + 1))
			result.add(MainState.instance.map.getTile(currentX, currentY + 1));
		
		return result;
	}
	
	public List<Tile> adjacentVacantTiles() {
		List<Tile> result = adjacentTiles();
		List<Tile> delStack = new ArrayList<Tile>();
		
		for (Tile t : result)
			if (!t.isClear() || (t.currentSheep != null && t.currentSheep.owner != owner))
				delStack.add(t);
		
		for (Tile t : delStack)
			result.remove(t);
		
		return result;
	}	
	
	/// @return true if target still alive
	public boolean attack() {
		// Tile ahead assumed to be legit and occupied, else why would you be attacking?
		Sheep foe =  tileAhead().currentSheep;
		MainState.instance.map.newPow(this.currentX, this.currentY);
		int damageDealt = Math.round(strength());
		int damageSustained = Math.round(foe.strength());
		foe.sustainDamage(damageDealt);
		sustainDamage(damageSustained);
		
		if (foe.dead)
			return false;
		
		return true;
	}
	
	public void beginMove() {
		// How many steps will the move take?
		moveStepsRemaining = 40;
		
		// Parametrize animation
		moveStartTime = MainState.instance.lastTick;
		moveEndTime = moveStartTime + moveStepsRemaining * MainState.stepSize;
	}
	
	/// @todo Hunger-driven crime
	Plan bestAttackPlan() {
		AttackPlan result;
		
		// Do we already have a plan of attack?
		if (plan != null && plan.type == Plan.Type.ATTACK)
			result = (AttackPlan)plan;
		
		else {
			result = new AttackPlan(this, this);
			result.utility = 0;
		}
		
		// Consider each sheep in view individually
		for (Tile t : tilesInView) {
			if (t.currentSheep == null || t.currentSheep == result.target)
				continue;
			
			Sheep s = t.currentSheep;
			// Is it hostile?
			if (s.owner != owner) {
				// Yes
				AttackPlan candidate = new AttackPlan(this, s);
				candidate.update();
				
				if (candidate.utility > result.utility)
					result = candidate;
			}
		}
				
		if (result.utility == 0)
			return null;
		return result;
	}
	
	Plan bestFoodPlan() {
		FoodPlan result;
		
		// Do we already have a food plan?
		if (plan != null && plan.type == Plan.Type.FOOD)
			result = (FoodPlan)plan;
		
		else {
			result = new FoodPlan(this, null);
			result.utility = 0;
		}
		
		// Consider each patch of grass in view individually. Including the patch on which we're standing
		Tile t;
		for (int i = 0; i <= tilesInView.size(); ++i) {
			if (i == tilesInView.size())
				t = currentTile();
			else
				t = tilesInView.get(i);
			
			if (!t.isClear() || t == result.target)
				continue;
			
			FoodPlan candidate = new FoodPlan(this, t);
			candidate.update();
			
			if (candidate.utility > result.utility)
				// New best target found
				result = candidate;						
		}
					
		if (result.utility == 0)
			return null;
		return result;
	}	
	
	Plan bestExplorePlan() {
		Plan result = new ExplorePlan(this);
		result.utility = curiosity * curiosityCoefficient;
		// Wait, recreating a new ExplorePlan each think step. Which is fine, since they're effectively stateless
		return result;
	}
	
	boolean busy() {
		return this.moveStepsRemaining > 0;
	}
	
	public Tile currentTile() {
		return MainState.instance.map.getTile(currentX, currentY);
	}
	
	void die() {
		dead = true;
		// Remove self from tile, but do not reset position attributes
		currentTile().currentSheep = null;
		
		for (Sheep s : sheepBeingFought)
			s.sheepBeingFought.remove(this);		
		sheepBeingFought.clear();
	}
	
	
	/** @return distance from the given point. In a world without diagonals.
	 * 
	 * @todo Consider leveraging pathfinding 
	 */
	public int distance(Point point) {
		return Math.abs(point.x - currentX) + Math.abs(point.y - currentY); 
	}
	
	public int distance(Sheep sheep) {
		return distance(new Point(sheep.currentX, sheep.currentY)); 
	}
	
	public void faceTile(int x, int y) {
		if (x > currentX) {
			facing = Direction.RIGHT;
			//assert (y == currentY);
			if (y != currentY)
				throw new RuntimeException();
		}
		else if (x < currentX) {
			facing = Direction.LEFT;
			//assert (y == currentY);
			if (y != currentY)
				throw new RuntimeException();
		}
		else {
			//assert (x == currentX);
			if (x != currentX)
				throw new RuntimeException();
			if (y > currentY)
				facing = Direction.DOWN;
			else {
				if (y == currentY)
					throw new RuntimeException();
				facing = Direction.UP;
			}
		}
	}
	
	void graze() {
		if (currentTile().grass == null)
			return;
		int amountToEat = Math.min(grazeRate, Math.min(maxNutrition - nutrition, currentTile().grass.amount));
		
		currentTile().grass.amount -= amountToEat;
		nutrition += amountToEat;
	}
	
	boolean isFighting() {
		return sheepBeingFought.size() > 0;
	}
	
	Point spritePos(long currentTicks) {
		// Position of top left corner of sheep's current tile
		int x = currentX * MainState.instance.map.tileSizePx;
		int y = currentY * MainState.instance.map.tileSizePx;
		
		if (isFighting() || dead)
			// Ignore movement
			return new Point(x,y);
		
		// Account for movement
		if (moveStartTime > 0) {
			float moveModifier = MainState.instance.map.tileSizePx * (currentTicks - moveStartTime) / (moveEndTime - moveStartTime);
			moveModifier = Math.min(moveModifier, (float)MainState.instance.map.tileSizePx);
			
			switch (facing) {
			case UP:
				y -= moveModifier;
				
				break;
			case RIGHT:
				x += moveModifier;
				
				break;
			case DOWN:
				y += moveModifier;
				
				break;
			case LEFT:
				x -= moveModifier;
				
				break;
			}
		}
		
		return new Point(x,y);
	}

	
	/// Calculate attack strength
	int strength() {
		if (dead)
			return 0;
		
		float injuryCoefficient = ((float)health) / ((float)maxHealth); // < Injury penalty
		injuryCoefficient = (1.0f + injuryCoefficient) / 2.0f;
		
		return Math.round(baseStrength * injuryCoefficient * strengthCoefficient);
	}
	
	void sustainDamage(int amount) {
		health -= amount;
		
		if (health < 0)
			die();
	}
		
		
	void think() {
		// Are we in mid-action?
		if (busy())
			return;
		
		// No. Make a plan
		thinkHighLevel();
		
		// Follow through
		if (plan.execute())
			// Plan done
			plan = null;
	}
	
	void thinkHighLevel() {
		// Reconsider current plan
		if (plan != null)
			plan.update();
		
		// Calculate utilities for high-level behavior
		List<Plan> bestPlans = new ArrayList<Plan>();
		
		Plan temp = bestAttackPlan();
		if (temp != null)
			{//System.out.println("MURDER PLAN " + temp.utility);
			bestPlans.add(temp);
			}
		temp = bestFoodPlan();
		if (temp != null)
			{//System.out.println("Dental Plan " + temp.utility);
			bestPlans.add(temp);}
		temp = bestExplorePlan();
		if (temp != null)
			{//System.out.println("Explore " + temp.utility);
			bestPlans.add(temp);}		
		
		// Choose best plan
		if (bestPlans.size() == 0)
			throw new RuntimeException("No plan. No future");			
		
		Collections.sort(bestPlans);
		plan = bestPlans.get(bestPlans.size() - 1);
	}
	
	/// Approximates this sheep's combat effectiveness
	public int threatLevel() {
		return strength() + (health / 10);
	}
	
	public Tile tileAhead() {
		Tile result = null;
		switch (facing) {
		case UP:
			if (MainState.instance.map.inBounds(currentX, currentY - 1))
				result = MainState.instance.map.getTile(currentX, currentY - 1);
			
			break;		
		case DOWN:
			if (MainState.instance.map.inBounds(currentX, currentY + 1))
				result = MainState.instance.map.getTile(currentX, currentY + 1);
			
			break;
		case LEFT:
			if (MainState.instance.map.inBounds(currentX - 1, currentY))
				result = MainState.instance.map.getTile(currentX - 1, currentY);
			
			break;		
		case RIGHT:
			if (MainState.instance.map.inBounds(currentX + 1, currentY))
				result = MainState.instance.map.getTile(currentX + 1, currentY);
			
			break;		
		}
			
		return result;
	}
	
	
	/// @return true on failure
	public boolean tryMove() {
		Tile destination = tileAhead();
		// Is the tile valid?
		if (destination == null)
			return true;
		
		// Is it vacant?
		if (!destination.isClear())
			return true;
		
		if (destination.currentSheep != null) {
			// There's a sheep. I wonder if it's friendly!
			if (destination.currentSheep.owner == owner)
				// HELLO, SHEEP!
				return true;
			
			// It's not. Attack
			sheepBeingFought.remove(destination.currentSheep);
			destination.currentSheep.sheepBeingFought.remove(this);
			sheepBeingFought.add(destination.currentSheep);
			destination.currentSheep.sheepBeingFought.add(this);

			if (attack())
				return true;
		}
		
		// Destination is vacant. Vacate current tile and occupy it
		currentTile().setSheep(null);
		destination.setSheep(this);
		currentX = destination.position.x;
		currentY = destination.position.y;
		
		for (Sheep s : sheepBeingFought)
			s.sheepBeingFought.remove(this);		
		sheepBeingFought.clear();
		
		visitedTiles[currentX][currentY] = true;
		
		// End animation
		moveStartTime = moveEndTime = 0;
		
		return false;
	}
	
	/// Approximates this sheep's value to its owner 
	public int worth() {
		return baseStrength + speed + (visionRange * 5) + (maxHealth / 10) + (health / 40);
	}	
}
