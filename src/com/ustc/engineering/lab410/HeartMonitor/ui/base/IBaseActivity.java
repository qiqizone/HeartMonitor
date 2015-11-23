package com.ustc.engineering.lab410.HeartMonitor.ui.base;
import android.os.Bundle;

public abstract interface IBaseActivity {

	/**
	 * ������activity onCreate ʱ���� 
	 * ���������UI�󶨣�ͬactivityonCreate�¼�
	 * 
	 * @param savedInstanceState
	 */
	public abstract void onCreateActivity(Bundle savedInstanceState);
	
	/**
	 * ��ͣ��activity onPause ʱ���� 
	 */
	public abstract void onPauseActivity();
	
	/**
	 * �ָ���activity onResume ʱ���� 
	 * ������ָ�ҳ���������
	 */
	public abstract void onResumeActivity();
	
	/**
	 * �ͷţ�activity onDestroy ʱ���� 
	 * �����������Դ���ͷ�
	 */
	public abstract void onDestroyActivity();
	
	/**
	 * ��ȡ����
	 */
	public abstract String getClassName();

}