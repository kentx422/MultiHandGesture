import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

//チャットサーバスレッド
public class ChatServerThread extends Thread {
	private static List<ChatServerThread> threads = new ArrayList<ChatServerThread>();// スレッド郡
	private Socket socket;// ソケット
	Device device[] = new Device[100];
	private int deviceNum = 0;
	static String recieveMessage = "";
	
	static int recieveLogState = 0;

	// private int ImageSomeSiihareFlag=0;
	// private long ImageSomeShareTimeStamp=0;
	// private int ImageSomeShareImageID=-1;

	// コンストラクタ
	public ChatServerThread(Socket socket) {
		super();
		this.socket = socket;
		threads.add(this);
	}

	// 処理
	@Override
	public void run() {
		InputStream in = null;
		String message;
		int size;
		byte[] w = new byte[10240];
		try {
			// ストリーム
			in = socket.getInputStream();
			while (true) {
				try {
					// 受信待ち
					size = in.read(w);

					// 切断
					if (size <= 0){
						recieveMessage = "";
						throw new IOException();
					}

					// 読み込み
					message = new String(w, 0, size, "UTF8");
					//System.out.println("Receive message: " + message);

					// 様々な処理
					//message = someProcess(message);
					
					//TransformMessage(message);
					//FormatMessage(message);
					RecieveLxFromMultiDevices(message);
					
					//System.out.println("Send message" + message);
					// 全員にメッセージ送信
					//sendMessageAll(message);

					// //デバイスの情報を表示
					// printDevice();

				} catch (IOException e) {
					socket.close();
					threads.remove(this);
					return;
				}
			}
		} catch (IOException e) {
			System.err.println(e);
		}
	}
	
	public void RecieveLxFromMultiDevices(String message){
		String[] splitSemicolon = message.split(";");
		
		
		for (int h = 0; h < splitSemicolon.length; h++) {
			String string = splitSemicolon[h];
			String[] str = string.split(",");
			
			if(str[0].equals("START")){
				sendMessageAll(str[0]);
				recieveLogState = 1;
				ChatServer.startTime= System.currentTimeMillis();
				ChatServer.lastTime = ChatServer.startTime;
				
//				//allResultに書き込み
//				ChatServer.luxData = "Time";
//				for (int i = 0; i < ChatServer.devices.size(); i++) {
//					ChatServer.luxData += ","+ChatServer.devices.get(i);
//					System.out.println(ChatServer.devices.get(i));
//				}
//				//ChatServer.luxData += "\n";
//				
//				writeCSV("AllResult"+ChatServer.startTime,ChatServer.luxData);
				
			}
			else if(str[0].equals("END")){
				sendMessageAll(str[0]);
				recieveLogState = 0;
				for (int i = 0; i < ChatServer.devices.size(); i++) {
					String writeData="Time,Lux\n";
					for (int j = 0; j < ChatServer.devicesTime.get(i).size(); j++) {
						writeData += ChatServer.devicesTime.get(i).get(j)+","+ChatServer.devicesLux.get(i).get(j)+"\n";
					}
					writeCSV("log_"+ChatServer.devices.get(i)+"_"+getNowTime(), writeData);
				}
			}
			else if(recieveLogState==1){
				overWriteCSV(str[0], str[2]);
				
				if(ChatServer.devices.indexOf(str[0])==-1){
					ArrayList<String> tempTime = new ArrayList<String>();
					ArrayList<String> tempLux = new ArrayList<String>();
					ChatServer.devices.add(str[0]);
					tempTime.add(str[1]);
					tempLux.add(str[2]);
					ChatServer.devicesTime.add(tempTime);
					ChatServer.devicesLux.add(tempLux);
					
				}
				else{
					ArrayList<String> tempTime = ChatServer.devicesTime.get(ChatServer.devices.indexOf(str[0]));
					ArrayList<String> tempLux = ChatServer.devicesLux.get(ChatServer.devices.indexOf(str[0]));
					tempTime.add(str[1]);
					tempLux.add(str[2]);
					ChatServer.devicesTime.set(ChatServer.devices.indexOf(str[0]),tempTime);
					ChatServer.devicesLux.set(ChatServer.devices.indexOf(str[0]),tempLux);
				}
				
				ChatServer.nowTime = System.currentTimeMillis();
				if(ChatServer.devices.get(0).equals(str[0])&&ChatServer.nowTime-ChatServer.lastTime>ChatServer.timeCount*1000){//timeCount * 1000msごと
					ChatServer.lastTime=ChatServer.nowTime;
					
//					//allResultに書き込み
//					ChatServer.luxData = ""+ChatServer.timeCount;
//					for (int i = 0; i < ChatServer.devices.size(); i++) {
//						ChatServer.luxData += ","+ChatServer.devicesLux.get(i).get(ChatServer.devicesLux.get(i).size()-1);
//					}
//					writeCSV("AllResult"+ChatServer.startTime,ChatServer.luxData);
					//System.out.println(ChatServer.timeCount+"s");
					ChatServer.timeCount++;
				}
			}
		}
		
	}
	
