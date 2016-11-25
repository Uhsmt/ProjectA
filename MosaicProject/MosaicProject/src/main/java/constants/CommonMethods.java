package constants;
//共通系メソッド

public class CommonMethods {

	//最大値
	public static int maxValue(int a, int b, int c) {
		int max = a;
		if (max < b) max = b;
		if (max < c) max = c;
		return max;
	}

	//最小値
	public static int minValue(int a, int b, int c) {
		int min = a;
		if (min > b) min = b;
		if (min > c) min = c;

		return min;
	}


	public static double maxValue(double a, double b, double c) {
		double max = a;
		if (max < b) max = b;
		if (max < c) max = c;
		return max;
	}

	public static double minValue(double a, double b,double c) {
		double min = a;
		if (min > b) min = b;
		if (min > c) min = c;

		return min;
	}


	public static int maxValue(int a, int b) {
		int max = a;
		if (max < b) max = b;
		return max;
	}

	public static int minValue(int a, int b) {
		int min = a;
		if (min > b) min = b;
		return min;
	}

}
