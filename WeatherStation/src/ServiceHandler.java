import java.util.logging.Level;
import java.util.logging.Logger;

public class ServiceHandler {
	private static final Logger log = Logger.getLogger( WeatherStation.class.getName() );
	DeviceHandler dev;
	DBHandler db;
	public ServiceHandler() {
		dev = new DeviceHandler();
		db = new DBHandler();
	}
	public void startService() {
		log.log(Level.INFO,"Start Devices");
		dev.startDevices(db);
	}
	public String getLogFilePath() {
		return dev.getLogfilePath();
	}
	
}
