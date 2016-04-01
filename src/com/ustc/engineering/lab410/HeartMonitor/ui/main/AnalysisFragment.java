package com.ustc.engineering.lab410.HeartMonitor.ui.main;

import com.ustc.engineering.lab410.HeartMonitor.R;
import com.ustc.engineering.lab410.HeartMonitor.ui.widget.PopWindow;
import com.ustc.engineering.lab410.HeartMonitor.utils.MyFileManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnalysisFragment extends Fragment implements View.OnClickListener {


	private View mRootView = null;
	private Button btnOpen;
	private Button btnCanShu;
	private Button btnXianShi;
	private Button btnDaoLian;
	private Button btnBaoCun;
	private Button btnOpen1;
	private TextView txtHeartRate;
	private RelativeLayout dynamic_chart_line_layout;
	private PopWindow popWinShare;
	// 用于存放每条折线的点数据
	private XYSeries line1;
	// 用于存放所有需要绘制的XYSeries
	private XYMultipleSeriesDataset mDataset;
	// 用于存放每条折线的风格
	private XYSeriesRenderer renderer1, renderer2;
	// 用于存放所有需要绘制的折线的风格
	private XYMultipleSeriesRenderer mXYMultipleSeriesRenderer;
	private GraphicalView chart;
	private String title = "心电信息";
	private String mDealString ="";//处理用数据缓存
	private String mDealData ="";//处理用数据缓存
	public  String filename=""; //用来保存存储的文件名
	public static final int FILE_RESULT_CODE = 1;
	public static final int FILE_RESULT_CODE1 = 2;
	private int curDaoLian = 1;
	private double[] y;
	private List<Double> ES_list = new ArrayList<Double>();
	private List<Double> AS_list = new ArrayList<Double>();
	private List<Double> AI_list = new ArrayList<Double>();
	private List<Double> x_list = new ArrayList<Double>();
	private List<Double> y_list = new ArrayList<Double>();

	private List<Double> newData_list = new ArrayList<Double>();
	private List<Double> newY_list = new ArrayList<Double>();


	private String//参数设置
			value11,value21,value31,value41,value51,value61,
			value71,value81,value91,value101,value111,value121,
			value13,value23,value33,value43,value53,value63,
			value73,value83,value93,value103,value113,value123,
			value15,value25,value35,value45,value55,value65,
			value75,value85,value95,value105,value115,value125;

	private float //参数设置
			value12,value14,value16,
			value22,value24,value26,
			value32,value34,value36,
			value42,value44,value46,
			value52,value54,value56,
			value62,value64,value66,
			value72,value74,value76,
			value82,value84,value86,
			value92,value94,value96,
			value102,value104,value106,
			value112,value114,value116,
			value122,value124,value126;

	/**
	 * 滤波系数
	 */
	public static double[] coefb = new double[] {
			0.067341990079247, 4.485877674603527e-17,
			-0.134683980158494, -1.495292558201176e-17,
			0.067341990079247};
	public static double[] coefa = new double[] {
			1,-3.143150780789496,3.699701744353724,
			-1.969711347520458,0.413160490504384};

	public static double[] coef1b = new double[] {
			0.887839755524807,-1.44797259742166,0.887839755524807};
	public static double[] coef1a = new double[] {
			1,-1.44797259742166,0.775679511049613};

	//心率计算
	// We need some initial data...
	private static double THRESHOLD_PARAM = 8;
	private static double FILTER_PARAMETER = 16;
	private static int SAMPLE_RATE = 500;
	@Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {

		  if (mRootView == null) {
			  mRootView = inflater.inflate(R.layout.analysis_fragment,
					  container, false);
			  initView();
			  initChart();

		  }
		  ViewGroup parent = (ViewGroup) mRootView.getParent();
		  if (parent != null) {
			  parent.removeView(mRootView);
		  }
		  return mRootView;
	  }

	private void initView(){
		dynamic_chart_line_layout = (RelativeLayout) mRootView.findViewById(R.id.chart);

		txtHeartRate = (TextView) mRootView.findViewById(R.id.heartRate);
		btnOpen = (Button) mRootView.findViewById(R.id.open);
		btnCanShu = (Button) mRootView.findViewById(R.id.canshu);
		btnXianShi = (Button) mRootView.findViewById(R.id.xianshi);
		btnDaoLian = (Button) mRootView.findViewById(R.id.daolian);
		btnBaoCun = (Button) mRootView.findViewById(R.id.baocun);
		btnOpen1 = (Button) mRootView.findViewById(R.id.open1);
		btnOpen.setOnClickListener(this);
		btnCanShu.setOnClickListener(this);
		btnXianShi.setOnClickListener(this);
		btnDaoLian.setOnClickListener(this);
		btnBaoCun.setOnClickListener(this);
		btnOpen1.setOnClickListener(this);
	}

	//aChartEngine初始化
	private void initChart() {
		//
		// TODO Auto-generated method stub
		// 初始化，必须保证XYMultipleSeriesDataset对象中的XYSeries数量和
		// XYMultipleSeriesRenderer对象中的XYSeriesRenderer数量一样多
		line1 = new XYSeries("折线1");
		renderer1 = new XYSeriesRenderer();
		mDataset = new XYMultipleSeriesDataset();
		mXYMultipleSeriesRenderer = new XYMultipleSeriesRenderer();

		// 对XYSeries和XYSeriesRenderer的对象的参数赋值
		// initLine(line1);
		// initLine(line2);
		initRenderer(renderer1, Color.GREEN, PointStyle.CIRCLE, true);

		// 将XYSeries对象和XYSeriesRenderer对象分别添加到XYMultipleSeriesDataset对象和XYMultipleSeriesRenderer对象中
		mDataset.addSeries(line1);
		mXYMultipleSeriesRenderer.addSeriesRenderer(renderer1);
		mXYMultipleSeriesRenderer.setZoomButtonsVisible(true);

		// 配置chart参数
		setChartSettings(mXYMultipleSeriesRenderer, "X", "Y", -150, 1600, -0.01, 0.01, Color.RED,
				Color.WHITE);

		// 通过该函数获取到一个View 对象
		chart = ChartFactory.getCubeLineChartView(getActivity().getApplicationContext(), mDataset, mXYMultipleSeriesRenderer, 0.05f);
		// 将该View 对象添加到layout中
		dynamic_chart_line_layout.addView(chart, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));

	}

	private XYSeriesRenderer initRenderer(XYSeriesRenderer renderer, int color,
										  PointStyle style, boolean fill) {
		// 设置图表中曲线本身的样式，包括颜色、点的大小以及线的粗细等
		renderer.setColor(color);
		//renderer.setPointStyle(style);
		//renderer.setFillPoints(fill);
		renderer.setLineWidth(3);
		return renderer;
	}

	protected void setChartSettings(XYMultipleSeriesRenderer mXYMultipleSeriesRenderer,
									String xTitle, String yTitle, double xMin, double xMax,
									double yMin, double yMax, int axesColor, int labelsColor) {
		// 有关对图表的渲染可参看api文档
		//mXYMultipleSeriesRenderer.setChartTitle(title);
		mXYMultipleSeriesRenderer.setXTitle(xTitle);
		mXYMultipleSeriesRenderer.setYTitle(yTitle);
		mXYMultipleSeriesRenderer.setXAxisMin(xMin);
		mXYMultipleSeriesRenderer.setAxisTitleTextSize(30);
		mXYMultipleSeriesRenderer.setChartTitleTextSize(50);
		mXYMultipleSeriesRenderer.setLabelsTextSize(15);
		mXYMultipleSeriesRenderer.setXAxisMax(xMax);
		mXYMultipleSeriesRenderer.setYAxisMin(yMin);
		mXYMultipleSeriesRenderer.setYAxisMax(yMax);
		mXYMultipleSeriesRenderer.setAxesColor(axesColor);
		mXYMultipleSeriesRenderer.setLabelsColor(labelsColor);
		mXYMultipleSeriesRenderer.setShowGrid(true);
		mXYMultipleSeriesRenderer.setGridColor(Color.parseColor("#EAEAEA"));
		mXYMultipleSeriesRenderer.setXLabels(20);
		mXYMultipleSeriesRenderer.setYLabels(10);
		mXYMultipleSeriesRenderer.setXTitle("time");
		mXYMultipleSeriesRenderer.setYLabelsAlign(Paint.Align.RIGHT);
		mXYMultipleSeriesRenderer.setPointSize((float) 5);
		mXYMultipleSeriesRenderer.setShowLegend(true);
		mXYMultipleSeriesRenderer.setLegendTextSize(20);
	}

	/**
	 * 添加新的数据，多组，更新曲线，只能运行在主线程
	 * @param xList
	 * @param yList
	 */
	public void updateChart(List<Double> xList, List<Double> yList, XYSeries series) {
		series.clear();
		for (int i = 0; i < xList.size(); i++) {
			series.add(xList.get(i), yList.get(i));
		}
		chart.repaint();
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.open:
				Intent intent = new Intent(getActivity(),MyFileManager.class);
				startActivityForResult(intent, FILE_RESULT_CODE);
				break;
			case R.id.canshu:
				showDialogs();
				break;
			case R.id.xianshi:
				xianshi();
				break;
			case R.id.daolian:
				showPopwindow();
				break;
			case R.id.baocun:
				save();
				break;
			case R.id.open1:
				Intent intent1 = new Intent(getActivity(),MyFileManager.class);
				startActivityForResult(intent1, FILE_RESULT_CODE1);
				break;
			default:
				break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(FILE_RESULT_CODE == requestCode){
			Bundle bundle = null;
			if(data!=null&&(bundle=data.getExtras())!=null){
				mDealString = "";
				mDealString = ReadTxtFile(bundle.getString("file"));
				Log.d("Analysis_mDealString",mDealString);
				y=stringSplitAndDataToFloats(mDealString);
				double[] y1 =new double[y.length/4];
				double[] y2 =new double[y.length/4];
				double[] y3 =new double[y.length/4];
				double[] y4 =new double[y.length/4];
				double[] oldy1 =new double[y.length/4];
				double[] oldy2 =new double[y.length/4];
				double[] oldy3 =new double[y.length/4];
				double[] oldy4 =new double[y.length/4];
				double[] x0 =new double[y.length/4];
				for(int i = 0; i < y.length/4 ; i++){
					x0[i]= i;
					oldy1[i]= y[i*4];  //第一个通道点集E
					oldy2[i]= y[i*4+1];//第二个通道点集A
					oldy3[i]= y[i*4+2];//第三个通道点集S
					oldy4[i]= y[i*4+3];//第四个通道点集I
				}
				double sum1 = 0;
				double sum2 = 0;
				double sum3 = 0;
				double sum4 = 0;
				double junzhi1 = 0;
				double junzhi2 = 0;
				double junzhi3 = 0;
				double junzhi4 = 0;
				//去除通道1均值
				for(int i = 0; i < oldy1.length ; i++){
					sum1 = sum1 + oldy1[i];
				}
				junzhi1 = sum1 / oldy1.length;
				for(int i = 0; i < oldy1.length ; i++){
					y1[i] = oldy1[i] - junzhi1;
				}
				//去除通道2均值
				for(int i = 0; i < oldy2.length ; i++){
					sum2 = sum2 + oldy2[i];
				}
				junzhi2 = sum2 / oldy2.length;
				for(int i = 0; i < oldy2.length ; i++){
					y2[i] = oldy2[i] - junzhi2;
				}
				//去除通道3均值
				for(int i = 0; i < oldy3.length ; i++){
					sum3 = sum3 + oldy3[i];
				}
				junzhi3 = sum3 / oldy3.length;
				for(int i = 0; i < oldy3.length ; i++){
					y3[i] = oldy3[i] - junzhi3;
				}
				//去除通道4均值
				for(int i = 0; i < oldy4.length ; i++){
					sum4 = sum4 + oldy4[i];
				}
				junzhi4 = sum4 / oldy4.length;
				for(int i = 0; i < oldy4.length ; i++){
					y4[i] = oldy4[i] - junzhi4;
				}
				//保存各点集到list
				ES_list.clear();
				AS_list.clear();
				AI_list.clear();
				for(int i = 0;i < y1.length; i++){
					ES_list.add((y1[i]-y3[i]));
					AS_list.add(y2[i]-y3[i]);
					AI_list.add(y2[i]-y4[i]);
				}
				x_list.clear();
				for(int i = 0; i < y.length/4; i++){
					x_list.add(x0[i]);
				}
			}
		}
		if(FILE_RESULT_CODE1 == requestCode){
			Bundle bundle = null;
			if(data!=null&&(bundle=data.getExtras())!=null) {
				mDealData = "";
				mDealData = ReadTxtFile(bundle.getString("file"));
				String[] se = mDealData.split("\n");
				double[] sd = new double[se.length];

				newData_list.clear();
				for(int i = 0;i<se.length;i++) {
					sd[i] = Double.valueOf(se[i]);
				}

				for(int i = 0;i<sd.length;i++) {
					newData_list.add(sd[i]);
				}
				x_list.clear();
				for(int i = 0; i < newData_list.size(); i++){
					x_list.add((double)i);
				}

				updateChart(x_list,newData_list,line1);
			}
		}
	}


	public  void xianshi(){
		if( y == null || y.length == 0){
			Toast.makeText(getActivity().getApplicationContext(),"请先打开数据",Toast.LENGTH_LONG).show();
		}else{
			Log.d("curDaoLian",curDaoLian+"");
			switch (curDaoLian){
				case 1:
					y_list.clear();
					if(value11.equals("-") && value13.equals("-") && value15.equals("-")){
						for(int i= 0;i < ES_list.size(); i++){
							y_list.add(- value12 * ES_list.get(i) - value14 * AS_list.get(i) - value16 * AI_list.get(i));
						}
					}else if(value11.equals("-") && value13.equals("-") && value15.equals("+")){
						for(int i= 0;i < ES_list.size(); i++){
							y_list.add(- value12 * ES_list.get(i) - value14 * AS_list.get(i) + value16 * AI_list.get(i));
						}
					}else if(value11.equals("-") && value13.equals("+") && value15.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(-value12 * ES_list.get(i) + value14 * AS_list.get(i) - value16 * AI_list.get(i));
						}
					}else if(value11.equals("-") && value13.equals("+") && value15.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(-value12 * ES_list.get(i) + value14 * AS_list.get(i) + value16 * AI_list.get(i));
						}
					}else if(value11.equals("+") && value13.equals("-") && value15.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value12 * ES_list.get(i) - value14 * AS_list.get(i) - value16 * AI_list.get(i));
						}
					}else if(value11.equals("+") && value13.equals("-") && value15.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value12 * ES_list.get(i) - value14 * AS_list.get(i) + value16 * AI_list.get(i));
						}
					}else if(value11.equals("+") && value13.equals("+") && value15.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value12 * ES_list.get(i) + value14 * AS_list.get(i) - value16 * AI_list.get(i));
						}
					}else if(value11.equals("+") && value13.equals("+") && value15.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value12 * ES_list.get(i) + value14 * AS_list.get(i) + value16 * AI_list.get(i));
						}
					}
					break;
				case 2:
					y_list.clear();
					if(value21.equals("-") && value23.equals("-") && value25.equals("-")){
						for(int i= 0;i < ES_list.size(); i++){
							y_list.add(- value22 * ES_list.get(i) - value24 * AS_list.get(i) - value26 * AI_list.get(i));
						}
					}else if(value21.equals("-") && value23.equals("-") && value25.equals("+")){
						for(int i= 0;i < ES_list.size(); i++){
							y_list.add(- value22 * ES_list.get(i) - value24 * AS_list.get(i) + value26 * AI_list.get(i));
						}
					}else if(value21.equals("-") && value23.equals("+") && value25.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(-value22 * ES_list.get(i) + value24 * AS_list.get(i) - value26 * AI_list.get(i));
						}
					}else if(value21.equals("-") && value23.equals("+") && value25.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(-value22 * ES_list.get(i) + value24 * AS_list.get(i) + value26 * AI_list.get(i));
						}
					}else if(value21.equals("+") && value23.equals("-") && value25.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value22 * ES_list.get(i) - value24 * AS_list.get(i) - value26 * AI_list.get(i));
						}
					}else if(value21.equals("+") && value23.equals("-") && value25.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value22 * ES_list.get(i) - value24 * AS_list.get(i) + value26 * AI_list.get(i));
						}
					}else if(value21.equals("+") && value23.equals("+") && value25.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value22 * ES_list.get(i) + value24 * AS_list.get(i) - value26 * AI_list.get(i));
						}
					}else if(value21.equals("+") && value23.equals("+") && value25.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value22 * ES_list.get(i) + value24 * AS_list.get(i) + value26 * AI_list.get(i));
						}
					}
					break;
				case 3:
					y_list.clear();
					if(value31.equals("-") && value33.equals("-") && value35.equals("-")){
						for(int i= 0;i < ES_list.size(); i++){
							y_list.add(- value32 * ES_list.get(i) - value34 * AS_list.get(i) - value36 * AI_list.get(i));
						}
					}else if(value31.equals("-") && value33.equals("-") && value35.equals("+")){
						for(int i= 0;i < ES_list.size(); i++){
							y_list.add(- value32 * ES_list.get(i) - value34 * AS_list.get(i) + value36 * AI_list.get(i));
						}
					}else if(value31.equals("-") && value33.equals("+") && value35.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(-value32 * ES_list.get(i) + value34 * AS_list.get(i) - value36 * AI_list.get(i));
						}
					}else if(value31.equals("-") && value33.equals("+") && value35.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(-value32 * ES_list.get(i) + value34 * AS_list.get(i) + value36 * AI_list.get(i));
						}
					}else if(value31.equals("+") && value33.equals("-") && value35.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value32 * ES_list.get(i) - value34 * AS_list.get(i) - value36 * AI_list.get(i));
						}
					}else if(value31.equals("+") && value33.equals("-") && value35.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value32 * ES_list.get(i) - value34 * AS_list.get(i) + value36 * AI_list.get(i));
						}
					}else if(value31.equals("+") && value33.equals("+") && value35.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value32 * ES_list.get(i) + value34 * AS_list.get(i) - value36 * AI_list.get(i));
						}
					}else if(value31.equals("+") && value33.equals("+") && value35.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value32 * ES_list.get(i) + value34 * AS_list.get(i) + value36 * AI_list.get(i));
						}
					}
					break;
				case 4:
					y_list.clear();
					if(value41.equals("-") && value43.equals("-") && value45.equals("-")){
						for(int i= 0;i < ES_list.size(); i++){
							y_list.add(- value42 * ES_list.get(i) - value44 * AS_list.get(i) - value46 * AI_list.get(i));
						}
					}else if(value41.equals("-") && value43.equals("-") && value45.equals("+")){
						for(int i= 0;i < ES_list.size(); i++){
							y_list.add(- value42 * ES_list.get(i) - value44 * AS_list.get(i) + value46 * AI_list.get(i));
						}
					}else if(value41.equals("-") && value43.equals("+") && value45.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(-value42 * ES_list.get(i) + value44 * AS_list.get(i) - value46 * AI_list.get(i));
						}
					}else if(value41.equals("-") && value43.equals("+") && value45.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(-value42 * ES_list.get(i) + value44 * AS_list.get(i) + value46 * AI_list.get(i));
						}
					}else if(value41.equals("+") && value43.equals("-") && value45.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value42 * ES_list.get(i) - value44 * AS_list.get(i) - value46 * AI_list.get(i));
						}
					}else if(value41.equals("+") && value43.equals("-") && value45.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value42 * ES_list.get(i) - value44 * AS_list.get(i) + value46 * AI_list.get(i));
						}
					}else if(value41.equals("+") && value43.equals("+") && value45.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value42 * ES_list.get(i) + value44 * AS_list.get(i) - value46 * AI_list.get(i));
						}
					}else if(value41.equals("+") && value43.equals("+") && value45.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value42 * ES_list.get(i) + value44 * AS_list.get(i) + value46 * AI_list.get(i));
						}
					}
					break;
				case 5:
					y_list.clear();
					if(value51.equals("-") && value53.equals("-") && value55.equals("-")){
						for(int i= 0;i < ES_list.size(); i++){
							y_list.add(- value52 * ES_list.get(i) - value54 * AS_list.get(i) - value56 * AI_list.get(i));
						}
					}else if(value51.equals("-") && value53.equals("-") && value55.equals("+")){
						for(int i= 0;i < ES_list.size(); i++){
							y_list.add(- value52 * ES_list.get(i) - value54 * AS_list.get(i) + value56 * AI_list.get(i));
						}
					}else if(value51.equals("-") && value53.equals("+") && value55.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(-value52 * ES_list.get(i) + value54 * AS_list.get(i) - value56 * AI_list.get(i));
						}
					}else if(value51.equals("-") && value53.equals("+") && value55.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(-value52 * ES_list.get(i) + value54 * AS_list.get(i) + value56 * AI_list.get(i));
						}
					}else if(value51.equals("+") && value53.equals("-") && value55.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value52 * ES_list.get(i) - value54 * AS_list.get(i) - value56 * AI_list.get(i));
						}
					}else if(value51.equals("+") && value53.equals("-") && value55.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value52 * ES_list.get(i) - value54 * AS_list.get(i) + value56 * AI_list.get(i));
						}
					}else if(value51.equals("+") && value53.equals("+") && value55.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value52 * ES_list.get(i) + value54 * AS_list.get(i) - value56 * AI_list.get(i));
						}
					}else if(value51.equals("+") && value53.equals("+") && value55.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value52 * ES_list.get(i) + value54 * AS_list.get(i) + value56 * AI_list.get(i));
						}
					}
					break;
				case 6:
					y_list.clear();
					if(value61.equals("-") && value63.equals("-") && value65.equals("-")){
						for(int i= 0;i < ES_list.size(); i++){
							y_list.add(- value62 * ES_list.get(i) - value64 * AS_list.get(i) - value66 * AI_list.get(i));
						}
					}else if(value61.equals("-") && value63.equals("-") && value65.equals("+")){
						for(int i= 0;i < ES_list.size(); i++){
							y_list.add(- value62 * ES_list.get(i) - value64 * AS_list.get(i) + value66 * AI_list.get(i));
						}
					}else if(value61.equals("-") && value63.equals("+") && value65.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(-value62 * ES_list.get(i) + value64 * AS_list.get(i) - value66 * AI_list.get(i));
						}
					}else if(value61.equals("-") && value63.equals("+") && value65.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(-value62 * ES_list.get(i) + value64 * AS_list.get(i) + value66 * AI_list.get(i));
						}
					}else if(value61.equals("+") && value63.equals("-") && value65.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value62 * ES_list.get(i) - value64 * AS_list.get(i) - value66 * AI_list.get(i));
						}
					}else if(value61.equals("+") && value63.equals("-") && value65.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value62 * ES_list.get(i) - value64 * AS_list.get(i) + value66 * AI_list.get(i));
						}
					}else if(value61.equals("+") && value63.equals("+") && value65.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value62 * ES_list.get(i) + value64 * AS_list.get(i) - value66 * AI_list.get(i));
						}
					}else if(value61.equals("+") && value63.equals("+") && value65.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value62 * ES_list.get(i) + value64 * AS_list.get(i) + value66 * AI_list.get(i));
						}
					}
					break;
				case 7:
					y_list.clear();
					if(value71.equals("-") && value73.equals("-") && value75.equals("-")){
						for(int i= 0;i < ES_list.size(); i++){
							y_list.add(- value72 * ES_list.get(i) - value74 * AS_list.get(i) - value76 * AI_list.get(i));
						}
					}else if(value71.equals("-") && value73.equals("-") && value75.equals("+")){
						for(int i= 0;i < ES_list.size(); i++){
							y_list.add(- value72 * ES_list.get(i) - value74 * AS_list.get(i) + value76 * AI_list.get(i));
						}
					}else if(value71.equals("-") && value73.equals("+") && value75.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(-value72 * ES_list.get(i) + value74 * AS_list.get(i) - value76 * AI_list.get(i));
						}
					}else if(value71.equals("-") && value73.equals("+") && value75.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(-value72 * ES_list.get(i) + value74 * AS_list.get(i) + value76 * AI_list.get(i));
						}
					}else if(value71.equals("+") && value73.equals("-") && value75.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value72 * ES_list.get(i) - value74 * AS_list.get(i) - value76 * AI_list.get(i));
						}
					}else if(value71.equals("+") && value73.equals("-") && value75.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value72 * ES_list.get(i) - value74 * AS_list.get(i) + value76 * AI_list.get(i));
						}
					}else if(value71.equals("+") && value73.equals("+") && value75.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value72 * ES_list.get(i) + value74 * AS_list.get(i) - value76 * AI_list.get(i));
						}
					}else if(value71.equals("+") && value73.equals("+") && value75.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value72 * ES_list.get(i) + value74 * AS_list.get(i) + value76 * AI_list.get(i));
						}
					}
					break;
				case 8:
					y_list.clear();
					if(value81.equals("-") && value83.equals("-") && value85.equals("-")){
						for(int i= 0;i < ES_list.size(); i++){
							y_list.add(- value82 * ES_list.get(i) - value84 * AS_list.get(i) - value86 * AI_list.get(i));
						}
					}else if(value81.equals("-") && value83.equals("-") && value85.equals("+")){
						for(int i= 0;i < ES_list.size(); i++){
							y_list.add(- value82 * ES_list.get(i) - value84 * AS_list.get(i) + value86 * AI_list.get(i));
						}
					}else if(value81.equals("-") && value83.equals("+") && value85.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(-value82 * ES_list.get(i) + value84 * AS_list.get(i) - value86 * AI_list.get(i));
						}
					}else if(value81.equals("-") && value83.equals("+") && value85.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(-value82 * ES_list.get(i) + value84 * AS_list.get(i) + value86 * AI_list.get(i));
						}
					}else if(value81.equals("+") && value83.equals("-") && value85.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value82 * ES_list.get(i) - value84 * AS_list.get(i) - value86 * AI_list.get(i));
						}
					}else if(value81.equals("+") && value83.equals("-") && value85.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value82 * ES_list.get(i) - value84 * AS_list.get(i) + value86 * AI_list.get(i));
						}
					}else if(value81.equals("+") && value83.equals("+") && value85.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value82 * ES_list.get(i) + value84 * AS_list.get(i) - value86 * AI_list.get(i));
						}
					}else if(value81.equals("+") && value83.equals("+") && value85.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value82 * ES_list.get(i) + value84 * AS_list.get(i) + value86 * AI_list.get(i));
						}
					}
					break;
				case 9:
					y_list.clear();
					if(value91.equals("-") && value93.equals("-") && value95.equals("-")){
						for(int i= 0;i < ES_list.size(); i++){
							y_list.add(- value92 * ES_list.get(i) - value94 * AS_list.get(i) - value96 * AI_list.get(i));
						}
					}else if(value91.equals("-") && value93.equals("-") && value95.equals("+")){
						for(int i= 0;i < ES_list.size(); i++){
							y_list.add(- value92 * ES_list.get(i) - value94 * AS_list.get(i) + value96 * AI_list.get(i));
						}
					}else if(value91.equals("-") && value93.equals("+") && value95.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(-value92 * ES_list.get(i) + value94 * AS_list.get(i) - value96 * AI_list.get(i));
						}
					}else if(value91.equals("-") && value93.equals("+") && value95.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(-value92 * ES_list.get(i) + value94 * AS_list.get(i) + value96 * AI_list.get(i));
						}
					}else if(value91.equals("+") && value93.equals("-") && value95.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value92 * ES_list.get(i) - value94 * AS_list.get(i) - value96 * AI_list.get(i));
						}
					}else if(value91.equals("+") && value93.equals("-") && value95.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value92 * ES_list.get(i) - value94 * AS_list.get(i) + value96 * AI_list.get(i));
						}
					}else if(value91.equals("+") && value93.equals("+") && value95.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value92 * ES_list.get(i) + value94 * AS_list.get(i) - value96 * AI_list.get(i));
						}
					}else if(value91.equals("+") && value93.equals("+") && value95.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value92 * ES_list.get(i) + value94 * AS_list.get(i) + value96 * AI_list.get(i));
						}
					}
					break;
				case 10:
					y_list.clear();
					if(value101.equals("-") && value103.equals("-") && value105.equals("-")){
						for(int i= 0;i < ES_list.size(); i++){
							y_list.add(- value102 * ES_list.get(i) - value104 * AS_list.get(i) - value106 * AI_list.get(i));
						}
					}else if(value101.equals("-") && value103.equals("-") && value105.equals("+")){
						for(int i= 0;i < ES_list.size(); i++){
							y_list.add(- value102 * ES_list.get(i) - value104 * AS_list.get(i) + value106 * AI_list.get(i));
						}
					}else if(value101.equals("-") && value103.equals("+") && value105.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(-value102 * ES_list.get(i) + value104 * AS_list.get(i) - value106 * AI_list.get(i));
						}
					}else if(value101.equals("-") && value103.equals("+") && value105.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(-value102 * ES_list.get(i) + value104 * AS_list.get(i) + value106 * AI_list.get(i));
						}
					}else if(value101.equals("+") && value103.equals("-") && value105.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value102 * ES_list.get(i) - value104 * AS_list.get(i) - value106 * AI_list.get(i));
						}
					}else if(value101.equals("+") && value103.equals("-") && value105.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value102 * ES_list.get(i) - value104 * AS_list.get(i) + value106 * AI_list.get(i));
						}
					}else if(value101.equals("+") && value103.equals("+") && value105.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value102 * ES_list.get(i) + value104 * AS_list.get(i) - value106 * AI_list.get(i));
						}
					}else if(value101.equals("+") && value103.equals("+") && value105.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value102 * ES_list.get(i) + value104 * AS_list.get(i) + value106 * AI_list.get(i));
						}
					}
					break;
				case 11:
					y_list.clear();
					if(value111.equals("-") && value113.equals("-") && value115.equals("-")){
						for(int i= 0;i < ES_list.size(); i++){
							y_list.add(- value112 * ES_list.get(i) - value114 * AS_list.get(i) - value116 * AI_list.get(i));
						}
					}else if(value111.equals("-") && value113.equals("-") && value115.equals("+")){
						for(int i= 0;i < ES_list.size(); i++){
							y_list.add(- value112 * ES_list.get(i) - value114 * AS_list.get(i) + value116 * AI_list.get(i));
						}
					}else if(value111.equals("-") && value113.equals("+") && value115.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(-value112 * ES_list.get(i) + value114 * AS_list.get(i) - value116 * AI_list.get(i));
						}
					}else if(value111.equals("-") && value113.equals("+") && value115.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(-value112 * ES_list.get(i) + value114 * AS_list.get(i) + value116 * AI_list.get(i));
						}
					}else if(value111.equals("+") && value113.equals("-") && value115.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value112 * ES_list.get(i) - value114 * AS_list.get(i) - value116 * AI_list.get(i));
						}
					}else if(value111.equals("+") && value113.equals("-") && value115.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value112 * ES_list.get(i) - value114 * AS_list.get(i) + value116 * AI_list.get(i));
						}
					}else if(value111.equals("+") && value113.equals("+") && value115.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value112 * ES_list.get(i) + value114 * AS_list.get(i) - value116 * AI_list.get(i));
						}
					}else if(value111.equals("+") && value113.equals("+") && value115.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value112 * ES_list.get(i) + value114 * AS_list.get(i) + value116 * AI_list.get(i));
						}
					}
					break;
				case 12:
					y_list.clear();
					if(value121.equals("-") && value123.equals("-") && value125.equals("-")){
						for(int i= 0;i < ES_list.size(); i++){
							y_list.add(- value122 * ES_list.get(i) - value124 * AS_list.get(i) - value126 * AI_list.get(i));
						}
					}else if(value121.equals("-") && value123.equals("-") && value125.equals("+")){
						for(int i= 0;i < ES_list.size(); i++){
							y_list.add(- value122 * ES_list.get(i) - value124 * AS_list.get(i) + value126 * AI_list.get(i));
						}
					}else if(value121.equals("-") && value123.equals("+") && value125.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(-value122 * ES_list.get(i) + value124 * AS_list.get(i) - value126 * AI_list.get(i));
						}
					}else if(value121.equals("-") && value123.equals("+") && value125.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(-value122 * ES_list.get(i) + value124 * AS_list.get(i) + value126 * AI_list.get(i));
						}
					}else if(value121.equals("+") && value123.equals("-") && value125.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value122 * ES_list.get(i) - value124 * AS_list.get(i) - value126 * AI_list.get(i));
						}
					}else if(value121.equals("+") && value123.equals("-") && value125.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value122 * ES_list.get(i) - value124 * AS_list.get(i) + value126 * AI_list.get(i));
						}
					}else if(value121.equals("+") && value123.equals("+") && value125.equals("-")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value122 * ES_list.get(i) + value124 * AS_list.get(i) - value126 * AI_list.get(i));
						}
					}else if(value121.equals("+") && value123.equals("+") && value125.equals("+")) {
						for (int i = 0; i < ES_list.size(); i++) {
							y_list.add(value122 * ES_list.get(i) + value124 * AS_list.get(i) + value126 * AI_list.get(i));
						}
					}
					break;
			}

