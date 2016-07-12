import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class FormatCSV {

//	private static String readData  = "";
//	private static String writeData = "";
	
	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		//ファイル名の一覧を取得する
		String readData  = "";
		String writeData = "";
		
        //File file = new File("C:\\test");
        File file = new File("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\data");
		File files[] = file.listFiles();
		
		
		writeData += "waveCountForSingle,TotalWidth,tiltAve,variance,sd,deepest/totalTop,class\n";
		
		
        //取得した一覧を表示する
        for (int i=0; i<files.length; i++) {
            //System.out.println("ファイル" + (i+1) + "→" + files[i]);
        	String filename = files[i].toString();
        	//CSVを読み出し
        	readData = readCSV(filename);
        	//データ解析
        	writeData += analyticsData(readData);
        	
        }
        
        
        //CSVに書き込み
        //writeCSV("all",writeData);
	}
	
	//データ解析
	public static String analyticsData(String data){
		String result = "";
		String gesture = "";
		ArrayList<String> startTime = new ArrayList<>();
		ArrayList<String> nowTime 	= new ArrayList<>();
		ArrayList<String> nanoTime 	= new ArrayList<>();
		ArrayList<String> lx 		= new ArrayList<>();
		
		String[] splitN = data.split("\n");
		for(int i=0;i<splitN.length;i++){
			String[] splitComma = splitN[i].split(",");
			String firstData = splitComma[0];
			if(firstData.equals("slash")||firstData.equals("up")||firstData.equals("down")||firstData.equals("roll")||firstData.equals("hide")){
				if(!gesture.equals("")){
					result += extractFeature(gesture,nanoTime,lx);
				}
				startTime 	= new ArrayList<>();
				nowTime 	= new ArrayList<>();
				nanoTime 	= new ArrayList<>();
				lx 			= new ArrayList<>();
				gesture = firstData;
				
			}
			else if(!gesture.equals("")){
				startTime.add(splitComma[0]);
				nowTime.add(splitComma[1]);
				nanoTime.add(splitComma[2]);
				lx.add(splitComma[3]);
			}
		}
		result += extractFeature(gesture,nanoTime,lx);
		
		return result;
	}
	
	//特徴点抽出
	public static String extractFeature(String gesture,ArrayList<String> nanoTime, ArrayList<String> lx){
		String result = "";
		String startPoint = "";
		String endPoint = "";
		
		System.out.println(gesture);
		
		return result;
	}
	
	// メッセージをｃｓｖに書き込み
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
}