	public void FormatMessage(String message){
		String[] str = message.split(",");
		if(str[0].equals("delete")){
			//System.out.println("transform!!");
			TransformMessage(recieveMessage);
			recieveMessage="";
		}
		else if(str[0].equals("off")){
			recieveMessage="";
			System.out.println("reset recivemessage");
		}
		else{
			recieveMessage += message+"\n"; 
		}	
	}
	
	public void TransformMessage(String message){
		System.out.println("transform!!");
		System.out.println(">>"+message);
		long start = ChatServer.startTime;
		
		String[] splitN = message.split("\n");
		String[] splitSemicolon = splitN[0].split(";");
		String[] splitComma = splitSemicolon[0].split(",");
		System.out.println(splitComma);
		String filename = splitComma[2]+"_"+splitComma[1]+"_"+splitComma[0]+"";
		
		String transformMessage = "startTime,nowTime,nanoTime,lx\n";
		
		for(int i = 1;i<splitN.length;i++){
			splitSemicolon = splitN[i].split(";");
			for(int j = 0;j<splitSemicolon.length;j++){
				splitComma = splitSemicolon[j].split(",");
				if(isLong(splitComma[0])){
					transformMessage += String.valueOf(start)+","+splitComma[0]+","+splitComma[1]+","+splitComma[2]+"\n";
					//System.out.println(transformMessage);
				}
				else{
					transformMessage += splitComma[0]+"\n";
					//System.out.println(transformMessage);
				}
			}
		}
		writeCSV(filename,transformMessage);
		recieveMessage="";
	}

	// 全員にメッセージ送信
	public void sendMessageAll(String message) {
		ChatServerThread thread;
		for (int i = 0; i < threads.size(); i++) {
			thread = (ChatServerThread) threads.get(i);
			if (thread.isAlive())
				thread.sendMessage(this, message);
		}

		System.out.println("Send message all user:	" + message);
		// writeCSV(message);
	}

	//Longかどうかを確かめる
	static boolean isLong(String number) {
	    try {
	    	Long.parseLong(number);
	        return true;
	    } catch (NumberFormatException e) {
	        return false;
	    }
	} 
	
	// メッセージをｃｓｖに書き込み
	public void writeCSV(String filename,String message) {
		Calendar cal = Calendar.getInstance();
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		//String date = sdf.format(cal.getTime());
		try {
			String FS = File.separator;
			// File f = new
			// File("c:"+FS+"Users"+FS+"Kurisu"+FS+"Downloads"+FS+"pleiades"+FS+"workspace"+FS+"TestSocket"+FS+"MultiHandGestureLog("+date+").csv");
			File f = new File(ChatServer.path+"\\"+filename + ".csv");

			FileWriter fw = new FileWriter(f, true); // 書き込むファイル指定。ファイルが既にあるなら、そのファイルの末尾に書き込む
			BufferedWriter bw = new BufferedWriter(fw); // バッファクラスでfwを包んであげる
			PrintWriter pw = new PrintWriter(bw); // さらに、PrintWriterで包む

			pw.write(message);
			pw.println();
			pw.close(); // ファイル閉じる
		} catch (IOException e) {
			System.out.println("エラー：" + e);
		}
	}
	
