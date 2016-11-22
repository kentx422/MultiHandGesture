import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;

public class FormatCSV2 {

//	private static String readData  = "";
//	private static String writeData = "";
	//static String nowTime = "";
	//static String path = "";

	public static void main(String[] args) {
		String nowTime = getNowTime();
		String path = System.getProperty("user.dir")+"\\result\\"+nowTime;
		makeDirectory(path);
		String pathForTemp = transformTemp(path);
		transformOnly(path,pathForTemp);
		transformAll(path,pathForTemp);
		transformExcept(path, pathForTemp);
		transformSubject(path,pathForTemp);
		transformExceptSubject(path,pathForTemp);
		transformDevice(path, pathForTemp);
		transformExceptDevice(path, pathForTemp);
	}

	//結果を格納するファイルを作成
	public static void makeDirectory(String path){
		File newfile = new File(path);

	    if (newfile.mkdir()){
	      System.out.println(path+"の作成に成功しました");
	    }else{
	      System.out.println(path+"の作成に失敗しました");
	    }
	}

	//とりあえず、特徴点抽出
	public static String transformTemp(String path){
		String pathForTemp = path+"\\temp";
		makeDirectory(pathForTemp);

		String readData  = "";
		String writeData = "";
		String filename  = ""; //(all) or (only xxx) or  (except xxx) or (temp xxx)

		String deviceAndSubjectData = "";

		String pathForData = System.getProperty("user.dir") + "\\data";

        File file = new File(pathForData);
		File files[] = file.listFiles();

		//writeData += "waveCount,TotalWidth,tiltAve,deepest,class\n";

        //取得した一覧を表示する
        for (int i=0; i<files.length; i++) {
            writeData="";
        	//System.out.println("ファイル" + (i+1) + "→" + files[i]);
        	String getFilename = files[i].toString();
        	//System.out.println(getFilename);
        	//Fileを読み出し
        	readData = readFile(getFilename);
        	//データ解析
        	writeData += analyticsData(readData);
        	//filename決定
        	String[] splitEn = getFilename.split("\\\\");
        	String[] splitPeriod = splitEn[splitEn.length-1].split("\\.");
        	String[] splitUnderber = splitPeriod[0].split("_");
        	deviceAndSubjectData = splitUnderber[0]+"_"+splitUnderber[1];
        	filename = pathForTemp+"\\temp_"+deviceAndSubjectData;
        	//Fileの書き込み
        	writeFile(filename+".csv",writeData);
        }
        return pathForTemp;
	}

	public static void transformOnly(String path, String pathForTemp){
		String pathForOnly = path+"\\only";
		makeDirectory(pathForOnly);

		String readData  = "";
		String writeData = "";
		String filename  = ""; //(all) or (only xxx) or  (except xxx) or (temp xxx)

		String deviceAndSubjectData = "";
		String subject = "";
		String device  = "";

        File file = new File(pathForTemp);
		File files[] = file.listFiles();


		 //取得した一覧を表示する
        for (int i=0; i<files.length; i++) {
        	writeData = "waveCount,TotalWidth,tiltAve,deepest,class\n";
        	String getFilename = files[i].toString();
        	//Fileを読み出し
        	readData = readFile(getFilename);
        	writeData += readData;
        	//filename決定
        	String[] splitEn = getFilename.split("\\\\");
        	String[] splitPeriod = splitEn[splitEn.length-1].split("\\.");
        	String[] splitUnderber = splitPeriod[0].split("_");
        	subject = splitUnderber[1];
        	device  = splitUnderber[2];
        	//deviceAndSubjectData = splitUnderber[1]+"_"+splitUnderber[2];
        	filename = pathForOnly+"\\only_"+subject+"_"+device;
        	//Fileの書き込み
        	writeFile(filename+".csv",writeData);
        }
	}

