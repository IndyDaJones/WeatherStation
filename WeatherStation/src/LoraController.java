import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoraController {
	private static String topic = "LoraController   ";
	private ServiceLoraThread loraThread;
	
	public LoraController() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * 
	 * @param status
	 * @param temperature
	 * @param humidity
	 * @param create_dt
	 * @param create_by
	 * @param update_dt
	 * @param update_by
	 */
	public void sendData(String devicename, String status, double temperature, double humidity, Date createTimestamp){
		SimpleDateFormat format = new SimpleDateFormat(ServiceProperties.getDateFormat());
		String command = "";
		
		command = "sudo /home/lora/lmic_pi-master/examples/thethingsnetwork-send-v1/thethingsnetwork-send-v1 \"T"+temperature+";H"+humidity+";"+format.format(createTimestamp)+"\"";
		
		if(executeCommand(command)){
			log("data successfully read from device!");
		}else{
			logWarn("no data read from device!");
		}
	}
	public boolean executeCommand(String command){
		log("execute "+command);
		String loraReturn;
		Runtime r = Runtime.getRuntime();
		Process p;
		try {
			p = r.exec(command);
			p.waitFor();
			BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((loraReturn = b.readLine()) != null) {
				log("result from device <"+loraReturn+">");
			}
			b.close();
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
	/**
	 * Timer-Thread starten.
	 * 
	 * @param display Displayobjekt
	 */
	public void startLoraService() {
		
		loraThread = new ServiceLoraThread();
		loraThread.start();
	}
	
	/**
	 * Timer-Thread stoppen.
	 */
	public void stopLoraService() {
		if (loraThread != null) {
			
			loraThread.interrupt();
			loraThread = null;
		}
	}
}