	// メッセージをtempのｃｓｖに上書き
		public void overWriteCSV(String filename,String message) {
			Calendar cal = Calendar.getInstance();
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			//String date = sdf.format(cal.getTime());
			try {
				String FS = File.separator;
				// File f = new
				// File("c:"+FS+"Users"+FS+"Kurisu"+FS+"Downloads"+FS+"pleiades"+FS+"workspace"+FS+"TestSocket"+FS+"MultiHandGestureLog("+date+").csv");
				File f = new File(System.getProperty("user.dir")+"\\temp\\"+filename + ".csv");

				FileWriter fw = new FileWriter(f, false); // 書き込むファイル指定。ファイルが既にあるなら、そのファイルの末尾に書き込む
				BufferedWriter bw = new BufferedWriter(fw); // バッファクラスでfwを包んであげる
				PrintWriter pw = new PrintWriter(bw); // さらに、PrintWriterで包む

				pw.write(message);
				pw.println();
				pw.close(); // ファイル閉じる
			} catch (IOException e) {
				System.out.println("エラー：" + e);
			}
		}

	// メッセージ送信
	public void sendMessage(ChatServerThread talker, String message) {
		try {
			OutputStream out = socket.getOutputStream();
			byte[] w = message.getBytes("UTF8");
			out.write(w);
			out.flush();
		} catch (IOException e) {
		}
	}

	// 様々な処理
	public String someProcess(String str){
		String result="";
		
		//文字列を分解して配列に変換
		String strSplit[] = str.split(",");
		long receiveTime  = Long.parseLong(strSplit[0]);
		String macAddress = strSplit[1];
		long startTime    = Long.parseLong(strSplit[2]);
		String gesture    = strSplit[3];
		//int imageID       = Integer.parseInt(strSplit[4]);
		int imageID=-1;
		//ImageSomeShareFlagが上がっているときに,任意の時間が過ぎていればflagを下ろす
		if(ChatServer.ImageSomeShareFlag==1){
			long diff = startTime- ChatServer.ImageSomeShareTimeStamp ;
			if (diff>(long)(10000000)){
				ChatServer.ImageSomeShareFlag=0;
				ChatServer.ImageSomeShareImageID   = -1;
				ChatServer.ImageSomeShareTimeStamp = 0;
				System.out.println("*+*+*+*+*+*+*+*++*+*+*+*\n*+*+*+*+*+*+*+*++*+*+*+*\nImageSomeShareFlag OFF\n*+*+*+*+*+*+*+*++*+*+*+*\n*+*+*+*+*+*+*+*++*+*+*+*");
			}
		}
		
		
		//macアドレスでの識別
		if(macAddress.equals("30:85:a9:2f:00:af")){
			macAddress = "nexus7_2012_hmurakami";
		}else if (macAddress.equals("ac:22:0b:5c:8c:0c")) {
			macAddress = "nexus7_2013_haida";
		}
		else if (macAddress.equals("02:00:00:00:00:00")) {
			macAddress = "nexus7_2013_amiyoshi";
		}
		
		
		
		if(gesture.equals("log")){
			String filename = macAddress+"_"+String.valueOf(startTime);
			String strSplitEnter[] = str.split("\n");
			String log = strSplitEnter[1];
			for(int i=2;i<strSplitEnter.length;i++){
				log += ","+strSplitEnter[i];
			}
			writeCSV(filename, log);
		
		}
		else{
		//コンストラクタに追加
			imageID   = Integer.parseInt(strSplit[4]);
			addDevice(macAddress, gesture, receiveTime, startTime, imageID);
		}
		if(gesture.equals("ROLL")){
			result=rollProcess(imageID);
		}else if(gesture.equals("SLASH")){
			result=slashProcess(macAddress);
		}
		
		
		return result;
	}

