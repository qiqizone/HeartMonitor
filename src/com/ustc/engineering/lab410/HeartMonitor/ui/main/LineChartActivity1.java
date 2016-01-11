
package com.ustc.engineering.lab410.HeartMonitor.ui.main;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ustc.engineering.lab410.HeartMonitor.R;
import com.ustc.engineering.lab410.HeartMonitor.notimportant.DemoBase;
import com.ustc.engineering.lab410.HeartMonitor.ui.bean.Common;
import com.ustc.engineering.lab410.HeartMonitor.ui.widget.PopWindow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.List;
import java.util.Collections;
import java.util.Arrays;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class LineChartActivity1 extends DemoBase implements OnClickListener{

    public static String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	private InputStream mmInStream;
	private OutputStream mmOutStream;
	private ScrollView svResult;
	
	private ArrayList<Integer> nNeeds; //需要的长度
	private ArrayList<String>  sRecvs; //存放接收的数据

	private TextView tvTitle, tvLog;

	private BluetoothAdapter btAdapt = null;
	private BluetoothSocket btSocket = null;

	private Boolean bConnect = false;
	private String strName = null;
	private String strAddress = null;

	private Button btnWave;
	private Button btnSave;
	private Button btnSave1;
	private Button btnDaoLian;
	private Button btnParaments;
	private Button btnStart;

	private PopWindow popWinShare;
	private RelativeLayout lineChart;
	private LinearLayout linearLayout1;
	private Button btnReturn;
	public  String filename=""; //用来保存存储的文件名
	private String mTempSaveString ="";//临时数据缓存
	private String mDealString ="";//处理用数据缓存
	private String mSaveString ="";//保存用数据缓存
	private String mDisplayString ="";//显示用数据缓存

	private InputStream tmpIn;
	private OutputStream tmpOut;
	private int fileNum = 1;
	private StringBuffer hex;
	private boolean IsFirst = true;
	private int count = 0;
	private int xTemp, yTemp;
	private double[] x, y;
	private int curDaoLian = 1;

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
	private String//参数设置
			value11,value21,value31,value41,value51,value61,
			value71,value81,value91,value101,value111,value121,
			value13,value23,value33,value43,value53,value63,
			value73,value83,value93,value103,value113,value123,
			value15,value25,value35,value45,value55,value65,
			value75,value85,value95,value105,value115,value125;


	private List<Double> EA_list = new ArrayList<Double>();
	private List<Double> AS_list = new ArrayList<Double>();
	private List<Double> AI_list = new ArrayList<Double>();

    ArrayList<String> xVals = new ArrayList<String>();
//    ArrayList<Entry> yVals = new ArrayList<Entry>();
    
	private TableLayout tableLayout;
	private RelativeLayout dynamic_chart_line_layout;
	
    // 用于存放每条折线的点数据
    private XYSeries line1, line2;
    // 用于存放所有需要绘制的XYSeries
    private XYMultipleSeriesDataset mDataset;
    // 用于存放每条折线的风格
    private XYSeriesRenderer renderer1, renderer2;
    // 用于存放所有需要绘制的折线的风格
    private XYMultipleSeriesRenderer mXYMultipleSeriesRenderer;
    private GraphicalView chart;
	private String title = "心电信息";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);   
        setContentView(R.layout.detail);

//----------------------------------------------------------------------------
        svResult = (ScrollView) findViewById(R.id.svResult);
        lineChart = (RelativeLayout) findViewById(R.id.line_chart);
        linearLayout1 = (LinearLayout) findViewById(R.id.LinearLayout1);
        

        tableLayout =(TableLayout) findViewById(R.id.TableLayout01);

		btnWave = (Button) this.findViewById(R.id.btn_wave);
		btnWave.setOnClickListener(this);
		btnDaoLian = (Button) this.findViewById(R.id.btn_daoLian);
		btnDaoLian.setOnClickListener(this);
		btnParaments = (Button) this.findViewById(R.id.btn_paraments);
		btnParaments.setOnClickListener(this);
		btnStart = (Button) this.findViewById(R.id.btn_start);
		btnStart.setOnClickListener(this);
		btnStart.setClickable(true);


		btnReturn = (Button) this.findViewById(R.id.btn_return);
		btnReturn.setOnClickListener(this);
		btnSave = (Button) this.findViewById(R.id.btn_save);
		btnSave.setOnClickListener(this);
		btnSave1 = (Button) this.findViewById(R.id.btn_save1);
		btnSave1.setOnClickListener(this);
		
        dynamic_chart_line_layout = (RelativeLayout) findViewById(R.id.chart);
        //aChartEngine初始化
        initChart();
		showDialogs();
