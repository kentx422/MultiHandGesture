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
		
		
		writeData += "waveCount,TotalWidth,tiltAve,deepest,class\n";
		
		
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
		ArrayList<String> lux 		= new ArrayList<>();
		
		ArrayList<String> tempLux 	= new ArrayList<>();
		double aveLux =0.0;
		
		String[] splitN = data.split("\n");
		for(int i=0;i<splitN.length;i++){
			String[] splitComma = splitN[i].split(",");
			String firstData = splitComma[0];
			if(firstData.equals("slash")||firstData.equals("up")||firstData.equals("down")||firstData.equals("roll")||firstData.equals("hide")){
				if(!gesture.equals("")){
					result += extractFeature(gesture,aveLux,nanoTime,lux);
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
		result += extractFeature(gesture,aveLux,nanoTime,lux);
		
		return result;
	}
	
	//特徴点抽出
	public static String extractFeature(String gesture,Double aveLux,ArrayList<String> nanoTime, ArrayList<String> lux){
		String result = "";
		
		int startPoint = -1;
		int endPoint = -1;
		
		double waveCount = 0.0;
		double totalWidth = 0.0;
		double tiltAve = 0.0;
		double deepness = 0.0;

		//どれだけ変化したらstartあるいはendとみなすかの閾値
		double threshold = aveLux*0.02;
		
		
		//lux(ArrayList<String>) >> illumiLog(ArrayList(Double))
		ArrayList<Double> illumiLog = new ArrayList<Double>();
		for(int i = 0;i<lux.size();i++){
			illumiLog.add(Double.parseDouble(lux.get(i)));
		}
		
		//start探し
		for(int i = 0;i<lux.size();i++){
			if(Math.abs(aveLux - illumiLog.get(i)) > threshold){
				startPoint = i-1;
				break;
			}
		}
		//end探し
		for(int i = lux.size()-1;i>=0;i--){
			if(Math.abs(aveLux - illumiLog.get(i)) > threshold){
				endPoint = i+1;
				break;
			}
		}
		
		//weveCount
		waveCount = judgeWaveNum(illumiLog, startPoint, endPoint, aveLux);
		
		
		
		System.out.println(illumiLog.get(startPoint)+","+illumiLog.get(endPoint));
		result = waveCount+","+totalWidth+","+tiltAve+","+deepness+","+gesture;
		System.out.println(result);
		
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
      for (int i=start;i<=end;i++){
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
