package jam;

import java.awt.Color;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class UI {

Image backDrop;
Image bronze;

public volatile Integer sheeppoints = 13;
public volatile Integer aggression = 1;
public volatile Integer curiousity = 1;
public volatile Integer gluttony = 1;

public volatile long lastCommand = 0;

public volatile Tile selectedTile;
public volatile Sheep selectedSheep;


public UI() throws SlickException {
	backDrop = new Image("resources/sheep/woolTex.jpg");
	bronze = new Image("resources/UI/bronze.png").getScaledCopy(150,50);
}

public void drawUIONCE(GameContainer container, StateBasedGame game,
		org.newdawn.slick.Graphics g) {
	
	org.newdawn.slick.Color orig = g.getColor();
	org.newdawn.slick.Color c = new org.newdawn.slick.Color(255);
	
	g.drawImage(getImage(), 864, 0);
	g.drawImage(bronze,869,150);
	g.setColor(c);
	g.drawString("Curiosity", 880, 151);
	g.drawString("+'Y'/ -'H'", 920, 170);
	g.drawImage(bronze,869,210);
	g.drawString("Aggression", 880, 211);
	g.drawString("+'U'/ -'J'", 920, 230);
	g.drawImage(bronze,869,270);
	g.drawString("Gluttony", 880, 271);
	g.drawString("+'I'/ -'K'", 920, 290);
	g.drawImage(bronze,869,330);
	g.drawString("Remaining", 880, 331);
	g.drawImage(bronze,869,390);
	g.drawImage(bronze,869,440);
	g.drawImage(bronze,869,490);
	g.drawString("Sheep:", 880, 391);
	
	g.setColor(orig);
}
public void drawUI(GameContainer container, StateBasedGame game,
		org.newdawn.slick.Graphics g) {
	org.newdawn.slick.Color orig = g.getColor();
	org.newdawn.slick.Color c = new org.newdawn.slick.Color(205);
	g.setColor(c);
	g.drawString(curiousity.toString(), 890, 171);
	g.drawString(aggression.toString(), 890, 231);
	g.drawString(gluttony.toString(), 890, 291);
	g.drawString(sheeppoints.toString(), 890, 351);
	if (this.selectedSheep != null){
	g.drawString("HP: "+Integer.toString(this.selectedSheep.health), 890, 426);
	
	if (this.selectedSheep.type == Sheep.Type.SHEEP) {
		g.drawString("Player: "+Integer.toString(this.selectedSheep.owner.playerNumber), 890, 411);
		g.drawString("Agg: "+Float.toString(this.selectedSheep.aggression), 890, 441);
		g.drawString("Glutt: "+Float.toString(this.selectedSheep.gluttony), 890, 456);
		g.drawString("Cur: "+Float.toString(this.selectedSheep.curiosity), 890, 471);
		g.drawString("Hunger: "+Float.toString(this.selectedSheep.nutrition), 890, 486);
	} else {
		g.drawString(((Viking)this.selectedSheep).name, 890, 411);
	}
	
	String planString;
	if (this.selectedSheep.plan == null)
		// This can legitimately happen between game steps
		planString = "Mind: Thinking";
	else
		planString = "Mind: "+(this.selectedSheep.plan.toString());
	g.drawString(planString, 890, 501);
	g.drawString("Position: "+selectedSheep.currentX+","+selectedSheep.currentY, 890, 516);
	
	
	
	
	
	}
	g.setColor(orig);
}

private Image getImage() {
Image returner =  backDrop.copy();
	
	return returner;
		
}

private boolean checkKeyRepeat() {
	
	if (System.currentTimeMillis() - lastCommand > 250) {
		lastCommand = System.currentTimeMillis();
		return true;// prevents over keying due to quick update 
	} else {return false;}
	
}

public void tryUpAggression() {
	if (checkKeyRepeat()) {
		if (sheeppoints > 0) {
			sheeppoints--;
			aggression++;
		}	
	}	
}
public void tryUpCuriousity() {
	if (checkKeyRepeat()) {
		if (sheeppoints > 0) {
			sheeppoints--;
			curiousity++;
		}	
	}	
}
public void tryUpGluttony() {
	if (checkKeyRepeat()) {
		if (sheeppoints > 0) {
			sheeppoints--;
			gluttony++;
		}	
	}	
}

public void tryDownAggression() {
	if (checkKeyRepeat()) {
		if (aggression > 0) {
			sheeppoints++;
			aggression--;
		}	
	}	
}
public void tryDownCuriousity() {
	if (checkKeyRepeat()) {
		if (curiousity > 0) {
			sheeppoints++;
			curiousity--;
		}	
	}	
}
public void tryDownGluttony() {
	if (checkKeyRepeat()) {
		if (gluttony > 0) {
			sheeppoints++;
			gluttony--;
		}	
	}	
}


	
}
