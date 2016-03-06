package com.ustc.engineering.lab410.HeartMonitor.ui.main;

public class DataFilter {

	/**
	 * 滤波系数
	 */
	public static double[] coef = new double[] { -0.000737, -0.004407,
			-0.010269, -0.009895, 0.000345, 0.006114, -0.001088, -0.004233,
			0.002859, 0.001928, -0.003856, 0.001091, 0.002873, -0.003576,
			0.000212, 0.003640, -0.003678, -0.000504, 0.004641, -0.003991,
			-0.001351, 0.005956, -0.004402, -0.002469, 0.007638, -0.004855,
			-0.003989, 0.009752, -0.005303, -0.006033, 0.012445, -0.005717,
			-0.008826, 0.015960, -0.006084, -0.012778, 0.020775, -0.006410,
			-0.018727, 0.028033, -0.006661, -0.028798, 0.040760, -0.006851,
			-0.050231, 0.071214, -0.006964, -0.133751, 0.278871, 0.659665,
			0.278871, -0.133751, -0.006964, 0.071214, -0.050231, -0.006851,
			0.040760, -0.028798, -0.006661, 0.028033, -0.018727, -0.006410,
			0.020775, -0.012778, -0.006084, 0.015960, -0.008826, -0.005717,
			0.012445, -0.006033, -0.005303, 0.009752, -0.003989, -0.004855,
			0.007638, -0.002469, -0.004402, 0.005956, -0.001351, -0.003991,
			0.004641, -0.000504, -0.003678, 0.003640, 0.000212, -0.003576,
			0.002873, 0.001091, -0.003856, 0.001928, 0.002859, -0.004233,
			-0.001088, 0.006114, 0.000345, -0.009895, -0.010269, -0.004407,
			-0.000737 };

	private double[] yi = new double[198];
	private double[] Overlap = new double[98];
	double b[] = { 1, -1 }; // 这个是DC滤波的系数
	double a[] = { 1, -0.992 }; // 这个是DC滤波的系数
	double nt_b[] = { 1.9999, -1.2359, 1.9999 };
	double nt_a[] = { 2, -1.2359, 0.9999 };
	double dc_x_last = 0; // 保存上一次的输入值，以作计算用，这个是DC滤波部分用到的
	double dc_y_last = 0; // 保存上一次的输出值，以作计算用，这个是DC滤波部分用到的
	double notch1_x_last[] = { 0, 0 };
	double notch1_y_last[] = { 0, 0 };
	double notch2_x_last[] = { 0, 0 };
	double notch2_y_last[] = { 0, 0 };

	public DataFilter() {

	}

	/**
	 * 滤波
	 *
	 * @param original
	 *            原始数据
	 * @return 滤波后数据
	 */
	public double[] Filter(double[] original) {

		double[] DC = new double[original.length]; // 初始化DC滤波部分的100个数据
		double[] notch1 = new double[original.length]; // 初始化DC滤波部分的100个数据
		double[] notch2 = new double[original.length]; // 初始化DC滤波部分的100个数据

		double[] xi = new double[original.length];

		// 先是DC部分的滤波，这里数据逐个进行，累计到100个交给低通滤波，就是LPF
		for (int i = 0; i < original.length; i++) {
			DC[i] = b[0] * original[i] + b[1] * dc_x_last - a[1] * dc_y_last;
			notch1[i] = (nt_b[0] * DC[i] + nt_b[1] * notch1_x_last[0] + nt_b[2]
					* notch1_x_last[1] - nt_a[1] * notch1_y_last[0] - nt_a[2]
					* notch1_y_last[1])
					/ nt_a[0];
			notch2[i] = (nt_b[0] * notch1[i] + nt_b[1] * notch2_x_last[0]
					+ nt_b[2] * notch2_x_last[1] - nt_a[1] * notch2_y_last[0] - nt_a[2]
					* notch2_y_last[1])
					/ nt_a[0];
			dc_x_last = original[i];
			dc_y_last = DC[i];
			notch1_x_last[1] = notch1_x_last[0];
			notch1_y_last[1] = notch1_y_last[0];
			notch1_x_last[0] = DC[i];
			notch1_y_last[0] = notch1[i];
			notch2_x_last[1] = notch2_x_last[0];
			notch2_y_last[1] = notch2_y_last[0];
			notch2_x_last[0] = notch1[i];
			notch2_y_last[0] = notch2[i];
		}
		// <---DC部分滤波结束，100个数据，下面开始低通滤波

		for (int i = 0; i < original.length; i++) {
			xi[i] = notch2[i]; // 先拿到数据
		}

		try { // 调用滤波函数，这个算法是一样的，只是我们后面另外加上重叠部分的相加计算
			DataFilter.Filtering(yi, DataFilter.coef, DataFilter.coef.length,
					new double[] { 1 }, 1, xi, 198);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < 98; i++) {
			yi[i] = yi[i] + Overlap[i]; // 计算重叠部分的相加
			Overlap[i] = yi[i + 100]; // 保存下当前的重叠部分数据，以作下次重叠相加用
		}

		double[] filterData = new double[100];
		for (int i = 0; i < 100; i++) {
			filterData[i] = yi[i]; // 保存所有滤波数据，实际实时处理中不必要这样了，可以直接画出每次处理的100个数据
		}

		return filterData;
	}

