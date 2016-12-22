import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
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
public class ChatServerThreadForSOMG4DUntillDecember extends Thread {
	private static List<ChatServerThreadForSOMG4DUntillDecember> threads = new ArrayList<ChatServerThreadForSOMG4DUntillDecember>();// スレッド郡
	private Socket socket;// ソケット
	Device device[] = new Device[100];
	private int deviceNum = 0;
	static String recieveMessage = "";
	private String udid;
	String messageForWriting;

	ArrayList<String> lxLog = new ArrayList<>();
	ArrayList<String> timeLxLog = new ArrayList<>();

	// private int ImageSomeShareFlag=0;
	// private long ImageSomeShareTimeStamp=0;
	// private int ImageSomeShareImageID=-1;

	// コンストラクタ
	public ChatServerThreadForSOMG4DUntillDecember(Socket socket) {
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
			// long diffTime = System.currentTimeMillis() -
			// ChatServerForSOMGUntillDecember.startTime;
			// sendMessage(this,"server,timeFromServer,"+String.valueOf(diffTime));

			while (true) {
				try {
					// 受信待ち
					size = in.read(w);

					// 切断
					if (size <= 0) {
						recieveMessage = "";
						throw new IOException();
					}

					// 読み込み
					message = new String(w, 0, size, "UTF8");
					System.out.println("Receive message: " + message);

					// 様々な処理
					// message = someProcess(message);
					message = someProcessForDecember(message);
					// TransformMessage(message);
					// FormatMessage(message);

					// System.out.println("Send message" + message);
					// 全員にメッセージ送信
					// sendMessageAll(message);

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

	String someProcessForDecember(String message) {

		String[] splitSemicolon = message.split(";");

		for (int i = 0; i < splitSemicolon.length; i++) {
			String[] splitComma = splitSemicolon[i].split(",");
			udid = splitComma[1];
			String deviceName = getDeviceNameByUDID(udid);
			String deviceChar = getDeviceCharByUDID(udid);
			String operator = splitComma[2];

			if (operator.equals("connect")) {
				ArrayList<Double> lxLog = new ArrayList<>();
				ArrayList<Long> timeLog = new ArrayList<>();
				ChatServerForSOMG4DUntillDecember.requestOrder = new ArrayList<String>();
				ChatServerForSOMG4DUntillDecember.answerStartOrder = new ArrayList<String>();
				ChatServerForSOMG4DUntillDecember.orderList = new ArrayList<String>();

				long diffTime = System.currentTimeMillis()
						- ChatServerForSOMG4DUntillDecember.startTime;
				ChatServerForSOMG4DUntillDecember.devices.add(deviceName);
				sendMessage(this,
						"server,timeFromServer," + String.valueOf(diffTime));
				// messageForWriting = ","+ deviceName+"\n";
				messageForWriting = "";

			} else if (operator.equals("disconnect")) {
				String filename = deviceName + "_" + getNowTime();
				for (int j = 0; j < lxLog.size(); j++) {
					// System.out.println(timeLxLog.get(j)+","+lxLog.get(j));
					messageForWriting += timeLxLog.get(j) + "," + lxLog.get(j)
							+ "\n";
				}
				writeCSV(filename, messageForWriting);
				sendMessageAll(udid + ",replyDisconnect");
			} else if (operator.equals("replyReplyDisconnect")) {
				String filename = deviceName + "_" + getNowTime();
				for (int j = 0; j < lxLog.size(); j++) {
					// System.out.println(timeLxLog.get(j)+","+lxLog.get(j));
					messageForWriting += timeLxLog.get(j) + "," + lxLog.get(j)
							+ "\n";
				}
				writeCSV(filename, messageForWriting);
			}

			else if (operator.equals("startOrder")) {
				if (ChatServerForSOMG4DUntillDecember.kindExp
						.equals("Ordering")) {

					if (ChatServerForSOMG4DUntillDecember.deviceNum == 2) {
						ChatServerForSOMG4DUntillDecember.orderList = new ArrayList<>(
								Arrays.asList("AB", "AC", "AD", "BA", "BC",
										"BD", "CA", "CB", "CD", "DA", "DB",
										"DC"));

					} else if (ChatServerForSOMG4DUntillDecember.deviceNum == 3) {
						ChatServerForSOMG4DUntillDecember.orderList = new ArrayList<>(
								Arrays.asList("ABC", "ABD", "ACB", "ACD",
										"ADB", "ADC", "BAC", "BAD", "BCA",
										"BCD", "BDA", "BDC", "CAB", "CAD",
										"CBA", "CBD", "CDA", "CDB", "DAB",
										"DAC", "DBA", "DBC", "DCA", "DCB"));

					} else if (ChatServerForSOMG4DUntillDecember.deviceNum == 4) {
						ChatServerForSOMG4DUntillDecember.orderList = new ArrayList<>(
								Arrays.asList("ABCD", "ABDC", "ACBD", "ACDB",
										"ADBC", "ADCB", "BACD", "BADC", "BCAD",
										"BCDA", "BDAC", "BDCA", "CABD", "CADB",
										"CBAD", "CBDA", "CDAB", "CDBA", "DABC",
										"DACB", "DBAC", "DBCA", "DCAB", "DCBA"));

					}
				} else if (ChatServerForSOMG4DUntillDecember.kindExp
						.equals("Grouping")) {
					ChatServerForSOMG4DUntillDecember.orderList = new ArrayList<>(
							Arrays.asList("ABCD",// 4C4
									"ABC", "ABD", "ACD", "BCD",// 4C3
									"AB", "AC", "AD", "BC", "BD", "CD"// 4C2
							));
				}

				else if (ChatServerForSOMG4DUntillDecember.kindExp
						.equals("Distance")) {
					ChatServerForSOMG4DUntillDecember.orderList = new ArrayList<>(
							Arrays.asList("AB", "AC", "AD", "BC", "BD", "CD"// 4C2
							));
				}

				// ChatServerForSOMG4DUntillDecember.orderList = new
				// ArrayList<>(Arrays.asList(
				// "ABCD","ABDC","ACBD","ACDB","ADBC","ADCB",
				// "BACD","BADC","BCAD","BCDA","BDAC","BDCA",
				// "CABD","CADB","CBAD","CBDA","CDAB","CDBA",
				// "DABC","DACB","DBAC","DBCA","DCAB","DCBA",
				//
				// "ABC","ABD","ACB","ACD","ADB","ADC",
				// "BAC","BAD","BCA","BCD","BDA","BDC",
				// "CAB","CAD","CBA","CBD","CDA","CDB",
				// "DAB","DAC","DBA","DBC","DCA","DCB",
				//
				// "AB","AC","AD",
				// "BA","BC","BD",
				// "DA","DB","DC"
				//
				// ));

				if (ChatServerForSOMG4DUntillDecember.orderList.size() != 0) {
					int temp = (int) (Math.random() * ChatServerForSOMG4DUntillDecember.orderList
							.size());
					String tempOrder = ChatServerForSOMG4DUntillDecember.orderList
							.get(temp);
					ChatServerForSOMG4DUntillDecember.orderList.remove(temp);
					ChatServerForSOMG4DUntillDecember.requestOrder
							.add(tempOrder);
					sendMessageAll(udid + ",replyStartOrder," + tempOrder);
				} else {
					sendMessageAll(udid + ",replyStartOrder,endOrder");
				}
			}

			else if (operator.equals("nextOrder")) {
				if (ChatServerForSOMG4DUntillDecember.orderList.size() != 0) {
					int temp = (int) (Math.random() * ChatServerForSOMG4DUntillDecember.orderList
							.size());
					String orderButtonFlag = splitComma[3];
					String tempOrder = ChatServerForSOMG4DUntillDecember.orderList
							.get(temp);
					ChatServerForSOMG4DUntillDecember.orderList.remove(temp);
					ChatServerForSOMG4DUntillDecember.requestOrder
							.add(tempOrder);
					sendMessageAll(udid + ",replyNextOrder," + tempOrder + ","
							+ orderButtonFlag);

					// ----------startOrder-----------

					String tempAnswerStartOrder = "";
					String tempTimeOfAnswerStartOrder = "";
					String tempLxOfAnswerStartOrder = "";

					while (ChatServerForSOMG4DUntillDecember.tempStartOrder
							.size() != 0) {

						int tempNum = 0;
						long timeMin = 999999999;
						for (int j = 0; j < ChatServerForSOMG4DUntillDecember.tempStartOrder
								.size(); j++) {
							if (timeMin > Long
									.parseLong(ChatServerForSOMG4DUntillDecember.tempTimeOfStartOrder
											.get(j))) {
								timeMin = Long
										.parseLong(ChatServerForSOMG4DUntillDecember.tempTimeOfStartOrder
												.get(j));
								tempNum = j;
							}
						}
						tempAnswerStartOrder += ChatServerForSOMG4DUntillDecember.tempStartOrder
								.get(tempNum);
						ChatServerForSOMG4DUntillDecember.tempStartOrder
								.remove(tempNum);

						tempTimeOfAnswerStartOrder += ChatServerForSOMG4DUntillDecember.tempTimeOfStartOrder
								.get(tempNum);
						ChatServerForSOMG4DUntillDecember.tempTimeOfStartOrder
								.remove(tempNum);

						tempLxOfAnswerStartOrder += ChatServerForSOMG4DUntillDecember.tempLxOfStartOrder
								.get(tempNum);
						ChatServerForSOMG4DUntillDecember.tempLxOfStartOrder
								.remove(tempNum);
					}
					ChatServerForSOMG4DUntillDecember.answerStartOrder
							.add(tempAnswerStartOrder);
					ChatServerForSOMG4DUntillDecember.timeOfAnswerStartOrder
							.add(tempTimeOfAnswerStartOrder);
					ChatServerForSOMG4DUntillDecember.lxOfAnswerStartOrder
							.add(tempLxOfAnswerStartOrder);

					// ----------endOrder-----------
					String tempAnswerEndOrder = "";
					String tempTimeOfAnswerEndOrder = "";
					String tempLxOfAnswerEndOrder = "";

					while (ChatServerForSOMG4DUntillDecember.tempEndOrder
							.size() != 0) {

						int tempNum = 0;
						long timeMin = 999999999;
						for (int j = 0; j < ChatServerForSOMG4DUntillDecember.tempEndOrder
								.size(); j++) {
							if (timeMin > Long
									.parseLong(ChatServerForSOMG4DUntillDecember.tempTimeOfEndOrder
											.get(j))) {
								timeMin = Long
										.parseLong(ChatServerForSOMG4DUntillDecember.tempTimeOfEndOrder
												.get(j));
								tempNum = j;
							}
						}
						tempAnswerEndOrder += ChatServerForSOMG4DUntillDecember.tempEndOrder
								.get(tempNum);
						ChatServerForSOMG4DUntillDecember.tempEndOrder
								.remove(tempNum);

						tempTimeOfAnswerEndOrder += ChatServerForSOMG4DUntillDecember.tempTimeOfEndOrder
								.get(tempNum);
						ChatServerForSOMG4DUntillDecember.tempTimeOfEndOrder
								.remove(tempNum);

						tempLxOfAnswerEndOrder += ChatServerForSOMG4DUntillDecember.tempLxOfEndOrder
								.get(tempNum);
						ChatServerForSOMG4DUntillDecember.tempLxOfEndOrder
								.remove(tempNum);
					}
					ChatServerForSOMG4DUntillDecember.answerEndOrder
							.add(tempAnswerEndOrder);
					ChatServerForSOMG4DUntillDecember.timeOfAnswerEndOrder
							.add(tempTimeOfAnswerEndOrder);
					ChatServerForSOMG4DUntillDecember.lxOfAnswerEndOrder
							.add(tempLxOfAnswerEndOrder);

					// ---------------------

				} else {
					sendMessageAll(udid + ",replyNextOrder,endOrder");
				}

			} else if (operator.equals("endOrder")) {

				// ----------startOrder-----------

				String tempAnswerStartOrder = "";
				String tempTimeOfAnswerStartOrder = "";
				String tempLxOfAnswerStartOrder = "";

				while (ChatServerForSOMG4DUntillDecember.tempStartOrder
						.size() != 0) {

					int tempNum = 0;
					long timeMin = 999999999;
					for (int j = 0; j < ChatServerForSOMG4DUntillDecember.tempStartOrder
							.size(); j++) {
						if (timeMin > Long
								.parseLong(ChatServerForSOMG4DUntillDecember.tempTimeOfStartOrder
										.get(j))) {
							timeMin = Long
									.parseLong(ChatServerForSOMG4DUntillDecember.tempTimeOfStartOrder
											.get(j));
							tempNum = j;
						}
					}
					tempAnswerStartOrder += ChatServerForSOMG4DUntillDecember.tempStartOrder
							.get(tempNum);
					ChatServerForSOMG4DUntillDecember.tempStartOrder
							.remove(tempNum);

					tempTimeOfAnswerStartOrder += ChatServerForSOMG4DUntillDecember.tempTimeOfStartOrder
							.get(tempNum);
					ChatServerForSOMG4DUntillDecember.tempTimeOfStartOrder
							.remove(tempNum);

					tempLxOfAnswerStartOrder += ChatServerForSOMG4DUntillDecember.tempLxOfStartOrder
							.get(tempNum);
					ChatServerForSOMG4DUntillDecember.tempLxOfStartOrder
							.remove(tempNum);
				}
				ChatServerForSOMG4DUntillDecember.answerStartOrder
						.add(tempAnswerStartOrder);
				ChatServerForSOMG4DUntillDecember.timeOfAnswerStartOrder
						.add(tempTimeOfAnswerStartOrder);
				ChatServerForSOMG4DUntillDecember.lxOfAnswerStartOrder
						.add(tempLxOfAnswerStartOrder);

				// ----------endOrder-----------
				String tempAnswerEndOrder = "";
				String tempTimeOfAnswerEndOrder = "";
				String tempLxOfAnswerEndOrder = "";

				while (ChatServerForSOMG4DUntillDecember.tempEndOrder
						.size() != 0) {

					int tempNum = 0;
					long timeMin = 999999999;
					for (int j = 0; j < ChatServerForSOMG4DUntillDecember.tempEndOrder
							.size(); j++) {
						if (timeMin > Long
								.parseLong(ChatServerForSOMG4DUntillDecember.tempTimeOfEndOrder
										.get(j))) {
							timeMin = Long
									.parseLong(ChatServerForSOMG4DUntillDecember.tempTimeOfEndOrder
											.get(j));
							tempNum = j;
						}
					}
					tempAnswerEndOrder += ChatServerForSOMG4DUntillDecember.tempEndOrder
							.get(tempNum);
					ChatServerForSOMG4DUntillDecember.tempEndOrder
							.remove(tempNum);

					tempTimeOfAnswerEndOrder += ChatServerForSOMG4DUntillDecember.tempTimeOfEndOrder
							.get(tempNum);
					ChatServerForSOMG4DUntillDecember.tempTimeOfEndOrder
							.remove(tempNum);

					tempLxOfAnswerEndOrder += ChatServerForSOMG4DUntillDecember.tempLxOfEndOrder
							.get(tempNum);
					ChatServerForSOMG4DUntillDecember.tempLxOfEndOrder
							.remove(tempNum);
				}
				ChatServerForSOMG4DUntillDecember.answerEndOrder
						.add(tempAnswerEndOrder);
				ChatServerForSOMG4DUntillDecember.timeOfAnswerEndOrder
						.add(tempTimeOfAnswerEndOrder);
				ChatServerForSOMG4DUntillDecember.lxOfAnswerEndOrder
						.add(tempLxOfAnswerEndOrder);

				// ---------------------

				String filename = "SOMG4Devices_" + getNowTime();

				// if(ChatServerForSOMG4DUntillDecember.requestOrder.size()==ChatServerForSOMG4DUntillDecember.answerOrder.size()){
				for (int j = 0; j < ChatServerForSOMG4DUntillDecember.answerStartOrder
						.size(); j++) {
					messageForWriting += ChatServerForSOMG4DUntillDecember.requestOrder
							.get(j)
							+ ","
							+ ChatServerForSOMG4DUntillDecember.answerStartOrder
									.get(j)
							+ ","
							+ ChatServerForSOMG4DUntillDecember.timeOfAnswerStartOrder
									.get(j) 
							+ ","
							+ ChatServerForSOMG4DUntillDecember.lxOfAnswerStartOrder
									.get(j) 
							+ ","
							+ ChatServerForSOMG4DUntillDecember.answerEndOrder
									.get(j)
							+ ","
							+ ChatServerForSOMG4DUntillDecember.timeOfAnswerEndOrder
									.get(j) 
							+ ","
							+ ChatServerForSOMG4DUntillDecember.lxOfAnswerEndOrder
									.get(j) 		
							+ "\n";
				}
				// }
				writeCSV(filename, messageForWriting);
				sendMessageAll(udid + ",replyEndOrder");
			}

			else if (operator.equals("makeCSV")) {
				File file = new File(ChatServerForSOMG4DUntillDecember.path);
				File files[] = file.listFiles();
				String readData = "";
				String filename = files.length + "devices_" + getNowTime();
				String tempData[] = new String[files.length];

				for (int j = 0; j < files.length; j++) {
					String getFilename = files[j].toString();
					// System.out.println(getFilename);
					// Fileを読み出し
					// tempData[j] += readFile(getFilename)+"\n";
					readData += readFile(getFilename) + "\n";
				}

				writeCSV(filename, readData);

				// pathを変更
				ChatServerForSOMG4DUntillDecember.path = System
						.getProperty("user.dir") + "\\result\\" + getNowTime();

			} else if (operator.equals("setTime")) {
				long diffTime = System.currentTimeMillis()
						- ChatServerForSOMG4DUntillDecember.startTime;
				sendMessageAll(udid + ",replySetTime,"
						+ String.valueOf(diffTime));
			}

			else if (operator.equals("illuminanceAndTimeData")) {
				// double lx = Double.parseDouble(splitComma[3]);
				// long timeLx = Long.parseLong(splitComma[4]);
				String lx = splitComma[3];
				String timeLx = splitComma[4];
				if (lxLog.size() > 0) {
					Double lastLx = Double
							.parseDouble(lxLog.get(lxLog.size() - 1));
					if (ChatServerForSOMG4DUntillDecember.tempStartOrder
							.indexOf(deviceChar) == -1) {
						if (Math.abs(Double.parseDouble(lx) - lastLx) > lastLx * 0.2) { // 20%
							ChatServerForSOMG4DUntillDecember.tempStartOrder
									.add(deviceChar);
							ChatServerForSOMG4DUntillDecember.tempTimeOfStartOrder
									.add(timeLx);
							ChatServerForSOMG4DUntillDecember.tempLxOfStartOrder
									.add(lx);

						}
					}
					if (ChatServerForSOMG4DUntillDecember.tempStartOrder
							.indexOf(deviceChar) != -1
							&& ChatServerForSOMG4DUntillDecember.tempEndOrder
									.indexOf(deviceChar) == -1) {
						Double tempLxOfStartOrder = Double
								.parseDouble(ChatServerForSOMG4DUntillDecember.tempLxOfStartOrder
										.get(ChatServerForSOMG4DUntillDecember.tempStartOrder
												.indexOf(deviceChar)));
						if (Math.abs(tempLxOfStartOrder
								- Double.parseDouble(lx)) < tempLxOfStartOrder * 0.1) {
							ChatServerForSOMG4DUntillDecember.tempEndOrder
									.add(deviceChar);
							ChatServerForSOMG4DUntillDecember.tempTimeOfEndOrder
									.add(timeLx);
							ChatServerForSOMG4DUntillDecember.tempLxOfEndOrder
									.add(lx);
						}
					}

				}
				lxLog.add(lx);
				timeLxLog.add(timeLx);
			}

			else if (operator.equals("chooseGesture")) {
				String gesture = splitComma[3];
				String timeGesture = splitComma[4];
				lxLog.add(gesture);
				timeLxLog.add(timeGesture);
				sendMessageAll(udid + ",replyChooseGesture," + timeGesture
						+ "," + gesture);
			} else if (operator.equals("replyReplyChooseGesture")) {
				String gesture = splitComma[3];
				String timeGesture = splitComma[4];
				lxLog.add(gesture);
				timeLxLog.add(timeGesture);
			}
		}

		return "";
	}

	// public void FormatMessage(String message){
	// String[] str = message.split(",");
	// if(str[0].equals("delete")){
	// //System.out.println("transform!!");
	// TransformMessage(recieveMessage);
	// recieveMessage="";
	// }
	// else if(str[0].equals("off")){
	// recieveMessage="";
	// System.out.println("reset recivemessage");
	// }
	// else{
	// recieveMessage += message+"\n";
	// }
	// }
	//
	// public void TransformMessage(String message){
	// System.out.println("transform!!");
	// System.out.println(">>"+message);
	// long start = ChatServerForSOMGUntillDecember.startTime;
	//
	// String[] splitN = message.split("\n");
	// String[] splitSemicolon = splitN[0].split(";");
	// String[] splitComma = splitSemicolon[0].split(",");
	// //System.out.println(splitComma);
	// String filename = splitComma[2]+"_"+splitComma[1]+"_"+splitComma[0]+"";
	//
	// String transformMessage = "startTime,nowTime,nanoTime,lx\n";
	//
	// for(int i = 1;i<splitN.length;i++){
	// splitSemicolon = splitN[i].split(";");
	// for(int j = 0;j<splitSemicolon.length;j++){
	// splitComma = splitSemicolon[j].split(",");
	// if(isLong(splitComma[0])){
	// transformMessage +=
	// String.valueOf(start)+","+splitComma[0]+","+splitComma[1]+","+splitComma[2]+"\n";
	// //System.out.println(transformMessage);
	// }
	// else{
	// transformMessage += splitComma[0]+"\n";
	// //System.out.println(transformMessage);
	// }
	// }
	// }
	// writeCSV(filename,transformMessage);
	// recieveMessage="";
	// }

	// 全員にメッセージ送信
	public void sendMessageAll(String message) {
		ChatServerThreadForSOMG4DUntillDecember thread;
		System.out.println("Start of Send message all user:	" + message);
		for (int i = 0; i < threads.size(); i++) {
			thread = (ChatServerThreadForSOMG4DUntillDecember) threads.get(i);
			if (thread.isAlive())
				thread.sendMessage(this, message);
		}
		System.out.println("End of Send message all user");

		// writeCSV(message);
	}

	// メッセージ送信
	public void sendMessage(ChatServerThreadForSOMG4DUntillDecember talker,
			String message) {
		try {
			String serveTime = getNowTime();
			final String temp = serveTime + "," + message + ";";
			OutputStream out = socket.getOutputStream();
			byte[] w = temp.getBytes("UTF8");
			out.write(w);
			out.flush();
			System.out.println("Send message to " + udid + ":	" + temp);
		} catch (IOException e) {
		}

	}

	// Longかどうかを確かめる
	static boolean isLong(String number) {
		try {
			Long.parseLong(number);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	// メッセージをｃｓｖに書き込み
	public void writeCSV(String filename, String message) {
		Calendar cal = Calendar.getInstance();
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		// String date = sdf.format(cal.getTime());
		try {
			String FS = File.separator;
			// File f = new
			// File("c:"+FS+"Users"+FS+"Kurisu"+FS+"Downloads"+FS+"pleiades"+FS+"workspace"+FS+"TestSocket"+FS+"MultiHandGestureLog("+date+").csv");
			File f = new File(ChatServerForSOMG4DUntillDecember.path + "\\"
					+ filename + ".csv");

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

	// 様々な処理
	public String someProcess(String str) {
		String result = "";

		// 文字列を分解して配列に変換
		String strSplit[] = str.split(",");
		long receiveTime = Long.parseLong(strSplit[0]);
		String macAddress = strSplit[1];
		long startTime = Long.parseLong(strSplit[2]);
		String gesture = strSplit[3];
		// int imageID = Integer.parseInt(strSplit[4]);
		int imageID = -1;
		// ImageSomeShareFlagが上がっているときに,任意の時間が過ぎていればflagを下ろす
		if (ChatServerForSOMG4DUntillDecember.ImageSomeShareFlag == 1) {
			long diff = startTime
					- ChatServerForSOMG4DUntillDecember.ImageSomeShareTimeStamp;
			if (diff > (long) (10000000)) {
				ChatServerForSOMG4DUntillDecember.ImageSomeShareFlag = 0;
				ChatServerForSOMG4DUntillDecember.ImageSomeShareImageID = -1;
				ChatServerForSOMG4DUntillDecember.ImageSomeShareTimeStamp = 0;
				System.out
						.println("*+*+*+*+*+*+*+*++*+*+*+*\n*+*+*+*+*+*+*+*++*+*+*+*\nImageSomeShareFlag OFF\n*+*+*+*+*+*+*+*++*+*+*+*\n*+*+*+*+*+*+*+*++*+*+*+*");
			}
		}

		// macアドレスでの識別
		if (macAddress.equals("30:85:a9:2f:00:af")) {
			macAddress = "nexus7_2012_hmurakami";
		} else if (macAddress.equals("ac:22:0b:5c:8c:0c")) {
			macAddress = "nexus7_2013_haida";
		} else if (macAddress.equals("02:00:00:00:00:00")) {
			macAddress = "nexus7_2013_amiyoshi";
		}

		if (gesture.equals("log")) {
			String filename = macAddress + "_" + String.valueOf(startTime);
			String strSplitEnter[] = str.split("\n");
			String log = strSplitEnter[1];
			for (int i = 2; i < strSplitEnter.length; i++) {
				log += "," + strSplitEnter[i];
			}
			writeCSV(filename, log);

		} else {
			// コンストラクタに追加
			imageID = Integer.parseInt(strSplit[4]);
			addDevice(macAddress, gesture, receiveTime, startTime, imageID);
		}
		if (gesture.equals("ROLL")) {
			result = rollProcess(imageID);
		} else if (gesture.equals("SLASH")) {
			result = slashProcess(macAddress);
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
			ChatServerForSOMG4DUntillDecember.ImageSomeShareFlag = 1;
			ChatServerForSOMG4DUntillDecember.ImageSomeShareImageID = device[deviceIDBuf]
					.getImageID()[lastTimes];
			ChatServerForSOMG4DUntillDecember.ImageSomeShareTimeStamp = device[deviceIDBuf]
					.getStartTime()[lastTimes];
			System.out.println("ImageSomeShareFlag ON");
			return "ImageSomeShareFlagON,"
					+ ChatServerForSOMG4DUntillDecember.ImageSomeShareImageID
					+ "," + macAddress;
		}
		// 誰かがHIDE&SLASHをしているとき
		else if (ChatServerForSOMG4DUntillDecember.ImageSomeShareFlag == 1) {
			return "ImageSomeShare,"
					+ ChatServerForSOMG4DUntillDecember.ImageSomeShareImageID
					+ "," + macAddress;
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

	public String getDeviceNameByUDID(String udid) {
		if (udid.equals("a4c7b9190b6bd931")) {
			return "nexus7-2012-hmurakami";
		} else if (udid.equals("c58ce7becdb6013")) {
			return "nexus7-2012-tshimakawa";
		} else if (udid.equals("8e9e784548c0cb6a")) {
			return "nexus7-2013-haida";
		} else if (udid.equals("f7196b5116fe5f4d")) {
			return "nexus7-2013-amiyoshi";
		} else if (udid.equals("a63f8c393f29b971")) {
			return "Galaxy-S5-atonomura";
		} else if (udid.equals("7b2f5bfd497b875f")) {
			return "Xperia-Z5-tyamamoto";
		} else if (udid.equals("6834af3a92999f3b")) {
			return "Galaxy-S6edge-dyamashita";
		} else if (udid.equals("b58cf0a0466b2ace")) {
			return "Xperia-Z3-smorimura";
		} else {
			return "unknown";
		}
	}

	public String getDeviceCharByUDID(String udid) {
		if (udid.equals("a4c7b9190b6bd931")) {
			// return "nexus7-2012-hmurakami";
			return "A";
		} else if (udid.equals("c58ce7becdb6013")) {
			// return "nexus7-2012-tshimakawa";
			return "B";
		} else if (udid.equals("8e9e784548c0cb6a")) {
			// return "nexus7-2013-haida";
			return "C";
		} else if (udid.equals("f7196b5116fe5f4d")) {
			// return "nexus7-2013-amiyoshi";
			return "D";
		}
		// else if (udid.equals("a63f8c393f29b971")) {
		// return "Galaxy-S5-atonomura";
		// }
		// else if (udid.equals("7b2f5bfd497b875f")) {
		// return "Xperia-Z5-tyamamoto";
		// }
		// else if (udid.equals("6834af3a92999f3b")) {
		// return "Galaxy-S6edge-dyamashita";
		// }
		// else if (udid.equals("b58cf0a0466b2ace")) {
		// return "Xperia-Z3-smorimura";
		// }
		else {
			return "unknown";
		}
	}

	public int changeDeviceCharToNum(String deviceChar) {
		int ans = 0;
		deviceChar = deviceChar.toUpperCase();
		char tempDeviceChar[] = deviceChar.toCharArray();
		if (tempDeviceChar.length != 1) {
			return -1;
		}
		ans = tempDeviceChar[0] - 'A';
		return ans;
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
		if (calendar.get(Calendar.DAY_OF_MONTH) + 1 < 10) {
			nowTime += "0" + calendar.get(Calendar.DAY_OF_MONTH);
		} else {
			nowTime += "" + calendar.get(Calendar.DAY_OF_MONTH);
		}

		// nowTime+="_";

		if (calendar.get(Calendar.HOUR_OF_DAY) < 10) {
			nowTime += "0" + calendar.get(Calendar.HOUR_OF_DAY);
		} else {
			nowTime += "" + calendar.get(Calendar.HOUR_OF_DAY);
		}
		if (calendar.get(Calendar.MINUTE) < 10) {
			nowTime += "0" + calendar.get(Calendar.MINUTE);
		} else {
			nowTime += "" + calendar.get(Calendar.MINUTE);
		}
		if (calendar.get(Calendar.SECOND) < 10) {
			nowTime += "0" + calendar.get(Calendar.SECOND);
		} else {
			nowTime += "" + calendar.get(Calendar.SECOND);
		}
		return nowTime;
	}

	// Fileの読み出し
	public static String readFile(String filename) {
		String message = "";
		try {
			// ファイルを読み込む
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);

			// 読み込んだファイルを１行ずつ処理する
			String line;
			// StringTokenizer token;

			if ((line = br.readLine()) != null) {
				message += line;
			}

			while ((line = br.readLine()) != null) {
				message += "\n" + line;

				// //区切り文字","で分割する
				// token = new StringTokenizer(line, ",");
				// //分割した文字を画面出力する
				// while (token.hasMoreTokens()) {
				// System.out.println(token.nextToken());
				// }
				// //System.out.println("**********");
			}
			// 終了処理
			br.close();

		} catch (IOException ex) {
			// 例外発生時処理
			ex.printStackTrace();
		}
		return message;
	}
}
