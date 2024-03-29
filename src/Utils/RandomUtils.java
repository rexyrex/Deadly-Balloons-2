package Utils;

public class RandomUtils {

	public static boolean runChance(double chance) {
		chance = chance/100;
		if(chance > Math.random()) {
			return true;
		}
		
		return false;
	}

	
	public static double[] getRandomOffset(double x, double y) {
		double[] offset = new double[2];
		x = x * (Math.random()*2 -1);
		y = y * (Math.random()*2 -1);
		offset[0] = x;
		offset[1] = y;
		return offset;
	}
	
	public static int[] getRandomDest(int width, int height) {
		int[] dest = new int[2];
		dest[0] = (int) (Math.random() * width);
		dest[1] = (int) (Math.random() * height);
		return dest;
	}
	
	public static int[] getRandomDestNoBorder(int width, int height) {
		int[] dest = new int[2];
		dest[0] = (int) (Math.random() * width * 8/10) + width/10;
		dest[1] = (int) (Math.random() * height* 8/10) + height/10;
		return dest;
	}
	
	public static double getPlusMinus(double original, double plus, double minus) {
		if(Math.random() > 0.5) {
			return original + Math.random() * plus;
		} else {
			return original - Math.random() * minus;
		}
	}
	
	public static double getPlusMinusPercentage(double original, double percentage) {
		return getPlusMinus(original, original*percentage, original*percentage);
	}
	
	public static double getNumBetween(double min, double max) {
		return Math.random() * (max - min) + min;
	}
	
}
