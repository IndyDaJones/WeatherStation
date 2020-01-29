public class ServiceController {
	private static String topic = "ServiceController";
	public static ServiceState state;
	DeviceController dev;
	DBController db;
	LoraController lora;
	/**
	 * Constructor
	 */
	public ServiceController() {
		setServiceState(ServiceState.STARTUP);
		initDatabase();
		initDevice();
		initLora();
	}
	public void initDatabase(){
		db = new DBController();
	}
	public DBController getDatabaseHandler() {
		return db;
	}
	public DeviceController getDeviceHandler() {
		return dev;
	}
	public LoraController getLoraHandler() {
		return lora;
	}
	public void initDevice(){
		dev = new DeviceController();
	}
	public void initLora(){
		lora = new LoraController();
	}
	public void startService() {
		log("Start Devices");
		setServiceState(ServiceState.RUN);
		db.startDBService();
		dev.startDeviceService();
		lora.startLoraService();
		
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
