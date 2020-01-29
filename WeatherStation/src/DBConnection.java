

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class DBConnection {
	private static String topic = "DBConnection     ";
	private String dbms;
	private String serverName;
	private String databaseName;
	private String portNumber;
	private String userName;
	private Connection con;
	static Connection connection = null;
	/** Gecachte Datenbankverbindung wurde benutzt */
	static boolean connectionReused = false;
	
	public DBConnection (){
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
				logError(e.getLocalizedMessage(), e);
			}
		}
	}
	
    /**
	 * Gibt eine neu erstellte oder vom letzten Aufruf gecachte Verbindung zurueck.
	 * Die zurueckgegebene Verbindung ist im Auto-Commit Modus.
	 * Die Funktion ist synchronized, damit beim Aufstarten von einem anderen Thread
	 * Die Verbindung aufgebaut werden kann.
	 * 
	 * @return Neu erstellte oder gecachte Verbindung zur Datenbank
	 * @throws SQLException	wenn ein Datenbankfehler auftritt
	 */
	public static synchronized Connection getConnection() throws SQLException {
		// Gecachte Verbindung pruefen
		if (connection != null) {
 			if (connection.isClosed()) {
				connection = null;
	 			log("checking existing connection - is down");
			} else {
	 			//log("checking existing connection - seems allright");
			}
		}
		
		// Bei Bedarf neue Verbindung aufbauen
		if (connection != null) {
			connectionReused = true;
			
		} else {
			connectionReused = false;
			log("creating new connection...");
			connection = createConnection();
		}
		return connection;
	}
	
	/**
	 * Create connection to Oracle database using the information found in
	 * the application properties.
	 *
	 * @return			Created JDBC connection
	 * @throws SQLException	in case of database failure
	 */
	public static Connection createConnection() throws SQLException {
		String dbms = ServiceProperties.getDbms(); 
		String server = ServiceProperties.getDatabaseServer();
		String port = Integer.toString(ServiceProperties.getDatabasePort());
		String database = ServiceProperties.getDatabaseName();
		String username = ServiceProperties.getDatabaseUsername();
		String password = ServiceProperties.getDatabasePassword();
		
		return createConnection(dbms, server, port, database, username, password);
	}
	
	
	/**
	 * Create connection to Oracle database using the thin client driver.
	 * For this driver of JDBC type 4, no local installation of Oracle software is needed.
	 * The connection is in auto-commit mode.
	 * 
	 * @param server	IP number or name of server
	 * @param port		Port number to use (typically 1521)
	 * @param database	Name of databaes
	 * @param username	Username to use for login
	 * @param password	Password to use for login
	 * @return			Created JDBC connection
	 * @throws SQLException	in case of database failure
	 */
    public static Connection createConnection(String dbms, String server, String port,
    	String database, String username, String password) throws SQLException {
    	if (connection != null){
			return connection;
		}
		else{
			Connection conn = null;
		    Properties connectionProps = new Properties();
		    connectionProps.put("user", username);
		    connectionProps.put("password", password);
		    log("call getConnection("+connectionProps.toString()+")");
		    if (dbms.equals("mysql")) {
		    	try{
			        conn = (Connection)DriverManager.getConnection(
			                   "jdbc:" + dbms + "://" +
			                		   server +
			                   ":" + port + "/" +
			                   database,
			                   connectionProps);
			        log("Connected to database");
		    	}catch (SQLException e){
		    		logError(e.getLocalizedMessage(), e);
		    	} 
		    }else {
		    	conn = null;
		    	logError("Database unknown");
		    }
		    return conn;
		}
    }
	/**
	 * This method is used to insert the measured temperature and humidity into the database given
	 * @param temp
	 * @param humidity
	 * @throws SQLException 
	 */
	public void insertData(Connection con, String status, String device, double temp, double humidity, Timestamp createTimestamp) throws SQLException{
		try{
			// create a sql date object so we can use it in our INSERT statement
			Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
						
		    // the mysql insert statement
		    String query = " insert into Weather (status, temperature, humidity, create_dt, create_by, update_dt, update_by)"
		      + " values (?, ?, ?, ?, ?, ?, ?)";
		 
		    // create the mysql insert preparedstatement
		    java.sql.PreparedStatement preparedStmt = con.prepareStatement(query);
		    preparedStmt.setString (1, status);
		    preparedStmt.setDouble (2, temp);
		    preparedStmt.setDouble (3, humidity);
		    preparedStmt.setTimestamp(4, createTimestamp);
		    preparedStmt.setString(5, device );
		    preparedStmt.setTimestamp   (6, currentTimestamp);
		    preparedStmt.setString(7, System.getProperty("user.name") );
		    log("Execute query: "+preparedStmt.toString());
		    // execute the preparedstatement
		    preparedStmt.execute();
		    log("Query execution took: "+preparedStmt.getQueryTimeout() +"seconds");  
		} catch (SQLException e) {
			logError(e.getLocalizedMessage(),e);
			throw e;
		}
		}
	/**
	 * This method is used to insert the measured temperature and humidity into the database given
	 * @param temp
	 * @param humidity
	 * @throws SQLException 
	 */
	public void insertLargeData(Connection con, String status, Double temperature, Double humidity, Timestamp create_dt, String create_by, Timestamp update_dt, String update_by) throws SQLException{
		try{
			log("Start method inserData(<"+con.toString()+">,<"+status+">,<"+temperature.toString()+">,<"+humidity.toString()+">,<"+create_dt.toString()+">,<"+create_by+">,<"+update_dt.toString()+">,<"+update_by+">)");
			// create a sql date object so we can use it in our INSERT statement
			Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
			
		    // the mysql insert statement
		    String query = " insert into Weather (status, temperature, humidity, create_dt, create_by, update_dt, update_by)"
		      + " values (?, ?, ?, ?, ?, ?, ?)";
		 
		    // create the mysql insert preparedstatement
		    java.sql.PreparedStatement preparedStmt = con.prepareStatement(query);
		    preparedStmt.setString (1, "ACH");
		    preparedStmt.setDouble (2, temperature);
		    preparedStmt.setDouble (3, humidity);
		    preparedStmt.setTimestamp   (4, create_dt);
		    preparedStmt.setString(5, create_by );
		    preparedStmt.setTimestamp   (6, update_dt);
		    preparedStmt.setString(7, update_by );
		    preparedStmt.setTimestamp   (8, currentTimestamp);
		    log("Execute query: "+preparedStmt.toString());
		    // execute the preparedstatement
		    preparedStmt.execute();
		    log("Query execution took: "+preparedStmt.getQueryTimeout() +"seconds");  
		} catch (SQLException e) {
			logError(e.getLocalizedMessage(),e);
			throw e;
		}
		}
		public ResultSet getResultSetFromDb(DBConnection connection) throws SQLException{
			Connection con = connection.getConnection();
			String dbName = connection.getDatabaseName();
			
			Statement stmt = null;
		    String query = "select status, temperature, humidity, create_dt, create_by, update_dt, update_by from "+dbName+".Weather";
		    try {
		        stmt = (Statement) con.createStatement();
		        ResultSet rs = stmt.executeQuery(query);
		        return rs;
		    } catch (SQLException e ) {
		    	logError(e.getLocalizedMessage(),e);
		    	throw e;
		    } finally {
		    	log("Statement closed");
		        if (stmt != null) { stmt.close(); }
		    }
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
}
