package com.ustc.engineering.lab410.HeartMonitor.ui.main;

/*
 * HM����ģ��Զ�̿���DEMO(for Android)
 * ���ϻ�ï�Ƽ����޹�˾
 * �ο����ϣ�http://www.jnhuamao.cn/bluetooth.pdf
 * ��������ʾ�����ͨ���ֻ���Զ������ģ����п���
 * E-Mail: webmaster@jnhuamao.cn
 * QQ: 454313
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;







import com.ustc.engineering.lab410.HeartMonitor.R;
import com.ustc.engineering.lab410.HeartMonitor.ui.bean.Common;
import com.ustc.engineering.lab410.HeartMonitor.ui.main.LineChartActivity1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class showDetail extends Activity implements OnClickListener {
	public static String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	private InputStream mmInStream;
	private OutputStream mmOutStream;
	ScrollView svResult;
	
	private ArrayList<Integer> nNeeds; //��Ҫ�ĳ���
	private ArrayList<String>  sRecvs; //��Ž��յ�����
	private int nCurrent;

	Button btnSend;
	TextView tvTitle, tvLog;
	EditText edtSend;

	BluetoothAdapter btAdapt = null;
	BluetoothSocket btSocket = null;

	Boolean bConnect = false;
	String strName = null;
	String strAddress = null;
	private Button btn_Wave;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.detail);

		svResult = (ScrollView) findViewById(R.id.svResult);

//		btnSend = (Button) this.findViewById(R.id.btnSend);
		btn_Wave= (Button) this.findViewById(R.id.btn_wave);
		btn_Wave.setOnClickListener(this);
//		edtSend = (EditText) this.findViewById(R.id.edtSend);

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

	public void setButtonColor(Button btn, Boolean bOn) {
		if (bOn) {
			btn.setTextColor(Color.GREEN);
			btn.setId(1);
		} else {
			btn.setTextColor(Color.BLACK);
			btn.setId(0);
		}
	}

	// Hander
	public final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Common.MESSAGE_CONNECT:
				new Thread(new Runnable() {
					public void run() {
						InputStream tmpIn;
						OutputStream tmpOut;
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
//							btSocket = btDev.createRfcommSocketToServiceRecord(uuid);
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
				addLog("���ӳɹ�");
				bConnect = true;
				new Thread(new Runnable() {
					public void run() {
						byte[] bufRecv = new byte[1024];
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
								Thread.sleep(100);
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
				addLog("�����쳣�����˳����������������");
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
				addLog("��������: " + bytesToString(bBuf, msg.arg1));
				break;
			case Common.MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(Common.TOAST),
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	private BroadcastReceiver connectDevices = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d(Common.TAG, "Receiver:" + action);
			if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
			} else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {

			}
		}
	};

	@Override
	protected void onDestroy() {
		this.unregisterReceiver(connectDevices);
		Log.e(Common.TAG, "Free detail");
		super.onDestroy();
	}

	/* DEMO���Ϊ�򵥣��ڱ�д����Ӧ��ʱ���뽫�˺����ŵ��߳���ִ�У�����UI����Ӧ */
	public void send(String strValue) {
		if (!bConnect)
			return;
		try {
			if (mmOutStream == null)
				return;
			mmOutStream.write(strValue.getBytes());
			addLog("����:" + strValue + "\r\n");
		} catch (Exception e) {
			Toast.makeText(this, "����ָ��ʧ��!", Toast.LENGTH_SHORT).show();
			return;
		}
	}
	
	
	// ��ť�¼�
	class ClickEvent implements View.OnClickListener, View.OnTouchListener {
		@SuppressLint("ShowToast")
		@Override
		public void onClick(View v) {
			if (v == btnSend) {
				if (edtSend.length() < 1) {
					Toast.makeText(showDetail.this, "������Ҫ���͵�����~~", 500).show();
					return;
				}
				send(edtSend.getText().toString());
				return;
			}
		}
		
		public boolean onTouch(View v, MotionEvent event) {  
			return false;
		}
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

	public static String bytesToString(byte[] b, int length) {
		StringBuffer result = new StringBuffer("");
		for (int i = 0; i < length; i++) {
			result.append((char) (b[i]));
		}

		return result.toString();
	}

	public void addLog(String str) {
		tvLog.append(str + "\n");
		svResult.post(new Runnable() {
			public void run() {
				svResult.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_wave:
			Intent i = new Intent(this, LineChartActivity1.class);
            startActivity(i);
            break;

		default:
			break;
		}
	}

}
