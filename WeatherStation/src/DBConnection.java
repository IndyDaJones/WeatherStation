

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class DBConnection {
	private static final Logger log = Logger.getLogger( WeatherStation.class.getName() );
	private String dbms;
	private String serverName;
	private String databaseName;
	private String portNumber;
	private Connection con;
	DBConnectionProperty props;
	Connection conn;
	
	public DBConnection (){
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
	/**
	 * This method is used to insert the measured temperature and humidity into the database given
	 * @param temp
	 * @param humidity
	 * @throws SQLException 
	 */
	public void insertData(Connection con, double temp, double humidity) throws SQLException{
		try{
			// create a sql date object so we can use it in our INSERT statement
			Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
			
		    // the mysql insert statement
		    String query = " insert into Weather (status, temperature, humidity, create_dt, create_by, update_dt, update_by)"
		      + " values (?, ?, ?, ?, ?, ?, ?)";
		 
		    // create the mysql insert preparedstatement
		    java.sql.PreparedStatement preparedStmt = con.prepareStatement(query);
		    preparedStmt.setString (1, "NOK");
		    preparedStmt.setDouble (2, temp);
		    preparedStmt.setDouble (3, humidity);
		    preparedStmt.setTimestamp   (4, currentTimestamp);
		    preparedStmt.setString(5, DBConnection.class.getName() );
		    preparedStmt.setTimestamp   (6, currentTimestamp);
		    preparedStmt.setString(7, DBConnection.class.getName() );
		    log.log(Level.INFO,"Execute query: "+preparedStmt.toString());
		    // execute the preparedstatement
		    preparedStmt.execute();
		    log.log(Level.INFO,"Query execution took: "+preparedStmt.getQueryTimeout() +"seconds");  
		} catch (SQLException e) {
			log.log(Level.SEVERE,e.getLocalizedMessage());
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
			log.log(Level.INFO,"Start method inserData(<"+con.toString()+">,<"+status+">,<"+temperature.toString()+">,<"+humidity.toString()+">,<"+create_dt.toString()+">,<"+create_by+">,<"+update_dt.toString()+">,<"+update_by+">)");
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
		    log.log(Level.INFO,"Execute query: "+preparedStmt.toString());
		    // execute the preparedstatement
		    preparedStmt.execute();
		    log.log(Level.INFO,"Query execution took: "+preparedStmt.getQueryTimeout() +"seconds");  
		} catch (SQLException e) {
			log.log(Level.SEVERE,e.getLocalizedMessage());
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
		    	log.log(Level.SEVERE,e.getLocalizedMessage());
		    	throw e;
		    } finally {
		    	log.log(Level.INFO,"Statement closed");
		        if (stmt != null) { stmt.close(); }
		    }
		}
}
