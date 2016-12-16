import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

class Client {
    public static void main(String args[]) {
        try {
            String server = "172.20.11.214";
            int port = Integer.parseInt("8080"); //サーバー側のポート番号
            Socket s = new Socket(server, port);

            // サーバーに数値を送信
            OutputStream os = s.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeInt(Integer.parseInt("3"));

            // 演算結果を受信
            InputStream is = s.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            int res = dis.readInt();
            System.out.println(res);

            // ストリームを閉じる
            dis.close();
            dos.close();
        }
        catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }
}