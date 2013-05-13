package jam;

import org.newdawn.slick.Image;

public class dumbSprite {

	public int tilex;
	public int tiley;
	public Image icon;
	public int ticks;
	public int tickLife;
	
public dumbSprite(int x, int y, Image ico, int ticklife) {
	this.tilex = x;
	this.tiley = y;
	this.icon = ico;
	
}
	
public void tick() {
	ticks++;
}
}//end class
