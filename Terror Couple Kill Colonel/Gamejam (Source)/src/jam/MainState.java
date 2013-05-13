package jam;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;


public class MainState extends BasicGameState {
	
	public static MainState instance;
	
	public static int stepSize = 5;
	
	public static Random random = new Random();
	
	List<Sheep> sheep = new ArrayList<Sheep>();
	List<Player> players = new ArrayList<Player>();
	boolean showGrid = false;
	boolean showVisited = false;
	
	Image background;
	
	public Map map;
	
	public UI ui;
	
	
	public long lastTick;
	public long currentStep = -1;

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		
		Player player1 = new Player();
		Player player2 = new Player();
		Player player3 = new Player();
		players.add(player1);
		players.add(player2);
		
		
		instance = this;
		
		map = new Map();
		
		List<Integer> z = new ArrayList<Integer>();
		z.remove(new Integer(20));
		ui = new UI();
	
		sheep.add(new Sheep(24, 5, player1));
		
		sheep.add(new Sheep(20, 8, player2));
		sheep.add(new Viking(22, 8, player3));
		sheep.add(new Viking(23, 8, player3));
		for (int i = 0; i < random.nextInt(25); ++i) {
			int x = random.nextInt(map.widthTile);
			int y = random.nextInt(map.heightTile);
			
			if (map.isVacant(x, y))
				sheep.add(new Sheep(x,y,players.get(random.nextInt(players.size()))));
		}
		
		
		//Testing
		MainState.instance.map.newPow(22, 22);
		
		// Load sprites
		HashMap<Sheep.Direction, Image> sheepSprites = new HashMap<Sheep.Direction, Image>();
		HashMap<Sheep.Direction, Image> sheepSpritesAlt = new HashMap<Sheep.Direction, Image>();
		//HashMap<Sheep.Direction, Image> vikingSprites = new HashMap<Sheep.Direction, Image>();
		HashMap<Sheep.Direction, Image> vikingSprites = sheepSprites;
		sheepSprites.put(Sheep.Direction.UP, new Image("resources/sheep/sheepUp.png").getScaledCopy(map.tileSizePx, map.tileSizePx));
		sheepSprites.put(Sheep.Direction.DOWN, new Image("resources/sheep/sheepDown.png").getScaledCopy(map.tileSizePx, map.tileSizePx));
		sheepSprites.put(Sheep.Direction.LEFT, new Image("resources/sheep/sheepLeft.png").getScaledCopy(map.tileSizePx, map.tileSizePx));
		sheepSprites.put(Sheep.Direction.RIGHT, new Image("resources/sheep/sheepRight.png").getScaledCopy(map.tileSizePx, map.tileSizePx));
		sheepSpritesAlt.put(Sheep.Direction.UP, new Image("resources/sheep2/sheepUp.png").getScaledCopy(map.tileSizePx, map.tileSizePx));
		sheepSpritesAlt.put(Sheep.Direction.DOWN, new Image("resources/sheep2/sheepDown.png").getScaledCopy(map.tileSizePx, map.tileSizePx));
		sheepSpritesAlt.put(Sheep.Direction.LEFT, new Image("resources/sheep2/sheepLeft.png").getScaledCopy(map.tileSizePx, map.tileSizePx));
		sheepSpritesAlt.put(Sheep.Direction.RIGHT, new Image("resources/sheep2/sheepRight.png").getScaledCopy(map.tileSizePx, map.tileSizePx));
		Image powSprite = new Image("resources/pow.png");
		
		for (Sheep s : sheep){
			if (s.owner == player1)
				s.sprites = sheepSprites;
			else if (s.owner == player2)
				s.sprites = sheepSpritesAlt;
			else
				s.sprites = vikingSprites;
			s.powSprite = powSprite;
			}
		