//        
//        x = new double[10];
//        y = new double[10];
        
//		refreshChart();
		//顶部标题显示设置
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		tvLog = (TextView) this.findViewById(R.id.tvInfo);
		Bundle bunde = this.getIntent().getExtras();
		strName = bunde.getString("NAME");
		strAddress = bunde.getString("MAC");
		tvTitle.setText(strName);
		tvLog.append(strName + "......\n");
		
		btAdapt = BluetoothAdapter.getDefaultAdapter();
		if (btAdapt == null) {
			tvLog.append("本机无蓝牙，连接失败\n");
			finish();
			return;
		}

		if (btAdapt.getState() != BluetoothAdapter.STATE_ON) {
			tvLog.append("本机蓝牙状态不正常，连接失败\n");
			finish();
			return;
		}

		IntentFilter intent = new IntentFilter();
		intent.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		intent.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

		registerReceiver(connectDevices, intent);

		//mHandler.sendEmptyMessageDelayed(Common.MESSAGE_CONNECT, 1000);
		

    }

//--------------------------------------------------------------------------------------------------
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

        // 配置chart参数
        setChartSettings(mXYMultipleSeriesRenderer, "X", "Y", 0, 1800, -5, 5, Color.RED,
                Color.WHITE);

        // 通过该函数获取到一个View 对象
        chart = ChartFactory.getCubeLineChartView(this, mDataset, mXYMultipleSeriesRenderer, 0.05f);
        // 将该View 对象添加到layout中
        dynamic_chart_line_layout.addView(chart, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

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
        mXYMultipleSeriesRenderer.setYLabelsAlign(Align.RIGHT);
        mXYMultipleSeriesRenderer.setPointSize((float) 5);
        mXYMultipleSeriesRenderer.setShowLegend(true);
        mXYMultipleSeriesRenderer.setLegendTextSize(20);
    }

//    private void refreshChart() {
//        Timer timer = new Timer();
//        timer.schedule(new RefreshSeriesTask(), 0,1 * 1000);
//    }
//    
//    class RefreshSeriesTask extends TimerTask {
//        public void run() {
//            initLine(line1);
//            chart.postInvalidate();
//        }
//    }
//    
//    private void initLine(XYSeries series) {
//
//        Random r = new Random();
//        xTemp = 0;
//        yTemp = r.nextInt(100);
//
//        count = series.getItemCount();
//        if (count > 10) {
//            count = 10;
//        }
//
//        for (int i = 0; i < count; i++) {
//            x[i] = series.getX(i);
//            y[i] = series.getY(i);
//        }
//        series.clear();
//
//        series.add(xTemp, yTemp);
//
//        for (int i = 0; i < count; i++) {
//            series.add(x[i] + 1, y[i]);
//        }
//    }

    
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
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_wave:
			linearLayout1.setVisibility(View.GONE);
			tableLayout.setVisibility(View.GONE);
			lineChart.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_return:
//			tableLayout.setVisibility(View.VISIBLE);
//			linearLayout1.setVisibility(View.VISIBLE);
//			lineChart.setVisibility(View.GONE);
			closeAndExit();//每次返回关闭蓝牙连接
