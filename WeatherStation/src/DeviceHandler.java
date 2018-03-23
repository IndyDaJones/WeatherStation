import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeviceHandler {
	private static String topic = "DeviceHandler  ";
	/** Background Thread, der periodisch die Datenbank ueberprueft */
	private ServiceDeviceThread deviceThread;
	AM2302 device = new AM2302 ();
	
	
	public DeviceHandler() {
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
			long Cycletime = Long.parseLong(Integer.toString(ServiceProperties.getDeviceCycleTime()));
			executor.scheduleAtFixedRate(periodicTask, 0, Cycletime, TimeUnit.SECONDS);
	}
	private void launchWhileAM2302(DBHandler db) {
		
		while (true) {			    	
					device.getDatafromdevice();
			    	db.insertData("AM2302", "OK", device.getTemperature(), device.getHumidity());
			    	try {
			    		
						Thread.sleep(Long.parseLong(Integer.toString(ServiceProperties.getDeviceCycleTime())));
					} catch (InterruptedException e) {
						logError(e.getLocalizedMessage(),e);
					}
			    }
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
		deviceThread = new ServiceDeviceThread();
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
