
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
	private RelativeLayout lineChart;
	private LinearLayout linearLayout1;
	private Button btnReturn;
	public  String filename=""; //用来保存存储的文件名
	private String mTempSaveString ="";//临时数据缓存
	private String mSaveString ="";//保存用数据缓存
	private String mDisplayString ="";//显示用数据缓存
	private Button btnSave;
	private Button btnSave1;
	private InputStream tmpIn;
	private OutputStream tmpOut;
	private int fileNum = 1;
	private StringBuffer hex;
	private boolean IsFirst = true;
	private int count = 0;
	private int xTemp, yTemp;
	private double[] x, y;
	
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
		btnReturn = (Button) this.findViewById(R.id.btn_return);
		btnReturn.setOnClickListener(this);
		btnSave = (Button) this.findViewById(R.id.btn_save);
		btnSave.setOnClickListener(this);
		btnSave1 = (Button) this.findViewById(R.id.btn_save1);
		btnSave1.setOnClickListener(this);
		
        dynamic_chart_line_layout = (RelativeLayout) findViewById(R.id.chart);
        //aChartEngine初始化
        initChart();
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

		mHandler.sendEmptyMessageDelayed(Common.MESSAGE_CONNECT, 1000);
		

    }
////MpAndroid初始化------------------------------------------------------------------------
//        tvX = (TextView)findViewById(R.id.tvXMax);
//        tvY = (TextView)findViewById(R.id.tvYMax);
//
//        mSeekBarX = (SeekBar)findViewById(R.id.seekBar1);
//        mSeekBarY = (SeekBar)findViewById(R.id.seekBar2);
//
////        mSeekBarX.setProgress(40);
////        mSeekBarY.setProgress(100);
//
//        mSeekBarY.setOnSeekBarChangeListener(this);
//        mSeekBarX.setOnSeekBarChangeListener(this);
//
//        mChart = (LineChart) findViewById(R.id.chart1);
//        mChart.setOnChartGestureListener(this);
//        mChart.setOnChartValueSelectedListener(this);
//        mChart.setDrawGridBackground(true);//设置背景颜色
//
//        // no description text
//        mChart.setDescription("");
//        mChart.setNoDataTextDescription("You need to provide data for the chart.");
//
//        // enable value highlighting
//        mChart.setHighlightEnabled(true);
//
//        // enable touch gestures
//        mChart.setTouchEnabled(true);
//
//        // enable scaling and dragging
//        mChart.setDragEnabled(true);
//        mChart.setScaleEnabled(true);
////         mChart.setScaleXEnabled(true);
////         mChart.setScaleYEnabled(true);
//
//        // if disabled, scaling can be done on x- and y-axis separately
//        mChart.setPinchZoom(false);
//
//        // set an alternative background color
//        // mChart.setBackgroundColor(Color.GRAY);
//
//        // create a custom MarkerView (extend MarkerView) and specify the layout
//        // to use for it
//        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);//点击后弹出值界面
//
//        // set the marker to the chart
//        mChart.setMarkerView(mv);
//
//        // enable/disable highlight indicators (the lines that indicate the
//        // highlighted Entry)
//        mChart.setHighlightEnabled(true);
//        
//        // x-axis limit line
////        LimitLine llXAxis = new LimitLine(10f, "Index 10");
////        llXAxis.setLineWidth(4f);
////        llXAxis.enableDashedLine(10f, 10f, 0f);
////        llXAxis.setLabelPosition(LimitLabelPosition.POS_RIGHT);
////        llXAxis.setTextSize(10f);
////        
////        XAxis xAxis = mChart.getXAxis();
////        xAxis.addLimitLine(llXAxis);
//        
//        LimitLine ll1 = new LimitLine(130f, "Upper Limit");//设置上限虚线位置
//        ll1.setLineWidth(4f);//设置线宽
//        ll1.enableDashedLine(10f, 10f, 0f);//设置虚线样式
//        ll1.setLabelPosition(LimitLabelPosition.POS_RIGHT);//设置lable的位置
//        ll1.setTextSize(10f);//设置lable字体大小
//
//        LimitLine ll2 = new LimitLine(-30f, "Lower Limit");//设置下限虚线位置
//        ll2.setLineWidth(4f);
//        ll2.enableDashedLine(10f, 10f, 0f);
//        ll2.setLabelPosition(LimitLabelPosition.POS_RIGHT);
//        ll2.setTextSize(10f);
//
//        YAxis leftAxis = mChart.getAxisLeft();//y轴坐标设置
//        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
//        leftAxis.addLimitLine(ll1);//设置上限和下限
//        leftAxis.addLimitLine(ll2);
//        leftAxis.setAxisMaxValue(5f);//设置y轴上限坐标
//        leftAxis.setAxisMinValue(-5f);//设置y轴下限坐标
//        leftAxis.setStartAtZero(false);//设置y轴从0开始
////        leftAxis.setYOffset(20f);//设置y轴偏移
////        leftAxis.enableGridDashedLine(10f, 10f, 0f);//设置虚线网格
//        
//        // limit lines are drawn behind data (and not on top)
////        leftAxis.setDrawLimitLinesBehindData(true);
//
//        mChart.getAxisRight().setEnabled(false);//设置右侧坐标不显示
//
//        // add data
////        setData(45, 100);//第一个参数为点的数量。第二个参数为y值的范围
//
////        mChart.setVisibleXRange(20);
////        mChart.setVisibleYRange(20f, AxisDependency.LEFT);
////        mChart.centerViewTo(20, 50, AxisDependency.LEFT);
//        
//        mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);
////        mChart.invalidate();
//        
//        // get the legend (only possible after setting data)
//        Legend l = mChart.getLegend();
//
//        // modify the legend ...
//        // l.setPosition(LegendPosition.LEFT_OF_CHART);
//        l.setForm(LegendForm.LINE);
//
//        // // dont forget to refresh the drawing
//        // mChart.invalidate();
//    }
//    
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.line, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//            case R.id.actionToggleValues: { 
//                for (DataSet<?> set : mChart.getData().getDataSets())
//                    set.setDrawValues(!set.isDrawValuesEnabled());
//
//                mChart.invalidate();
//                break;
//            }
//            case R.id.actionToggleHighlight: {
//                if (mChart.isHighlightEnabled())
//                    mChart.setHighlightEnabled(false);
//                else
//                    mChart.setHighlightEnabled(true);
//                mChart.invalidate();
//                break;
//            }
//            case R.id.actionToggleFilled: {
//
//                ArrayList<LineDataSet> sets = (ArrayList<LineDataSet>) mChart.getData()
//                        .getDataSets();
//
//                for (LineDataSet set : sets) {
//                    if (set.isDrawFilledEnabled())
//                        set.setDrawFilled(false);
//                    else
//                        set.setDrawFilled(true);
//                }
//                mChart.invalidate();
//                break;
//            }
//            case R.id.actionToggleCircles: {
//                ArrayList<LineDataSet> sets = (ArrayList<LineDataSet>) mChart.getData()
//                        .getDataSets();
//
//                for (LineDataSet set : sets) {
//                    if (set.isDrawCirclesEnabled())
//                        set.setDrawCircles(false);
//                    else
//                        set.setDrawCircles(true);
//                }
//                mChart.invalidate();
//                break;
//            }
//            case R.id.actionToggleCubic: {
//                ArrayList<LineDataSet> sets = (ArrayList<LineDataSet>) mChart.getData()
//                        .getDataSets();
//
//                for (LineDataSet set : sets) {
//                    if (set.isDrawCubicEnabled())
//                        set.setDrawCubic(false);
//                    else
//                        set.setDrawCubic(true);
//                }
//                mChart.invalidate();
//                break;
//            }
//            case R.id.actionToggleStartzero: {
//                mChart.getAxisLeft().setStartAtZero(!mChart.getAxisLeft().isStartAtZeroEnabled());
//                mChart.getAxisRight().setStartAtZero(!mChart.getAxisRight().isStartAtZeroEnabled());
//                mChart.invalidate();
//                break;
//            }
//            case R.id.actionTogglePinch: {
//                if (mChart.isPinchZoomEnabled())
//                    mChart.setPinchZoom(false);
//                else
//                    mChart.setPinchZoom(true);
//
//                mChart.invalidate();
//                break;
//            }
//            case R.id.actionToggleAutoScaleMinMax: {
//                mChart.setAutoScaleMinMaxEnabled(!mChart.isAutoScaleMinMaxEnabled());
//                mChart.notifyDataSetChanged();
//                break;
//            }
//            case R.id.animateX: {
//                mChart.animateX(3000);
//                break;
//            }
//            case R.id.animateY: {
//                mChart.animateY(3000, Easing.EasingOption.EaseInCubic);
//                break;
//            }
//            case R.id.animateXY: {
//                mChart.animateXY(3000, 3000);
//                break;
//            }
//            case R.id.actionToggleFilter: {
//
//                // the angle of filtering is 35掳
//                Approximator a = new Approximator(ApproximatorType.DOUGLAS_PEUCKER, 35);
//
//                if (!mChart.isFilteringEnabled()) {
//                    mChart.enableFiltering(a);
//                } else {
//                    mChart.disableFiltering();
//                }
//                mChart.invalidate();
//
//                //
//                // for(int i = 0; i < 10; i++) {
//                // mChart.addEntry(new Entry((float) (Math.random() * 100),
//                // i+2), 0);
//                // mChart.invalidate();
//                // }
//                //
//                // Toast.makeText(getApplicationContext(), "valcount: " +
//                // mChart.getDataOriginal().getYValCount() + ", valsum: " +
//                // mChart.getDataOriginal().getYValueSum(),
//                // Toast.LENGTH_SHORT).show();
//                //
//                break;
//            }
//            case R.id.actionSave: {
//   
////            	FileUtils.saveToSdCard(yVals, "title" + System.currentTimeMillis());//保存为txt
//            	//保存为png图片
//                if (mChart.saveToPath("title" + System.currentTimeMillis(), "")) {
//                    Toast.makeText(getApplicationContext(), "Saving SUCCESSFUL!",
//                            Toast.LENGTH_SHORT).show();
//                } else
//                    Toast.makeText(getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT)
//                            .show();
//
//                // mChart.saveToGallery("title"+System.currentTimeMillis())
//                break;
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//        tvX.setText("" + (mSeekBarX.getProgress() + 1));
//        tvY.setText("" + (mSeekBarY.getProgress()));
//
////        setData(mSeekBarX.getProgress() + 1, mSeekBarY.getProgress());
//
//        // redraw
//        mChart.invalidate();
//    }
//
//    @Override
//    public void onStartTrackingTouch(SeekBar seekBar) {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void onStopTrackingTouch(SeekBar seekBar) {
//        // TODO Auto-generated method stub
//
//    }
//
//    /**
//     * 1、设置x,y坐标
//     * 2、LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
//     * 3、ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
//     *    dataSets.add(set1); 
//     * 4、LineData data = new LineData(xVals, dataSets);
//     * 5、mChart.setData(data);
//     */
//    
//    private void setData(int x, Float[] y) {
//    	
//    	//x轴坐标
//        for (int i = 0; i < x; i++) {
//            xVals.add((i+count) + "");
//            count+=x;
//        }
//        
//        for (int i = 0; i < x; i++) {
//
//        	float val = y[i];
////            float mult = (y + 1);
////           
////            float val = (float) (Math.random() * mult) + 3;// + (float)
////                                                           // ((mult *
////                                                           // 0.1) / 10);
//            yVals.add(new Entry(val, i));
//        }
//
//        // create a dataset and give it a type
//        LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
//        // set1.setFillAlpha(110);
//        // set1.setFillColor(Color.RED);
//
//        // set the line to be drawn like this "- - - - - -"
////        set1.enableDashedLine(10f, 5f, 0f);//设置虚线连接
//        set1.setColor(Color.BLUE);
//        set1.setCircleColor(Color.BLACK);
//        set1.setLineWidth(1f);
//        set1.setCircleSize(3f);
//        set1.setDrawCircleHole(false);
//        set1.setValueTextSize(9f);
//        set1.setFillAlpha(65);
//        set1.setFillColor(Color.BLACK);
////        set1.setDrawFilled(true);
////         set1.setShader(new LinearGradient(0, 0, 0, mChart.getHeight(),
////         Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));
//
//        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
//        dataSets.add(set1); // add the datasets
//
//        // create a data object with the datasets
//        LineData data = new LineData(xVals, dataSets);
//
//        // set data
//        mChart.setData(data);
//    }
//    
//    @Override
//    public void onChartLongPressed(MotionEvent me) {
//        Log.i("LongPress", "Chart longpressed.");
//    }
//
//    @Override
//    public void onChartDoubleTapped(MotionEvent me) {
//        Log.i("DoubleTap", "Chart double-tapped.");
//    }
//
//    @Override
//    public void onChartSingleTapped(MotionEvent me) {
//        Log.i("SingleTap", "Chart single-tapped.");
//    }
//
//    @Override
//    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
//        Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
//    }
//
//    @Override
//    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
//        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
//    }
//
//	@Override
//	public void onChartTranslate(MotionEvent me, float dX, float dY) {
//		Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
//	}
//
//	@Override
//    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
//        Log.i("Entry selected", e.toString());
//        Log.i("", "low: " + mChart.getLowestVisibleXIndex() + ", high: " + mChart.getHighestVisibleXIndex());
//    }
//
//    @Override
//    public void onNothingSelected() {
//        Log.i("Nothing selected", "Nothing selected.");
//    }
//

