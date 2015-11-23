
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
	
	private ArrayList<Integer> nNeeds; //��Ҫ�ĳ���
	private ArrayList<String>  sRecvs; //��Ž��յ�����

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
	public  String filename=""; //��������洢���ļ���
	private String mTempSaveString ="";//��ʱ���ݻ���
	private String mSaveString ="";//���������ݻ���
	private String mDisplayString ="";//��ʾ�����ݻ���
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
	
    // ���ڴ��ÿ�����ߵĵ�����
    private XYSeries line1, line2;
    // ���ڴ��������Ҫ���Ƶ�XYSeries
    private XYMultipleSeriesDataset mDataset;
    // ���ڴ��ÿ�����ߵķ��
    private XYSeriesRenderer renderer1, renderer2;
    // ���ڴ��������Ҫ���Ƶ����ߵķ��
    private XYMultipleSeriesRenderer mXYMultipleSeriesRenderer;
    private GraphicalView chart;
	private String title = "�ĵ���Ϣ";
	
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
        //aChartEngine��ʼ��
        initChart();
//        
//        x = new double[10];
//        y = new double[10];
        
//		refreshChart();
		//����������ʾ����
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		tvLog = (TextView) this.findViewById(R.id.tvInfo);
		Bundle bunde = this.getIntent().getExtras();
		strName = bunde.getString("NAME");
		strAddress = bunde.getString("MAC");
		tvTitle.setText(strName);
		tvLog.append(strName + "......\n");
		
		btAdapt = BluetoothAdapter.getDefaultAdapter();
		if (btAdapt == null) {
			tvLog.append("����������������ʧ��\n");
			finish();
			return;
		}

		if (btAdapt.getState() != BluetoothAdapter.STATE_ON) {
			tvLog.append("��������״̬������������ʧ��\n");
			finish();
			return;
		}

		IntentFilter intent = new IntentFilter();
		intent.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		intent.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

		registerReceiver(connectDevices, intent);

		mHandler.sendEmptyMessageDelayed(Common.MESSAGE_CONNECT, 1000);
		

    }