	public static void transformAll(String path, String pathForTemp){
		String pathForAll = path+"\\all";
		makeDirectory(pathForAll);

		String readData  = "";
		String writeData = "";
		String filename  = ""; //(all) or (only xxx) or  (except xxx) or (temp xxx)

        File file = new File(pathForTemp);
		File files[] = file.listFiles();


		ArrayList<String> subjects = new ArrayList<String>();
		ArrayList<String> devices  = new ArrayList<String>();
		String subject = "";
		String device  = "";

		writeData = "waveCount,TotalWidth,tiltAve,deepest,class\n";

		//取得した一覧を表示する
        for (int i=0; i<files.length; i++) {
            String getFilename = files[i].toString();
        	//Fileを読み出し
        	readData = readFile(getFilename);
        	writeData += readData;
        	//filename決定
        	String[] splitEn = getFilename.split("\\\\");
        	String[] splitPeriod = splitEn[splitEn.length-1].split("\\.");
        	String[] splitUnderber = splitPeriod[0].split("_");
        	subject = splitUnderber[1];
        	device  = splitUnderber[2];
        	if(subjects.indexOf(subject)==-1){
        		subjects.add(subject);
        	}
        	if(devices.indexOf(device)==-1){
        		devices.add(device);
        	}
        }

        filename = pathForAll+"\\all_"+subjects.size()+"subjects_"+devices.size()+"devices";
    	//Fileの書き込み
    	writeFile(filename+".csv",writeData);
	}

	public static void transformExcept(String path, String pathForTemp){
		String pathForExcept = path+"\\except";
		makeDirectory(pathForExcept);

		String readData  = "";
		String writeData = "";
		String filename  = ""; //(all) or (only xxx) or  (except xxx) or (temp xxx)

        File file = new File(pathForTemp);
		File files[] = file.listFiles();


		ArrayList<String> subjects = new ArrayList<String>();
		ArrayList<String> devices  = new ArrayList<String>();
		String subject = "";
		String device  = "";
		int subjectsNum = 0;
		int devicesNum  = 0;



		 //取得した一覧を表示する
        for (int i=0; i<files.length; i++) {
        	writeData = "waveCount,TotalWidth,tiltAve,deepest,class\n";
        	String getFilename = files[i].toString();
        	for(int j=0; j<files.length; j++){
        		if(!files[j].toString().equals(getFilename)){
        			String getExceptFilename = files[i].toString();
        			//Fileを読み出し
                	readData = readFile(getExceptFilename);
                	writeData += readData;
        		}
        	}
        	//filename決定
        	String[] splitEn = getFilename.split("\\\\");
        	String[] splitPeriod = splitEn[splitEn.length-1].split("\\.");
        	String[] splitUnderber = splitPeriod[0].split("_");
        	subject = splitUnderber[1];
        	device  = splitUnderber[2];
        	filename = pathForExcept+"\\except_"+subject+"_"+device;
        	//Fileの書き込み
        	//System.out.println(i+":"+writeData.length());
        	writeFile(filename+".csv",writeData);
//            String getFilename = files[i].toString();
//        	//Fileを読み出し
//        	readData = readFile(getFilename);
//        	writeData += readData;
//        	//filename決定
//        	String[] splitEn = getFilename.split("\\\\");
//        	String[] splitUnderber = splitEn[splitEn.length-1].split("_");
//        	subject = splitUnderber[1];
//        	device  = splitUnderber[2];
//        	if(subjects.indexOf(subject)==-1){
//        		subjects.add(subject);
//        		subjectsNum++;
//        	}
//        	if(devices.indexOf(device)==-1){
//        		devices.add(device);
//        		devicesNum++;
//        	}
        }


	}


