import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mysql.jdbc.Connection;

public class DBHandler {
	DBConnection connection;
	
	public DBHandler() {
		connection = new DBConnection();
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
	public void insertData(String devicename, String status, double temperature, double humidity){
		try{
			log("call insertTempHumidity");
			connection.insertData(connection.getConnection(),status ,devicename , temperature, humidity);	
		}catch (SQLException e){
			logError(e.getLocalizedMessage(),e);
		}	
	}
 public void closeDB() {
	 
 }
 	/**
	 * Loggt die uebergebene Meldung.
	 * 
	 * @param msg	Logmeldung
	 **/
	private static void log(String msg) {
		WeatherStation.logInfo("DBHandler", msg);
	}
	/**
	 * Loggt die uebergebene Meldung.
	 * 
	 * @param msg	Logmeldung
	 **/
	private static void logWarn(String msg) {
		WeatherStation.logWarn("DBHandler", msg);
	}
	/**
	 * Loggt die uebergebene Fehlermeldung.
	 * 
	 * @param msg	Logmeldung
	 **/
	private static void logError(String msg, Throwable thro) {
		WeatherStation.logError("DBHandler", msg, thro);
	}
	/**
	 * Loggt die uebergebene Fehlermeldung.
	 * 
	 * @param msg	Logmeldung
	 **/
	private static void logError(String msg) {
		WeatherStation.logError("DBHandler", msg);
	}
}
