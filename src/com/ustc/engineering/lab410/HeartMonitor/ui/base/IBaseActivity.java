package com.ustc.engineering.lab410.HeartMonitor.ui.base;
import android.os.Bundle;

public abstract interface IBaseActivity {

	/**
	 * 创建，activity onCreate 时调用 
	 * 在这里进行UI绑定，同activityonCreate事件
	 * 
	 * @param savedInstanceState
	 */
	public abstract void onCreateActivity(Bundle savedInstanceState);
	
	/**
	 * 暂停，activity onPause 时调用 
	 */
	public abstract void onPauseActivity();
	
	/**
	 * 恢复，activity onResume 时调用 
	 * 在这里恢复页面相关数据
	 */
	public abstract void onResumeActivity();
	
	/**
	 * 释放，activity onDestroy 时调用 
	 * 在这里进行资源的释放
	 */
	public abstract void onDestroyActivity();
	
	/**
	 * 获取类名
	 */
	public abstract String getClassName();

}