	public static void transformSubject(String path, String pathForTemp){
		String pathForSubject = path+"\\subject";
		makeDirectory(pathForSubject);

		String readData  = "";
		String writeData = "";
		String filename  = ""; //(all) or (only xxx) or  (except xxx) or (temp xxx)

        File file = new File(pathForTemp);
		File files[] = file.listFiles();


		ArrayList<String> subjects = new ArrayList<String>();
		ArrayList<String> devices  = new ArrayList<String>();
		String subject = "";
		String device  = "";

		writeData = "waveCount,TotalWidth,tiltAve,deepest,class\n";

		 //被験者数を取得
        for (int i=0; i<files.length; i++) {
            String getFilename = files[i].toString();
        	//Fileを読み出し
        	//readData = readFile(getFilename);
        	//filename決定
        	String[] splitEn = getFilename.split("\\\\");
        	String[] splitUnderber = splitEn[splitEn.length-1].split("_");
        	subject = splitUnderber[1];
        	device  = splitUnderber[2];
        	if(subjects.indexOf(subject)==-1){
        		subjects.add(subject);
        	}
        }

        String[] subjectsData = new String[subjects.size()];
        int[] deviceNumPerSubject = new int[subjects.size()];
        subjects = new ArrayList<String>();


        for (int i=0; i<files.length; i++) {
            String getFilename = files[i].toString();
        	//Fileを読み出し
        	readData = readFile(getFilename);
        	//filename決定
        	String[] splitEn = getFilename.split("\\\\");
        	String[] splitUnderber = splitEn[splitEn.length-1].split("_");
        	subject = splitUnderber[1];
        	device  = splitUnderber[2];

        	if(subjects.indexOf(subject)==-1){
        		subjects.add(subject);
        		subjectsData[subjects.indexOf(subject)] = writeData + readData;
        		deviceNumPerSubject[subjects.indexOf(subject)]=1;
        	}
        	else{
        		subjectsData[subjects.indexOf(subject)] += readData;
        		deviceNumPerSubject[subjects.indexOf(subject)]++;
        	}
        }

        for(int i=0;i<subjectsData.length;i++){
        	//filenameの決定
        	filename = pathForSubject+"\\subject_"+subjects.get(i)+"_"+deviceNumPerSubject[i]+"devices";
        	//Fileの書き込み
        	writeFile(filename+".csv",subjectsData[i]);
        }
	}



	public static void transformExceptSubject(String path, String pathForTemp){
		String pathForExceptSubject = path+"\\exceptSubject";
		makeDirectory(pathForExceptSubject);

		String readData  = "";
		String writeData = "";
		String filename  = ""; //(all) or (only xxx) or  (except xxx) or (temp xxx)

        File file = new File(pathForTemp);
		File files[] = file.listFiles();


		ArrayList<String> subjects = new ArrayList<String>();
		ArrayList<String> devices  = new ArrayList<String>();
		String subject = "";
		String device  = "";

		writeData = "waveCount,TotalWidth,tiltAve,deepest,class\n";

		 //被験者数を取得
        for (int i=0; i<files.length; i++) {
            String getFilename = files[i].toString();
        	//Fileを読み出し
        	//readData = readFile(getFilename);
        	//filename決定
            String[] splitEn = getFilename.split("\\\\");
        	String[] splitPeriod = splitEn[splitEn.length-1].split("\\.");
        	String[] splitUnderber = splitPeriod[0].split("_");
        	subject = splitUnderber[1];
        	device  = splitUnderber[2];
        	if(subjects.indexOf(subject)==-1){
        		subjects.add(subject);
        	}
        }

        String[] exceptSubjectsData = new String[subjects.size()];
        int[] deviceNumPerSubject = new int[subjects.size()];

        if(subjects.size()>1){
        	for(int i=0;i<subjects.size();i++){
        		exceptSubjectsData[i]=writeData;
        		deviceNumPerSubject[i]=0;
        		 for (int j=0; j<files.length; j++) {
                     String getFilename = files[j].toString();
                 	//Fileを読み出し
                 	readData = readFile(getFilename);
                 	//filename決定
                 	String[] splitEn = getFilename.split("\\\\");
                	String[] splitPeriod = splitEn[splitEn.length-1].split("\\.");
                	String[] splitUnderber = splitPeriod[0].split("_");
                	subject = splitUnderber[1];
                	device  = splitUnderber[2];
                	if(!subjects.get(i).equals(subject)){
                		exceptSubjectsData[i] += readData;
                		deviceNumPerSubject[i]++;
                	}
        		 }
        	}

            for(int i=0;i<exceptSubjectsData.length;i++){
            	//filenameの決定
            	filename = pathForExceptSubject+"\\exceptSubject_"+subjects.get(i)+"_"+deviceNumPerSubject[i]+"devices";
            	//Fileの書き込み
            	writeFile(filename+".csv",exceptSubjectsData[i]);
            }
        }
	}

