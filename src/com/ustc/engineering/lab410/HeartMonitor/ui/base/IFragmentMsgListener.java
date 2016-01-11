package com.ustc.engineering.lab410.HeartMonitor.ui.base;

import android.os.Message;

public interface IFragmentMsgListener {
	/**
	 * 接收消息
	 * 
	 * @param msg
	 */
	void onFragmentMessage(Message msg);
}
