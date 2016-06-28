import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//チャットサーバスレッド
public class ChatServerThread extends Thread {
	private static List<ChatServerThread> threads= new ArrayList<ChatServerThread>();//スレッド郡
	private Socket socket;//ソケット

	// コンストラクタ
	public ChatServerThread(Socket socket) {
		super();
		this.socket=socket;
		threads.add(this);
	}

	//処理
	public void run() {
		InputStream in =null;
		String message;
		int size;
		byte[] w=new byte[10240];
		try {
			//ストリーム
			in =socket.getInputStream();
			while(true) {
				try {
					//受信待ち
					size=in.read(w);

					//切断
					if (size<=0) throw new IOException();

					//読み込み
					message=new String(w,0,size,"UTF8");
					
					//全員にメッセージ送信
					sendMessageAll(message);
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

	//全員にメッセージ送信
	public void sendMessageAll(String message) {
		ChatServerThread thread;
		for (int i=0;i<threads.size();i++) {
			thread=(ChatServerThread)threads.get(i);
			if (thread.isAlive()) thread.sendMessage(this,message);
		}

		System.out.println("Send message all user:	"+message);
		//writeCSV(message);
	}

	//メッセージをｃｓｖに書き込み
	public void writeCSV(String message){
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(cal.getTime());
		try{
			String FS = File.separator;
			//File f = new File("c:"+FS+"Users"+FS+"Kurisu"+FS+"Downloads"+FS+"pleiades"+FS+"workspace"+FS+"TestSocket"+FS+"MultiHandGestureLog("+date+").csv");
			File f = new File("MultiHandGestureLog("+date+").csv");

			FileWriter fw = new FileWriter(f,true); //書き込むファイル指定。ファイルが既にあるなら、そのファイルの末尾に書き込む
			BufferedWriter bw = new BufferedWriter(fw); //バッファクラスでfwを包んであげる
			PrintWriter pw = new PrintWriter(bw); //さらに、PrintWriterで包む

			pw.write(message);
			pw.println();
			pw.close(); //ファイル閉じる
		}catch(IOException e){
			System.out.println("エラー："+e);
		}
	}
	//メッセージ送信
	public void sendMessage(ChatServerThread talker,String message){
		try {
			OutputStream out=socket.getOutputStream();
			byte[] w=message.getBytes("UTF8");
			out.write(w);
			out.flush();
		} catch (IOException e) {
		}
	}
}

