

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mysql.jdbc.Connection;

public class DBConnection {
	private static final Logger log = Logger.getLogger( WeatherStation.class.getName() );
	private String dbms;
	private String serverName;
	private String databaseName;
	private String portNumber;
	private Connection con;
	DBConnectionProperty props;
	
	public DBConnection (String system, String location){
		props = new DBConnectionProperty();
	}
	public String getDbms(){
		return this.dbms;
	}
	public String getServerName(){
		return this.serverName;
	}
	public String getDatabaseName(){
		return this.databaseName;
	}
	public void closeConnection(){
		if (this.con != null){
			try {
				this.con.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE,"call closeDB() "+e.getMessage());
			}
		}
	}
	public Connection getConnection() throws SQLException {
		if (this.con != null){
			return this.con;
		}
		else{
			Connection conn = null;
		    dbms = props.getDBProperty("dbms");
		    serverName = props.getDBProperty("Server");
		    databaseName = props.getDBProperty("Database");
		    portNumber = props.getDBProperty("Port");
		    
		    
		    Properties connectionProps = new Properties();
		    connectionProps.put("user", props.getDBProperty("user"));
		    connectionProps.put("password", props.getDBProperty("password"));
		    log.log(Level.INFO,"call getConnection("+connectionProps.toString()+")");
		    if (this.dbms.equals("mysql")) {
		    	try{
			        conn = (Connection)DriverManager.getConnection(
			                   "jdbc:" + dbms + "://" +
			                   serverName +
			                   ":" + portNumber + "/" +
			                   databaseName,
			                   connectionProps);
			        log.log(Level.INFO,"Connected to database");
		    	}catch (SQLException e){
		    		log.log(Level.SEVERE,"DBConnection error " +e);
		    	} 
		    }else {
		    	conn = null;
		    	log.log(Level.SEVERE,"Database unknown");
		    }
		    this.con = conn;
		    return conn;
		}
	}
}
