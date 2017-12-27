

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnectionProperty {
	private static final Logger log = Logger.getLogger( WeatherStation.class.getName() );
	private Properties properties = new Properties();
	public DBConnectionProperty(){
		BufferedInputStream stream;
		try {
			//Mac
			//stream = new BufferedInputStream(new FileInputStream("/Users/Jonas/git/WeatherServer/WeatherStation/src/database.property"));
			//Linux
			//stream = new BufferedInputStream(new FileInputStream("/home/jonas/workspace/WeatherService/src/database.property"));
			//Windows
			stream = new BufferedInputStream(new FileInputStream("C:\\Users\\j.nyffeler\\git\\WeatherStation\\WeatherStation\\config\\database.property"));
			//stream = new BufferedInputStream(new FileInputStream("database.property"));
			log.log(Level.INFO,"call properties.load(stream)");
			properties.load(stream);
			stream.close();
		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE,e.getMessage());
		} catch (IOException e) {
			log.log(Level.SEVERE,e.getMessage());
		}
	}
	public String getDBProperty(String key){
		log.log(Level.INFO,"call properties.getProperty("+key+")");
		return properties.getProperty(key);
	}
}