//			finish();//返回但不关闭蓝牙连接e
			break;
		case R.id.btn_save:
			Save();
			break;
		case R.id.btn_save1:
			Save();
			break;
		case R.id.btn_daoLian:
			showPopwindow(v);
			break;
		case R.id.btn_paraments:
			showDialogs();
			break;
		case R.id.btn_start:
			//开始接受数据
			mHandler.sendEmptyMessageDelayed(Common.MESSAGE_CONNECT, 1000);
			btnStart.setClickable(false);
			break;
		default:
			break;
		}
	}


	public  void showDialogs(){
		LayoutInflater factory = LayoutInflater.from(this);//提示框

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

		AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle("参数设置")
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

		WindowManager m = getWindowManager();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
		params.height = (int) (d.getHeight() * 0.95);   //高度设置为屏幕的0.95
		params.width = (int) (d.getWidth() * 0.85);    //宽度设置为屏幕的0.85
		dialog.getWindow().setAttributes(params);
	}

	public void showPopwindow(View v){
		//获取导联按钮控件的宽度
//		int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//		int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//		btnDaoLian.measure(w, h);
//		int btnDaoLianWidth =btnDaoLian.getMeasuredWidth();

		WindowManager wm = this.getWindowManager();
		int height = wm.getDefaultDisplay().getHeight();
		if (popWinShare == null) {
			//自定义的单击事件
			OnClickLintener paramOnClickListener = new OnClickLintener();
			popWinShare = new PopWindow(this, paramOnClickListener, dip2px(this, 100), height);
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
		popWinShare.showAtLocation(findViewById(R.id.line_chart), Gravity.RIGHT, btnDaoLian.getWidth(), 0);
		//如果窗口存在，则更新
		popWinShare.update();
	}
	class OnClickLintener implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.layout1:

					break;
				case R.id.layout2:

					break;

				default:
					break;
			}
		}
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


	private BroadcastReceiver connectDevices = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d(Common.TAG, "Receiver:" + action);
			if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
			} else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {

			}
		}
	};
	
	// Hander
	public final Handler mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Common.MESSAGE_CONNECT:
					new Thread(new Runnable() {
						public void run() {
							Log.d("sss","MESSAGE_CONNECT");
							BluetoothSocket tmp = null;
							try {
								
								UUID uuid = UUID.fromString(SPP_UUID);
								BluetoothDevice btDev = btAdapt
										.getRemoteDevice(strAddress);
								Method m;
								try {
									m = btDev.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
									tmp = (BluetoothSocket) m.invoke(btDev, Integer.valueOf(1));
								} catch (SecurityException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								} catch (NoSuchMethodException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								} catch (IllegalArgumentException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (InvocationTargetException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								btSocket = tmp;
//								btSocket = btDev.createRfcommSocketToServiceRecord(uuid);
										//.createRfcommSocketToServiceRecord(uuid);
								btSocket.connect();
								tmpIn = btSocket.getInputStream();
								tmpOut = btSocket.getOutputStream();
								
							} catch (Exception e) {
								Log.d(Common.TAG, "Error connected to: "
										+ strAddress);
								bConnect = false;
								mmInStream = null;
								mmOutStream = null;
								btSocket = null;
								e.printStackTrace();
								mHandler.sendEmptyMessage(Common.MESSAGE_CONNECT_LOST);
								return;
							}
							mmInStream = tmpIn;
							mmOutStream = tmpOut;
							mHandler.sendEmptyMessage(Common.MESSAGE_CONNECT_SUCCEED);

						}

					}).start();
					break;
				case Common.MESSAGE_CONNECT_SUCCEED:
					Log.d("sss","MESSAGE_CONNECT_SUCCEED");
					tvTitle.setText(strName+"连接成功");
					tvTitle.setBackgroundColor(Color.GREEN);
//					addLog("连接成功");
					bConnect = true;
					new Thread(new Runnable() {
						public void run() {
							
							byte[] bufRecv = new byte[1024*10];//这里决定屏幕每次显示的字节长度
							int nRecv = 0;
							while (bConnect) {
								try {		
								
									nRecv = mmInStream.read(bufRecv);
									if (nRecv < 1) {
										Thread.sleep(100);
										continue;
									}

									byte[] nPacket = new byte[nRecv];
									System.arraycopy(bufRecv, 0, nPacket, 0, nRecv);
									mHandler.obtainMessage(Common.MESSAGE_RECV,
											nRecv, -1, nPacket).sendToTarget();
									Log.d("sss","SEND_To_MESSAGE_RECV");
									Thread.sleep(100);//决定数据刷新速度
								} catch (Exception e) {
									Log.e(Common.TAG, "Recv thread:" + e.getMessage());
									mHandler.sendEmptyMessage(Common.MESSAGE_EXCEPTION_RECV);
									break;
								}
							}
							Log.e(Common.TAG, "Exit while");
						}
					}).start();
					break;
				case Common.MESSAGE_EXCEPTION_RECV:
				case Common.MESSAGE_CONNECT_LOST:
					Log.d("sss","MESSAGE_CONNECT_LOST");
					tvTitle.setText(strName+"连接异常");
					tvTitle.setBackgroundColor(Color.RED);
//					addLog("连接异常，请退出本界面后重新连接");
					try {
						if (mmInStream != null)
							mmInStream.close();
						if (mmOutStream != null)
							mmOutStream.close();
						if (btSocket != null)
							btSocket.close();
					} catch (IOException e) {
						Log.e(Common.TAG, "Close Error");
						e.printStackTrace();
					} finally {
						mmInStream = null;
						mmOutStream = null;
						btSocket = null;
						bConnect = false;
					}
					break;
				case Common.MESSAGE_WRITE:

					break;
				case Common.MESSAGE_READ:

					break;
				case Common.MESSAGE_RECV:
					Log.d("sss","MESSAGE_RECV");
					byte[] bBuf = (byte[]) msg.obj;					
					mTempSaveString = bytesToHexString(bBuf);
					Log.d("mTempSaveString",mTempSaveString);
					mDealString += mTempSaveString;
					Log.d("mDealString",mDealString);
					//每隔3s刷新一次界面
					if( mDealString.length() > 45000 ){
				        double[] y=stringSplitAndDataToFloats(mDealString);
						Log.d("sss","stringSplitAndDataToFloats");
						Log.d("sss-y[]",y+"");
						Log.d("mDealString.length()",mDealString.length()+"");
						mSaveString = mDealString;
						mDealString = "";
				        double[] y1 =new double[y.length/4];
				        double[] y2 =new double[y.length/4];
				        double[] y3 =new double[y.length/4];
				        double[] y4 =new double[y.length/4];
				        double[] x0 =new double[y.length/4];
				        for(int i = 0; i < y.length/4 ; i++){
				        	x0[i]= i;
				        	y1[i]= y[i*4];  //第一个通道点集E
				        	y2[i]= y[i*4+1];//第二个通道点集A
				        	y3[i]= y[i*4+2];//第三个通道点集S
				        	y4[i]= y[i*4+3];//第四个通道点集I
				        }
				        //保存各点集到list
						EA_list.clear();
						AS_list.clear();
						AI_list.clear();
				        for(int i = 0;i < y1.length; i++){
							EA_list.add(y1[i]-y2[i]);
							AS_list.add(y2[i]-y3[i]);
							AI_list.add(y2[i]-y4[i]);
				        }

				        List<Double> x_list = new ArrayList<Double>();
						List<Double> y_list = new ArrayList<Double>();
						x_list.clear();
						for(int i=0;i<y1.length;i++){
								x_list.add(x0[i]);
				        }
						switch (curDaoLian){
							case 1:
								y_list.clear();
								if(value11.equals("-") && value13.equals("-") && value15.equals("-")){
									for(int i= 0;i < EA_list.size(); i++){
										y_list.add(- value12 * EA_list.get(i) - value14 * AS_list.get(i) - value16 * AI_list.get(i));
									}
								}else if(value11.equals("-") && value13.equals("-") && value15.equals("+")){
									for(int i= 0;i < EA_list.size(); i++){
										y_list.add(- value12 * EA_list.get(i) - value14 * AS_list.get(i) + value16 * AI_list.get(i));
									}
								}else if(value11.equals("-") && value13.equals("+") && value15.equals("-")) {
									for (int i = 0; i < EA_list.size(); i++) {
										y_list.add(-value12 * EA_list.get(i) + value14 * AS_list.get(i) - value16 * AI_list.get(i));
									}
								}else if(value11.equals("-") && value13.equals("+") && value15.equals("+")) {
									for (int i = 0; i < EA_list.size(); i++) {
										y_list.add(-value12 * EA_list.get(i) + value14 * AS_list.get(i) + value16 * AI_list.get(i));
									}
								}else if(value11.equals("+") && value13.equals("-") && value15.equals("-")) {
									for (int i = 0; i < EA_list.size(); i++) {
										y_list.add(value12 * EA_list.get(i) - value14 * AS_list.get(i) - value16 * AI_list.get(i));
									}
								}else if(value11.equals("+") && value13.equals("-") && value15.equals("+")) {
									for (int i = 0; i < EA_list.size(); i++) {
										y_list.add(value12 * EA_list.get(i) - value14 * AS_list.get(i) + value16 * AI_list.get(i));
									}
								}else if(value11.equals("+") && value13.equals("+") && value15.equals("-")) {
									for (int i = 0; i < EA_list.size(); i++) {
										y_list.add(value12 * EA_list.get(i) + value14 * AS_list.get(i) - value16 * AI_list.get(i));
									}
								}else if(value11.equals("+") && value13.equals("+") && value15.equals("+")) {
									for (int i = 0; i < EA_list.size(); i++) {
										y_list.add(value12 * EA_list.get(i) + value14 * AS_list.get(i) + value16 * AI_list.get(i));
									}
								}
							case 2:
								y_list.clear();
								if(value21.equals("-") && value23.equals("-") && value25.equals("-")){
									for(int i= 0;i < EA_list.size(); i++){
										y_list.add(- value22 * EA_list.get(i) - value24 * AS_list.get(i) - value26 * AI_list.get(i));
									}
								}else if(value21.equals("-") && value23.equals("-") && value25.equals("+")){
									for(int i= 0;i < EA_list.size(); i++){
										y_list.add(- value22 * EA_list.get(i) - value24 * AS_list.get(i) + value26 * AI_list.get(i));
									}
								}else if(value21.equals("-") && value23.equals("+") && value25.equals("-")) {
									for (int i = 0; i < EA_list.size(); i++) {
										y_list.add(-value22 * EA_list.get(i) + value24 * AS_list.get(i) - value26 * AI_list.get(i));
									}
								}else if(value21.equals("-") && value23.equals("+") && value25.equals("+")) {
									for (int i = 0; i < EA_list.size(); i++) {
										y_list.add(-value22 * EA_list.get(i) + value24 * AS_list.get(i) + value26 * AI_list.get(i));
									}
								}else if(value21.equals("+") && value23.equals("-") && value25.equals("-")) {
									for (int i = 0; i < EA_list.size(); i++) {
										y_list.add(value22 * EA_list.get(i) - value24 * AS_list.get(i) - value26 * AI_list.get(i));
									}
								}else if(value21.equals("+") && value23.equals("-") && value25.equals("+")) {
									for (int i = 0; i < EA_list.size(); i++) {
										y_list.add(value22 * EA_list.get(i) - value24 * AS_list.get(i) + value26 * AI_list.get(i));
									}
								}else if(value21.equals("+") && value23.equals("+") && value25.equals("-")) {
									for (int i = 0; i < EA_list.size(); i++) {
										y_list.add(value22 * EA_list.get(i) + value24 * AS_list.get(i) - value26 * AI_list.get(i));
									}
								}else if(value21.equals("+") && value23.equals("+") && value25.equals("+")) {
									for (int i = 0; i < EA_list.size(); i++) {
										y_list.add(value22 * EA_list.get(i) + value24 * AS_list.get(i) + value26 * AI_list.get(i));
									}
								}
							case 3:
								y_list.clear();
								if(value31.equals("-") && value33.equals("-") && value35.equals("-")){
									for(int i= 0;i < EA_list.size(); i++){
										y_list.add(- value32 * EA_list.get(i) - value34 * AS_list.get(i) - value36 * AI_list.get(i));
									}
								}else if(value31.equals("-") && value33.equals("-") && value35.equals("+")){
									for(int i= 0;i < EA_list.size(); i++){
										y_list.add(- value32 * EA_list.get(i) - value34 * AS_list.get(i) + value36 * AI_list.get(i));
									}
								}else if(value31.equals("-") && value33.equals("+") && value35.equals("-")) {
									for (int i = 0; i < EA_list.size(); i++) {
										y_list.add(-value32 * EA_list.get(i) + value34 * AS_list.get(i) - value36 * AI_list.get(i));
									}
								}else if(value31.equals("-") && value33.equals("+") && value35.equals("+")) {
									for (int i = 0; i < EA_list.size(); i++) {
										y_list.add(-value32 * EA_list.get(i) + value34 * AS_list.get(i) + value36 * AI_list.get(i));
									}
								}else if(value31.equals("+") && value33.equals("-") && value35.equals("-")) {
									for (int i = 0; i < EA_list.size(); i++) {
										y_list.add(value32 * EA_list.get(i) - value34 * AS_list.get(i) - value36 * AI_list.get(i));
									}
								}else if(value31.equals("+") && value33.equals("-") && value35.equals("+")) {
									for (int i = 0; i < EA_list.size(); i++) {
										y_list.add(value32 * EA_list.get(i) - value34 * AS_list.get(i) + value36 * AI_list.get(i));
									}
								}else if(value31.equals("+") && value33.equals("+") && value35.equals("-")) {
									for (int i = 0; i < EA_list.size(); i++) {
										y_list.add(value32 * EA_list.get(i) + value34 * AS_list.get(i) - value36 * AI_list.get(i));
									}
								}else if(value31.equals("+") && value33.equals("+") && value35.equals("+")) {
									for (int i = 0; i < EA_list.size(); i++) {
										y_list.add(value32 * EA_list.get(i) + value34 * AS_list.get(i) + value36 * AI_list.get(i));
									}
								}
						}
						Log.d("ss-y_list",y_list.size()+"");
						Log.d("ss-x_list",x_list.size()+"");
						updateChart(x_list, y_list, line1);
					}
					Log.d("sss","MESSAGE_RECV_END");
					//添加数据到图表
//						setData(x,y1);
//					}
					
//					char[] chars = mShowString.toCharArray();
////
//					StringBuffer hex = new StringBuffer();
//					for(int i = 0; i < chars.length; i++){
//					    hex.append(Integer.toHexString((int)chars[i]));
//					}
//					addLog("接收数据: " + mTempSaveString);
//					Save();
					break;
				case Common.MESSAGE_TOAST:
					Toast.makeText(getApplicationContext(),
							msg.getData(). getString(Common.TOAST),
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		};
		
		/**
		 *  转换十六进制通信协议字符串为其对应的double数值
		 * @param s
		 * return double[]
		 */ 	
		private static double[] stringSplitAndDataToFloats(String s)
			{  	   
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
	
		
		/**
		 * Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)
		 * 来转换成16进制字符串
		 * @param src byte[] data  
		 * @return hex string  
		 */ 
		public static String bytesToHexString(byte[] src){  
		    StringBuilder stringBuilder = new StringBuilder("");  
		    if (src == null || src.length <= 0) {  
		        return null;  
		    }  
		    for (int i = 0; i < src.length; i++) {  
		        int v = src[i] & 0xFF;  
		        String hv = Integer.toHexString(v);  
		        if (hv.length() < 2) {  
		            stringBuilder.append(0);  
		        }  
		        stringBuilder.append(hv);  
		    }  
		    return stringBuilder.toString();  
		}  
		
		public void closeAndExit() {
			if (bConnect) {
				bConnect = false;
				
				try {
					Thread.sleep(100);
					if (mmInStream != null)
						mmInStream.close();
					if (mmOutStream != null)
						mmOutStream.close();
					if (btSocket != null)
						btSocket.close();
				} catch (Exception e) {
					Log.e(Common.TAG, "Close error...");
					e.printStackTrace();
				}
			}
			finish();
		}

		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				closeAndExit();
				return true;
			} else {
				return super.onKeyDown(keyCode, event);
			}
		}
		
		@Override
		protected void onDestroy() {
			this.unregisterReceiver(connectDevices);
			Log.e(Common.TAG, "Free detail");
			super.onDestroy();
		}
		
		public void addLog(String str) {
			tvLog.append(str + "\n");
			svResult.post(new Runnable() {
				public void run() {
					svResult.fullScroll(ScrollView.FOCUS_DOWN);
				}
			});
		}
		
		public static String bytesToString(byte[] b, int length) {
			StringBuffer result = new StringBuffer("");
			for (int i = 0; i < length; i++) {
				result.append((char) (b[i]));
			}

			return result.toString();
		}

	    //保存功能实现
		private void Save() {
			//显示对话框输入文件名
			LayoutInflater factory = LayoutInflater.from(LineChartActivity1.this);  //图层模板生成器句柄
			final View DialogView =  factory.inflate(R.layout.sname, null);  //用sname.xml模板生成视图模板
			new AlertDialog.Builder(LineChartActivity1.this)
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
													if(mSaveString != null){
														stream.write(mSaveString.getBytes());
														stream.close();
														Toast.makeText(LineChartActivity1.this, "存储成功！", Toast.LENGTH_SHORT).show();
													}
												}else{
													Toast.makeText(LineChartActivity1.this, "没有存储卡！", Toast.LENGTH_LONG).show();
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
