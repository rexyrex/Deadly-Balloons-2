package Utils;
import java.time.LocalTime;

public class StringUtils {

	//seconds to time format hh:mm:ss
	public static String getTime(long elapsedTime) {
		long seconds = elapsedTime / 1000; // Maybe no need to divide if the input is in seconds
		LocalTime timeOfDay = LocalTime.ofSecondOfDay(seconds);
		String time = timeOfDay.toString();
		return time;
	}
	
}