//--------------------------------------------------------------------------------------------------
    //aChartEngine初始化
	private void initChart() {
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

        // 将XYSeries对象和XYSeriesRenderer对象分别添加到XYMultipleSeriesDataset对象和XYMultipleSeriesRenderer对象中。
        mDataset.addSeries(line1);
        mXYMultipleSeriesRenderer.addSeriesRenderer(renderer1);

        // 配置chart参数
        setChartSettings(mXYMultipleSeriesRenderer, "X", "Y", 0, 10, 0, 5, Color.RED,
                Color.WHITE);

        // 通过该函数获取到一个View 对象
        chart = ChartFactory.getCubeLineChartView(this, mDataset, mXYMultipleSeriesRenderer, 0.05f);
        // 将该View 对象添加到layout中。
        dynamic_chart_line_layout.addView(chart, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

	}
	
	private XYSeriesRenderer initRenderer(XYSeriesRenderer renderer, int color,
            PointStyle style, boolean fill) {
        // 设置图表中曲线本身的样式，包括颜色、点的大小以及线的粗细等
        renderer.setColor(color);
        renderer.setPointStyle(style);
        renderer.setFillPoints(fill);
        renderer.setLineWidth(1);
        return renderer;
    }

    protected void setChartSettings(XYMultipleSeriesRenderer mXYMultipleSeriesRenderer,
            String xTitle, String yTitle, double xMin, double xMax,
            double yMin, double yMax, int axesColor, int labelsColor) {
        // 有关对图表的渲染可参看api文档
        mXYMultipleSeriesRenderer.setChartTitle(title);
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
        mXYMultipleSeriesRenderer.setGridColor(Color.GRAY);
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
		case R.id.btn_save1:
			Save();
		default:
			break;
		}
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
					tvTitle.setText(strName+"连接成功");
					tvTitle.setBackgroundColor(Color.GREEN);
//					addLog("连接成功");
					bConnect = true;
					new Thread(new Runnable() {
						public void run() {
							
							byte[] bufRecv = new byte[1024];//这里决定屏幕每次显示的字节长度
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

					byte[] bBuf = (byte[]) msg.obj;					
					mTempSaveString = bytesToHexString(bBuf);
					mSaveString += mTempSaveString;
					//每隔5s刷新一次界面
					if( mSaveString.length() > 75000 ){
				        double[] y=stringSplitAndDataToFloats(mSaveString);
				        mSaveString = "";
				        double[] y1 =new double[y.length/4];
				        double[] y2 =new double[y.length/4];
				        double[] y3 =new double[y.length/4];
				        double[] y4 =new double[y.length/4];
				        double[] x0 =new double[y.length/4];
				        for(int i = 0; i < y.length/4 ; i++){
				        	x0[i]= i;
				        	y1[i]= y[i*4];//第一个点集
				        	y2[i]= y[i*4+1];//第二个点集
				        	y3[i]= y[i*4+2];//第三个点集
				        	y4[i]= y[i*4+3];//第四个点集
				        }
				        //保存各点集到list
				        List<Double> y1_list = new ArrayList<Double>();
				        for(int i=0;i<y1.length;i++){
				        	y1_list.add(y1[i]);
				        }
				        
				        List<Double> x_list = new ArrayList<Double>();
				        for(int i=0;i<y1.length;i++){
				        	x_list.add(x0[i]);
				        }
				        
				        updateChart(x_list, y1_list, line1);
					}
				        //添加数据到图表
//						setData(x,y1);
//					}
					
//					char[] chars = mSaveString.toCharArray();
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
		 * 转换十六进制通信协议字符串为其对应的double数值
		 * 
		 * @param String 		  
		 * @return double[]
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
		 * 来转换成16进制字符串。 
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
													stream.write(mSaveString.getBytes());
													stream.close();
													Toast.makeText(LineChartActivity1.this, "存储成功！", Toast.LENGTH_SHORT).show();
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
