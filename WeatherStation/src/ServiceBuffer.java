import java.util.concurrent.ArrayBlockingQueue;

public class ServiceBuffer {
	private static ServiceBuffer instance;
	private static ArrayBlockingQueue<AM2302> queue;
	
	public ServiceBuffer() {
		if (instance != null)
			throw new IllegalStateException("ServiceBuffer can only be initialized once");
		instance = this;
		queue = new ArrayBlockingQueue<AM2302>(32768);		
	}
	public static synchronized void addBufferElement(AM2302 data) throws InterruptedException{
		queue.put(data);
	}
	public static synchronized AM2302 getBufferElement() throws InterruptedException {
		return queue.take();
	}
	public static synchronized int getBufferSize() {
		return queue.size();
	}
}