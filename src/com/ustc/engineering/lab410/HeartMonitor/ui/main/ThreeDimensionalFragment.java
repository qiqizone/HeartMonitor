package com.ustc.engineering.lab410.HeartMonitor.ui.main;


import com.ustc.engineering.lab410.HeartMonitor.R;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.ustc.engineering.lab410.HeartMonitor.ui.main.STLViewActivity;
public class ThreeDimensionalFragment extends Fragment implements View.OnClickListener{

	private View mRootView = null;
	private Button btn3D;
	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
		  if (mRootView == null) {
			  mRootView = inflater.inflate(R.layout.three_dimensional_fragment,
					  container, false);
			  initView();

		  }
		  ViewGroup parent = (ViewGroup) mRootView.getParent();
		  if (parent != null) {
			  parent.removeView(mRootView);
		  }
		  return mRootView;
	  }

	private void initView(){
		btn3D = (Button) mRootView.findViewById(R.id.btn_3d_display);
		btn3D.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_3d_display:
				Intent intent = new Intent(getActivity(), STLViewActivity.class);
				this.startActivity(intent);
				break;
			default:
				break;
		}
	}
}
