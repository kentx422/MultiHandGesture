import java.io.File;
import java.util.Calendar;

public class FeatureExtractionUntillDecember {

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		String path = System.getProperty("user.dir")+"\\source\\"+getNowTime();
		makeDirectory(path);
		
		
		
		
	}

	// 結果を格納するファイルを作成
	public static void makeDirectory(String path) {
		File newfile = new File(path);

		if (newfile.mkdirs()) {
			System.out.println(path + "の作成に成功しました");
		}
		else {
			System.out.println(path + "の作成に失敗しました");
		}
	}

	// 現在の時間を取得
	public static String getNowTime() {
		// 時刻取得
		Calendar calendar = Calendar.getInstance();
		String nowTime = "" + calendar.get(Calendar.YEAR);
		if (calendar.get(Calendar.MONTH) + 1 < 10) {
			nowTime += "0" + (calendar.get(Calendar.MONTH) + 1);
		} else {
			nowTime += "" + (calendar.get(Calendar.MONTH) + 1);
		}
		if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
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
}
