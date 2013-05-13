package jam;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;


public class Map implements TileBasedMap {

enum plane {
	HORIZONTAL, VERTICAL
}
	
	
	
public final int widthPx = 864;	
public final int heightPx = 768;	
public final int heightTile = 24;
@Override
public String toString() {
	String holder = "";
	for(int i=0; i<widthTile; i++){
		for(int j=0; j<heightTile; j++){
		holder += "x="+i+",y="+j+mapArray[i][j];
	   }
		holder += "\n";
	}// end out for	
return holder;
}

public final int widthTile = 27;
public final int tileSizePx = 32;	

public AStarPathFinder pathfinder;

private final Tile[][] mapArray = new Tile[widthTile][heightTile];

public static Image pow;

public volatile ArrayList<dumbSprite> dumbSprites;


public Map() throws SlickException {
// add objects	
pow = new Image("resources/pow.png");
dumbSprites = new ArrayList<dumbSprite>();


// Set entire map to clear	
	for(int i=0; i<widthTile; i++){
		for(int j=0; j<heightTile; j++){
			mapArray[i][j] = new Tile(Tile.tileType.CLEAR, new Point(i,j));
	   }
	}// end out for	
	
// Add features

	
	mapArray[5][5] = new Tile(Tile.tileType.ROCK, new Point(5,5));
	
	drawStones(10, 10, plane.HORIZONTAL, 3);
	drawStones(10, 1, plane.VERTICAL, 6);
	drawStones(4, 2, plane.HORIZONTAL, 7);
	drawStones(4, 3, plane.HORIZONTAL, 4);

	drawLog(0,2,plane.VERTICAL);
	drawLog(1,2,plane.VERTICAL);
	drawLog(2,2,plane.VERTICAL);
	drawLog(13,23,plane.HORIZONTAL);
	drawLog(5,5,plane.HORIZONTAL);
	drawTree(15,18);
	drawTree(22,0);
	
	drawLog(22,5,plane.HORIZONTAL);
	drawLog(22,6,plane.HORIZONTAL);
	drawLog(23,7,plane.HORIZONTAL);
	
	
	drawTree(16,18);
	drawTree(17,18);
	drawTree(18,18);
	drawLog(20,18,plane.VERTICAL);
	drawLog(21,19,plane.VERTICAL);
	drawStones(12, 12, plane.HORIZONTAL, 9);
	drawStones(12, 12, plane.VERTICAL, 5);
	drawTree(15,14);
	drawTree(16,14);
	drawTree(17,14);
	drawTree(20,14);
	
	drawLog(2,17,plane.HORIZONTAL);
	drawTree(2,18);
	drawTree(4,11);
	
	drawStones(17, 0, plane.VERTICAL, 6);
	drawStones(17, 6, plane.HORIZONTAL, 4);
	
	// randomly grow some patches of grass
	for(int i=0; i<widthTile; i++){
		for(int j=0; j<heightTile; j++){
			if (mapArray[i][j].type == Tile.tileType.CLEAR) {
				
				if ((Math.random()*100) > 96) { mapArray[i][j].grass = new Grass();
				mapArray[i][j].type = Tile.tileType.GRASS;
				mapArray[i][j].grass.maxAmount = (int) (Math.random()*100);
				mapArray[i][j].grass.maxAmount *= 100;
				mapArray[i][j].grass.amount = (int) (Math.random()*mapArray[i][j].grass.maxAmount);
				mapArray[i][j].grass.growthInterval = 400;
				mapArray[i][j].grass.growthAmount = mapArray[i][j].grass.maxAmount / 10;
				//System.out.println("grass grows! at "+i+" "+j+ " amount: "+mapArray[i][j].grass.amount);	
				} }
	   }
	}// end out for	
	
	// Create pathfinder
	pathfinder = new AStarPathFinder(this, 100, false);
}

public HashMap<Point, Tile>  getNonClears() {
	HashMap<Point, Tile> holder = new HashMap<Point, Tile>();
	for(int i=0; i<widthTile; i++){
		for(int j=0; j<heightTile; j++){
			if (mapArray[i][j].type != Tile.tileType.CLEAR) {
				holder.put(new Point(i*this.tileSizePx,j*this.tileSizePx), mapArray[i][j]);
			}
	   }	
	}// end out for	
	return holder;
}


public boolean isClear(int x, int y) {
	return mapArray[x][y].type == Tile.tileType.CLEAR
			|| mapArray[x][y].type == Tile.tileType.GRASS;
}

public boolean isVacant(int x, int y) {
	return (isClear(x,y)&&(mapArray[x][y].currentSheep==null));
}

public boolean inBounds(int x, int y) {
	return (x >= 0 && x < widthTile && y >= 0 && y < heightTile);
}
public boolean inBounds(Point p) {
	return inBounds(p.x, p.y);
}

public Tile getTile(int x, int y) {
	return mapArray[x][y];
	
}

private void drawLog(int x, int y, plane dir) throws SlickException {
	Point position = new Point(x,y);
	if (dir == plane.HORIZONTAL) {
		mapArray[x+1][y] = new Tile(Tile.tileType.LOGHTOP, position); 
		mapArray[x][y] = new Tile(Tile.tileType.LOGHBOTTOM, position);
	} else {  // plane.VERTICAL
		mapArray[x][y] = new Tile(Tile.tileType.LOGVTOP, position); 
		mapArray[x][y+1] = new Tile(Tile.tileType.LOGVBOTTOM, position);
	}	
}

private void drawStones (int x, int y, plane dir, int length) throws SlickException {
	Point position = new Point(x,y);
	if (dir == plane.HORIZONTAL) { 
		for(int j=0; j<length; j++){
			mapArray[x+j][y] = new Tile(Tile.tileType.ROCK, position);
	   }
		
	} else {
		for(int j=0; j<length; j++){
			mapArray[x][y+j] = new Tile(Tile.tileType.ROCK, position);
	   }
	}
	
	
}

private void drawTree (int x, int y) throws SlickException {
	Point position = new Point(x,y);
	mapArray[x][y] = new Tile(Tile.tileType.TREE, position);
	mapArray[x][y+1] = new Tile(Tile.tileType.TREEPART, position);
	mapArray[x][y+2] = new Tile(Tile.tileType.TREEPART, position);
	mapArray[x+1][y] = new Tile(Tile.tileType.TREEPART, position);
	mapArray[x+1][y+1] = new Tile(Tile.tileType.TREEPART, position);
	mapArray[x+1][y+2] = new Tile(Tile.tileType.TREEPART, position);
}


	@Override
	public boolean blocked(PathFindingContext context, int tx, int ty) {
		return !isClear(tx, ty);
	}
	
	@Override
	public float getCost(PathFindingContext context, int tx, int ty) {
		return 1;
	}
	
	@Override
	public int getHeightInTiles() {
		return heightTile;
	}
	
	@Override
	public int getWidthInTiles() {
		return widthTile;
	}
	
	@Override
	public void pathFinderVisited(int x, int y) {
		// Nothing to see here
	}
	
	public void update() {
		for (int x = 0; x < mapArray.length; ++x)
			for (int y = 0; y < mapArray[0].length; ++y)
				mapArray[x][y].update();
	}

	public void newPow(int x, int y) {

		
	this.dumbSprites.add(new dumbSprite(x, y, pow, 10000));
	}
	
}// end class