	public static void transformDevice(String path, String pathForTemp){
		String pathForDevice = path+"\\device";
		makeDirectory(pathForDevice);

		String readData  = "";
		String writeData = "";
		String filename  = ""; //(all) or (only xxx) or  (except xxx) or (temp xxx)

        File file = new File(pathForTemp);
		File files[] = file.listFiles();


		ArrayList<String> subjects = new ArrayList<String>();
		ArrayList<String> devices  = new ArrayList<String>();
		String subject = "";
		String device  = "";

		writeData = "waveCount,TotalWidth,tiltAve,deepest,class\n";

		 //被験者数を取得
        for (int i=0; i<files.length; i++) {
            String getFilename = files[i].toString();
        	//Fileを読み出し
        	//readData = readFile(getFilename);
        	//filename決定
            String[] splitEn = getFilename.split("\\\\");
        	String[] splitPeriod = splitEn[splitEn.length-1].split("\\.");
        	String[] splitUnderber = splitPeriod[0].split("_");
        	subject = splitUnderber[1];
        	device  = splitUnderber[2];
        	if(devices.indexOf(device)==-1){
        		devices.add(device);
        	}
        }

        String[] devicesData = new String[devices.size()];
        int[] subjectsNumPerDevice = new int[devices.size()];
        devices = new ArrayList<String>();


        for (int i=0; i<files.length; i++) {
            String getFilename = files[i].toString();
        	//Fileを読み出し
        	readData = readFile(getFilename);
        	//filename決定
        	String[] splitEn = getFilename.split("\\\\");
        	String[] splitPeriod = splitEn[splitEn.length-1].split("\\.");
        	String[] splitUnderber = splitPeriod[0].split("_");
        	subject = splitUnderber[1];
        	device  = splitUnderber[2];

        	if(devices.indexOf(device)==-1){
        		devices.add(device);
        		devicesData[devices.indexOf(device)] = writeData + readData;
        		subjectsNumPerDevice[devices.indexOf(device)]=1;
        	}
        	else{
        		devicesData[devices.indexOf(device)] += readData;
        		subjectsNumPerDevice[devices.indexOf(device)]++;
        	}
        }

        for(int i=0;i<devicesData.length;i++){
        	//filenameの決定
        	filename = pathForDevice+"\\device_"+devices.get(i)+"_"+subjectsNumPerDevice[i]+"subjects";
        	//Fileの書き込み
        	writeFile(filename+".csv",devicesData[i]);
        }
	}



	public static void transformExceptDevice(String path, String pathForTemp){
		String pathForExceptSubject = path+"\\exceptDevice";
		makeDirectory(pathForExceptSubject);

		String readData  = "";
		String writeData = "";
		String filename  = ""; //(all) or (only xxx) or  (except xxx) or (temp xxx)

        File file = new File(pathForTemp);
		File files[] = file.listFiles();


		ArrayList<String> subjects = new ArrayList<String>();
		ArrayList<String> devices  = new ArrayList<String>();
		String subject = "";
		String device  = "";

		writeData = "waveCount,TotalWidth,tiltAve,deepest,class\n";

		 //被験者数を取得
        for (int i=0; i<files.length; i++) {
            String getFilename = files[i].toString();
        	//Fileを読み出し
        	//readData = readFile(getFilename);
        	//filename決定
            String[] splitEn = getFilename.split("\\\\");
        	String[] splitPeriod = splitEn[splitEn.length-1].split("\\.");
        	String[] splitUnderber = splitPeriod[0].split("_");
        	subject = splitUnderber[1];
        	device  = splitUnderber[2];
        	if(devices.indexOf(device)==-1){
        		devices.add(device);
        	}
        }

        String[] exceptDevicesData = new String[devices.size()];
        int[] subjectsNumPerDevice = new int[devices.size()];

        if(devices.size()>1){
        	for(int i=0;i<devices.size();i++){
        		exceptDevicesData[i]=writeData;
        		subjectsNumPerDevice[i]=0;
        		 for (int j=0; j<files.length; j++) {
                     String getFilename = files[j].toString();
                 	//Fileを読み出し
                 	readData = readFile(getFilename);
                 	//filename決定
                 	String[] splitEn = getFilename.split("\\\\");
                	String[] splitPeriod = splitEn[splitEn.length-1].split("\\.");
                	String[] splitUnderber = splitPeriod[0].split("_");
                	subject = splitUnderber[1];
                	device  = splitUnderber[2];
                	if(!devices.get(i).equals(device)){
                		exceptDevicesData[i] += readData;
                		subjectsNumPerDevice[i]++;
                	}
        		 }
        	}

            for(int i=0;i<exceptDevicesData.length;i++){
            	//filenameの決定
            	filename = pathForExceptSubject+"\\exceptDevice_"+devices.get(i)+"_"+subjectsNumPerDevice[i]+"subjects";
            	//Fileの書き込み
            	writeFile(filename+".csv",exceptDevicesData[i]);
            }
        }
	}


