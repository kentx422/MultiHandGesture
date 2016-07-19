import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;


public class TransformData {
	public static void main(String[] args) {
		String nowTime = getNowTime();
		String path = System.getProperty("user.dir")+"\\result\\Transform_"+nowTime;
		makeDirectory(path);
		
		String writeData;
		String readData;
		String tempData;
		String pathForData = System.getProperty("user.dir") + "\\data";
		
        File file = new File(pathForData);
		File files[] = file.listFiles();
		
		
		
		//writeData += "waveCount,TotalWidth,tiltAve,deepest,class\n";
	
        //取得した一覧を表示する
        for (int i=0; i<files.length; i++) {
            writeData="";tempData="";
            long startTime = 0;
            int step = 0;
            int startFlag = 0;
            
        	//System.out.println("ファイル" + (i+1) + "→" + files[i]);
        	String getFilename = files[i].toString();
        	System.out.println(getFilename);
        	//Fileを読み出し
        	readData = readFile(getFilename);
        	
        	//filenameで分岐
        	String[] splitEn = getFilename.split("\\\\");
        	String[] splitPeriod = splitEn[splitEn.length-1].split("\\.");
        	String[] splitUnderber = splitPeriod[0].split("_");
        	if(splitUnderber[1].equals("ill")){
        		System.out.println("ill");
        	}
        	
        	else{
	        	String[] splitN = readData.split("\n");
	        	for (int j = 0; j < splitN.length; j++) {
					String[] splitConma = splitN[j].split(",");
					if(splitConma[1].toString().equals("0.0")){
						if(startFlag == 0){
							startTime = Long.parseLong(splitConma[0]);
							//startTime += 1.5 * 1000;
							startFlag = 1;
						}
					}
					else if(startFlag == 1){
						long time = Long.parseLong(splitConma[0]) - startTime;
						if(time>step*3000){
							System.out.println(startTime+"-"+Long.parseLong(splitConma[0])+"="+time+","+step+","+splitConma[1]);
							step++;
						}
							
					}
				}
        	}
        }
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
		            	
//		                //区切り文字","で分割する
//		                token = new StringTokenizer(line, ",");
//		                //分割した文字を画面出力する
//		                while (token.hasMoreTokens()) {
//		                    System.out.println(token.nextToken());
//		                }
//		                //System.out.println("**********");
		            }
		            //終了処理
		            br.close();

		        } catch (IOException ex) {
		            //例外発生時処理
		            ex.printStackTrace();
		        }
		        return message;
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
