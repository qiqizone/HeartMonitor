package com.ustc.engineering.lab410.HeartMonitor.ui.bean;

/*
 * HM蓝牙模块远程控制DEMO(for Android)
 * 济南华茂科技有限公司
 * 参考资料：http://www.jnhuamao.cn/bluetooth.pdf
 * 本程序演示了如何通过手机对远程蓝牙模块进行控制
 * E-Mail: webmaster@jnhuamao.cn
 * QQ: 454313
 */

public class Common {
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	public static final int MESSAGE_CONNECT = 6;
	public static final int MESSAGE_CONNECT_SUCCEED = 7;
	public static final int MESSAGE_CONNECT_LOST = 8;
	public static final int MESSAGE_RECV = 10;
	public static final int MESSAGE_EXCEPTION_RECV = 11;
	public static final String TOAST = "toast";
	public static final String TAG = "BlueToothTool";

}
