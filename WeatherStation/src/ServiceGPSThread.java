import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServiceGPSThread extends Thread{

    /**
	 * pollzyklus
	 */
	private static long wait;
	/**
	 * Next Variable
	 */
	private volatile long next = 0;
	
	private static String topic = "GPSThread        ";
	
	private static Devices device;
	/**
	 * Constructor.
	 * 
	 * @param display	Display, welcher fuer die Ausfuehrung von SWT-Funktionen benoetigt wird
	 */
	public ServiceGPSThread(Devices Devices) {
		device = Devices;
		if (device.equals(Devices.AM2302)){
			wait = Long.parseLong(Integer.toString(ServiceProperties.getGPSCycleTime()));
		}else {
			logError("Device cycletime property not set");
			wait = 10000;
		}
		
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
		WeatherStation.logInfo("Background thread starts");

		final ServiceController handler = WeatherStation.getServiceHandler();
			
		// Background periodisch ausfuehren
		while (!handler.getServiceState().equals(ServiceState.STOP)) {
			try {
				long next = getNext();
				if (next > 0) {
					long now = System.currentTimeMillis();
					if (next > now) {
						WeatherStation.logInfo("Background check deferred " + (next-now));
						sleep(next-now);
						continue;
					}
				}
				//TODO:
				
				handler.getDeviceHandler().setDeviceState(DeviceState.REQUEST);

				handler.getDeviceHandler().getDataFromAM2302();
				//sleep(wait);
				handler.getDeviceHandler().setDeviceState(DeviceState.FEEDBACK);
				AM2302 data = new AM2302(handler.getDeviceHandler().getTemperature(), handler.getDeviceHandler().getHumidity());
				try {
					log("Data element added to buffer <"+data.getDevice()+">");
					ServiceBuffer.addBufferElement(data);
				} catch (InterruptedException e) {
					logError("Unable to add data element to buffer");
					//TODO: Temporary storage of element which are not added to the main buffer
				}
				// Konfigurierte Zeit warten
				setNext(0);
			    sleep(wait);
			}
			catch (InterruptedException e) {
			}
		}
		WeatherStation.logInfo("Background thread ends");
	}	
	
	public void getDatafromdevice(){
		// Starts the GPS RS232 communicaton
		String command = " sudo cat /dev/ttyS0";
		
		if(executeCommand(command)){
			log("data successfully read from gps device!");
		}else{
			logWarn("no data read from gps device!");
		}
	}
	public boolean executeCommand(String command){
		log("execute "+command);
		String tempDevRet = "";
		Runtime r = Runtime.getRuntime();
		Process p;
		try {
			p = r.exec(command);
			p.waitFor();
			BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((tempDevRet = b.readLine()) != null) {
				log("result from device <"+tempDevRet+">");

			}
			b.close();
			log("call parseTemperature() <"+"result"+">");
			log("call parseHumidity() <"+"result"+">");

		} catch (IOException e) {
			logError(e.getMessage(), e);
			return false;
		}catch (InterruptedException e) {
			logError(e.getMessage(), e);
			return false;
		}
		return true;
	}
	/**
	 * Loggt die uebergebene Meldung.
	 * 
	 * @param msg	Logmeldung
	 **/
	private static void logWarn(String msg) {
		WeatherStation.logWarn(topic, msg);
	}
	
	private static void log(String msg) {
		WeatherStation.logInfo(topic, msg);
	}
	private static void logError(String msg) {
		WeatherStation.logError(topic, msg);
	}
	/**
	 * Loggt die uebergebene Fehlermeldung.
	 * 
	 * @param msg	Logmeldung
	 **/
	private static void logError(String msg, Throwable thro) {
		WeatherStation.logError(topic, msg, thro);
	}
}
