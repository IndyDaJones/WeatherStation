import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mysql.jdbc.Connection;

public class DBHandler {
	private static final Logger log = Logger.getLogger( WeatherStation.class.getName() );
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
			log.log(Level.INFO,"call insertTempHumidity");
			connection.insertData(connection.getConnection(),status ,devicename , temperature, humidity);	
		}catch (SQLException e){
			log.log(Level.SEVERE,e.getLocalizedMessage());
		}	
	}
 public void closeDB() {
	 
 }
}
