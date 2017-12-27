import java.util.logging.Level;
import java.util.logging.Logger;

public class WeatherStation {
	private static final Logger log = Logger.getLogger( WeatherStation.class.getName() );
	
	static ServiceHandler service;
	public static void main(String[] args) {
		/**
		 * Load WeatherService properties
		 */
		service = new ServiceHandler();
		log.log(Level.INFO,"call startWeather()");
		startService();
		log.log(Level.INFO,"end main");

	}


	/**
	 * This method creates a new database instance
	 */
	private static void startService(){
		service.startService();
	}
}
