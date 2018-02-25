import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WeatherStation {
	private static final Logger log = Logger.getLogger( WeatherStation.class.getName() );
	static String logFile;
	static ServiceHandler service;
	
	public static void main(String[] args) {
		Handler handler;
		try {
			/**
			 * Load WeatherService properties
			 */
			service = new ServiceHandler();
			logFile = service.getLogFilePath();
			handler = new FileHandler( logFile +"WeatherStation.txt" );
			log.addHandler(handler);
			log.log(Level.INFO,"File location of Service:"+logFile);
			log.log(Level.INFO,"call startWeather()");
			startService();
			log.log(Level.INFO,"end main");
		} catch (SecurityException | IOException e) {
			System.out.println("Exception catched "+ e.getLocalizedMessage());
			log.log(Level.SEVERE,"Exception catched "+ e.getLocalizedMessage());
		}

	}


	/**
	 * This method creates a new database instance
	 */
	private static void startService(){
		service.startService();
	}
}
