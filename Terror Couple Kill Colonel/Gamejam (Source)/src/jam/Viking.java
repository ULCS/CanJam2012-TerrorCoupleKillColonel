package jam;

import jam.think.Plan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Viking extends Sheep {
	
	String name;
	
	static String[] givenNames = {"Arne", "Bjorn", "Eirik", "Geir", "Gunnar", "Harald", "Hakon", "Inge" , "Ivar", "Knut",
		"Leif", "Magnus", "Olaf", "Rolf", "Sigurd", "Snorri", "Steinar", "Torstein", "Trygve", "Ulf", "Valdemar",
		"Vidar", "Yngve"};
	
	public Viking(int x, int y, Player owner) {
		super(x,y,owner);
		
		type = Type.VIKING;
		
		metabolism = 0;
		
		name = generateName();
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
	
	public String generateName() {
		//if (MainState.random.nextInt(2) > 0) {
			return givenNames[MainState.random.nextInt(givenNames.length)] + " " + givenNames[MainState.random.nextInt(givenNames.length)] + "sson";
		/*}
		return givenNames[MainState.random.nextInt(givenNames.length)] + " ";*/
	}
}
