import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class ExtractFeatureForSOMGFSEUtillDecember {

	static int intervalForDeepnessRate = 10;

	public static void main(String[] args) {
		String nowTime = getNowTime();
		String pathForResult = System.getProperty("user.dir")
				+ "\\analysis\\result\\" + nowTime;
		String pathForSource = System.getProperty("user.dir")
				+ "\\analysis\\source\\";
		makeDirectory(pathForResult);
		// transformTemp(pathForResult + "\\temp", pathForSource);
		// transformTempCombain(pathForResult + "\\tempCombain", pathForResult
		// + "\\temp");
		// extractFromTempCombain(pathForResult + "\\extractFromTempCombain",
		// pathForResult + "\\tempCombain");
		extractFromTempCombain(pathForResult + "\\extractFromTempCombain",
				pathForSource);
	}

	public static void extractFromTempCombain(String pathForResult,
			String pathForSource) {
		System.out.println(">>extractFromTempCombain");
		makeDirectory(pathForResult);
		String writeData="", readData = "", filename="test";
		File file = new File(pathForSource);
		String deviceAndSubjectData = "";
		File files[] = file.listFiles();
		// 取得した一覧を表示する
		for (int i = 0; i < files.length; i++) {
			writeData = "";
			// System.out.println("ファイル" + (i+1) + "→" + files[i]);
			String getFilename = files[i].toString();
			// System.out.println(getFilename);
			// Fileを読み出し
			readData = readFile(getFilename);

			//
			String splitN[] = readData.split("\n");

			// 抽出
			Long diffStartTime, diffEndTime;
			double ratioGestureTime, ratioDeepestRate;
			double Device1Descent, Device1Ascent, Device1DiffDescentAscent, Device1DeepestRate;
			double[] Device1Deepness = new double[intervalForDeepnessRate];
			double Device2Descent, Device2Ascent, Device2DiffDescentAscent, Device2DeepestRate;
			double[] Device2Deepness = new double[intervalForDeepnessRate];
			String gesture;

			for (int j = 0; j < splitN.length; j++) {
				String splitComma[] = splitN[j].split(",");
				int splitCommalengthSplit2 = (int) (splitComma.length / 2);

				//
				gesture = splitComma[splitComma.length - 1];

				//
				diffStartTime = Long.parseLong(splitComma[0])
						- Long.parseLong(splitComma[0 + splitCommalengthSplit2]);
				diffEndTime = Long.parseLong(splitComma[1])
						- Long.parseLong(splitComma[1 + splitCommalengthSplit2]);
				ratioGestureTime = Long.parseLong(splitComma[2])
						/ (double) Long
								.parseLong(splitComma[2 + splitCommalengthSplit2]);
				ratioDeepestRate = Double.parseDouble(splitComma[4])
						/ Double.parseDouble(splitComma[4 + splitCommalengthSplit2]);

				//
				Device1Descent = Double.parseDouble(splitComma[4])
						/ (Long.parseLong(splitComma[5]) - Long
								.parseLong(splitComma[0]))
						/ (double) Long.parseLong(splitComma[2]);
				Device2Descent = Double
						.parseDouble(splitComma[4 + splitCommalengthSplit2])
						/ (Long.parseLong(splitComma[5 + splitCommalengthSplit2]) - Long
								.parseLong(splitComma[0 + splitCommalengthSplit2]))
						/ (double) Long
								.parseLong(splitComma[2 + splitCommalengthSplit2]);
				Device1Ascent = Double.parseDouble(splitComma[4])
						/ (Long.parseLong(splitComma[1]) - Long
								.parseLong(splitComma[5]))
						/ (double) Long.parseLong(splitComma[2]);
				Device2Ascent = Double
						.parseDouble(splitComma[4 + splitCommalengthSplit2])
						/ (Long.parseLong(splitComma[1]
								+ splitCommalengthSplit2) - Long
									.parseLong(splitComma[5 + splitCommalengthSplit2]))
						/ (double) Long
								.parseLong(splitComma[2 + splitCommalengthSplit2]);
				Device1DiffDescentAscent = Device1Descent - Device1Ascent;
				Device2DiffDescentAscent = Device2Descent - Device2Ascent;
				Device1DeepestRate = Double.parseDouble(splitComma[4]);
				Device2DeepestRate = Double
						.parseDouble(splitComma[4 + splitCommalengthSplit2]);

				for (int k = 0; k < intervalForDeepnessRate; k++) {
					Device1Deepness[k] = Double.parseDouble(splitComma[8 + k]);
					Device2Deepness[k] = Double.parseDouble(splitComma[8 + k
							+ splitCommalengthSplit2]);
				}

				//
				writeData += diffStartTime + "," + diffEndTime + ","
						+ ratioGestureTime + "," + ratioDeepestRate;
				writeData += "," + Device1Descent + "," + Device1Ascent + ","
						+ Device1DiffDescentAscent + "," + Device1DeepestRate;
				for (int k = 0; k < intervalForDeepnessRate; k++) {
					writeData += "," + Device1Deepness[k];
				}
				writeData += "," + Device2Descent + "," + Device2Ascent + ","
						+ Device2DiffDescentAscent + "," + Device2DeepestRate;
				for (int k = 0; k < intervalForDeepnessRate; k++) {
					writeData += "," + Device2Deepness[k];
				}
				writeData += "," + gesture + "\n";
			}

			// filename決定
			String[] splitEn = getFilename.split("\\\\");
			String[] splitPeriod = splitEn[splitEn.length - 1].split("\\.");
			String[] splitUnderber = splitPeriod[0].split("_");
			deviceAndSubjectData = splitUnderber[1] + "_" + splitUnderber[2];
			filename = pathForResult + "\\tempCombain_" + deviceAndSubjectData
					+ getNowTime();
		}

		//System.out.println(readData);
		writeFile(filename + ".csv", writeData);

	}

	public static void transformTempCombain(String pathForResult,
			String pathForSource) {
		System.out.println(">>transformTempCombain");
		makeDirectory(pathForResult);
		String writeData, filename = "test";
		File file = new File(pathForSource);
		String deviceAndSubjectData = "";
		File files[] = file.listFiles();
		String readData[] = new String[files.length];
		// 取得した一覧を表示する
		for (int i = 0; i < files.length; i++) {
			writeData = "";
			// System.out.println("ファイル" + (i+1) + "→" + files[i]);
			String getFilename = files[i].toString();
			// System.out.println(getFilename);
			// Fileを読み出し
			readData[i] = readFile(getFilename);
			// filename決定
			String[] splitEn = getFilename.split("\\\\");
			String[] splitPeriod = splitEn[splitEn.length - 1].split("\\.");
			String[] splitUnderber = splitPeriod[0].split("_");
			deviceAndSubjectData += splitUnderber[1] + "_";

		}
		filename = pathForResult + "\\tempCombain_" + deviceAndSubjectData
				+ getNowTime();
		String splitNReadData[][] = new String[files.length][readData[0]
				.split("\n").length];
		for (int i = 0; i < readData.length; i++) {
			String splitNTemp[] = readData[i].split("\n");
			for (int j = 0; j < splitNTemp.length; j++) {
				splitNReadData[i][j] = splitNTemp[j];
			}
		}
		String tempCombain = "";
		for (int j = 0; j < readData[0].split("\n").length; j++) {
			for (int i = 0; i < readData.length; i++) {
				String splitCommaTemp[] = splitNReadData[i][j].split(",");
				for (int k = 0; k < splitCommaTemp.length - 1; k++) {
					tempCombain += splitCommaTemp[k] + ",";
				}
				if (i == readData.length - 1
						&& j == readData[0].split("\n").length - 1) {
					tempCombain += splitCommaTemp[splitCommaTemp.length - 1]
							.substring(0,
									splitCommaTemp[splitCommaTemp.length - 1]
											.length() - 2);
				} else if (i == readData.length - 1) {
					tempCombain += splitCommaTemp[splitCommaTemp.length - 1]
							.substring(0,
									splitCommaTemp[splitCommaTemp.length - 1]
											.length() - 1);
				} else {
					tempCombain += ",";
				}
			}
			tempCombain += "\n";
		}
		writeFile(filename + ".csv", tempCombain);
		//System.out.println(tempCombain);
	}

	public static void transformTemp(String pathForResult, String pathForSource) {
		System.out.println(">>transformTemp");
		makeDirectory(pathForResult);
		String writeData, readData, filename;
		File file = new File(pathForSource);
		String deviceAndSubjectData = "";
		File files[] = file.listFiles();
		// 取得した一覧を表示する
		for (int i = 0; i < files.length; i++) {
			writeData = "";
			// System.out.println("ファイル" + (i+1) + "→" + files[i]);
			String getFilename = files[i].toString();
			// System.out.println(getFilename);
			// Fileを読み出し
			readData = readFile(getFilename);
			// データ解析
			writeData += analyticsData(readData);
			// System.out.println(writeData);
			// filename決定
			String[] splitEn = getFilename.split("\\\\");
			String[] splitPeriod = splitEn[splitEn.length - 1].split("\\.");
			String[] splitUnderber = splitPeriod[0].split("_");
			deviceAndSubjectData = splitUnderber[0] + "_" + "planSubject";
			filename = pathForResult + "\\temp_" + deviceAndSubjectData;
			// Fileの書き込み
			writeFile(filename + ".csv", writeData);
		}

	}

	public static String analyticsData(String readData) {
		System.out.println(">>analyticsData");
		String result = "";
		String gesture = "";

		ArrayList<Long> time = new ArrayList<>();
		ArrayList<Double> lx = new ArrayList<>();

		double medianLx = 0.0;
		String[] splitN = readData.split("\n");
		for (int i = 0; i < splitN.length; i++) {
			String[] splitComma = splitN[i].split(",");
			if (!isDouble(splitComma[1])) {
				if (!gesture.equals("")) {
					result += extractFeature(gesture, medianLx, time, lx);
				} else {
					Collections.sort(lx);
					medianLx = lx.get((int) (lx.size() / 2));
				}
				time = new ArrayList<>();
				lx = new ArrayList<>();
				gesture = splitComma[1];
			} else {
				time.add(Long.parseLong(splitComma[0]));
				lx.add(Double.parseDouble(splitComma[1]));
			}
		}
		result += extractFeature(gesture, medianLx, time, lx);
		return result;
	}

	static String extractFeature(String gesture, double medianLx,
			ArrayList<Long> time, ArrayList<Double> lx) {
		System.out.println(">>extractFeature");
		String result = "";

		// 抽出する特徴点
		Long startTime = time.get(0);
		int startPoint = 0;
		Long endTime = time.get(time.size() - 1);
		int endPoint = time.size() - 1;
		Long gestureTime;
		double deepest;
		Long deepestTime = -1l;
		double deepestRate;

		double deepness;
		double deepnessRate[] = new double[intervalForDeepnessRate];

		// どれだけ変化したらstartおよびendとみなすかの閾値
		double thresholdLxChange = medianLx * 0.05;

		// startTime探し
		for (int i = 1; i < lx.size(); i++) {
			if (Math.abs(lx.get(i - 1) - lx.get(i)) > thresholdLxChange) {
				startTime = time.get(i - 1);
				startPoint = i - 1;
				break;
			}
		}
		// endTime探し
		for (int i = lx.size() - 2; i >= 0; i--) {
			if (Math.abs(lx.get(i) - lx.get(i + 1)) > thresholdLxChange) {
				endTime = time.get(i + 1);
				endPoint = i + 1;
				break;
			}
		}

		// gestureTime
		gestureTime = endTime - startTime;

		// maxLx探し
		double maxLx = 0.0;
		Long maxLxTime = startTime;
		for (int i = startPoint; i < endPoint + 1; i++) {
			if (lx.get(i) > maxLx) {
				maxLx = lx.get(i);
				maxLxTime = time.get(i) - startTime;
			}
		}
		// deepest,deepestTime探し
		deepest = medianLx;
		deepestTime = startTime;
		for (int i = startPoint; i < endPoint + 1; i++) {
			if (lx.get(i) < deepest) {
				deepest = lx.get(i);
				deepestTime = time.get(i) - startTime;
			}
		}
		// deepness
		deepness = (maxLx - deepest) / maxLx;

		// deepnessRate

		for (int i = startPoint; i < endPoint + 1; i++) {
			if (lx.get(i) <= 0) {
				deepnessRate[0] += 1 / (double) (endPoint - startPoint + 1);
				// System.out.println(lx.get(i) + "," + 0);
			} else {
				for (int j = 0; j < intervalForDeepnessRate; j++) {
					if (maxLx * j / (double) intervalForDeepnessRate < lx
							.get(i)
							&& lx.get(i) <= maxLx * (j + 1)
									/ (double) intervalForDeepnessRate) {
						deepnessRate[j] += 1 / (double) (endPoint - startPoint + 1);
						// System.out.println(lx.get(i) + "," + j);
					}
				}
			}
		}

		result = startTime + "," + endTime + "," + gestureTime + "," + deepness
				+ "," + deepest + "," + deepestTime + "," + maxLx + ","
				+ maxLxTime;

		for (int i = 0; i < intervalForDeepnessRate; i++) {
			result += "," + (deepnessRate[i]);
		}
		result += "," + gesture;
		return result + "\n";
	}

	// ----------------以下便利グッズ---------------------------------
	static boolean isLong(String number) {
		try {
			Long.parseLong(number);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	static boolean isDouble(String number) {
		try {
			Double.parseDouble(number);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static void writeFile(String filename, String message) {
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

	public static String readFile(String filename) {
		String message = "";
		try {
			// ファイルを読み込む
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);

			// 読み込んだファイルを１行ずつ処理する
			String line;
			// StringTokenizer token;

			if ((line = br.readLine()) != null) {
				message += line;
			}

			while ((line = br.readLine()) != null) {
				message += "\n" + line;

				// //区切り文字","で分割する
				// token = new StringTokenizer(line, ",");
				// //分割した文字を画面出力する
				// while (token.hasMoreTokens()) {
				// System.out.println(token.nextToken());
				// }
				// //System.out.println("**********");
			}
			// 終了処理
			br.close();

		} catch (IOException ex) {
			// 例外発生時処理
			ex.printStackTrace();
		}
		return message;
	}

	// 結果を格納するファイルを作成
	public static void makeDirectory(String path) {
		File newfile = new File(path);

		if (newfile.mkdirs()) {
			System.out.println(path + "の作成に成功しました");
		} else {
			System.out.println(path + "の作成に失敗しました");
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
