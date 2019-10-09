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
	
}
