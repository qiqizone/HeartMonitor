package com.ustc.engineering.lab410.HeartMonitor.ui.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * fragment��ؽӿڣ��ɸ����������
 * @author qingwu
 *
 */
public interface IBaseFragment {

	/**
	 * fragment����
	 * @param savedInstanceState
	 */
	public abstract void onFragmentCreate(Bundle savedInstanceState);
	
	/**
	 * fragment��������,UI��ʼ���ڴ����
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 * @return
	 */
	public abstract View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState);
	
	/**
	 * Fragment�ָ���running��
	 * @param savedInstanceState
	 */
	public abstract void onFragmentResume();
	
	/**
	 * Fragment��ͣ
	 * @param savedInstanceState
	 */
	public abstract void onFragmentPause();
	
	/**
	 * fragment���ٽ���
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 * @return
	 */
	public abstract View onFragmentDestroyView();

	
	/**
	 * fragment����
	 */
	public abstract void onFragmentDestroy();
}
