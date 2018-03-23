public class WeatherStation {
	static ServiceHandler service;;
	/**
	 * Instanz des Loggers
	 **/
    private static ServiceLogger logger;
	
	public static void main(String[] args) {
		try {
			/**
			 * Load WeatherService properties
			 */
			initProperties();
			System.out.println("Start ");
			initLogging();
			logInfo("call Start Service()");
			startService();
			logInfo("end main");
		} catch (Exception e) {
			System.out.println("Exception catched "+ e.getLocalizedMessage());
			logInfo("Exception catched "+ e.getLocalizedMessage());
		}

	}
	/**
	 * Initialisiert Properties.
	 */
	static void initProperties() {
		new ServiceProperties("config/service.property");
	}
	/**
	 * Initialisiert Loggging.
	 */
	static void initLogging() {
		final String logDir = ServiceProperties.getLogDir();
		final String user = System.getProperty("user.name");
		logger = new ServiceLogger(logDir, user);
	}
	/**
	 * Beendet Loggging.
	 */
	static void closeLogging() {
		logger.close();
	}
	/**
	 * Gibt ServiceHandler der Applikation zurueck.
	 * 
	 * @return Applikations-Handler
	 */
	static public ServiceHandler getServiceHandler() {
		return service;
	}
	/**
	 * Gibt Logger der Applikation zurueck.
	 * 
	 * @return Applikations-Logger
	 */
	static public ServiceLogger getLogger() {
		return logger;
	}
	/**
	 * Erzeugt einen Logeintrag mit Level INFO.
	 * 
	 * @param msg	Text
	 */
	static public void logInfo(final String msg) {
		logInfo("Main", msg);
	}
	
	
	/**
	 * Erzeugt einen Logeintrag mit Level WARNING.
	 * 
	 * @param msg	Text	
	 */
	static public void logWarn(final String msg) {
		logWarn("Main", msg);
	}
	
	
	/**
	 * Erzeugt einen Logeintrag mit Level WARNING.
	 * 
	 * @param msg	Text
	 * @param thro		Throwable
	 */
	static public void logError(final String msg, final Throwable thro) {
		logError("Main", msg, thro);
	}
	
	/**
	 * Erzeugt einen Logeintrag mit Level WARNING.
	 * 
	 * @param msg	Text
	 */
	static public void logServerError(final String msg) {
		logError("Server", msg);
	}
	/**
	 * Erzeugt einen Logeintrag mit Level INFO.
	 * 
	 * @param topic	Thema
	 * @param msg	Text
	 */
	static public void logInfo(final String topic, final String msg) {
		logger.info(topic, msg);
	}
	/**
	 * Erzeugt einen Logeintrag mit Level WARNING.
	 * 
	 * @param topic	Thema
	 * @param msg	Text	
	 */
	static public void logWarn(final String topic, final String msg) {
		logger.warn(topic, msg);
	}
	/**
	 * Erzeugt einen Logeintrag mit Level ERROR.
	 * 
	 * @param topic	Thema
	 * @param msg	Text
	 * @param thro		Throwable
	 */
	static public void logError(final String topic, final String msg, final Throwable thro) {
		logger.error(topic, msg, thro);
	}
	/**
	 * Erzeugt einen Logeintrag mit Level ERROR.
	 * 
	 * @param topic	Thema
	 * @param msg	Text
	 */
	static public void logError(final String topic, final String msg) {
		logger.error(topic, msg);
	}

	/**
	 * This method creates a new database instance
	 */
	private static void startService(){
		service = new ServiceHandler();
		service.startService();
	}
}
