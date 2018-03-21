import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeviceHandler {
	DeviceProperty props;
	public DeviceHandler() {
		props = new DeviceProperty();
	}
	public String getLogfilePath() {
		return props.getDeviceProperty("logFile").toString();
	}
	public void startDevices(DBHandler db) {
		launchWhileAM2302(db);
		//launchAM2302(db);
	}
	private void launchAM2302(DBHandler db) {
		AM2302 device = new AM2302 ();
		ScheduledExecutorService executor =
			    Executors.newSingleThreadScheduledExecutor();

			Runnable periodicTask = new Runnable() {
			    public void run() {
			        // Invoke method(s) to do the work
			    	device.getDatafromdevice();
			    	db.insertData("AM2302","OK", device.getTemperature(), device.getHumidity());
			    }
			};
			long Cycletime = Long.parseLong(props.getDeviceProperty("Cycletime"));
			executor.scheduleAtFixedRate(periodicTask, 0, Cycletime, TimeUnit.SECONDS);
	}
	private void launchWhileAM2302(DBHandler db) {
		AM2302 device = new AM2302 ();
		while (true) {			    	
					device.getDatafromdevice();
			    	db.insertData("AM2302", "OK", device.getTemperature(), device.getHumidity());
			    	try {
			    		
						Thread.sleep(Long.parseLong(props.getDeviceProperty("Cycletime")));
					} catch (InterruptedException e) {
						logError(e.getLocalizedMessage(),e);
					}
			    }
	}
	/**
	 * Loggt die uebergebene Meldung.
	 * 
	 * @param msg	Logmeldung
	 **/
	private static void log(String msg) {
		WeatherStation.logInfo("DeviceHandler", msg);
	}
	/**
	 * Loggt die uebergebene Meldung.
	 * 
	 * @param msg	Logmeldung
	 **/
	private static void logWarn(String msg) {
		WeatherStation.logWarn("DeviceHandler", msg);
	}
	/**
	 * Loggt die uebergebene Fehlermeldung.
	 * 
	 * @param msg	Logmeldung
	 **/
	private static void logError(String msg, Throwable thro) {
		WeatherStation.logError("DeviceHandler", msg, thro);
	}
	/**
	 * Loggt die uebergebene Fehlermeldung.
	 * 
	 * @param msg	Logmeldung
	 **/
	private static void logError(String msg) {
		WeatherStation.logError("DeviceHandler", msg);
	}
}
