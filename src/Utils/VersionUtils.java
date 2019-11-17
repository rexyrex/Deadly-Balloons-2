package Utils;

public class VersionUtils {
	public static String getLatestVersion() {
		return RestUtils.get("https://deadly-balloons-2.firebaseio.com/Version.json");
	}
	
	public static String getPatchNotes() {
		return RestUtils.get("https://deadly-balloons-2.firebaseio.com/VersionPatchNotes.json");
	}
}