////MpAndroid��ʼ��------------------------------------------------------------------------
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
//        mChart.setDrawGridBackground(true);//���ñ�����ɫ
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
//        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);//����󵯳�ֵ����
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
//        LimitLine ll1 = new LimitLine(130f, "Upper Limit");//������������λ��
//        ll1.setLineWidth(4f);//�����߿�
//        ll1.enableDashedLine(10f, 10f, 0f);//����������ʽ
//        ll1.setLabelPosition(LimitLabelPosition.POS_RIGHT);//����lable��λ��
//        ll1.setTextSize(10f);//����lable�����С
//
//        LimitLine ll2 = new LimitLine(-30f, "Lower Limit");//������������λ��
//        ll2.setLineWidth(4f);
//        ll2.enableDashedLine(10f, 10f, 0f);
//        ll2.setLabelPosition(LimitLabelPosition.POS_RIGHT);
//        ll2.setTextSize(10f);
//
//        YAxis leftAxis = mChart.getAxisLeft();//y����������
//        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
//        leftAxis.addLimitLine(ll1);//�������޺�����
//        leftAxis.addLimitLine(ll2);
//        leftAxis.setAxisMaxValue(5f);//����y����������
//        leftAxis.setAxisMinValue(-5f);//����y����������
//        leftAxis.setStartAtZero(false);//����y���0��ʼ
////        leftAxis.setYOffset(20f);//����y��ƫ��
////        leftAxis.enableGridDashedLine(10f, 10f, 0f);//������������
//        
//        // limit lines are drawn behind data (and not on top)
////        leftAxis.setDrawLimitLinesBehindData(true);
//
//        mChart.getAxisRight().setEnabled(false);//�����Ҳ����겻��ʾ
//
//        // add data
////        setData(45, 100);//��һ������Ϊ����������ڶ�������Ϊyֵ�ķ�Χ
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
//                // the angle of filtering is 35°
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
////            	FileUtils.saveToSdCard(yVals, "title" + System.currentTimeMillis());//����Ϊtxt
//            	//����ΪpngͼƬ
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
//     * 1������x,y����
//     * 2��LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
//     * 3��ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
//     *    dataSets.add(set1); 
//     * 4��LineData data = new LineData(xVals, dataSets);
//     * 5��mChart.setData(data);
//     */
//    
//    private void setData(int x, Float[] y) {
//    	
//    	//x������
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
////        set1.enableDashedLine(10f, 5f, 0f);//������������
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
    //aChartEngine��ʼ��
	private void initChart() {
		// TODO Auto-generated method stub
		// ��ʼ�������뱣֤XYMultipleSeriesDataset�����е�XYSeries������
        // XYMultipleSeriesRenderer�����е�XYSeriesRenderer����һ����
        line1 = new XYSeries("����1");
        renderer1 = new XYSeriesRenderer();
        mDataset = new XYMultipleSeriesDataset();
        mXYMultipleSeriesRenderer = new XYMultipleSeriesRenderer();

        // ��XYSeries��XYSeriesRenderer�Ķ���Ĳ�����ֵ
        // initLine(line1);
        // initLine(line2);
        initRenderer(renderer1, Color.GREEN, PointStyle.CIRCLE, true);

        // ��XYSeries�����XYSeriesRenderer����ֱ���ӵ�XYMultipleSeriesDataset�����XYMultipleSeriesRenderer�����С�
        mDataset.addSeries(line1);
        mXYMultipleSeriesRenderer.addSeriesRenderer(renderer1);

        // ����chart����
        setChartSettings(mXYMultipleSeriesRenderer, "X", "Y", 0, 10, 0, 5, Color.RED,
                Color.WHITE);

        // ͨ���ú�����ȡ��һ��View ����
        chart = ChartFactory.getCubeLineChartView(this, mDataset, mXYMultipleSeriesRenderer, 0.05f);
        // ����View ������ӵ�layout�С�
        dynamic_chart_line_layout.addView(chart, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

	}
	
	private XYSeriesRenderer initRenderer(XYSeriesRenderer renderer, int color,
            PointStyle style, boolean fill) {
        // ����ͼ�������߱������ʽ��������ɫ����Ĵ�С�Լ��ߵĴ�ϸ��
        renderer.setColor(color);
        renderer.setPointStyle(style);
        renderer.setFillPoints(fill);
        renderer.setLineWidth(1);
        return renderer;
    }

    protected void setChartSettings(XYMultipleSeriesRenderer mXYMultipleSeriesRenderer,
            String xTitle, String yTitle, double xMin, double xMax,
            double yMin, double yMax, int axesColor, int labelsColor) {
        // �йض�ͼ�����Ⱦ�ɲο�api�ĵ�
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
	* ����µ����ݣ����飬�������ߣ�ֻ�����������߳�
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
			closeAndExit();//ÿ�η��عر���������
//			finish();//���ص����ر���������e

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
					tvTitle.setText(strName+"���ӳɹ�");
					tvTitle.setBackgroundColor(Color.GREEN);
//					addLog("���ӳɹ�");
					bConnect = true;
					new Thread(new Runnable() {
						public void run() {
							
							byte[] bufRecv = new byte[1024];//���������Ļÿ����ʾ���ֽڳ���
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
									Thread.sleep(100);//��������ˢ���ٶ�
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
					tvTitle.setText(strName+"�����쳣");
					tvTitle.setBackgroundColor(Color.RED);
//					addLog("�����쳣�����˳����������������");
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
					//ÿ��5sˢ��һ�ν���
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
				        	y1[i]= y[i*4];//��һ���㼯
				        	y2[i]= y[i*4+1];//�ڶ����㼯
				        	y3[i]= y[i*4+2];//�������㼯
				        	y4[i]= y[i*4+3];//���ĸ��㼯
				        }
				        //������㼯��list
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
				        //������ݵ�ͼ��
//						setData(x,y1);
//					}
					
//					char[] chars = mSaveString.toCharArray();
////
//					StringBuffer hex = new StringBuffer();
//					for(int i = 0; i < chars.length; i++){
//					    hex.append(Integer.toHexString((int)chars[i]));
//					}
//					addLog("��������: " + mTempSaveString);
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
		 * ת��ʮ������ͨ��Э���ַ���Ϊ���Ӧ��double��ֵ
		 * 
		 * @param String 		  
		 * @return double[]
		 */ 	
		private static double[] stringSplitAndDataToFloats(String s)
			{  	   
				String[] a = s.split("c00000"); //�ָ���ʶ��
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
		        char ss = '7';//���λ����7Ϊ����
		        for(int i=0;i<newStr.length;i++){
		        	if(newStr[i].charAt(0) > ss ){
		        		data[i] = -(~Integer.valueOf(newStr[i],16)+1 & 0xffffff); //���λ����7��ȡ����1��0xffffff����Ӹ���  
		        	}else{
		        		data[i] = Integer.valueOf(newStr[i],16);  
		        	}         
		            datas[i] = (double) (2.4*data[i]/8388607);
		        }
		        return datas;
			}
	
		
		/**
		 * Convert byte[] to hex string.�������ǿ��Խ�byteת����int��Ȼ������Integer.toHexString(int)
		 * ��ת����16�����ַ����� 
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
		
	    //���湦��ʵ��
		private void Save() {
			//��ʾ�Ի��������ļ���
			LayoutInflater factory = LayoutInflater.from(LineChartActivity1.this);  //ͼ��ģ�����������
			final View DialogView =  factory.inflate(R.layout.sname, null);  //��sname.xmlģ��������ͼģ��
			new AlertDialog.Builder(LineChartActivity1.this)
									.setTitle("�ļ���")
									.setView(DialogView)   //������ͼģ��
									.setPositiveButton("ȷ��",
									new DialogInterface.OnClickListener() //ȷ��������Ӧ����
									{
										public void onClick(DialogInterface dialog, int whichButton){
											EditText text1 = (EditText)DialogView.findViewById(R.id.sname);  //�õ��ļ����������
											filename = text1.getText().toString();  //�õ��ļ���
											
											try{
												if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  //���SD����׼����
													
													filename =filename+".txt";   //���ļ���ĩβ����.txt
//													fileNum++;
													File sdCardDir = Environment.getExternalStorageDirectory();  //�õ�SD����Ŀ¼
													File BuildDir = new File(sdCardDir, "/data");   //��dataĿ¼���粻����������
													if(BuildDir.exists()==false)BuildDir.mkdirs();
													File saveFile =new File(BuildDir, filename);  //�½��ļ���������Ѵ������½��ĵ�
													FileOutputStream stream = new FileOutputStream(saveFile);  //���ļ�������
													stream.write(mSaveString.getBytes());
													stream.close();
													Toast.makeText(LineChartActivity1.this, "�洢�ɹ���", Toast.LENGTH_SHORT).show();
												}else{
													Toast.makeText(LineChartActivity1.this, "û�д洢����", Toast.LENGTH_LONG).show();
												}
											
											}catch(IOException e){
												e.printStackTrace();
												return;
											}		
										}
									})
									.setNegativeButton("ȡ��",   //ȡ��������Ӧ����,ֱ���˳��Ի������κδ��� 
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) { 
										}
									}).show();  //��ʾ�Ի���
		}		
}
