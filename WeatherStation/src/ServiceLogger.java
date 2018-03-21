import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.text.*;


/**
 * Hilfsklasse, um ein Logfile zu schreiben.
 * Die Logmeldungen werden in einem Buffer gespeichert und von dort asynchron
 * in dsa Logfile geschrieben.
 * Wir benutzen eine eigene Logklasse, da sowohl die Java Logging-Klassen
 * (java.util.logging.*) als auch log4j sich nicht von einem Fehler erholen,
 * wie er beim Schreiben auf ein Netzlaufwerk ueber WLAN auftreten kann.
 * 
 * @author Jonas Nyffeler
 * @version $Id: ServiceLogger.java 2389 2009-10-12 10:24:44Z $
 */
public class ServiceLogger {
	/**
	 * Loggingformat
	 * */
	private static final DateFormat logFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.US);
	/**
	 * Loggingformat
	 * */
	private static final DateFormat logFileFormat = new SimpleDateFormat("yyyyMMdd.HHmmss", Locale.US);
	
	/**
	 * Directory, in welches die Logfiles geschrieben werden
	 */
	private String logDir;
	/**
	 * FIFO-Queue mit LogWriters
	 */
	private LinkedList<LogWriter> logWriters = new LinkedList<LogWriter>();
	/**
	 * Background Thread, welche die Logeintraege ins Logfile schreibt
	 */
	private LogThread logThread;
	/**
	 * Lokales Logfile
	 */
	private LocalLogWriter localLogWriter;
	
	/**
	 * Loglevel 
	 **/
	private enum Level {
		/**
		 * Fehler 
		 **/
		ERROR,
		/**
		 * Warnung 
		 **/
		WARN,
		/**
		 * Information
		 **/
		INFO
	};
	
	
	/**
	 * Erzeugt ein Logfile fuer das angegebene Verzeichnis und den angegebenen Namen.
	 * 
	 * @param dir	Log-Directory
	 * @param name	Basis-Name des Logfiles
	 */
	public ServiceLogger(String dir, String name) {
		logDir = dir;
		
		openLocal();
		open(name);
		
		logThread = new LogThread();
		logThread.start();
	}
	

	/**
	 * Oeffnet ein neues Logfile mit dem angegebenem Namen.
	 * Wenn bereits ein Logfile offen ist, wird dieses zuerst geschlossen.
	 * 
	 * @param name	Basis-Name des Logfiles
	 */
	public void open(String name) {
		String oldLogFile = null;
		LogWriter oldLogWriter = null;
		if (logWriters.size() > 0)
			oldLogWriter = logWriters.getLast();
		if (oldLogWriter != null)
			oldLogFile = oldLogWriter.getLogFile();

		String newLogFile;
		LogWriter newLogWriter = new LogWriter(name);
		newLogFile = newLogWriter.getLogFile();
		
		if (oldLogWriter != null) {
			if (newLogFile == null)
				newLogFile = logDir + "/" + newLogWriter.getLogName() + "-*.log (not yet opened)";
			log(oldLogWriter, Level.INFO, "Log", "Continuing in new log file " + newLogFile, null);
			oldLogWriter.close();
			
			if (oldLogFile == null)
				oldLogFile = logDir + "/" + newLogWriter.getLogName() + "-*.log (not yet opened)";
			log(newLogWriter, Level.INFO, "Log", "Continuing from old log file " + oldLogFile, null);
		}
		
		logWriters.addLast(newLogWriter);
	}
	
	
	/**
	 * Schliesst das Logfile.
	 */
	public void close() {
		// Thread zum Stoppen auffordern
		Thread t = logThread;
		logThread = null;
		t.interrupt();
		
		closeLocal();
	}
	
		
	/**
	 * Schreibt die uebergebene Meldung mit Level INFO ins Logfile.
	 * 
	 * @param topic	Thema der Meldung
	 * @param msg	Meldung
	 */
	public void info(String topic, String msg) {
		log(Level.INFO, topic, msg, null);
	}
	
	
	/**
	 * Schreibt die uebergebene Meldung mit Level WARN ins Logfile.
	 * 
	 * @param topic	Thema der Meldung
	 * @param msg	Meldung
	 */
	public void warn(String topic, String msg) {
		log(Level.WARN, topic, msg, null);
	}
	
	
	/**
	 * Schreibt die uebergebene Meldung mit Level ERROR ins Logfile.
	 * 
	 * @param topic	Thema der Meldung
	 * @param msg	Meldung
	 * @param error	Throwable mit Fehlerinformationen
	 */
	public void error(String topic, String msg, Throwable error) {
		log(Level.ERROR, topic, msg, error);
	}
	
	/**
	 * Schreibt die uebergebene Meldung mit Level ERROR ins Logfile.
	 * 
	 * @param topic	Thema der Meldung
	 * @param msg	Meldung
	 */
	public void error(String topic, String msg) {
		log(Level.ERROR, topic, msg);
	}
	
	
	/**
	 * Schreibt Logmeldung in die Queue des aktuellen LogWriters.
	 * 
	 * @param level	Log level
	 * @param topic	Log topic
	 * @param msg	Log message
	 * @param t		Throwable to log or null
	 */
	private void log(Level level, String topic, String msg, Throwable t) {
		LogWriter logWriter = logWriters.getLast();
		log(logWriter, level, topic, msg, t);
	}
	
	/**
	 * Schreibt Logmeldung in die Queue des aktuellen LogWriters.
	 * 
	 * @param level	Log level
	 * @param topic	Log topic
	 * @param msg	Log message
	 */
	private void log(Level level, String topic, String msg) {
		LogWriter logWriter = logWriters.getLast();
		log(logWriter, level, topic, msg);
	}
	
	
	/**
	 * Schreibt Logmeldung in die Queue des angebenen LogWriters.
	 * 
	 * @param logWriter	LogWriter
	 * @param level		Log level
	 * @param topic		Log topic
	 * @param msg		Log message
	 * @param t			Throwable to log or null
	 */
	private void log(LogWriter logWriter, Level level, String topic, String msg, Throwable t) {
		// Wenn ein Fehler im Logging auftritt, wird versucht, diesen zu loggen.
		// Wenn dabei nochmals ein Fehler auftritt, geben wir in lokal auf dem Error Stream aus.
		try {
			String logEntry = formatLogEntry(level, topic, msg, t);
			logWriter.logEntry(logEntry);
		}
		catch (Throwable e1) {
			try {
				log(Level.ERROR, "Log", "Unexpected error in logging", e1);
			}
			catch (Throwable e2) {
				e2.printStackTrace();
			}
		}
	}
	
	/**
	 * Schreibt Logmeldung in die Queue des angebenen LogWriters.
	 * 
	 * @param logWriter	LogWriter
	 * @param level		Log level
	 * @param topic		Log topic
	 * @param msg		Log message
	 */
	private void log(LogWriter logWriter, Level level, String topic, String msg) {
		// Wenn ein Fehler im Logging auftritt, wird versucht, diesen zu loggen.
		// Wenn dabei nochmals ein Fehler auftritt, geben wir in lokal auf dem Error Stream aus.
		try {
			String logEntry = formatLogEntry(level, topic, msg);
			logWriter.logEntry(logEntry);
		}
		catch (Throwable e1) {
			try {
				log(Level.ERROR, "Log", "Unexpected error in logging", e1);
			}
			catch (Throwable e2) {
				e2.printStackTrace();
			}
		}
	}
	/**
	 * Formatiert eine Logmeldung fuer die Ausgabe ins Logfile.
	 * 
	 * @param level		Log level
	 * @param topic		Log topic
	 * @param msg		Log message
	 * @param t			Throwable to log or null
	 * @return			Formattierte Logmeldung
	 */
	private String formatLogEntry(Level level, String topic, String msg, Throwable t) {
		StringWriter buf = new StringWriter();
		PrintWriter out = new PrintWriter(buf);
		out.append(logFormat.format(new Date()));
		out.append('\t');
		out.append(topic);
		out.append('\t');
		out.append(level.toString());
		out.append('\t');
		out.append(msg);
		out.println();
		
		if (t != null) {
			t.printStackTrace(out);
		}
		
		return buf.toString();
	}
	
	/**
	 * Formatiert eine Logmeldung fuer die Ausgabe ins Logfile.
	 * 
	 * @param level		Log level
	 * @param topic		Log topic
	 * @param msg		Log message
	 * @return			Formattierte Logmeldung
	 */
	private String formatLogEntry(Level level, String topic, String msg) {
		StringWriter buf = new StringWriter();
		PrintWriter out = new PrintWriter(buf);
		out.append(logFormat.format(new Date()));
		out.append('\t');
		out.append(topic);
		out.append('\t');
		out.append(level.toString());
		out.append('\t');
		out.append(msg);
		out.println();
		return buf.toString();
	}
		
	
	/**
	 * Background-Thread, der Logeintraege ins Logfile schreibt
	 */
	class LogThread extends Thread {
		/**
		 * Startmethode für Thread
		 **/
		public void run() {
			while (true) {
				LogWriter logWriter = logWriters.getFirst();
				if (logWriter != null) {
					// Solange Logeintraege schreiben, bis es keine mehr hat oder
					// ein Fehler auftritt
					while (logWriter.writeLogEntry()) {
					}
					
					// Pruefen, ob der Logwriter geschlossen ist
					if (logWriter.isFlushed()) {
						logWriters.removeFirst();
						continue;
					}
				}
				
				// Pruefen, ob Logfile geschlossen werden soll
				if (logThread == null)
					break;
			}	
		}
	}
	
	
	/**
	 * Status eines LogWriters.
	 */
	enum State {
		/**
		 * offen
		 **/
		OPEN,
		/**
		 * geschlossen
		 **/
		CLOSED,
		/**
		 * gelöscht
		 **/
		FLUSHED
	}
	
	
	/**
	 * Die Klasse LogWriter schreibt Logmeldungen in einer Queue in das Logfile.
	 */
	class LogWriter {
		/** 
		 * Basis-Name des Logfiles
		 * */
		private String logName;
		/**
		* Vollstaendiger Pfad des Logfiles
		**/
		private String logFile;
		/**
		 * Writer fuer das Logfile
		 **/
		private Writer logFileWriter;
		/**
		 * Queue mit fixer Groesse (ueberzaehlige Elemente werden geloescht)
		 **/
		private ArrayBlockingQueue<String> logQueue = new ArrayBlockingQueue<String>(1000);
		/**
		 * Logmeldung, die als naechstes geschrieben werden muss
		 **/
		private volatile String logEntry;
		/**
		* Status des LogWriters
		**/
		private volatile State state;
		
		
		/**
		 * Constructor.
		 * 
		 * @param name Basis-Name des Logfiles
		 */
		public LogWriter(String name) {
			state = State.OPEN;
			logName = name;
			logFile = createLogFile();
		}
		
		
		/**
		 * Gibt den Basis-Namen des Logfiles zurueck.
		 * 
		 * @return Name des Logfiles
		 */
		public String getLogName() {
			return logName;
		}
		
		
		/**
		 * Gibt den Pfad des Logfiles oder null zurueck.
		 * 
		 * @return Name des Logfiles
		 */
		public String getLogFile() {
			return logFile;
		}
		
		
		/**
		 * Gibt an, ob der LogWriter geschlossen und alle Meldungen geschrieben wurden.
		 *  
		 * @return true, wenn der LogWriter geschlossen wurde
		 */
		public boolean isFlushed() {
			return state == State.FLUSHED;
		}
		
		
		/**
		 * Schliesst den LogWriter. In diesem Zustand duerfen keine neuen 
		 * Meldungen mehr an diesen LogWriter geschickt werden. Physikalisch
		 * wird der LogWriter  erst dann geschlossen, wenn alle Meldungen
		 * ins Logfile geschrieben wurden.
		 */
		public void close() {
			assert(state == State.OPEN);
			
			state = State.CLOSED;
		}
		

		/**
		 * Schreibt die uebergebene Meldung ins Logfile.
		 * Diese Funktion uebernimmt einen String, der ins Logfile geschrieben
		 * werden soll. Der String wird in einem Buffer abgelegt und erst von dort
		 * von einem Background-Thread wirklich ins Logfile geschrieben.
		 * 
		 * @param logEntry	Logmeldung
		 */
		public void logEntry(String logEntry) {
			assert(state == State.OPEN);
			
			putEntry(logEntry);
		}
		
		
		/**
		 * Diese Funktion uebernimmt einen String, der ins Logfile geschrieben
		 * werden soll. Der String wird in einem Buffer abgelegt und erst von dort
		 * von einem Background-Thread wirklich ins Logfile geschrieben.
		 * 
		 * @param logEntry	Logmeldung
		 */
		private synchronized void putEntry(String logEntry) {			
			// Write log entry local
			logLocal(logEntry);
			
			if (!logQueue.offer(logEntry)) {
				// Queue ist voll, erste Meldung lesen und verwerfen
				String dropEntry = logQueue.poll();
				logLocal("Queue full - dropping log entry: " + dropEntry);
				logQueue.offer(logEntry);
				
				// Dieser Eintrag wird im Logfile nicht chronologisch richtig sortiert sein,
				// dafuer steht er dort, wo Meldungen ausgelassen worden. Nach diesem Eintrag
				// sind wieder alle Meldungen ohne Auslassungen in chronologischer Reihenfolge
				// im Logfile.
				this.logEntry = formatLogEntry(Level.ERROR, "Log", "Queue full - dropping log entries", null);
				return;
			}
		}
		
		
		/**
		 * Schreibt eine Logmeldung aus dem Buffer ins Logfile.
		 * 
		 * @return	true, wenn Logmeldung erfolgreich ins File geschrieben wurde, sonst false
		 */
		private boolean writeLogEntry() {
			try {
				if (logEntry == null) {
					// Wir rufen poll() zuerst ohne Wartezeit auf. Der Grund ist, dass der
					// Aufruf von von poll() mit Wartezeit auch eine InterruptedException
					// ausloest (wenn der Thread.interrupt() aufgerufen wurde), wenn noch
					// Elemente in der Queue enthalten sind.
					logEntry = logQueue.poll();
					if (logEntry == null)
						logEntry = logQueue.poll(1000, TimeUnit.MILLISECONDS);
					
				} else {
					// Der letzte Versuch, diese Meldung zu schreiben, ist fehlgeschlagen.
					// Wir warten deshalb ein paar Sekunden, bevor wir es wieder probieren.
					Thread.sleep(10*1000);
				}
				
				if (logEntry == null) {
					if (state == State.CLOSED) {
						// Keine Meldung in der Queue vorhanden und das Logfile wurde bereits
						// geschlossen, so dass wir das Logfile als beendet markieren
						closeLogFile();	
						state = State.FLUSHED;
					}
					return false;
				}
				
				if (!writeLogEntry(logEntry))
					return false;
				
				logEntry = null;
				return true;
			}
			catch (InterruptedException e) {
				// Der Background-Thread wird unterbrochen, wenn der Benutzer das Logfile schliesst.
				// Dies passiert nicht, solange Meldungen zum Ausgeben vorhanden sind, sondern erst,
				// wenn wir auf Meldungen warten muessen.
				return false;
			}
		}
		
		
		/**
		 * Schreibt die uebergebene Meldung ins Logfile.
		 * 
		 * @param logEntry Logeintrag
		 * @return true, wenn die Meldung ins Logfile geschrieben werden konnte, sonst false
		 */
		private boolean writeLogEntry(String logEntry) {
			// Write log entry to file
			boolean opened = false;
			if (logFileWriter == null) {
				if (!openLogFile()) {
					// Log file could not be opened
					return false;
				}
				opened = true;
			}
			
			// Log file is open, so we try to write the log entry
			assert(logFileWriter != null);
			try {
				logFileWriter.write(logEntry);
				logFileWriter.flush();
				
				// Log entry has been written so we return
				return true;
			}
			catch (IOException e) {
				logFileWriter = null;
				
				log(Level.WARN, "Writing to log file " + logFile + " failed: " + e.getMessage());
				if (opened) {
					// File has already been newly opened, so we do not try any further
					return false;
				}
				
				if (!openLogFile()) {
					// Log file could not be opened
					return false;
				}
			}
			
			// Log file should be open, so we try to write the log entry
			try {
				logFileWriter.write(logEntry);
				logFileWriter.flush();
				return true;
			}
			catch (IOException e) {
				logFileWriter = null;
				
				log(Level.WARN, "Writing to log file " + logFile + " failed: " + e.getMessage());
				return false;
			}
			
		}


		/**
		 * Konstruiert den vollstaendigen Pfad des Logfiles.
		 * Um zu pruefen, dass der Filename noch nicht existiert, wird auf
		 * das Filesystem zugegriffen. Gibt es beim Zugriff auf das Filesystem
		 * einen Fehler, wird null zurueckgegeben.
		 * 
		 * @return			Vollstaendiger Pfad des Logfiles (oder null im Fehlerfall)
		 */
		private String createLogFile() {
			String time = logFileFormat.format(new Date());

			int i = 0;
			while (true) {
				String path;
				if (i == 0)
					path = logDir + "/" + logName + "-" + time + ".log";
				else
					path = logDir + "/" + logName + "-" + time + "-" + i + ".log";

				try {
					File logFile = new File(path);
					if (logFile.createNewFile())
						return path;
					
					i++;
				}
				catch (IOException e) {
					log(Level.WARN, "Creating new log file " + path + " failed: " + e.getMessage());
					return null;
				}
			}
		}
		
		
		/**
		 * Oeffenet Logfile im Append-Modus.
		 * 
		 * @return	true, wenn Logfile erfolgreich geoeffnet wurde.
		 */
		private boolean openLogFile() {
			assert(logFileWriter == null);
				
			if (logFile == null) {
				logFile = createLogFile();
				if (logFile == null)
					return false;
			}
			
			try {
		        logFileWriter = new BufferedWriter(new FileWriter(logFile, true));
		        return true;
		    }
			catch (IOException e) {
				logFileWriter = null;
				log(Level.WARN, "Opening log file " + logFile + " failed: " + e.getMessage());
		    	return false;
		    }		
		}
		
		
		/**
		 * Schliesst Logfile.
		 */
		private void closeLogFile() {
			assert(logFileWriter != null);
			
			try {
				logFileWriter.close();
			}
			catch (IOException e) {
				log(Level.WARN, "Closing log file " + logFile + " failed: " + e.getMessage());
			}
			finally {
				logFileWriter = null;
			}
		}
		
		
		/**
		 * Schreibt Meldung ins Logfile.
		 * 
		 * @param level		Loglevel
		 * @param msg	Logmeldung
		 */
		private void log(Level level, String msg) {
			String logEntry = formatLogEntry(level, "Log", msg, null);
			putEntry(logEntry);
		}		
	}

	
	/**
	 * Schreibt Loggingtext in ein lokales File.
	 * @param logEntry Logeintrag
	 */
	private synchronized void logLocal(String logEntry) {
		if (localLogWriter != null) {
			try {
				localLogWriter.print(logEntry);
			}
			catch (Throwable t) {
				t.printStackTrace();
			}
		}
		
		logSystem(logEntry);
	}
	
	
	/**
	 * Schreibt Loggingtext lokal auf den Error Stream.
	 * @param logEntry Logeintrag
	 */
	private synchronized void logSystem(String logEntry) {
		System.err.print(logEntry);
		System.err.flush();
	}
	
	/**
	 * Log File 
	 * */
	
	
	private static final String localLogFile = "/home/weather/log";
	//private static final String localLogFile = ServiceProperty. .getLocalLogFile();
	/**
	 * Log File Grösse
	 * */
	private static final int localLogFileSize = 1024*1024;

	
	/**
	 * Oeffnet das lokale Logfile.
	 */
	private void openLocal() {
		if (localLogFile != null)
			localLogWriter = new LocalLogWriter(localLogFile, localLogFileSize);	
	}

	
	/**
	 * Schliesst das lokale Logfile.
	 */
	private void closeLocal() {
		if (localLogWriter != null)
			localLogWriter.close();
	}

	
	/**
	 * Klasse, welche Logmeldungen in ein File schreibt.
	 * Wenn ein Logfile eine Maximalgroesse erreicht hat, wird das alte Logfile
	 * umbenannt und ein neues begonnnen. Bei einem Fehler werden keine Meldungen mehr ausgegeben.
	 */
	class LocalLogWriter {
		/**
		 * Writer Name
		 **/
		private String name;
		/**
		 * Backup Name
		 **/
		private String backupName;
		/**
		 * Maximale Grösse
		 **/
		private int maxSize;
		/**
		 * Writer
		 **/
		private PrintWriter writer;
		/**
		 * Zähler
		 **/
		private int count;
		
		
		/**
		 * Oeffnet ein lokales Logfile.
		 * 
		 * @param name		Name des Logfiles
		 * @param maxSize	Maximale Groesse des Logfiles in Bytes
		 */
		public LocalLogWriter(String name, int maxSize) {
			this.name = name;
			this.backupName = name + ".bak";
			this.maxSize = maxSize;

			// Open log file
			openFile();
			
			// Remove backup log file
			File backupFile = new File(backupName);
			backupFile.delete();
		}
		
		
		/**
		 * Oeffnet Logfile.
		 */
		private void openFile() {
			try {
				writer = new PrintWriter(new FileWriter(name));	
				log(Level.INFO, "Opening local log file " + name);
			}
			catch (IOException e) {
				log(Level.WARN, "Opening local log file " + name + " failed: " + e.getMessage());
			}
		}
		
		
		/**
		 * Schliesst Logfile.
		 */
		public void close() {
			if (writer == null)
				return;
			
			writer.close();
			log(Level.INFO, "Closing local log file " + name);
		}
		
		
		/**
		 * Schreibt Meldung ins Logfile.
		 * 
		 * @param msg	Logmeldung
		 */
		public void print(String msg) {
			if (writer == null)
				return;
			
			writer.write(msg);
			writer.flush();
			count += msg.length();
			
			if (count > maxSize) {
				log(Level.INFO, "Backing up local log file " + name);

				count = 0;
				
				// Close current log file
				writer.close();
				
				// Backup current log file
				File file = new File(name);
				File backupFile = new File(backupName);
				if (!file.renameTo(backupFile)) {
					if (backupFile.delete()) {
						if (file.renameTo(backupFile)) {
							log(Level.WARN, "Backing up local log file " + name + " failed");
						}
					}
				}
				
				// Open new log file
				openFile();
			}
		}
		
		
		/**
		 * Schreibt Fehlermeldung ins Logfile.
		 * 
		 * @param level	Loglevel
		 * @param msg	Fehlermeldung
		 */
		private void log(Level level, String msg) {
			String logEntry = formatLogEntry(level, "Log", msg, null);
			ServiceLogger.this.logSystem(logEntry);
		}		
	}
}
