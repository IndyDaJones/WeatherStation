//import java.util.Calendar;
import java.util.Date;

public class AM2302 {
	private String device;
	private double temperature;
	private double humidity;
	Date currentDate;
	
	public AM2302(double Temperature, double Humidity) {
		device = "AM2302";
		temperature = Temperature;
		humidity = Humidity;
		// create a sql date object so we can use it in our INSERT statement
		// Create date;
		currentDate = new Date();
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
	public Date getCreateDate() {
		return currentDate;
	}
}
