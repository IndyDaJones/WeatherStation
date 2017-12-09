import java.util.logging.Level;
import java.util.logging.Logger;

public class WeatherStation {
	private static final Logger log = Logger.getLogger( WeatherStation.class.getName() );
	
	static String logFile;
	static String sys;
	static String location;
	static int cycle;
	static DBHandler db;
	static DeviceHandler dev;
	public static void main(String[] args) {
		/**
		 * Load WeatherService properties
		 */
		ServiceProperty props = new ServiceProperty();
		String logFile = props.getServiceProperty("LogFileDest");
		sys = props.getServiceProperty("System");
		location = props.getServiceProperty("Location");
		cycle = Integer.parseInt(props.getServiceProperty("Cycletime"));
				
		//initLogging();
		log.log(Level.INFO,"call initDB");
		initDB();
		log.log(Level.INFO,"Database initiated");
		log.log(Level.INFO,"call initDevice");
		initDevice();
		log.log(Level.INFO,"Device initiated");
		log.log(Level.INFO,"call startWeather()");
		startService();
		log.log(Level.INFO,"Database closed!");
		db.closeDB();
		log.log(Level.INFO,"end main");

	}
	/**
	 * This method creates a new database instance
	 */
	private static void initDB(){
		db = new DBHandler();
	}
	/**
	 * This method creates a new database instance
	 */
	private static void initDevice(){
		dev = new DeviceHandler();
	}
	/**
	 * This method creates a new database instance
	 */
	private static void startService(){
		dev = new DeviceHandler();
	}
}
