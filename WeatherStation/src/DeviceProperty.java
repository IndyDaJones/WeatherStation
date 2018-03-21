import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeviceProperty {
	private Properties properties = new Properties();
	public DeviceProperty(){
		BufferedInputStream stream;
		try {
			//Mac
			//stream = new BufferedInputStream(new FileInputStream("/Users/Jonas/git/WeatherServer/WeatherStation/src/device.property"));
			//Linux
			//stream = new BufferedInputStream(new FileInputStream("/home/jonas/workspace/WeatherService/src/device.property"));
			stream = new BufferedInputStream(new FileInputStream("config/device.property"));
			//Windows
			//stream = new BufferedInputStream(new FileInputStream("C:\\Users\\j.nyffeler\\git\\WeatherStation\\WeatherStation\\config\\device.property"));
			properties.load(stream);
			stream.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getLocalizedMessage());
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
		}
	}
	public String getDeviceProperty(String key){
		return properties.getProperty(key);
	}
}
