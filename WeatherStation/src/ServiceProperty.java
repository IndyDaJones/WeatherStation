import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ServiceProperty {
	Properties properties = new Properties();
	public ServiceProperty(){
		BufferedInputStream stream;
		try {
			//Mac
			//stream = new BufferedInputStream(new FileInputStream("/Users/Jonas/git/WeatherServer/WeatherStation/src/service.property"));
			//Linux
			//stream = new BufferedInputStream(new FileInputStream("/home/jonas/workspace/WeatherService/src/service.property"));
			//Windows
			stream = new BufferedInputStream(new FileInputStream("C:\\Users\\j.nyffeler\\git\\WeatherServer\\WeatherStation\\src\\service.property"));
			
			//Prod
			//stream = new BufferedInputStream(new FileInputStream("service.property"));
						properties.load(stream);
			stream.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	public String getServiceProperty(String key){
		return properties.getProperty(key);
	}
}