//			//中值滤波，45为区间值
//			newY_list.clear();
//			for(int i=45;i<y_list.size()-45;i++){
//				List<Double> sortList=new ArrayList<Double>(y_list.subList(i-45, i+45));
//				Collections.sort(sortList);
//				double mid=sortList.get(sortList.size()/2);
//				newY_list.add((y_list.get(i)-mid));
//			}计算计算
			//带通加陷波
			newY_list.clear();
			newY_list = lvbo(y_list);
			//刷新显示
			updateChart(x_list, newY_list, line1);
			//R波检测与心率计算
			double[] yo = new double[newY_list.size()];
			for(int i=0; i < newY_list.size();i++){
				yo[i]= newY_list.get(i);
			}
			int[] R_pos = SoAndChan(yo);
			int average = 0;
			int heartRate = 0 ;
			for(int i=0; i < R_pos.length-1;i++){
				average += R_pos[i+1] - R_pos[i];
			}
			average = (int) (average/(R_pos.length-1));
			heartRate = (int) (60/(average*0.002));
			txtHeartRate.setText("心率："+heartRate);
		}
	}

	public static int[] SoAndChan(double[] voltages)
	{

		// initial maxi should be the max slope of the first 250 points.
		double initial_maxi = -2 * voltages[0] - voltages[1] + voltages[3] + 2 * voltages[4];
		for (int i = 2; i < SAMPLE_RATE; i++)
		{
			double slope = -2 * voltages[i - 2] - voltages[i - 1] + voltages[i + 1] + 2 * voltages[i + 2];
			if (slope > initial_maxi)
				initial_maxi = slope;
		}

		// Since we don't know how many R peaks we'll have, we'll use an ArrayList
		List<Integer> rTime = new ArrayList<Integer>();
		List<Integer> new_results = new ArrayList<Integer>();
		// set initial maxi
		double maxi = initial_maxi;
		boolean first_satisfy = false;
		boolean second_satisfy = false;
		boolean data_choose = false;
		int onset_point = 0;
		int R_point = 0;
		boolean rFound = false;
		// I want a way to plot all the r dots that are found...
		int[] rExist = new int[voltages.length];
		// First two voltages should be ignored because we need rom length
		for (int i = 2; i < voltages.length - 2; i++)
		{

			// Last two voltages should be ignored too
			if (!first_satisfy || !second_satisfy)
			{
				// Get Slope:
				double slope = -2 * voltages[i - 2] - voltages[i - 1] + voltages[i + 1] + 2 * voltages[i + 2];

				// Get slope threshold
				double slope_threshold = (THRESHOLD_PARAM / 32) * maxi;

				// We need two consecutive datas that satisfy slope > slope_threshold
				if ( slope > slope_threshold)
				{
					if (!first_satisfy)
					{
						first_satisfy = true;
						onset_point = i;
					}
					else
					{
						if (!second_satisfy)
						{
							second_satisfy = true;
						}
					}
				}
			}
			// We found the ONSET already, now we find the R point
			else
			{

				if (voltages[i] < voltages[i - 1])
				{
					rTime.add(i - 1);
					R_point = i - 1;

					// Since we have the R, we should reset
					first_satisfy = false;
					second_satisfy = false;

					// and update maxi
					double first_maxi = voltages[R_point] - voltages[onset_point];
					maxi = ((first_maxi - maxi) / FILTER_PARAMETER) + maxi;
				}
			}
		}

		new_results.clear();
		for(int i=0; i < rTime.size();i++){
			new_results.add(rTime.get(i));
		}
		//数据整理
		for(int i=0; i < new_results.size()-1;i++){
			if (new_results.get(i+1)-new_results.get(i) <100){
				new_results.remove(i+1);
			}
		}

//		for(int j = 0; j< rTime.size();j++){
//			System.out.println(rTime.get(j)+"...");
//		}
//		for(int j = 0; j< new_results.size();j++){
//			System.out.println(new_results.get(j)+"<<<");
//		}

		int[] R_Pos = new int[new_results.size()];
		for(int i=0; i < new_results.size();i++){
			R_Pos[i]= new_results.get(i);
		}
		return R_Pos;
	}

	/**
	 * 滤波函数
	 * @param inPut
	 * @return  out
	 */

	public List<Double> lvbo(List<Double> inPut){
		double[] xi = new double[inPut.size()];
		List<Double> out = new ArrayList<Double>();
		for(int i=0; i<inPut.size();i++){
			xi[i]= inPut.get(i);
		}
		double[] yo1 = new double[xi.length];
		double[] yo2 = new double[xi.length];

		//0.05-50Hz带通滤波
		try {
			Filtering(yo1, coefb,coefb.length,coefa,coefa.length, xi, xi.length);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//50Hz陷波
		try {
			Filtering(yo2, coef1b,coef1b.length,coef1a,coef1a.length, yo1, yo1.length);
		} catch (Exception e) {
			e.printStackTrace();
		}

		out.clear();
		for(int i=0; i < yo2.length;i++){
			out.add(yo2[i]);
		}

		return out;
	}
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
	public void Filtering(double y[], double b[], int lenB, double a[],
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
		for (i = 1; i <lenX; i++) {
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
//		// 十万分位四舍五入
//		for (i = 0; i < y.length; i++) {
//			y[i] = (double) (Math.round(y[i] * 10000)) / 10000;
//		}
		return;
	}

	public void showPopwindow(){
		//获取导联按钮控件的宽度
//		int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//		int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//		btnDaoLian.measure(w, h);
//		int btnDaoLianWidth =btnDaoLian.getMeasuredWidth();

		Rect frame = new Rect();
		getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int height = frame.height();
		//statusBarHeight是上面所求的状态栏的高度
		if (popWinShare == null) {
			//自定义的单击事件
			OnClickLintener paramOnClickListener = new OnClickLintener();
			popWinShare = new PopWindow(getActivity(), paramOnClickListener, dip2px(getActivity(), 100), height);
			//监听窗口的焦点事件，点击窗口外面则取消显示
			popWinShare.getContentView().setOnFocusChangeListener(new View.OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (!hasFocus) {
						popWinShare.dismiss();
					}
				}
			});
		}
		//设置默认获取焦点
		popWinShare.setFocusable(true);
		//以父控件的x和y的偏移量位置开始显示窗口
		//popWinShare.showAsDropDown(v,0,0);
		popWinShare.showAtLocation(mRootView.findViewById(R.id.chart), Gravity.RIGHT, btnDaoLian.getWidth(), 0);
		//如果窗口存在，则更新
		popWinShare.update();
	}
	class OnClickLintener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.layout1:
					curDaoLian = 1;
					Toast.makeText(getActivity().getApplicationContext(), "当前显示导联I", Toast.LENGTH_SHORT).show();
					popWinShare.dismiss();
					break;
				case R.id.layout2:
					curDaoLian = 2;
					Toast.makeText(getActivity().getApplicationContext(), "当前显示导联II", Toast.LENGTH_SHORT).show();
					popWinShare.dismiss();
					break;
				case R.id.layout3:
					curDaoLian = 3;
					Toast.makeText(getActivity().getApplicationContext(), "当前显示导联III", Toast.LENGTH_SHORT).show();
					popWinShare.dismiss();
					break;
				case R.id.layout4:
					curDaoLian = 4;
					Toast.makeText(getActivity().getApplicationContext(), "当前显示导联aVR", Toast.LENGTH_SHORT).show();
					popWinShare.dismiss();
					break;
				case R.id.layout5:
					curDaoLian = 5;
					Toast.makeText(getActivity().getApplicationContext(), "当前显示导联aVL", Toast.LENGTH_SHORT).show();
					popWinShare.dismiss();
					break;
				case R.id.layout6:
					curDaoLian = 6;
					Toast.makeText(getActivity().getApplicationContext(), "当前显示导联aVF", Toast.LENGTH_SHORT).show();
					popWinShare.dismiss();
					break;
				case R.id.layout7:
					curDaoLian = 7;
					Toast.makeText(getActivity().getApplicationContext(), "当前显示导联V1", Toast.LENGTH_SHORT).show();
					popWinShare.dismiss();
					break;
				case R.id.layout8:
					curDaoLian = 8;
					Toast.makeText(getActivity().getApplicationContext(), "当前显示导联V2", Toast.LENGTH_SHORT).show();
					popWinShare.dismiss();
					break;
				case R.id.layout9:
					curDaoLian = 9;
					Toast.makeText(getActivity().getApplicationContext(), "当前显示导联V3", Toast.LENGTH_SHORT).show();
					popWinShare.dismiss();
					break;
				case R.id.layout10:
					curDaoLian = 10;
					Toast.makeText(getActivity().getApplicationContext(), "当前显示导联V4", Toast.LENGTH_SHORT).show();
					popWinShare.dismiss();
					break;
				case R.id.layout11:
					curDaoLian = 11;
					Toast.makeText(getActivity().getApplicationContext(), "当前显示导联V5", Toast.LENGTH_SHORT).show();
					popWinShare.dismiss();
					break;
				case R.id.layout12:
					curDaoLian = 12;
					Toast.makeText(getActivity().getApplicationContext(), "当前显示导联V6", Toast.LENGTH_SHORT).show();
					popWinShare.dismiss();
					break;
				default:
					break;
			}
		}
	}

	public  void showDialogs(){
		LayoutInflater factory = LayoutInflater.from(this.getActivity());//提示框

		final View view = factory.inflate(R.layout.set_paraments, null);//这里必须是final的
		final EditText editText11=(EditText)view.findViewById(R.id.editText11);//获得输入框对象
		final EditText editText12=(EditText)view.findViewById(R.id.editText12);
		final EditText editText13=(EditText)view.findViewById(R.id.editText13);
		final EditText editText14=(EditText)view.findViewById(R.id.editText14);
		final EditText editText15=(EditText)view.findViewById(R.id.editText15);
		final EditText editText16=(EditText)view.findViewById(R.id.editText16);
		final EditText editText21=(EditText)view.findViewById(R.id.editText21);
		final EditText editText22=(EditText)view.findViewById(R.id.editText22);
		final EditText editText23=(EditText)view.findViewById(R.id.editText23);
		final EditText editText24=(EditText)view.findViewById(R.id.editText24);
		final EditText editText25=(EditText)view.findViewById(R.id.editText25);
		final EditText editText26=(EditText)view.findViewById(R.id.editText26);
		final EditText editText31=(EditText)view.findViewById(R.id.editText31);
		final EditText editText32=(EditText)view.findViewById(R.id.editText32);
		final EditText editText33=(EditText)view.findViewById(R.id.editText33);
		final EditText editText34=(EditText)view.findViewById(R.id.editText34);
		final EditText editText35=(EditText)view.findViewById(R.id.editText35);
		final EditText editText36=(EditText)view.findViewById(R.id.editText36);
		final EditText editText41=(EditText)view.findViewById(R.id.editText41);
		final EditText editText42=(EditText)view.findViewById(R.id.editText42);
		final EditText editText43=(EditText)view.findViewById(R.id.editText43);
		final EditText editText44=(EditText)view.findViewById(R.id.editText44);
		final EditText editText45=(EditText)view.findViewById(R.id.editText45);
		final EditText editText46=(EditText)view.findViewById(R.id.editText46);
		final EditText editText51=(EditText)view.findViewById(R.id.editText51);
		final EditText editText52=(EditText)view.findViewById(R.id.editText52);
		final EditText editText53=(EditText)view.findViewById(R.id.editText53);
		final EditText editText54=(EditText)view.findViewById(R.id.editText54);
		final EditText editText55=(EditText)view.findViewById(R.id.editText55);
		final EditText editText56=(EditText)view.findViewById(R.id.editText56);
		final EditText editText61=(EditText)view.findViewById(R.id.editText61);
		final EditText editText62=(EditText)view.findViewById(R.id.editText62);
		final EditText editText63=(EditText)view.findViewById(R.id.editText63);
		final EditText editText64=(EditText)view.findViewById(R.id.editText64);
		final EditText editText65=(EditText)view.findViewById(R.id.editText65);
		final EditText editText66=(EditText)view.findViewById(R.id.editText66);
		final EditText editText71=(EditText)view.findViewById(R.id.editText71);
		final EditText editText72=(EditText)view.findViewById(R.id.editText72);
		final EditText editText73=(EditText)view.findViewById(R.id.editText73);
		final EditText editText74=(EditText)view.findViewById(R.id.editText74);
		final EditText editText75=(EditText)view.findViewById(R.id.editText75);
		final EditText editText76=(EditText)view.findViewById(R.id.editText76);
		final EditText editText81=(EditText)view.findViewById(R.id.editText81);
		final EditText editText82=(EditText)view.findViewById(R.id.editText82);
		final EditText editText83=(EditText)view.findViewById(R.id.editText83);
		final EditText editText84=(EditText)view.findViewById(R.id.editText84);
		final EditText editText85=(EditText)view.findViewById(R.id.editText85);
		final EditText editText86=(EditText)view.findViewById(R.id.editText86);
		final EditText editText91=(EditText)view.findViewById(R.id.editText91);
		final EditText editText92=(EditText)view.findViewById(R.id.editText92);
		final EditText editText93=(EditText)view.findViewById(R.id.editText93);
		final EditText editText94=(EditText)view.findViewById(R.id.editText94);
		final EditText editText95=(EditText)view.findViewById(R.id.editText95);
		final EditText editText96=(EditText)view.findViewById(R.id.editText96);
		final EditText editText101=(EditText)view.findViewById(R.id.editText101);
		final EditText editText102=(EditText)view.findViewById(R.id.editText102);
		final EditText editText103=(EditText)view.findViewById(R.id.editText103);
		final EditText editText104=(EditText)view.findViewById(R.id.editText104);
		final EditText editText105=(EditText)view.findViewById(R.id.editText105);
		final EditText editText106=(EditText)view.findViewById(R.id.editText106);
		final EditText editText111=(EditText)view.findViewById(R.id.editText111);
		final EditText editText112=(EditText)view.findViewById(R.id.editText112);
		final EditText editText113=(EditText)view.findViewById(R.id.editText113);
		final EditText editText114=(EditText)view.findViewById(R.id.editText114);
		final EditText editText115=(EditText)view.findViewById(R.id.editText115);
		final EditText editText116=(EditText)view.findViewById(R.id.editText116);
		final EditText editText121=(EditText)view.findViewById(R.id.editText121);
		final EditText editText122=(EditText)view.findViewById(R.id.editText122);
		final EditText editText123=(EditText)view.findViewById(R.id.editText123);
		final EditText editText124=(EditText)view.findViewById(R.id.editText124);
		final EditText editText125=(EditText)view.findViewById(R.id.editText125);
		final EditText editText126=(EditText)view.findViewById(R.id.editText126);
		final Button btnXishu1 = (Button)view.findViewById(R.id.btn_xishu1);
		final Button btnXishu2 = (Button)view.findViewById(R.id.btn_xishu2);
		btnXishu1.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				//I导联
				editText11.setText("+");editText12.setText("0.035");editText13.setText("-");
				editText14.setText("0.192");editText15.setText("+");editText16.setText("0.738");
				//II导联
				editText21.setText("+");editText22.setText("0.199");editText23.setText("+");
				editText24.setText("0.561");editText25.setText("-");editText26.setText("0.204");
				//III导联
				editText31.setText("+");editText32.setText("0.165");editText33.setText("+");
				editText34.setText("0.754");editText35.setText("-");editText36.setText("0.942");
				//aVR导联
				editText41.setText("-");editText42.setText("0.117");editText43.setText("-");
				editText44.setText("0.185");editText45.setText("-");editText46.setText("0.266");
				//aVL导联
				editText51.setText("-");editText52.setText("0.065");editText53.setText("-");
				editText54.setText("0.472");editText55.setText("+");editText56.setText("0.839");
				//aVF导联
				editText61.setText("+");editText62.setText("0.182");editText63.setText("+");
				editText64.setText("0.657");editText65.setText("-");editText66.setText("0.573");
				//V1导联
				editText71.setText("+");editText72.setText("0.652");editText73.setText("-");
				editText74.setText("0.701");editText75.setText("+");editText76.setText("0.305");
				//V2导联
				editText81.setText("+");editText82.setText("1.250");editText83.setText("-");
				editText84.setText("1.569");editText85.setText("+");editText86.setText("1.495");
				//V3导联
				editText91.setText("+");editText92.setText("1.080");editText93.setText("-");
				editText94.setText("1.028");editText95.setText("+");editText96.setText("1.457");
				//V4导联
				editText101.setText("+");editText102.setText("0.654");editText103.setText("-");
				editText104.setText("0.306");editText105.setText("+");editText106.setText("1.116");
				//V5导联
				editText111.setText("+");editText112.setText("0.287");editText113.setText("+");
				editText114.setText("0.004");editText115.setText("+");editText116.setText("0.883");
				//V6导联
				editText121.setText("+");editText122.setText("0.055");editText123.setText("+");
				editText124.setText("0.292");editText125.setText("+");editText126.setText("0.377");
			}
		});
		btnXishu2.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				//I导联
				editText11.setText("+");editText12.setText("0.026");editText13.setText("-");
				editText14.setText("0.174");editText15.setText("+");editText16.setText("0.701");
				//II导联
				editText21.setText("-");editText22.setText("0.002");editText23.setText("+");
				editText24.setText("1.098");editText25.setText("-");editText26.setText("0.763");
				//III导联
				editText31.setText("-");editText32.setText("0.028");editText33.setText("+");
				editText34.setText("1.272");editText35.setText("-");editText36.setText("1.464");
				//aVR导联
				editText41.setText("-");editText42.setText("0.012");editText43.setText("-");
				editText44.setText("0.462");editText45.setText("+");editText46.setText("0.031");
				//aVL导联
				editText51.setText("+");editText52.setText("0.027");editText53.setText("-");
				editText54.setText("0.723");editText55.setText("+");editText56.setText("1.082");
				//aVF导联
				editText61.setText("-");editText62.setText("0.015");editText63.setText("+");
				editText64.setText("1.185");editText65.setText("-");editText66.setText("1.114");
				//V1导联
				editText71.setText("+");editText72.setText("0.641");editText73.setText("-");
				editText74.setText("0.391");editText75.setText("+");editText76.setText("0.080");
				//V2导联
				editText81.setText("+");editText82.setText("1.229");editText83.setText("-");
				editText84.setText("1.050");editText85.setText("+");editText86.setText("1.021");
				//V3导联
				editText91.setText("+");editText92.setText("0.947");editText93.setText("-");
				editText94.setText("0.539");editText95.setText("+");editText96.setText("0.987");
				//V4导联
				editText101.setText("+");editText102.setText("0.525");editText103.setText("+");
				editText104.setText("0.004");editText105.setText("+");editText106.setText("0.841");
				//V5导联
				editText111.setText("+");editText112.setText("0.179");editText113.setText("+");
				editText114.setText("0.278");editText115.setText("+");editText116.setText("0.630");
				//V6导联
				editText121.setText("-");editText122.setText("0.043");editText123.setText("+");
				editText124.setText("0.431");editText125.setText("+");editText126.setText("0.213");
			}
		});
		AlertDialog dialog = new AlertDialog.Builder(this.getActivity())
				.setView(view)
				.setPositiveButton("确定",//提示框的两个按钮
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								//获取参数设置值
								value11 = editText11.getText().toString();
								value12 = (float)(Math.round(Float.parseFloat(editText12.getText().toString()) * 1000))/1000;
								value13 = editText13.getText().toString();
								value14 = (float)(Math.round(Float.parseFloat(editText14.getText().toString()) * 1000))/1000;
								value15 = editText15.getText().toString();
								value16 = (float)(Math.round(Float.parseFloat(editText16.getText().toString()) * 1000))/1000;
								value21 = editText21.getText().toString();
								value22 = (float)(Math.round(Float.parseFloat(editText22.getText().toString()) * 1000))/1000;
								value23 = editText23.getText().toString();
								value24 = (float)(Math.round(Float.parseFloat(editText24.getText().toString()) * 1000))/1000;
								value25 = editText25.getText().toString();
								value26 = (float)(Math.round(Float.parseFloat(editText26.getText().toString()) * 1000))/1000;
								value31 = editText31.getText().toString();
								value32 = (float)(Math.round(Float.parseFloat(editText32.getText().toString()) * 1000))/1000;
								value33 = editText33.getText().toString();
								value34 = (float)(Math.round(Float.parseFloat(editText34.getText().toString()) * 1000))/1000;
								value35 = editText35.getText().toString();
								value36 = (float)(Math.round(Float.parseFloat(editText36.getText().toString()) * 1000))/1000;
								value41 = editText41.getText().toString();
								value42 = (float)(Math.round(Float.parseFloat(editText42.getText().toString()) * 1000))/1000;
								value43 = editText43.getText().toString();
								value44 = (float)(Math.round(Float.parseFloat(editText44.getText().toString()) * 1000))/1000;
								value45 = editText45.getText().toString();
								value46 = (float)(Math.round(Float.parseFloat(editText46.getText().toString()) * 1000))/1000;
								value51 = editText51.getText().toString();
								value52 = (float)(Math.round(Float.parseFloat(editText52.getText().toString()) * 1000))/1000;
								value53 = editText53.getText().toString();
								value54 = (float)(Math.round(Float.parseFloat(editText54.getText().toString()) * 1000))/1000;
								value55 = editText55.getText().toString();
								value56 = (float)(Math.round(Float.parseFloat(editText56.getText().toString()) * 1000))/1000;
								value61 = editText61.getText().toString();
								value62 = (float)(Math.round(Float.parseFloat(editText62.getText().toString()) * 1000))/1000;
								value63 = editText63.getText().toString();
								value64 = (float)(Math.round(Float.parseFloat(editText64.getText().toString()) * 1000))/1000;
								value65 = editText65.getText().toString();
								value66 = (float)(Math.round(Float.parseFloat(editText66.getText().toString()) * 1000))/1000;
								value71 = editText71.getText().toString();
								value72 = (float)(Math.round(Float.parseFloat(editText72.getText().toString()) * 1000))/1000;
								value73 = editText73.getText().toString();
								value74 = (float)(Math.round(Float.parseFloat(editText74.getText().toString()) * 1000))/1000;
								value75 = editText75.getText().toString();
								value76 = (float)(Math.round(Float.parseFloat(editText76.getText().toString()) * 1000))/1000;
								value81 = editText81.getText().toString();
								value82 = (float)(Math.round(Float.parseFloat(editText82.getText().toString()) * 1000))/1000;
								value83 = editText83.getText().toString();
								value84 = (float)(Math.round(Float.parseFloat(editText84.getText().toString()) * 1000))/1000;
								value85 = editText85.getText().toString();
								value86 = (float)(Math.round(Float.parseFloat(editText86.getText().toString()) * 1000))/1000;
								value91 = editText91.getText().toString();
								value92 = (float)(Math.round(Float.parseFloat(editText92.getText().toString()) * 1000))/1000;
								value93 = editText93.getText().toString();
								value94 = (float)(Math.round(Float.parseFloat(editText94.getText().toString()) * 1000))/1000;
								value95 = editText95.getText().toString();
								value96 = (float)(Math.round(Float.parseFloat(editText96.getText().toString()) * 1000))/1000;
								value101 = editText101.getText().toString();
								value102 = (float)(Math.round(Float.parseFloat(editText102.getText().toString()) * 1000))/1000;
								value103 = editText103.getText().toString();
								value104 = (float)(Math.round(Float.parseFloat(editText104.getText().toString()) * 1000))/1000;
								value105 = editText105.getText().toString();
								value106 = (float)(Math.round(Float.parseFloat(editText106.getText().toString()) * 1000))/1000;
								value111 = editText111.getText().toString();
								value112 = (float)(Math.round(Float.parseFloat(editText112.getText().toString()) * 1000))/1000;
								value113 = editText113.getText().toString();
								value114 = (float)(Math.round(Float.parseFloat(editText114.getText().toString()) * 1000))/1000;
								value115 = editText115.getText().toString();
								value116 = (float)(Math.round(Float.parseFloat(editText116.getText().toString()) * 1000))/1000;
								value121 = editText121.getText().toString();
								value122 = (float)(Math.round(Float.parseFloat(editText122.getText().toString()) * 1000))/1000;
								value123 = editText123.getText().toString();
								value124 = (float)(Math.round(Float.parseFloat(editText124.getText().toString()) * 1000))/1000;
								value125 = editText125.getText().toString();
								value126 = (float)(Math.round(Float.parseFloat(editText126.getText().toString()) * 1000))/1000;

							}
						}).setNegativeButton("取消", null).create();
		dialog.show();

		WindowManager m = this.getActivity().getWindowManager();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
		params.height = (int) (d.getHeight() * 0.95);   //高度设置为屏幕的0.95
		params.width = (int) (d.getWidth() * 0.85);    //宽度设置为屏幕的0.85
		dialog.getWindow().setAttributes(params);
	}


	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 *  转换十六进制通信协议字符串为其对应的double数值
	 * @param s
	 * return double[]
	 */
	private double[] stringSplitAndDataToFloats(String s)
	{
		if(!s.contains("c00000")){
			Toast.makeText(getActivity().getApplicationContext(), "数据格式错误", Toast.LENGTH_SHORT).show();
		}
		String[] a = s.split("c00000"); //分隔标识符
		StringBuffer b = new StringBuffer();
//		        System.out.println(a.length);
		for(int j = 1; j < a.length ;j++){
			for(int i=0 ; i<a[j].length()/6 ; i++){
				String str = a[j].substring(i*6, (i+1)*6);
				b.append(str).append("#");
//			        	System.out.println("---");
			}
		}
		String[] newStr = b.toString().split("#");
		int[] data = new int[newStr.length];
		double[] datas = new double[newStr.length];
		char ss = '7';//最高位大于7为负数
		for(int i=0;i<newStr.length;i++){
			if(newStr[i].charAt(0) > ss ){
				data[i] = -(~Integer.valueOf(newStr[i],16)+1 & 0xffffff); //最高位大于7，取反加1与0xffffff相与加负号
			}else{
				data[i] = Integer.valueOf(newStr[i],16);
			}
			datas[i] = (double) (2.4*data[i]/8388607);
		}
		return datas;
	}

	//读取文本文件中的内容
	public static String ReadTxtFile(String strFilePath)
	{
		String path = strFilePath;
		String content = ""; //文件内容字符串
		//打开文件
		File file = new File(path);
		//如果path是传递过来的参数，可以做一个非目录的判断
		if (file.isDirectory())
		{
			Log.d("TestFile", "The File doesn't not exist.");
		}
		else
		{
			try {
				InputStream instream = new FileInputStream(file);
				if (instream != null)
				{
					InputStreamReader inputreader = new InputStreamReader(instream);
					BufferedReader buffreader = new BufferedReader(inputreader);
					String line;
					//分行读取
					while (( line = buffreader.readLine()) != null) {
						content += line + "\n";
					}
					instream.close();
				}
			}
			catch (java.io.FileNotFoundException e)
			{
				Log.d("TestFile", "The File doesn't not exist.");
			}
			catch (IOException e)
			{
				Log.d("TestFile", e.getMessage());
			}
		}
		return content;
	}

	//保存功能实现
	private void save() {
		//显示对话框输入文件名
		LayoutInflater factory = LayoutInflater.from(this.getActivity());  //图层模板生成器句柄
		final View DialogView =  factory.inflate(R.layout.sname, null);  //用sname.xml模板生成视图模板
		new AlertDialog.Builder(this.getActivity())
				.setTitle("文件名")
				.setView(DialogView)   //设置视图模板
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() //确定按键响应函数
						{
							public void onClick(DialogInterface dialog, int whichButton){
								EditText text1 = (EditText)DialogView.findViewById(R.id.sname);  //得到文件名输入框句柄
								filename = text1.getText().toString();  //得到文件名

								try{
									if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  //如果SD卡已准备好

										filename =filename+".txt";   //在文件名末尾加上.txt
//													fileNum++;
										File sdCardDir = Environment.getExternalStorageDirectory();  //得到SD卡根目录
										File BuildDir = new File(sdCardDir, "/data");   //打开data目录，如不存在则生成
										if(BuildDir.exists()==false)BuildDir.mkdirs();
										File saveFile =new File(BuildDir, filename);  //新建文件句柄，如已存在仍新建文档
										FileOutputStream stream = new FileOutputStream(saveFile);  //打开文件输入流
										if(y_list != null){
											for (int i = 0; i < y_list.size(); i++){
												stream.write(String.valueOf(y_list.get(i)).getBytes());
												if( i != y_list.size()-1){
													stream.write(" ".getBytes());
												}
											}
											stream.close();
											Toast.makeText(getActivity().getApplicationContext(), "存储成功！", Toast.LENGTH_SHORT).show();
										}
									}else{
										Toast.makeText(getActivity().getApplicationContext(), "没有存储卡！", Toast.LENGTH_LONG).show();
									}

								}catch(IOException e){
									e.printStackTrace();
									return;
								}
							}
						})
				.setNegativeButton("取消",   //取消按键响应函数,直接退出对话框不做任何处理
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						}).show();  //显示对话框
	}
}