	// コンストラクタに追加
	public void addDevice(String macAddress, String gesture, long receiveTime,
			long startTime, int imageID) {
		int i = 0, exist = 0;
		while (i < deviceNum) {
			if (device[i].getMacAddress().equals(macAddress)) {
				device[i].addData(gesture, receiveTime, startTime, imageID);
				exist++;
				break;
			}
			i++;
		}
		if (exist == 0) {
			device[deviceNum] = new Device(macAddress, gesture, receiveTime,
					startTime, imageID);
			deviceNum++;
		} else if (exist > 1) {
			System.out.println("error: exist is over 2");
		}
	}

	public String rollProcess(int imageID) {
		String result = "ImageAllShare," + imageID;
		return result;
	}

	public String slashProcess(String macAddress) {
		int deviceIDBuf = serchDevice(macAddress);
		if (deviceIDBuf < 0) {
			System.out.println("error: not search device");
		}
		int lastTimes = (device[deviceIDBuf].getTimes() - 2);

		// SLASHが初のジェスチャであったとき
		if (device[deviceIDBuf].getTimes() == 1) {

		}
		// 前回がHIDEのとき
		else if (device[deviceIDBuf].getGesture()[lastTimes].equals("HIDE")) {
			ChatServer.ImageSomeShareFlag = 1;
			ChatServer.ImageSomeShareImageID = device[deviceIDBuf].getImageID()[lastTimes];
			ChatServer.ImageSomeShareTimeStamp = device[deviceIDBuf]
					.getStartTime()[lastTimes];
			System.out.println("ImageSomeShareFlag ON");
			return "ImageSomeShareFlagON," + ChatServer.ImageSomeShareImageID
					+ "," + macAddress;
		}
		// 誰かがHIDE&SLASHをしているとき
		else if (ChatServer.ImageSomeShareFlag == 1) {
			return "ImageSomeShare," + ChatServer.ImageSomeShareImageID + ","
					+ macAddress;
		}

		return "";
	}

	public int serchDevice(String macAddress) {
		for (int i = 0; i < deviceNum; i++) {
			if (device[i].getMacAddress().equals(macAddress)) {
				return i;
			}
		}
		return -1;
	}

	public void printDevice() {
		for (int i = 0; i < deviceNum; i++) {
			System.out.println("+++++++++++++++++++++++++++++++");
			System.out.println("mac		: " + device[i].getMacAddress());
			System.out.println("gesture	: "
					+ Arrays.asList(device[i].getGesture()));
			System.out.println("+++++++++++++++++++++++++++++++");

		}
	}
	
	public static String getNowTime() {
        // 時刻取得
        Calendar calendar = Calendar.getInstance();
        String nowTime = "" + calendar.get(Calendar.YEAR);
        if (calendar.get(Calendar.MONTH) + 1 < 10) {
            nowTime += "0" + (calendar.get(Calendar.MONTH) + 1);
        } else {
            nowTime += "" + (calendar.get(Calendar.MONTH) + 1);
        }
        if (calendar.get(Calendar.DAY_OF_MONTH)  < 10) {
            nowTime += "0" + calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            nowTime += "" + calendar.get(Calendar.DAY_OF_MONTH);
        }

        //nowTime+="_";

        if (calendar.get(Calendar.HOUR_OF_DAY)  < 10) {
            nowTime += "0" + calendar.get(Calendar.HOUR_OF_DAY);
        } else {
            nowTime += "" + calendar.get(Calendar.HOUR_OF_DAY);
        }
        if (calendar.get(Calendar.MINUTE)  < 10) {
            nowTime += "0" + calendar.get(Calendar.MINUTE);
        } else {
            nowTime += "" + calendar.get(Calendar.MINUTE);
        }
        if (calendar.get(Calendar.SECOND)  < 10) {
            nowTime += "0" + calendar.get(Calendar.SECOND);
        } else {
            nowTime += "" + calendar.get(Calendar.SECOND);
        }
        return nowTime;
    }
}
