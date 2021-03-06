/**
 * Timer Thread.
 * Oeffnet die Datenbankverbindung und ueberprueft danach periodisch verschiedene Punkte.
 * Der Thread wird vom FriwilogController beim Start der Applikation gestartet und beim Schliessen
 * der Applikation beendet.
 * 
 * @author Thomas Mauch
 * @version $Id: FriwilogTimerThread.java 2389 2009-10-12 10:24:44Z elsaboot $
 */
class ServiceDbThread extends Thread {
    /**
	 * pollzyklus
	 */
	private static long wait;
	/**
	 * Next Variable
	 */
	private volatile long next = 0;
	
	private static String topic = "DatabaseThread   ";
	/**
	 * Constructor.
	 * 
	 * @param display	Display, welcher fuer die Ausfuehrung von SWT-Funktionen benoetigt wird
	 */
	public ServiceDbThread() {
		wait = Long.parseLong(Integer.toString(ServiceProperties.getDbCycleTime()));
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
		log("Background thread starts");

		final ServiceController handler = WeatherStation.getServiceHandler();
		
		// Oeffne Datenbankverbindung
		try {
			handler.getDatabaseHandler().getConnection();
		}
		catch (Exception e) {
			logError("Opening database connection failed");
		}
		
		// Background periodisch ausfuehren
		while (!handler.getServiceState().equals(ServiceState.SHUTDOWN)) {
			try {
				long next = getNext();
				if (next > 0) {
					long now = System.currentTimeMillis();
					if (next > now) {
						log("Background check deferred " + (next-now));
						sleep(next-now);
						continue;
					}
				}
				//TODO:
				log("Check buffer for DB");
				while (ServiceBuffer.getBufferSize()>0) {
					AM2302 data = ServiceBuffer.getBufferElement();
					handler.getDatabaseHandler().insertData(data.getDevice(), "DB", data.getTemperature(), data.getHumidity(), data.getCreateDate());
					
				}
				// Konfigurierte Zeit warten
				setNext(0);
			    sleep(wait);
			}
			catch (InterruptedException e) {
				logError("Database error! No data added!");
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