		background = new Image("resources/grassback.png");
			
	}

	@Override
	public void render(GameContainer container, StateBasedGame game,
			org.newdawn.slick.Graphics g) throws SlickException {
		// TODO Auto-generated method stub
		long currentTicks = System.currentTimeMillis();
		
		ui.drawUIONCE(container, game, g);
		ui.drawUI(container, game, g);
		
		// Background
		background.draw(0, 0);
		
		// gridLines
		if (showGrid) {
				for(int i=0; i<map.widthPx; i= i+map.tileSizePx){ 
				g.drawLine(i, 0, i, map.heightPx);}
				for(int i=0; i<map.heightPx; i= i+map.tileSizePx){ 
				g.drawLine(0, i, map.widthPx, i);}
			
		}
		
		// Visited list
		if (showVisited)
			for (int i = 0; i < sheep.get(0).visitedTiles.length; ++i)
				for (int j = 0; j < sheep.get(0).visitedTiles[0].length; ++j)
					if (sheep.get(0).visitedTiles[i][j])
						g.drawLine(map.tileSizePx * i, map.tileSizePx * j,
								map.tileSizePx * (i + 1), map.tileSizePx * (j + 1));
		
		
		HashMap<Point, Tile> tileMap = map.getNonClears();
		
		for (Point p : tileMap.keySet()) {
			if (tileMap.get(p).getImage() != null) {
			tileMap.get(p).getImage().draw(p.x, p.y);}
		}
		
		// Foreground objects
		// Sheep
		for (Sheep s : sheep) {
			Point spritePos = s.spritePos(currentTicks);
			int spriteX = spritePos.x; int spriteY = spritePos.y;
			s.getImage().draw(spriteX, spriteY);
		}
		
		for (dumbSprite s : map.dumbSprites) {
			
			int drawAtX = (s.tilex);
			int drawAtY = (s.tiley);
			System.out.println("DRAWING POW! at "+drawAtX+" "+drawAtY);
			
			
			s.icon.draw(drawAtX, drawAtY);
		}
		
				
	}//end render

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {

		long currentTick = System.currentTimeMillis();
		
		if (currentTick - lastTick > stepSize) {
			// Take game step
			lastTick = currentTick;
			gameStep();
		}
		
		if (container.getInput().isKeyDown(Input.KEY_Y)) {
			ui.tryUpCuriousity();
		}
		if (container.getInput().isKeyDown(Input.KEY_H)) {
			ui.tryDownCuriousity();
		}	
		if (container.getInput().isKeyDown(Input.KEY_U)) {
			ui.tryUpAggression();
		}	
		if (container.getInput().isKeyDown(Input.KEY_J)) {
			ui.tryDownAggression();
		}	
		if (container.getInput().isKeyDown(Input.KEY_I)) {
			ui.tryUpGluttony();
		}	
		if (container.getInput().isKeyDown(Input.KEY_K)) {
			ui.tryDownGluttony();
		}
		
		if (container.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
			int mousex = container.getInput().getMouseX();
			int mousey = container.getInput().getMouseY();
			
			if (mousex > 0 && mousex <= map.widthPx) {
				if (mousey > 0 && mousey <= map.heightPx)  {
					int tilex = mousex / map.tileSizePx;
					int tiley = mousey / map.tileSizePx;
					ui.selectedTile = map.getTile(tilex, tiley);
					
					/*System.out.println("selected tile: "+ui.selectedTile.toString());
					System.out.println("selected sheep: "+ui.selectedTile.currentSheep);*/
					if (ui.selectedTile.currentSheep != null) {
						ui.selectedSheep = ui.selectedTile.currentSheep;
					}
				}
			}
			
			
		}
		
	}
	
	void gameStep() {
		//remove any dumbsprites that are too old
		ArrayList<dumbSprite> temp = (ArrayList<dumbSprite>) map.dumbSprites.clone();
		for (dumbSprite s : temp) {
			s.tick();
			if (s.ticks>s.tickLife)
				map.dumbSprites.remove(s);
		}
		
		
	
		
		++currentStep;
		// Handle dead sheep
		List<Sheep> delStack = new ArrayList<Sheep>();
		
		
		
		for (Sheep s : sheep)
			if (s.dead) {
				delStack.add(s);
				// TODO: Corpse goes here
			}
		
		for (Sheep s : delStack)
			sheep.remove(s);
		
		// Update sheep
		for (Sheep s : sheep)
				s.update();
				
		// Update map
		map.update();
		
		
	} // end game step

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
}
