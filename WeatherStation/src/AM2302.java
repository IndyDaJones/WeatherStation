//import java.util.Calendar;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class AM2302 {
	private String device;
	private double temperature;
	private double humidity;
	Timestamp currentDate;
	/**
	 * Object handles AM2302 sensor activities
	 * 
	 * @param Temperature xx.x degrees celcius
	 * @param Humidity xx.x percentage
	 */
	public AM2302(double Temperature, double Humidity) {
		device = "AM2302";
		temperature = Temperature;
		humidity = Humidity;
		// create a sql date object so we can use it in our INSERT statement
		// Create date;
		currentDate = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
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
	public Timestamp getCreateDate() {
		return currentDate;
	}
}
