import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServiceProperties {
	/**
	 * Instanz der Properties
	 */
	private static ServiceProperties instance;
	private static String file;
	private static String dbms;
	private static String databaseServer;
	private static String databaseName;
	private static int databasePort;
	private static String databaseUsername;
	private static String databasePassword;
	private static int DevCycletime;
	private static int DbCycletime;
	private static String source;
	private static String CommandSrc;
	private static String CommandScpt;
	private static int am2302;
	private static int gpio;
	private static String logDir;
	
	/**
	 * Database management system
	 * @return dbms
	 */
	public static String getDbms() {
		return dbms;
	}
	/**
	 * Database Server
	 * @return databaseServer
	 */
	public static String getDatabaseServer() {
		return databaseServer;
	}
	/**
	 * Database Name
	 * @return databaseName
	 */
	public static String getDatabaseName() {
		return databaseName;
	}
	/**
	 * Database Port
	 * @return databasePort
	 */
	public static int getDatabasePort() {
		return databasePort;
	}
	/**
	 * Database Username
	 * @return databaseUsername
	 */
	public static String getDatabaseUsername() {
		return databaseUsername;
	}
	/**
	 * Database Password
	 * @return databasePassword
	 */
	public static String getDatabasePassword() {
		return databasePassword;
	}
	/**
	 * DB Cycletime
	 * @return Cycletime
	 */
	public static int getDBCycleTime() {
		return 5000;
	}
	/**
	 * Device Cycletime
	 * @return Cycletime
	 */
	public static int getDeviceCycleTime() {
		return DevCycletime;
	}
	/**
	 * Device Cycletime
	 * @return Cycletime
	 */
	public static int getDbCycleTime() {
		return DbCycletime;
	}
	
	/**
	 * Device Source
	 * @return source
	 */
	public static String getDeviceSource() {
		return source;
	}
	/**
	 * Command Srource
	 * @return CommandSrc
	 */
	public static String getCommandSrc() {
		return CommandSrc;
	}	
	/**
	 * Device Source
	 * @return source
	 */
	public static String getCommandScpt() {
		return CommandScpt;
	}
	/**
	 * Device 
	 * @return device
	 */
	public static int getAm2302Device() {
		return am2302;
	}
	/**
	 * Device Source
	 * @return source
	 */
	public static int getGPIO() {
		return gpio;
	}
	/**
	 * Device Source
	 * @return source
	 */
	public static String getLogDir() {
		return logDir;
	}

	
	/**
	 * Property instanz 
	 */
	private Properties properties = new Properties();
	
	/**
	 * Constructor which loads properties from given file.
	 * 
	 * @param file	file name
	 */
	public ServiceProperties(String file) {
		if (instance != null)
			throw new IllegalStateException("FriwilogProperties can only be initialized once");
		
		instance = this;
		this.file = file;
		
		load();
		
		dbms = instance.getString("database.dbms");
		databaseServer = instance.getString("database.server");
		databasePort = instance.getInt("database.port");
		databaseName = instance.getString("database.name");
		databaseUsername = instance.getString("database.username");
		databasePassword = instance.getString("database.password");
		logDir = instance.getString("service.logDir");
		DevCycletime = instance.getInt("device.cycletime");
		DbCycletime = instance.getInt("database.cycletime");
		source = instance.getString("service.source");
		CommandSrc = instance.getString("device.commandSrc");
		CommandScpt = instance.getString("device.commandScpt");
		am2302 = instance.getInt("device.AM2302");
		gpio = instance.getInt("device.gpio");
	}
	/**
	 * Load properties from file.
	 * @throws Exception 
	 */
	private void load()  {
		InputStream is = null;
		try {	
			is = new FileInputStream(file);
			properties.load(is);
		}
		catch (IOException e) {
			System.out.println("Property file " + file + " could not be loaded "+ e.getLocalizedMessage());
		}
		finally {
			try {
				if (is != null)
					is.close();
			}
			catch (Exception e) {
				// exception ignored
			}
		}
	}
	
	
	/**
	 * Returns string value for given property. If no property with this
	 * name is found, a FriwilogException is thrown.
	 * 
	 * @param key name of property to look for
	 * @return value of given property
	 */
	public String getString(String key) {
		String s = properties.getProperty(key);
		if (s == null)
			System.out.println("Property " + key + " not found in file " + file);
		return s;
	}

	
	/**
	 * Returns string value for given property. If no property with this
	 * name is found, the default value is returned.
	 * 
	 * @param key name of property to look for
	 * @param defaultValue	default value of property
	 * @return value of given property
	 */
	public String getString(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	
	/**
	 * Returns integer value for given property. If no property with this
	 * name is found or the value is not a valid integer, a FriwilogException is thrown.
	 * 
	 * @param key name of property to look for
	 * @return value of given property
	 */
	public int getInt(String key) {
		String s = getString(key);
		try {
			return Integer.parseInt(s);
		}
		catch (NumberFormatException e) {
			System.out.println("Value '" + s + "' of property " + key + " in file " + file + " is not a valid integer");
			return -1;
		}
	}
}
