/**
 * Stati, die bei der Verarbeitung auftreten koennen.
 * 
 * @author Jonas Nyffeler
 * @version $Id: ServiceState.java 2387 2009-09-08 13:34:44Z tma $
 */
public enum ServiceState {
	/** Service is starting up*/
	STARTUP,
	/** Service is running*/
	RUN,
	/** Service is stopping*/
	SHUTDOWN,
	/** Service is stopped*/
	STOP;
}