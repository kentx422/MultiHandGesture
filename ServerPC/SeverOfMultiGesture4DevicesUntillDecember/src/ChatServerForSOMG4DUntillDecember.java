
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;

//チャットサーバ
public class ChatServerForSOMG4DUntillDecember {

	static int  ImageSomeShareFlag=0;
	static long ImageSomeShareTimeStamp=0;
	static int  ImageSomeShareImageID=-1;

	static long startTime;

	static String nowTimeForPath;
	static String path;


	static ArrayList<String> orderList = new ArrayList<String>();
	static ArrayList<String> devices = new ArrayList<String>();
	static ArrayList<String> requestOrder = new ArrayList<String>();
	static ArrayList<String> answerOrder = new ArrayList<String>();

	static ArrayList<String> tempAnswerOrder = new ArrayList<String>();
	static ArrayList<String> tempTimeOfAnswerOrdr = new ArrayList<String>();

	//開始
	public void start(int port) {
		ServerSocket     server;//サーバソケット
		Socket           socket;//ソケット
		ChatServerThreadForSOMG4DUntillDecember thread;//スレッド


		startTime = System.currentTimeMillis();
		try {
			server=new ServerSocket(port);
			System.err.println("チャットサーバ実行開始:"+port);
			while(true) {
				try {
					//接続待機
					socket=server.accept();

					//チャットサーバスレッド開始
					thread=new ChatServerThreadForSOMG4DUntillDecember(socket);
					thread.start();
					thread.recieveMessage="";
					System.out.println("接続開始");
				} catch (IOException e) {
				}
			}
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	//メイン
	public static void main(String[] args) {
		nowTimeForPath = getNowTime();
		path = System.getProperty("user.dir")+"\\result\\"+nowTimeForPath;
		makeDirectory(path);

		ChatServerForSOMG4DUntillDecember server=new ChatServerForSOMG4DUntillDecember();
		server.start(8080);
	}

	//結果を格納するファイルを作成
	public static void makeDirectory(String path){
		File newfile = new File(path);

	    if (newfile.mkdirs()){
	      System.out.println(path+"の作成に成功しました");
	    }else{
	      System.out.println(path+"の作成に失敗しました");
	    }
	}

	//現在の時間を取得
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
