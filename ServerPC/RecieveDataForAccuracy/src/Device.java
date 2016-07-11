
public class Device {
	
	private final int scale = 1000;
	private int times=0;
	
	private long[] receiveTime = new long[scale];
	private String macAddress;
	private long[] startTime  = new long[scale];
	private String[] gesture  = new String[scale];
	private int[] imageID     = new int[scale];
	
	public Device() {
		
	}
	
	public Device(String macAddress, String gesture, long receiveTime, long startTime, int imageID){
		
		this.macAddress         = macAddress;
		this.gesture[times]     = gesture;
		this.receiveTime[times] = receiveTime;
		this.startTime[times]   = startTime;
		this.imageID[times]     = imageID; 
		times=1;
		System.out.println("init : "+this.macAddress);
		System.out.println("times: "+times);
	}

	public void addData(String gesture, long receiveTime, long startTime, int imageID){
		this.gesture[times]     = gesture;
		this.receiveTime[times] = receiveTime;
		this.startTime[times]   = startTime;
		this.imageID[times]     = imageID; 
		times++;
		System.out.println("add  : "+this.macAddress);
		System.out.println("times: "+times);
		
	}
	
	public long[] getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(long receiveTime) {
		this.receiveTime[times] = receiveTime;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public long[] getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime[times] = startTime;
	}

	public String[] getGesture() {
		return gesture;
	}

	public void setGesture(String gesture) {
		this.gesture[times] = gesture;
	}

	public int[] getImageID() {
		return imageID;
	}

	public void setImageID(int imageID) {
		this.imageID[times] = imageID;
	}
	
	public int getTimes(){
		return times;
	}
	
}
