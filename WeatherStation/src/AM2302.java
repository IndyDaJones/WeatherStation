import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AM2302 {
	private static final Logger log = Logger.getLogger( WeatherStation.class.getName() );
	private String device_return = "Temp=3.9*  Humidity=7.3%";
	double temperature;
	double humidity;
	DeviceProperty props;
	
	/**
	 * Konstruktor
	 */
	public AM2302 () {
		props = new DeviceProperty();

	}

		
	public void getDatafromdevice(){
		String command = "sudo "+props.getDeviceProperty("CommandSrc")+"/"+props.getDeviceProperty("CommandScpt").toString()+" "+props.getDeviceProperty("Device").toString()+" "+props.getDeviceProperty("GPIO").toString();
		/*
		if(executeCommand(command)){
			log.log(Level.INFO,"data successfully read from device!");
		}else{
			log.log(Level.WARNING,"no data read from device!");
		}
		*/
	}
	public boolean executeCommand(String command){
		log.log(Level.INFO,"execute "+command);
		String tempDevRet = "";
		Runtime r = Runtime.getRuntime();
		Process p;
		try {
			p = r.exec(command);
			p.waitFor();
			BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((tempDevRet = b.readLine()) != null) {
				log.log(Level.INFO,"result from device <"+tempDevRet+">");
				device_return = tempDevRet;
			}
			b.close();
			log.log(Level.INFO,"call parseTemperature() <"+this.device_return+">");
			parseTemperature();
			log.log(Level.INFO,"call parseHumidity() <"+this.device_return+">");
			parseHumidity();
		} catch (IOException e) {
			log.log(Level.SEVERE,e.getLocalizedMessage());
			return false;
		}catch (InterruptedException e) {
			log.log(Level.SEVERE,e.getLocalizedMessage());
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
		log.log(Level.INFO,"parseTemperature "+Temperature.substring(startResult, endResult));
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
		log.log(Level.INFO,"parseHumidity "+Humidity.substring(startResult, endResult));
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
}
