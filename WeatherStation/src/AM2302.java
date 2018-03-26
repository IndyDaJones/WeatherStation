import java.sql.Timestamp;
import java.util.Calendar;

public class AM2302 {
	private String device;
	private double temperature;
	private double humidity;
	Timestamp currentTimestamp;
	
	public AM2302(double Temperature, double Humidity) {
		device = "AM2302";
		temperature = Temperature;
		humidity = Humidity;
		// create a sql date object so we can use it in our INSERT statement
		// Create date;
		currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
	}
	public String getDevice() {
		return device;
	}
	public double getTemperature() {
		return temperature;
	}
	public double getHumidity() {
		return humidity;
	}
	public Timestamp getCreateTimestamp() {
		return currentTimestamp;
	}
}
