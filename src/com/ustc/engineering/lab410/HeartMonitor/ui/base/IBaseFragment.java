package com.ustc.engineering.lab410.HeartMonitor.ui.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * fragment相关接口，可根据需求添加
 * @author qingwu
 *
 */
public interface IBaseFragment {

	/**
	 * fragment创建
	 * @param savedInstanceState
	 */
	public abstract void onFragmentCreate(Bundle savedInstanceState);

	/**
	 * fragment创建界面,UI初始化在此完成
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 * @return
	 */
	public abstract View onCreateFragmentView(LayoutInflater inflater,
											  ViewGroup container, Bundle savedInstanceState);

	/**
	 * Fragment恢复（running）
	 * @param savedInstanceState
	 */
	public abstract void onFragmentResume();

	/**
	 * Fragment暂停
	 * @param savedInstanceState
	 */
	public abstract void onFragmentPause();

	/**
	 * fragment销毁界面
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 * @return
	 */
	public abstract View onFragmentDestroyView();


	/**
	 * fragment销毁
	 */
	public abstract void onFragmentDestroy();
}
