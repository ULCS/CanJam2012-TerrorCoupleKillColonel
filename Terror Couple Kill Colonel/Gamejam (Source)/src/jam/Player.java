package jam;

public class Player {

static Integer count = 0;	

public volatile int playerNumber;



Player() {
count++;
playerNumber = (int) count.longValue();	
	
}


} // end class
