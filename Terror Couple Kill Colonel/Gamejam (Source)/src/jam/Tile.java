package jam;
import java.awt.Point;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;


public class Tile {

private volatile boolean firstLoad = true;	
	
	public enum tileType {
		CLEAR, LOGVTOP, LOGVBOTTOM, LOGHTOP, LOGHBOTTOM, ROCK, TREE, TREEPART, GRASS	
	}
	public tileType type;
	/** @deprecated */
	boolean passable;
	Image tileimage;
	
	public Sheep currentSheep = null;
	public Grass grass = null;

	/**
	 * @brief Convenience attribute
	 * 
	 * It'd be nice if Java had a heterogeneous, non-generic tuple container. Also if Java wasn't Java. 
	 */
	public Point position;

@Override
public String toString() {
	return type.toString();
}

public Image getImage() {
	// Hax. Assume a null tileimage signifies a grassy tile
	if (this.type == Tile.tileType.GRASS) {
		if (grass == null){
			return null;}
		
		if (grass.amount == 0){
			return shortGrass;}
		if (grass.amount < 3) {
		return medGrass;} else { return tallGrass;}
		
	} else {return this.tileimage;}
	
	
	
} 

public void setSheep(Sheep sheep) {
	this.currentSheep = sheep;
	
}

public void update() {
	if (grass != null)
		grass.update();
}

static Image rock;
static Image logtop;
static Image logbottom;
static Image logtopH;
static Image logbottomH;
static Image tallGrass;
static Image medGrass;
static Image shortGrass;
static Image tree;

private synchronized void inits() throws SlickException {
// Load images to statics
firstLoad = false;
rock = new Image("resources/map/rock.png");
logtop = new Image("resources/map/logVtop.png");
logbottom = new Image("resources/map/logVbottom.png");
logtopH = new Image("resources/map/logHtop.png");
logbottomH = new Image("resources/map/logHbottom.png");
tallGrass = new Image("resources/map/tallGrass.png");
medGrass = new Image("resources/map/medGrass.png");
shortGrass = new Image("resources/map/shortGrass.png");
tree = new Image("resources/map/tree.png");

}


public Tile(tileType type, Point position) throws SlickException {

this.type = type;	
this.position = position;
if (firstLoad) {
	inits();
}
	
	
	switch (type) {
	case CLEAR:
		passable = true;
		tileimage = null;
		break;
	case LOGHBOTTOM:
		passable = false;
		tileimage = logbottomH;
		break;
	case LOGHTOP:
		passable = false;
		tileimage = logtopH;
		break;
	case LOGVBOTTOM:
		passable = false;
		tileimage = logbottom;
		break;
	case LOGVTOP:
		passable = false;
		tileimage = logtop;
		break;
	case ROCK:
		passable = false;
		tileimage = rock;
		break;
	case TREE:
		tileimage = tree;
		break;
	case TREEPART:
		tileimage = null;
		passable = false;
		break;
	case GRASS:
		tileimage = this.getImage();
		break;
}
}

	public boolean isClear() {
		return MainState.instance.map.isClear(position.x, position.y);
	}
	
	
	
}