	// /**
	// * 滤波
	// *
	// * @param original
	// * 原始数据
	// * @return 滤波后数据
	// */
	// public static double[] Filter(double[] original) {
	// double[] a = new double[] { 1, -0.992 };
	// double[] b = new double[] { 1, -1 };
	// int i=0;
	//
	// double[] DC1 = new double[original.length];
	// double[] DC2 = new double[original.length];
	// double[] LPF = new double[original.length];
	// double[] notch1 = new double[original.length];
	// double[] notch2 = new double[original.length];
	// double[] Final = new double[original.length];
	// try {
	// Filtering(DC1, b, b.length, a, a.length, original, original.length);
	// Filtering(DC2, b, b.length, a, a.length, DC1, DC1.length);
	//
	// Filtering(LPF, coef, coef.length, new double[] { 1 }, 1, DC2,
	// DC2.length);
	//
	// double[] b_x_50 = new double[] { 1.8816, -1.1629, 1.8816 };
	// double[] a_x_50 = new double[] { 2, -1.163, 1.7632 };
	// Filtering(notch1, b_x_50, b_x_50.length, a_x_50, a_x_50.length,
	// LPF, LPF.length);
	// Filtering(notch2, b_x_50, b_x_50.length, a_x_50, a_x_50.length,
	// notch1, notch1.length);
	// Filtering(Final, b_x_50, b_x_50.length, a_x_50, a_x_50.length,
	// notch2, notch2.length);
	// i=1;
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// return Final;
	// }

	/**
	 * 滤波运算
	 *
	 * @param y
	 * @param b
	 * @param lenB
	 * @param a
	 * @param lenA
	 * @param x
	 * @param lenX
	 * @throws Exception
	 */
	public static void Filtering(double y[], double b[], int lenB, double a[],
								 int lenA, final double x[], int lenX) throws Exception {
		int i, j;

		if (a[0] == 0) {
			throw new Exception("a[0] cannot be zero!");
		}

		// 滤波运算其实就是卷积运算，卷积运算就是移位乘加
		if (a[0] != 1) {
			for (i = 1; i != lenA; i++)
				a[i] /= a[0];

			for (i = 0; i != lenB; i++)
				b[i] /= a[0];

			a[0] = 1;
		}
		int na = lenA - 1, nb = lenB - 1;
		int len = na > nb ? na : nb;

		y[0] = b[0] * x[0];
		for (i = 1; i < lenX; i++) {

			y[i] = 0;

			for (j = 0; j <= nb; j++) {
				if (i - j < 0)
					break;

				y[i] += b[j] * x[i - j];
			}

			for (j = 1; j <= na; j++) {
				if (i - j < 0)
					break;

				y[i] -= a[j] * y[i - j];
			}
		}

		// 十万分位四舍五入
		for (i = 0; i < y.length; i++) {
			y[i] = (double) (Math.round(y[i] * 10000)) / 10000;
		}

		return;
	}
}
