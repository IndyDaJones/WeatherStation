import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AM2302Handler {
	//private String device_return = "Temp=3.9*  Humidity=7.3%";
	private String device_return;
	double temperature;
	double humidity;
	private static String topic = "AM2302Handler    ";
	
	/**
	 * Konstruktor
	 */
	public AM2302Handler () {
	}
	
	public void getDatafromdevice(){
		String command = "sudo "+ServiceProperties.getCommandSrc()+"/"+ServiceProperties.getCommandScpt()+" "+ServiceProperties.getAm2302Device()+" "+ServiceProperties.getGPIO();
		
		if(executeCommand(command)){
			log("data successfully read from device!");
		}else{
			logWarn("no data read from device!");
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
				device_return = tempDevRet;
			}
			b.close();
			log("call parseTemperature() <"+this.device_return+">");
			parseTemperature();
			log("call parseHumidity() <"+this.device_return+">");
			parseHumidity();
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
	 * 
	 */
	private void parseTemperature(){
		int startTemperature = device_return.indexOf("T");
		int startHumidity = device_return.indexOf("H");
		String Temperature = device_return.substring(startTemperature, startHumidity);
		int startResult = Temperature.indexOf("=")+1;
		int endResult = Temperature.indexOf("*");
		log("parseTemperature "+Temperature.substring(startResult, endResult));
		temperature = Double.parseDouble(Temperature.substring(startResult, endResult));
	}
	/**
	 * 
	 */
	private void parseHumidity(){
		int startTemperature = device_return.indexOf("T");
		int startHumidity = device_return.indexOf("H");
		String Humidity = device_return.substring(startHumidity, this.device_return.length());
		int startResult = Humidity.indexOf("=")+1;
		int endResult = Humidity.indexOf("%");
		log("parseHumidity "+Humidity.substring(startResult, endResult));
		humidity = Double.parseDouble(Humidity.substring(startResult, endResult));
	}
	/**
	 * 
	 * @return
	 */
	public double getTemperature(){
		return temperature;
	}
	/**
	 * 
	 * @return
	 */
	public double getHumidity(){
		return humidity;
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
}
