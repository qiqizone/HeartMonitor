package com.ustc.engineering.lab410.HeartMonitor.ui.base;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * fragment需要继承此类，并且使用fragment的activity需要实现继承与BaseFragmentShell，并实现MessageListener接口��Ҫ�̳д��࣬����ʹ��fragment��activity��Ҫʵ�ּ̳���BaseFragmentShell����ʵ��MessageListener�ӿ�
 * @author qingwu
 *
 */
public abstract class BaseFragment extends Fragment implements IBaseFragment{
	
	public IFragmentMsgListener mMessageListener;
	protected Context mContext;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		//�������෽��
		return onCreateFragmentView(inflater, container, savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		//Log.i("demo", "is onCreate");
		super.onCreate(savedInstanceState);
		onFragmentCreate(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
		if(activity instanceof IFragmentMsgListener)
			mMessageListener = (IFragmentMsgListener) activity;
	}
	
	@Override
	public void onPause() {
		//Log.i("demo", "is onPause");
		onFragmentPause();
		super.onPause();
		String className = this.getClass().getName();
		MobclickAgent.onPageEnd(className);
	}

	@Override
	public void onDestroyView() {
		//Log.i("demo", "is onDestroyView");
		onFragmentDestroyView();
		super.onDestroyView();
	}

	@Override
	public void onStart() {
		//Log.i("demo", "is onStart");
		super.onStart();
	}

	@Override
	public void onResume() {
		//Log.i("demo", "is onResume");
		onFragmentResume();
		super.onResume();
		String className = this.getClass().getName();
		MobclickAgent.onPageStart(className);
	}

	@Override
	public void onDestroy() {
		//Log.i("demo", "is onDestroy");
		onFragmentDestroy();
		super.onDestroy();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		//Log.i("demo", "is onSaveInstanceState");
		super.onSaveInstanceState(outState);
	}
	
}