	//データ解析
	public static String analyticsData(String data){
		String result = "";
		String gesture = "";
		ArrayList<String> startTime = new ArrayList<>();
		ArrayList<String> nowTime 	= new ArrayList<>();
		ArrayList<String> nanoTime 	= new ArrayList<>();
		ArrayList<String> lux 		= new ArrayList<>();

		ArrayList<String> tempLux 	= new ArrayList<>();
		double aveLux =0.0;

		String[] splitN = data.split("\n");
		for(int i=0;i<splitN.length;i++){
			String[] splitComma = splitN[i].split(",");
			String firstData = splitComma[0];
			if(firstData.equals("slash")||firstData.equals("up")||firstData.equals("down")||firstData.equals("roll")||firstData.equals("hide")){
				if(!gesture.equals("")){
					result += extractFeature(gesture,aveLux,nanoTime,lux)+"\n";
				}
				else{
					for(int j = 1; j<tempLux.size();j++){
						aveLux += Double.parseDouble(tempLux.get(j));
					}
					aveLux /= tempLux.size()-1;
				}
				startTime 	= new ArrayList<>();
				nowTime 	= new ArrayList<>();
				nanoTime 	= new ArrayList<>();
				lux 		= new ArrayList<>();
				gesture = firstData;

			}
			else if(!gesture.equals("")){
				startTime.add(splitComma[0]);
				nowTime.add(splitComma[1]);
				nanoTime.add(splitComma[2]);
				lux.add(splitComma[3]);
			}
			else{
				if(splitComma.length==4){
					tempLux.add(splitComma[3]);
				}
			}
		}
		result += extractFeature(gesture,aveLux,nanoTime,lux)+"\n";

		return result;
	}

	//特徴点抽出
	public static String extractFeature(String gesture,Double aveLux,ArrayList<String> nanoTime, ArrayList<String> lux){
		String result = "";

		int startPoint = 0;
		int endPoint = lux.size()-1;
		int maxPoint = 0;
		int minPoint = 0;

		double waveCount = 0.0;
		double totalWidth = 0.0;
		double tiltAve = 0.0;
		double deepness = 0.0;

		//どれだけ変化したらstartあるいはendとみなすかの閾値
		double threshold = aveLux*0.05;


		//lux(ArrayList<String>) >> illumiLog(ArrayList(Double))
		ArrayList<Double> illumiLog = new ArrayList<Double>();
		for(int i = 0;i<lux.size();i++){
			//System.out.println(lux.get(i));
			illumiLog.add(Double.parseDouble(lux.get(i)));
		}

		//start探し
		for(int i = 1;i<lux.size();i++){
			if(Math.abs(illumiLog.get(i-1) - illumiLog.get(i)) > threshold){
				startPoint = i-1;
				break;
			}
		}
		//end探し
		for(int i = lux.size()-2;i>=0;i--){
			if(Math.abs(illumiLog.get(i) - illumiLog.get(i+1)) > threshold){
				endPoint = i+1;
				break;
			}
		}

		//max探し
		double max = 0.0;
		for(int i = 0;i<lux.size();i++){
			if(illumiLog.get(i) > max){
				maxPoint = i;
				max = illumiLog.get(i);
			}
		}
		//max探し
		double min = aveLux;
		for(int i = 0;i<lux.size();i++){
			if(illumiLog.get(i) < min){
				minPoint = i;
				min = illumiLog.get(i);
			}
		}


//		System.out.println(startPoint+","+endPoint);
//		System.out.println(illumiLog.get(startPoint)+","+illumiLog.get(endPoint));

		//weveCount
		waveCount = judgeWaveNum(illumiLog, startPoint, endPoint, aveLux);

		//totalWidth msで表現
		totalWidth = (Double.parseDouble(nanoTime.get(endPoint)) - Double.parseDouble(nanoTime.get(startPoint)))/1000000.0;

		//tiltAve
		double ts = (Double.parseDouble(nanoTime.get(minPoint)) - Double.parseDouble(nanoTime.get(startPoint)))/1000000.0;
		double te = (Double.parseDouble(nanoTime.get(endPoint)) - Double.parseDouble(nanoTime.get(minPoint)))/1000000.0;
		double A  = illumiLog.get(maxPoint) - illumiLog.get(minPoint);
		tiltAve = A/ts - A/te;

		//deepness
		deepness = A / illumiLog.get(maxPoint);


		//System.out.println(illumiLog.get(startPoint)+","+illumiLog.get(endPoint));
		result = waveCount+","+totalWidth+","+tiltAve+","+deepness+","+gesture;
		//System.out.println(result);

		return result;
	}


