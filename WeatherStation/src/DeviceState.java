/**
 * Stati, die bei der Verarbeitung auftreten koennen.
 * 
 * @author Jonas Nyffeler
 * @version $Id: ServiceState.java 2387 2009-09-08 13:34:44Z tma $
 */
public enum DeviceState {
	/** Service connects to device*/
	START,
	/** Service connects to device*/
	CONNECT,
	/** Service sends data request to device*/
	REQUEST,
	/** Service gets feedback from device*/
	FEEDBACK,
	/** Service disconnects from device*/
	DISCONNET,
	/** Service connects to device*/
	STOP;
}