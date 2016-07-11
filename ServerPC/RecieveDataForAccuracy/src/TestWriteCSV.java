import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class TestWriteCSV {
	public static void main(String[] args){
		//FileWriter fw = null;
		//BufferedWriter bw = null;
		//PrintWriter pw = null;
		try{
			String FS = File.separator;
			File f = new File("sample.txt");

			FileWriter fw = new FileWriter(f,true); //書き込むファイル指定。ファイルが既にあるなら、そのファイルの末尾に書き込む
			BufferedWriter bw = new BufferedWriter(fw); //バッファクラスでfwを包んであげる
			PrintWriter pw = new PrintWriter(bw); //さらに、PrintWriterで包む

			pw.write("書き込む内容１行目"); //　行単位でデータ出力
			pw.write("書き込む内容２行目");

			pw.close(); //ファイル閉じる
		}catch(IOException e){
			System.out.println("エラー："+e);
		}
	}
}