

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnectionProperty {
	private Properties properties = new Properties();
	public DBConnectionProperty(){
		BufferedInputStream stream;
		try {
			//Mac
			//stream = new BufferedInputStream(new FileInputStream("/Users/Jonas/git/WeatherServer/WeatherStation/src/database.property"));
			//Linux
			//stream = new BufferedInputStream(new FileInputStream("config/database.property"));
			//Windows
			stream = new BufferedInputStream(new FileInputStream("C:\\Users\\j.nyffeler\\git\\WeatherStation\\WeatherStation\\config\\database.property"));
			properties.load(stream);
			stream.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getLocalizedMessage());
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
		}
	}
	public String getDBProperty(String key){
		return properties.getProperty(key);
	}
}