	// メッセージをFileに書き込み
		public static void writeFile(String filename,String message) {
			try {
				File f = new File(filename);
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

		//Fileの読み出し
		public static String readFile(String filename) {
	        String message = "";
			try {
	            //ファイルを読み込む
	            FileReader fr = new FileReader(filename);
	            BufferedReader br = new BufferedReader(fr);

	            //読み込んだファイルを１行ずつ処理する
	            String line;
	            //StringTokenizer token;

	            if((line=br.readLine())!=null){
	            	message += line;
	            }

	            while ((line = br.readLine()) != null) {
	            	message += "\n"+line;

//	                //区切り文字","で分割する
//	                token = new StringTokenizer(line, ",");
//	                //分割した文字を画面出力する
//	                while (token.hasMoreTokens()) {
//	                    System.out.println(token.nextToken());
//	                }
//	                //System.out.println("**********");
	            }
	            //終了処理
	            br.close();

	        } catch (IOException ex) {
	            //例外発生時処理
	            ex.printStackTrace();
	        }
	        return message;
	    }


	// メッセージをTXTに書き込み
	public static void writeTXT(String filename,String message) {
		try {
			//String FS = File.separator;
			// File f = new
			// File("c:"+FS+"Users"+FS+"Kurisu"+FS+"Downloads"+FS+"pleiades"+FS+"workspace"+FS+"TestSocket"+FS+"MultiHandGestureLog("+date+").csv");
			File f = new File("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\list\\"+filename + ".txt");

			FileWriter fw = new FileWriter(f, true); // 書き込むファイル指定。（true 追記 / false 上書き）ファイルが既にあるなら、そのファイルの末尾に書き込む
			BufferedWriter bw = new BufferedWriter(fw); // バッファクラスでfwを包んであげる
			PrintWriter pw = new PrintWriter(bw); // さらに、PrintWriterで包む

			pw.write(message);
			pw.println();
			pw.close(); // ファイル閉じる
		} catch (IOException e) {
			System.out.println("エラー：" + e);
		}
	}

	// メッセージをCSVに書き込み
	public static void writeCSV(String filename,String message) {
		//Calendar cal = Calendar.getInstance();
		try {
			//String FS = File.separator;
			// File f = new
			// File("c:"+FS+"Users"+FS+"Kurisu"+FS+"Downloads"+FS+"pleiades"+FS+"workspace"+FS+"TestSocket"+FS+"MultiHandGestureLog("+date+").csv");
			File f = new File("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\result\\"+filename + ".csv");

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

	//CSVの読み出し
	public static String readCSV(String filename) {
        String message = "";
		try {
            //ファイルを読み込む
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);

            //読み込んだファイルを１行ずつ処理する
            String line;
            //StringTokenizer token;

            if((line=br.readLine())!=null){
            	message += line;
            }

            while ((line = br.readLine()) != null) {
            	message += "\n"+line;

//                //区切り文字","で分割する
//                token = new StringTokenizer(line, ",");
//
//                //分割した文字を画面出力する
//                while (token.hasMoreTokens()) {
//                    System.out.println(token.nextToken());
//                }
//                //System.out.println("**********");
            }
            //終了処理
            br.close();

        } catch (IOException ex) {
            //例外発生時処理
            ex.printStackTrace();
        }
        return message;
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



	//----------以下、ジェスチャ認識------------
	public static String judgeGesture(ArrayList<Double>illumiLog, ArrayList<Long>timeDataLog, int start, int end){
//      int startTime = 0;
//      int endTime   = (int)(end-start);

      //end特化型認識
      double max = ((illumiLog.get(start)+illumiLog.get(end))/2);
      int bottom = 0;

      for (int i = start; i <= end; i++) {;
          if (i > 0 && illumiLog.get(i) < illumiLog.get(bottom)) bottom = i;
      }

      double Ts = (timeDataLog.get(bottom)-timeDataLog.get(start));
      double Te = (timeDataLog.get(end)-timeDataLog.get(bottom));

      double A= max - illumiLog.get(bottom);
      double deepness = (double)A/(double)max;
      double slope = (double)A/(double)Ts-(double)A/(double)Te;
//      Log.d("slope",String.valueOf(slope));
//      Log.d("A",String.valueOf(A));
//      Log.d("Ts",String.valueOf(Ts));
//      Log.d("Te",String.valueOf(Te));a
      double time  = (double)(Ts+Te);
      //wave特化型認識
      double wave = judgeWaveNum(illumiLog,start,end,max);

      //全ジェスチャ
//      if (deepness >= dps) return "HIDE";
//      else if (wave >= wav) return "ROLL";
//      else if (time >= tse) {
//          if (slope >= slp) return "UP";
//              //if(St<0) gesture = 2;
//          else return "DOWN";
//      } else {
//          if (slope >= 130) return "UP";
//          else return "SLASH";
//      }
      return "";
      //startからendまでをarraylistから抽出してillumiandtimeData


      //HIDEとSLASHのジェスチャ
//      if (deepness >= dps) return "HIDE";
//      else return "SLASH";

  }

  //波特化型認識
  public static double judgeWaveNum(ArrayList<Double> illumiLog, int start, int end, double max){
      int waveFlag=0;
      double lastDiff=0.0;
      ArrayList<Double> illumiMountainLog =  new ArrayList<Double>();
      illumiMountainLog.add(max);
      for (int i=start;i<=end-1;i++){
    	  //System.out.println(illumiLog.get(i+1)+"-"+illumiLog.get(i));
          double diff = illumiLog.get(i+1)-illumiLog.get(i);
          if(Math.abs(diff)==0 || diff*lastDiff<0){
    	    illumiMountainLog.add(illumiLog.get(i));
              //System.out.println(illumiLog.get(i));
          }
          lastDiff=diff;
      }
      illumiMountainLog.add(max);

      if(illumiMountainLog.size()==2){
          return 0;
      }

      ArrayList<Double> illumiMountainClusterLog =  new ArrayList<Double>();
      illumiMountainClusterLog.add(illumiMountainLog.get(0));
      for(int i=1; i<illumiMountainLog.size();i++){
    	  double diff = illumiMountainLog.get(i-1)-illumiMountainLog.get(i);
    	  if(Math.abs(diff)>max*0.2){
    		  illumiMountainClusterLog.add(illumiMountainLog.get(i));
    	  }
      }

//      System.out.println(illumiMountainLog);
//      System.out.println(illumiMountainClusterLog);

      double lastIllumiMountainCluster = illumiMountainClusterLog.get(0);
      double lastIllumiDiff = 0.0;

      for(int i=1; i<illumiMountainClusterLog.size();i++){
          double illumiDiff = illumiMountainClusterLog.get(i)-lastIllumiMountainCluster;
          //System.out.println(illumiDiff+","+lastIllumiDiff);
          if(illumiDiff*lastIllumiDiff<0){
        	  waveFlag++;
        	  //System.out.println("wave");
          }

//          if(Math.abs(illumiDiff)>(double)max*0.2){
//              waveFlag++;
//              System.out.println("diff:"+illumiDiff);
//          }
          lastIllumiMountainCluster=illumiMountainLog.get(i);
          lastIllumiDiff = illumiDiff;
      }
      return (waveFlag+1.0)/2.0;
  }

  //end特化型認識
  public static int judgeEnd(ArrayList<Double> illumiLog, int start, int end){

      int endPoint = end;
      double startIllumi = illumiLog.get(start);
      for(int i=endPoint;i>0;i--){
          double diff = Math.abs(startIllumi-illumiLog.get(i));
          if(diff>(double)startIllumi*0.05){
              endPoint = i+1;
              return endPoint;
          }
      }
      return endPoint;
  }
}
//----------以上、ジェスチャ認識------------
