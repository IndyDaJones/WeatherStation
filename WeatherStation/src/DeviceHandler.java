import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeviceHandler {
	private static String topic = "DeviceHandler  ";
	public static DeviceState state;
	/** Background Thread, der periodisch die Datenbank ueberprueft */
	private ServiceDeviceThread deviceThread;
	AM2302 device = new AM2302 ();
	
	
	public DeviceHandler() {
		setDeviceState(DeviceState.START);
	}
	public static void setDeviceState(DeviceState State) {
		log("Device state changed to : <"+State+">");
		state = State;
	}
	public void getDataFromAM2302() {
		device.getDatafromdevice();
	}
	public double getTemperature() {
		return device.getTemperature();
	}
	
	public double getHumidity() {
		return device.getHumidity();
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
	/**
	 * Timer-Thread starten.
	 * 
	 * @param display Displayobjekt
	 */
	public void startDeviceService() {
		setDeviceState(DeviceState.CONNECT);
		deviceThread = new ServiceDeviceThread(Devices.AM2302);
		deviceThread.start();
	}
	
	/**
	 * Timer-Thread stoppen.
	 */
	public void stopDeviceService() {
		if (deviceThread != null) {
			deviceThread.interrupt();
			deviceThread = null;
		}
	}
}
