public class ServiceHandler {
	private static String topic = "ServiceHandler ";
	public static ServiceState state;
	DeviceHandler dev;
	DBHandler db;
	public ServiceHandler() {
		setServiceState(ServiceState.STARTUP);
		initDatabase();
		initDevice();
	}
	public void initDatabase(){
		db = new DBHandler();
	}
	public DBHandler getDatabaseHandler() {
		return db;
	}
	public DeviceHandler getDeviceHandler() {
		return dev;
	}
	public void initDevice(){
		dev = new DeviceHandler();
	}
	public void startService() {
		log("Start Devices");
		setServiceState(ServiceState.RUN);
		db.startDBService();
		dev.startDeviceService();
		
	}
	public ServiceState getServiceState() {
		return state;
	}
	public static void setServiceState(ServiceState State) {
		log("Service state changed to : <"+State+">");
		state = State;
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
