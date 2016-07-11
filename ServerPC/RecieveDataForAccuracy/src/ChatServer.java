import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//チャットサーバ
public class ChatServer {
	
	static int  ImageSomeShareFlag=0;
	static long ImageSomeShareTimeStamp=0;
	static int  ImageSomeShareImageID=-1;
	
	static long startTime;
	
	
	//開始
	public void start(int port) {
		ServerSocket     server;//サーバソケット
		Socket           socket;//ソケット
		ChatServerThread thread;//スレッド

		startTime = System.currentTimeMillis();
		try {
			server=new ServerSocket(port);
			System.err.println("チャットサーバ実行開始:"+port);
			while(true) {
				try {
					//接続待機
					socket=server.accept();

					//チャットサーバスレッド開始
					thread=new ChatServerThread(socket);
					thread.start();
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
		ChatServer server=new ChatServer();
		server.start(8080);
	}
}