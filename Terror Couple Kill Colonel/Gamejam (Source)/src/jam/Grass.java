package jam;

public class Grass {
	int maxAmount;
	public int amount;
	/// In steps
	int growthInterval;
	int growthAmount;
	
	long lastGrowthStep = 0;
	
	public void update() {
		
		if (MainState.instance.currentStep - lastGrowthStep == growthInterval) {
			// Suck it, Molyneux
			lastGrowthStep = MainState.instance.currentStep;
			
			amount += growthAmount;
			amount = Math.min(amount, maxAmount);
		}
		
		
	}
}
