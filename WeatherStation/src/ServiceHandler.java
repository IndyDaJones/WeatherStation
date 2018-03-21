import java.util.logging.Level;
import java.util.logging.Logger;

public class ServiceHandler {
	private static String topic = "ServiceHandler ";
	DeviceHandler dev;
	DBHandler db;
	public ServiceHandler() {
		dev = new DeviceHandler();
		db = new DBHandler();
	}
	public void startService() {
		log("Start Devices");
		dev.startDevices(db);
	}
	public String getLogFilePath() {
		return dev.getLogfilePath();
	}
	/**
	 * Loggt die uebergebene Meldung.
	 * 
	 * @param msg	Logmeldung
	 **/
	private static void log(String msg) {
		WeatherStation.logInfo(topic, msg);
	}
	/**
	 * Loggt die uebergebene Meldung.
	 * 
	 * @param msg	Logmeldung
	 **/
	private static void logWarn(String msg) {
		WeatherStation.logWarn(topic, msg);
	}
	/**
	 * Loggt die uebergebene Fehlermeldung.
	 * 
	 * @param msg	Logmeldung
	 **/
	private static void logError(String msg, Throwable thro) {
		WeatherStation.logError(topic, msg, thro);
	}
	/**
	 * Loggt die uebergebene Fehlermeldung.
	 * 
	 * @param msg	Logmeldung
	 **/
	private static void logError(String msg) {
		WeatherStation.logError(topic, msg);
	}
}
