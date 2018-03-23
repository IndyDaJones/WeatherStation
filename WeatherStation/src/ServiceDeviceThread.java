/**
 * Timer Thread.
 * Oeffnet die Datenbankverbindung und ueberprueft danach periodisch verschiedene Punkte.
 * Der Thread wird vom FriwilogController beim Start der Applikation gestartet und beim Schliessen
 * der Applikation beendet.
 * 
 * @author Thomas Mauch
 * @version $Id: FriwilogTimerThread.java 2389 2009-10-12 10:24:44Z elsaboot $
 */
class ServiceDeviceThread extends Thread {
    /**
	 * pollzyklus
	 */
	private static long wait;
	/**
	 * Next Variable
	 */
	private volatile long next = 0;
	
	private static String topic = "DeviceThread   ";
	
	private static Devices device;
	/**
	 * Constructor.
	 * 
	 * @param display	Display, welcher fuer die Ausfuehrung von SWT-Funktionen benoetigt wird
	 */
	public ServiceDeviceThread(Devices Devices) {
		device = Devices;
		if (device.equals(Devices.AM2302)){
			wait = Long.parseLong(Integer.toString(ServiceProperties.getDeviceCycleTime()));
		}else {
			logError("Device cycletime property not set");
			wait = 10000;
		}
		
	}
	
	
	/**
	 * Verschiebt den naechsten Lauf des Backgroundthreads auf den jetzt aktuelllen
	 * Zeitpunkt plus die konfigurierte Wartezeit.
	 *
	 */
	public void defer() {
		long now = System.currentTimeMillis();
		setNext(now + wait);
	}
	
	
	/**
	 * Setze Member next synchronisiert.
	 * @param next Counter
	 */
	 
	private synchronized void setNext(long next) {
		this.next = next;
	}
	
	
	/**
	 * Lese Member next synchronisiert.
	 * @return long next counter
	 */
	 
	private synchronized long getNext() {
		return next;
	}
	
	
	/**
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {	
		WeatherStation.logInfo("Background thread starts");

		final ServiceHandler handler = WeatherStation.getServiceHandler();
		
		// Oeffne Datenbankverbindung
		try {
			handler.getDatabaseHandler().getConnection();
		}
		catch (Exception e) {
			WeatherStation.logError("Opening database connection failed", e);
		}
		
		// Background periodisch ausfuehren
		while (!handler.getServiceState().equals(ServiceState.STOP)) {
			try {
				long next = getNext();
				if (next > 0) {
					long now = System.currentTimeMillis();
					if (next > now) {
						WeatherStation.logInfo("Background check deferred " + (next-now));
						sleep(next-now);
						continue;
					}
				}
				//TODO:
				handler.getDeviceHandler().setDeviceState(DeviceState.REQUEST);
				//log("Try to get Data from Device: handler.getDeviceHandler().getDataFromAM2302();");
				handler.getDeviceHandler().getDataFromAM2302();
				//sleep(wait);
				handler.getDeviceHandler().setDeviceState(DeviceState.FEEDBACK);
				//log("Try to insert data in Database: handler.getDatabaseHandler().insertData(\"AM2302\", \"OK\", handler.getDeviceHandler().getTemperature(), handler.getDeviceHandler().getHumidity())");
				handler.getDatabaseHandler().insertData("AM2302", "OK", handler.getDeviceHandler().getTemperature(), handler.getDeviceHandler().getHumidity());
				
				// Konfigurierte Zeit warten
				setNext(0);
			    sleep(wait);
			}
			catch (InterruptedException e) {
			}
		}
		WeatherStation.logInfo("Background thread ends");
	}	
	private static void log(String msg) {
		WeatherStation.logInfo(topic, msg);
	}
	private static void logError(String msg) {
		WeatherStation.logError(topic, msg);
	}
	
}
