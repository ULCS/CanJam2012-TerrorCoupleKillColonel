package jam;


import java.awt.Image;
import java.util.Calendar;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class MainGame extends StateBasedGame {

	Calendar now = Calendar.getInstance();
	
	public final int tickTime = 5000; //ms
	
	Image land = null;
	Sheep ant1;
	int currentSecs = 0;
	int currentMillis= 0;
	
	public MainGame() {
		super("Norse Sheep");
	}
	
	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		// TODO Auto-generated method stub

		
		
	}
	
	static public void main(String[] args) {
		String osName = System.getProperty("os.name").toUpperCase();
		String sep = System.getProperty("file.separator");
		if(osName.indexOf("WIN") > 0){
			System.out.println("Windows detected");
			System.setProperty("org.lwjgl.librarypath",System.getProperty("user.dir")+sep+"lib"+sep+"Natives"+sep+"Win32");
		}else{
			System.out.println("Not Windows detected");
			System.setProperty("org.lwjgl.librarypath",System.getProperty("user.dir")+sep+"lib"+sep+"Natives"+sep+"OSX");
		}
		
		try { 
			MainGame game = new MainGame();
			game.addState(new MainState());
			game.enterState(0);
			AppGameContainer app = new AppGameContainer(game, 1024, 768, false);
			app.start(); 
		}
		catch (SlickException e) {
			e.printStackTrace();
			}
	}
	

}